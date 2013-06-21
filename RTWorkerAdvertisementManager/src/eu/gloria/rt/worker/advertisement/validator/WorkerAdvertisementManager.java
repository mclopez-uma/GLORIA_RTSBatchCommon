package eu.gloria.rt.worker.advertisement.validator;

import java.nio.channels.ClosedByInterruptException;
import java.util.Date;

import javax.persistence.EntityManager;

import eu.gloria.rt.catalogue.Observer;
import eu.gloria.rt.db.scheduler.Advertisement;
import eu.gloria.rt.db.scheduler.AdvertisementManager;
import eu.gloria.rt.db.scheduler.AdvertisementState;
import eu.gloria.rt.db.scheduler.ObservingPlanManager;
import eu.gloria.rt.db.scheduler.ObservingPlanState;
import eu.gloria.rt.db.util.DBUtil;
import eu.gloria.rt.worker.core.Worker;
import eu.gloria.rti.sch.core.ObservingPlan;
import eu.gloria.rti.sch.core.TimeFrame;
import eu.gloria.rti.sch.core.TimeFrameIterator;
import eu.gloria.rti.sch.core.plan.constraint.Constraints;
import eu.gloria.tools.log.LogUtil;

public class WorkerAdvertisementManager  extends Worker  {
	
	
	@Override
	protected void doAction() throws InterruptedException, ClosedByInterruptException, Exception {
		
		String fileBasePath = getPropertyStringValue("xmlPath");
		String xsdFile = getPropertyStringValue("opXSD");
		int days = getPropertyIntValue("days_ahead");
		int opPerDay = getPropertyIntValue("op_per_day");
		
		Observer observer = new Observer();
		observer.setLatitude(getPropertyDoubleValue("obs_latitude"));
		observer.setLongitude(getPropertyDoubleValue("obs_longitude"));
		
		boolean logs = true;
		Advertisement adv = null;
		AdvertisementManager advManager = new AdvertisementManager();
		
		EntityManager em = DBUtil.getEntityManager();
		
		try {
			
			DBUtil.beginTransaction(em);

			adv = advManager.getNextToProcess(em);

			if (adv != null) {
				
				try{
					
					//Checks if the ObservingPlan already exist.
					ObservingPlanManager opManager = new ObservingPlanManager();
					if (opManager.getByUuid(em, adv.getUuid()) != null){
						throw new Exception("The Observing Plan already exists into the Observing Plan database table (ObservingPlan). UUID=" + adv.getUuid());
					}
					
					PlanningTimeFrameLocator planningTimeFrameLocator = new PlanningTimeFrameLocator(observer, days, true/*night time*/, true /*verbose*/, opPerDay);
					AstronomicalTimeFrameLocator astronomicalTimeFrameLocator = new AstronomicalTimeFrameLocator(observer, days, true);
					
					String file = fileBasePath + adv.getFile();
					ObservingPlan plan = new ObservingPlan(file, xsdFile);
					
					adv.setProcessIni(new Date());
					
					Constraints constraints = plan.getConstraints();
					TimeFrameIterator timeFrameIterator =  planningTimeFrameLocator.getAvailableTimeFrameIterator(plan.getPredictedExecTime());
					
					TimeFrame timeFrame = null;
					TimeFrame astronomyTimeFrame = null;
					if (logs) LogUtil.info(this, "Scheduler::Advertise.BEGIN. fileName=" + file);
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
						
						adv.setPredIntervalIni(timeFrame.getInit());
						adv.setPredIntervalEnd(timeFrame.getEnd());
						adv.setPredAstr(astronomyTimeFrame.getInit());
						adv.setState(AdvertisementState.OFFERED);
						adv.setProcessEnd(new Date());
						adv.setComment(null);
						
						
						//TODO: Communicate to GLORIA -> actually save into table ObservingPlan
						communicateAcceptedObservingPlan(em, adv.getFile(), timeFrame, plan);

					}else{
						if (logs) LogUtil.info(this, "Scheduler::Advertise.REJECTED.");
						
						adv.setState(AdvertisementState.REJECTED);
						adv.setProcessEnd(new Date());
						String[] names = {
								"CheckedTimeFrames",
								"AstronomyRefusalsTimeFrames"
						};
						String[] values = {
								String.valueOf(countPlanningTimeFrame),
								String.valueOf(countAstronomyRefusals)
						};
						
						adv.setComment(LogUtil.getLog(names, values));
					}
					
					if (logs) LogUtil.info(this, "Scheduler::Advertise.END.");
					
					
					if (logs) LogUtil.info(this, "PROCESSED: " + adv.getUuid());
					
				}catch(Exception ex){
					
					adv.setState(AdvertisementState.ERROR);
					adv.setProcessEnd(new Date());
					
				}
				
				
			}else{
				if (logs) LogUtil.info(this, "PROCESSED: NONE");
			}

			DBUtil.commit(em);
			
			if (logs) LogUtil.info(this, "OK");

		} catch (Exception ex) {
			if (logs) LogUtil.info(this, "EXC");
			DBUtil.rollback(em);
		} finally {
			if (logs) LogUtil.info(this, "FINAL");
			DBUtil.close(em);
		}
		
	}
	
	private void communicateAcceptedObservingPlan(EntityManager em, String file, TimeFrame timeFrame, ObservingPlan opFile) throws Exception{
		
		eu.gloria.rt.db.scheduler.ObservingPlan op = new eu.gloria.rt.db.scheduler.ObservingPlan();
		op.setScheduleDateIni(timeFrame.getInit());
		op.setScheduleDateEnd(timeFrame.getEnd());
		op.setPriority(opFile.getMetadata().getPriority());
		op.setFile(file);
		op.setState(ObservingPlanState.QUEUED);
		op.setUser(opFile.getMetadata().getUser());
		op.setUuid(opFile.getMetadata().getUuid());
		
		ObservingPlanManager manager = new ObservingPlanManager();
		manager.create(em, op);
	}

}
