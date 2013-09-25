package eu.gloria.rt.worker.advertisement.validator;

import java.nio.channels.ClosedByInterruptException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import eu.gloria.rt.catalogue.Catalogue;
import eu.gloria.rt.catalogue.CatalogueTools;
import eu.gloria.rt.catalogue.ObjInfo;
import eu.gloria.rt.catalogue.Observer;
import eu.gloria.rt.catalogue.RTSInfo;
import eu.gloria.rt.db.scheduler.AdvertisementState;
import eu.gloria.rt.db.scheduler.ObservingPlanManager;
import eu.gloria.rt.db.scheduler.ObservingPlanState;
import eu.gloria.rt.db.util.DBUtil;
import eu.gloria.rt.worker.core.Worker;
import eu.gloria.rti.sch.core.ObservingPlan;
import eu.gloria.rti.sch.core.ObservingPlanExecTimePredictor;
import eu.gloria.rti.sch.core.TimeFrame;
import eu.gloria.rti.sch.core.TimeFrameIterator;
import eu.gloria.rti.sch.core.plan.constraint.Constraint;
import eu.gloria.rti.sch.core.plan.constraint.ConstraintTarget;
import eu.gloria.rti.sch.core.plan.constraint.ConstraintType;
import eu.gloria.rti.sch.core.plan.constraint.Constraints;
import eu.gloria.tools.log.LogUtil;
import eu.gloria.tools.time.DateTools;

public class WorkerAdvertisementManager  extends Worker  {
	
	
	@Override
	protected void doAction() throws InterruptedException, ClosedByInterruptException, Exception {
		
		String fileBasePath = getPropertyStringValue("xmlPath");
		String xsdFile = getPropertyStringValue("opXSD");
		int days = getPropertyIntValue("days_ahead");
		int sessionMaxOpCount = getPropertyIntValue("session_max_op_count");
		long sessionMaxSharedTime = getPropertyLongValue("session_max_shared_time");
		
		Observer observer = new Observer();
		observer.setLatitude(getPropertyDoubleValue("obs_latitude"));
		observer.setLongitude(getPropertyDoubleValue("obs_longitude"));
		
		long millisecondsMountMove = getPropertyLongValue("predictor_op_exec_time_mount_move_msegs");
		long millisecondsFilterMove = getPropertyLongValue("predictor_op_exec_time_filter_move_msegs"); 
		long millisecondsLooseness = getPropertyLongValue("predictor_op_exec_time_looseness_msegs"); 
		long millisecondsCameraSettings = getPropertyLongValue("predictor_op_exec_time_camera_setting_msegs");
		
		boolean nightTime = getPropertyBooleanValue("observation_night_time");
		
		String timeUserRequestsDeadline = getPropertyStringValue("time_user_requests_deadline");
		
		boolean logs = true;
		eu.gloria.rt.db.scheduler.ObservingPlan op = null;
		ObservingPlanManager opManager = new ObservingPlanManager();
		
		EntityManager em = DBUtil.getEntityManager();
		
		try {
			
			DBUtil.beginTransaction(em);

			op = opManager.getNextAvertToProcess(em);

			if (op != null) {
				
				try{
					
					op.setAdvertDateIni(new Date());
					
					String file = fileBasePath + op.getFile();
					
					if (logs) LogUtil.info(this, "Scheduler::Advertise.BEGIN. fileName=" + file + ", OP UUID=" + op.getUuid());
					
					if (logs) LogUtil.info(this, "Scheduler::Advertise. Parsing. fileName=" + file + ", OP UUID=" + op.getUuid());
					ObservingPlan plan = new ObservingPlan(file, xsdFile);
					
					if (logs) LogUtil.info(this, "Scheduler::Advertise. Verify targets are valid.");
					verifyTargetsExistence(plan, observer);
					
					//Resolves the Scheduling Start Time
					if (logs) LogUtil.info(this, "Scheduler::Advertise. Resolving the Scheduling Start Time");
					Date now = new Date();
					Date tomorrow = DateTools.increment(now, Calendar.DATE, 1);
					
					int daysAheadOverPlanning = days;
					int daysAheadOverAstronomialPlanning = days;
					
					Date schedulingStartDate  = now;
					if (timeUserRequestsDeadline != null){
						
						if (logs) LogUtil.info(this, "Scheduler::Advertise. There is timeUserRequestsDeadline: " + timeUserRequestsDeadline);
						
						daysAheadOverAstronomialPlanning = days + 1; //One more day..
						
						Date deadline = DateTools.getDate(DateTools.getDate(now, "yyyy-MM-dd") + " " + timeUserRequestsDeadline, "yyyy-MM-dd HH:mm:ss");
						RTSInfo rtsSunToday = CatalogueTools.getSunRTSInfo(observer, now);
						RTSInfo rtsSunTomorrow = CatalogueTools.getSunRTSInfo(observer, tomorrow);
						if (nightTime){ //Night time
							if (now.compareTo(deadline) >= 0) { // after deadline -> schedulingStartDate = tomorrow.sunset
								schedulingStartDate = rtsSunTomorrow.getSet();
								
							}else{ //Before deadline -> schedulingStartDate = today.sunset
								schedulingStartDate = rtsSunToday.getSet();
							}
						}else{ //Sun time
							
							if (now.compareTo(deadline) >= 0) { // after deadline -> schedulingStartDate = tomorrow.sunrise
								schedulingStartDate = rtsSunTomorrow.getRise();
							}else{ //Before deadline -> schedulingStartDate = today.sunrise
								schedulingStartDate = rtsSunToday.getRise();
							}
							
						}
					}
					
					if (logs) LogUtil.info(this, "Scheduler::Advertise. schedulingStartDate=" + schedulingStartDate);
					if (logs) LogUtil.info(this, "Scheduler::Advertise. daysAheadOverPlanning=" + daysAheadOverPlanning);
					if (logs) LogUtil.info(this, "Scheduler::Advertise. daysAheadOverAstronomialPlanning=" + daysAheadOverAstronomialPlanning);
					
					PlanningTimeFrameLocator planningTimeFrameLocator = new PlanningTimeFrameLocator(observer, schedulingStartDate, daysAheadOverPlanning, nightTime /*night time*/, true /*verbose*/, sessionMaxOpCount, sessionMaxSharedTime);
					AstronomicalTimeFrameLocator astronomicalTimeFrameLocator = new AstronomicalTimeFrameLocator(observer, daysAheadOverAstronomialPlanning, true);
					
					Constraints constraints = plan.getConstraints();
					ObservingPlanExecTimePredictor predictor = new ObservingPlanExecTimePredictor(millisecondsMountMove, millisecondsFilterMove, millisecondsLooseness, millisecondsCameraSettings);
					long predDuration = plan.getPredictedExecTime(predictor);
					TimeFrameIterator timeFrameIterator =  planningTimeFrameLocator.getAvailableTimeFrameIterator(predDuration);
					
					TimeFrame timeFrame = null;
					TimeFrame astronomyTimeFrame = null;
					boolean accepted = false;
					int countPlanningTimeFrame = 0;
					int countAstronomyRefusals = 0;
					while (timeFrameIterator.hasNext()){
						countPlanningTimeFrame++;
						timeFrame = timeFrameIterator.next();
						if (logs) LogUtil.info(this, "Scheduler::Advertise. TimeFrame:" + timeFrame.toString());
						astronomyTimeFrame = astronomicalTimeFrameLocator.getValidTimeFrame(constraints, timeFrame);
						if (astronomyTimeFrame != null){
							accepted = true;
							break;
						}else{
							countAstronomyRefusals++;
						}
					}
					
					if (accepted){
						if (logs) LogUtil.info(this, "Scheduler::Advertise.ACCEPTED. TimeFrame:" + timeFrame.toString());
						if (logs) LogUtil.info(this, "Scheduler::Advertise.ACCEPTED. AstronomicalTimeFrame:" + astronomyTimeFrame.toString());
						
						op.setOfferDeadline(DateTools.increment(now, Calendar.HOUR, 1)); //OfferDeadLine after 1 hour
						op.setScheduleDateIni(timeFrame.getInit());
						op.setScheduleDateEnd(timeFrame.getEnd());
						op.setExecDeadline(timeFrame.getEnd());
						op.setPredAstr(astronomyTimeFrame.getInit());
						op.setState(ObservingPlanState.ADVERT_ACCEPTED);
						op.setAdvertDateEnd(new Date());
						op.setComment(null);
						op.setPredDuration(predDuration);
						
						op.setPriority(Integer.parseInt(plan.getMetadata().getPriority()));
						op.setUser(plan.getMetadata().getUser());
						op.setUuid(plan.getMetadata().getUuid());
						
						
						//TODO: Communicate to GLORIA -> actually save into table ObservingPlan
						communicateAcceptedObservingPlan(em, op, timeFrame, plan);

					}else{
						
						if (logs) LogUtil.info(this, "Scheduler::Advertise.REJECTED.");
						
						op.setState(ObservingPlanState.ADVERT_REJECTED);
						op.setAdvertDateEnd(new Date());
						String[] names = {
								"CheckedTimeFrames",
								"AstronomyRefusalsTimeFrames"
						};
						String[] values = {
								String.valueOf(countPlanningTimeFrame),
								String.valueOf(countAstronomyRefusals)
						};
						
						op.setComment("No time slot to satisfy the observing plan constraints." + LogUtil.getLog(names, values));
					}
					
					if (logs) LogUtil.info(this, "PROCESSED: " + op.getUuid());
					
				} catch(Exception ex){
					
					op.setState(ObservingPlanState.ERROR);
					op.setAdvertDateEnd(new Date());
					op.setComment(ex.getMessage());
					
				}
				
				if (logs) LogUtil.info(this, "Scheduler::Advertise.END.");
				
				
			}else{
				if (logs) LogUtil.info(this, "PROCESSED: NONE");
			}

			DBUtil.commit(em);
			
			if (logs) LogUtil.info(this, "OK");

		} catch (Exception ex) {
			ex.printStackTrace();
			if (logs) LogUtil.info(this, "EXC:" + ex.getMessage());
			DBUtil.rollback(em);
		} finally {
			if (logs) LogUtil.info(this, "FINAL");
			DBUtil.close(em);
		}
		
	}
	
	private void verifyTargetsExistence(ObservingPlan plan, Observer observer) throws Exception{
		
		if (plan != null){
			Constraints constraints = plan.getConstraints();
			List<Constraint> targets = constraints.getTargets();
			if (targets != null && targets.size() > 0){
				Catalogue catalogue = new Catalogue(observer.getLongitude(), observer.getLatitude(), observer.getAltitude());
				for (Constraint constraint : targets) {
					ConstraintTarget target = (ConstraintTarget) constraint;
					if (target != null && target.getObjName() != null){
						
						ObjInfo obj = catalogue.getObject(target.getObjName());
					
						if (obj == null) throw new Exception("Unknown object name: " + target.getObjName());
					}
				}
			}
		}
		
	}
	
	private void communicateAcceptedObservingPlan(EntityManager em, eu.gloria.rt.db.scheduler.ObservingPlan op, TimeFrame timeFrame, ObservingPlan opFile) throws Exception{
		
		Date now = new Date();
		

		op.setState(ObservingPlanState.QUEUED);
		
		op.setOfferedDate(now);
		op.setEventAdvertReplyDeadline(now);
		op.setEventAdvertReplyDate(now);
		op.setEventAdvertReplyAccepted(1);
		op.setEventOfferConfirmDate(now);
		op.setEventOfferConfirmAccepted(1);
		
	}

}
