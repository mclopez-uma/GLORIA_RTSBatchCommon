package eu.gloria.rt.worker.scheduler.core;

import java.math.BigInteger;
import java.nio.channels.ClosedByInterruptException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import eu.gloria.rt.catalogue.Catalogue;
import eu.gloria.rt.catalogue.CatalogueTools;
import eu.gloria.rt.catalogue.ObjInfo;
import eu.gloria.rt.catalogue.Observer;
import eu.gloria.rt.catalogue.RTSInfo;
import eu.gloria.rt.db.scheduler.ObservingPlan;
import eu.gloria.rt.db.scheduler.ObservingPlanState;
import eu.gloria.rt.db.scheduler.SchTimeFrame;
import eu.gloria.rt.db.task.TaskProperty;
import eu.gloria.rt.db.util.DBUtil;
import eu.gloria.rt.ephemeris.EphemerisOutOfScopeException;
import eu.gloria.rt.worker.scheduler.constraints.ConstraintTarget;
import eu.gloria.rt.worker.scheduler.constraints.Constraints;
import eu.gloria.rt.worker.scheduler.context.SchedulerContext;
import eu.gloria.rt.worker.scheduler.interfaces.DataBaseInterface;
import eu.gloria.rt.worker.scheduler.iterator.AstronomicalTimeFrameLocator;
import eu.gloria.rt.worker.scheduler.iterator.IAstronomicalTimeFrameLocator;
import eu.gloria.rt.worker.scheduler.iterator.IteratorSunshineNightTime;
import eu.gloria.rt.worker.scheduler.iterator.TimeFrame;
import eu.gloria.rt.worker.scheduler.iterator.TimeFrameIterator;
import eu.gloria.rt.worker.scheduler.times.GeneratorSlots;
import eu.gloria.rt.worker.scheduler.times.SharedTimePortion;
import eu.gloria.rt.worker.scheduler.xmlop.XmlObservingPlan;
import eu.gloria.tools.log.LogUtil;
import eu.gloria.tools.time.DateTools;

public class WorkerScheduler extends eu.gloria.rt.worker.core.Worker {
	
	private SchedulerContext context;
	private DataBaseInterface database;
	
	public WorkerScheduler(){
		
		super();
		
	}
	
	@Override
	public void init(String id, long sleepTime, List<TaskProperty> properties){
		
		super.init(id, sleepTime, properties);
		
		this.context = getContext();
		LogUtil.info(this, "Scheduler::init(): context: " + context.toString());
		this.database = new MassiveStorageDbMysql(context);
	}
	
	@Override
	protected void doAction() throws InterruptedException, ClosedByInterruptException, Exception {
		
		try{
			
			
			// Modify the OPs RUNNING to QUEUED
			database.resetAllOpsRunningToQueued();
			
			// Set ERROR the OPs with exceeded deadline
			database.rejectOpsPassedAdvertDeadLine();
			// Update the OP canceled by GLORIA
			database.updateOpCanceledByGloria();
			
			// Create and start the generator slots
			GeneratorSlots genSlots = new GeneratorSlots(this.context, database);
			genSlots.checkSlots();
			
			// Search one OP to scheduler
			ObservingPlan op = database.getNextOpToProcess();
			// If op!=null, exists an OP to schedule
			if (op != null) {
				
				database.setOpScheduling(BigInteger.valueOf(op.getId()));
				op.setAdvertDateIni(new Timestamp(System.currentTimeMillis()));
				try {
					// Make the necessary object to schedule
					String xmlFile = context.getXmlPath() + op.getFile();
					LogUtil.info(this, "Scheduler::BEGIN. fileName=" + xmlFile + ", OP UUID=" + op.getUuid());
					
					LogUtil.info(this, "Scheduler::Parsing. fileName=" + xmlFile + ", OP UUID=" + op.getUuid());
					Constraints constraints = XmlObservingPlan.getConstraints(context.getXsdFile(), xmlFile);
					
					// Verify if all targets exist in the OP
					LogUtil.info(this, "Scheduler:: Verify targets are valid. fileName=" + xmlFile + ", OP UUID=" + op.getUuid());
					verifyTargetsExistence(constraints.getTargets(), context.getObserver());
					
					LogUtil.info(this, "Scheduler::Resolving the Predicted Exec Time. fileName=" + xmlFile + ", OP UUID=" + op.getUuid());
					long predDuration = XmlObservingPlan.getPredictedExecTime(context.getXsdFile(), xmlFile, context.getPredictionMsecLooseness(), context.getPredictionMsecMountMove(), context.getPredictionMsecFilterMove(), context.getPredictionMsecCameraSettings());
					LogUtil.info(this, "Scheduler::Resolved the Predicted Exec Time=" + predDuration + ". fileName=" + xmlFile + ", OP UUID=" + op.getUuid());
					
					LogUtil.info(this, "Scheduler::Resolving the Scheduling Start Time. fileName=" + xmlFile + ", OP UUID=" + op.getUuid());
					// Make the iterators: astronomical iterator and free time iterator
					IAstronomicalTimeFrameLocator astronomicalTimeFrameLocator = new AstronomicalTimeFrameLocator(context);
					TimeFrameIterator timeFrameIterator = new IteratorSunshineNightTime(context, database, op.getPriority());
					
					// Looking for a slot where the iterators match the OP
					TimeFrame timeFrame = iterateLookingForTheFrame(timeFrameIterator, astronomicalTimeFrameLocator, constraints, predDuration, op.getUser());

					// Finished --> save the advertisement date end
					op.setAdvertDateEnd(new Timestamp(System.currentTimeMillis()));
					// If the response of the seek is null...
					if (timeFrame == null) {
						// ...the OP is rejected
						opRejected(op);
						LogUtil.info(this, "Scheduler::REJECTED. fileName=" + xmlFile + ", OP UUID=" + op.getUuid());
					} else {
						// ...if not, the OP is accepted
						List<SchTimeFrame> stfCrasheds = database.searchSchTimeFramesCrasheds(timeFrame.getInit().getTime(), timeFrame.getEnd().getTime());
						// If the SchTimeFrame are used by other OP with minor priority...
						if (stfCrasheds.size() > 0) {
							// Reset the affected OP 
							database.resetOps(stfCrasheds);
							// Reset the affected SchTimeFrame 
							database.resetSchTimeFrames(stfCrasheds);
						}
						// Save the OP in the database
						opAccepted(op, timeFrame, predDuration);
						
						LogUtil.info(this, "Scheduler::ACCEPTED. TimeFrame:"  + timeFrame.toString() + ". fileName=" + xmlFile + ", OP UUID=" + op.getUuid());
					}
					
					LogUtil.info(this, "Scheduler::PROCESSED: " + op.getUuid());
					
					// Communicate the result to GLORIA
					communicateResultSchedulerToGloria(op);

					LogUtil.fine(this, String.format(context.language.getString("WorkerAdvMngr_Processed_uuid"), op.getUuid()));

				} catch (EphemerisOutOfScopeException ex) {
					
					// This exception is caused by not finding free slots, show it and save in OP with rejected state
					ex.printStackTrace();
					LogUtil.severe(this, String.format(context.language.getString("WorkerAdvMngr_Exception"), "EphemerisOutOfScopeException", ex.getMessage()));
					op.setAdvertDateEnd(new Timestamp(System.currentTimeMillis()));
					opRejected(op);

				} catch (Exception ex) {
					
					// This exception is caused by several reasons, show it and save in OP with error state
					ex.printStackTrace();
					LogUtil.severe(this, String.format(context.language.getString("WorkerAdvMngr_Exception"), ex.getClass().getCanonicalName(), ex.getMessage()));
					op.setAdvertDateEnd(new Timestamp(System.currentTimeMillis()));
					opError(op, ex.getMessage());

				} finally {

				}
				
				LogUtil.info(this, "Scheduler::END. OP UUID=" + op.getUuid());
				
			}else{
				LogUtil.info(this, "Scheduler::PROCESSED: NONE");
			}
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.info(this, "Scheduler::doAction().Error:" + ex.getMessage());
		} finally {
		}
		
	}
	
	private SchedulerContext getContext(){ 
		
		SchedulerContext context = new SchedulerContext(false);
		context.setPredictionMsecCameraSettings(getPropertyLongValue("PredictionMsecCameraSettings", 5000));
		context.setPredictionMsecFilterMove(getPropertyLongValue("PredictionMsecFilterMove", 15000));
		context.setPredictionMsecLooseness(getPropertyLongValue("PredictionMsecLooseness", 10000));
		context.setPredictionMsecMountMove(getPropertyLongValue("PredictionMsecMountMove", 60000));
		
		context.setXmlPath(getPropertyStringValue("XmlPath", "./xml/"));
		context.setXsdFile(getPropertyStringValue("XsdFile", "./gloria_rti_plan.xsd"));
		
		context.setMaxCountOpSession(getPropertyIntValue("MaxCountOpSession", 0));
		context.setMaxCountOpUser(getPropertyIntValue("MaxCountOpUser", 0));
		context.setMaxSharedTimeSession(getPropertyIntValue("MaxSharedTimeSession", 0));
		context.setMaxTimeUser(getPropertyIntValue("MaxShareTimeUser", 0));
		
		context.setDaysFutures(getPropertyIntValue("DaysFutures", 7));
		context.setDaysScheduling(getPropertyIntValue("DaysScheduling", 3));
		
		if (context.getDaysFutures() < 1) {
			context.setDaysFutures(3);
		}
		if (context.getDaysFutures() <= context.getDaysScheduling()) {
			context.setDaysFutures(context.getDaysScheduling() + 1);
		}
		
		context.setNightTelescope(getPropertyBooleanValue("IsNightTelescope", true));
		context.setAdvertAcceptedToQueue(getPropertyBooleanValue("AdvertAcceptedToQueue", false));
		
		context.setSharedTimeFrame(makeShareTime(getPropertyStringValue("SharedTimeFrame", "monday,*;tuesday,*;wednesday,*;thursday,*;friday,*;saturday,*;sunday,*")));
		
		int[] timeLimitToday = null;
		try {
			String timeLimitTodayStr = getPropertyStringValue("TimeLimitToday", "");
			StringTokenizer st = new StringTokenizer(timeLimitTodayStr, ":");
			timeLimitToday = new int[3];
			timeLimitToday[0] = Integer.parseInt(st.nextToken());
			timeLimitToday[1] = Integer.parseInt(st.nextToken());
			timeLimitToday[2] = Integer.parseInt(st.nextToken());
		} catch (Exception e) {
			timeLimitToday = null;
		}
		context.setTimeLimitToday(timeLimitToday);
		
		int[] timeLimitExec = null;
		try {
			String timeLimitExecStr = getPropertyStringValue("TimeLimitExec", "");
			context.setTimeLimitExecString(timeLimitExecStr);
			
			StringTokenizer st = new StringTokenizer(timeLimitExecStr, ":");
			timeLimitExec = new int[3];
			timeLimitExec[0] = Integer.parseInt(st.nextToken());
			timeLimitExec[1] = Integer.parseInt(st.nextToken());
			timeLimitExec[2] = Integer.parseInt(st.nextToken());
		} catch (Exception e) {
			timeLimitExec = null;
		}
		context.setTimeLimitExec(timeLimitExec);
		
		Observer observer = new Observer();
		observer.setAltitude(getPropertyDoubleValue("Altitude", 0));
		observer.setLatitude(getPropertyDoubleValue("Latitude", 0));
		observer.setLongitude(getPropertyDoubleValue("Longitude", 0));
		context.setObserver(observer);
		
		return context;
		
	}
	
	private LinkedList<SharedTimePortion> makeShareTime(String share) {
		LinkedList<SharedTimePortion> sharedTimeFrame = new LinkedList<SharedTimePortion>();
		StringTokenizer st1 = new StringTokenizer(share, ";");
		while (st1.hasMoreTokens()) {
			StringTokenizer st2 = new StringTokenizer(st1.nextToken(), ",");
			String moment = st2.nextToken();
			String ini = st2.nextToken();
			if (st2.hasMoreTokens()) {
				String end = st2.nextToken();
				sharedTimeFrame.add(new SharedTimePortion(moment, ini, end));
			}else{
				sharedTimeFrame.add(new SharedTimePortion(moment));
			}
		}
		return sharedTimeFrame;
	}
	
	/**
	 * Method to verify if the all targets in the OP exists.
	 * 
	 * @param targets
	 *            The list of targets to check.
	 * @param observer
	 *            The position of the telescope.
	 * 
	 * @throws Exception
	 *             If any target don't exists.
	 */
	private void verifyTargetsExistence(List<ConstraintTarget> targets, Observer observer) throws Exception {
		LogUtil.info(this, context.language.getString("WorkerAdvMngr_Verify_targets_existence"));
		if (targets != null && targets.size() > 0) {
			Catalogue catalogue = new Catalogue(observer.getLongitude(), observer.getLatitude(), observer.getAltitude());
			for (ConstraintTarget target : targets) {
				if (target != null && target.getObjName() != null) {
					String objName = target.getObjName();
					LogUtil.info(this, String.format(context.language.getString("WorkerAdvMngr_Verify_target_exists"), objName));
					ObjInfo obj = catalogue.getObject(objName);
					if (obj == null) {
						throw new Exception("Unknown object name: " + objName);
					}
				}
			}
		}
	}
	
	/**
	 * Method to check the use of the telescope: in total time, in total count, in user time and in user count.
	 * 
	 * @param date
	 *            Date to check.
	 * @param predDuration
	 *            Time predicted to execute the OP.
	 * @param user
	 *            User that made the OP.
	 * @return <b>true</b> if the OP satisfied the checks, <b>false</b> in other case.
	 */
	private boolean hasSessionValuesAvailable(Date date, long predDuration, String user) {
		// Check the user count
		if (context.getMaxCountOpUser() > 0) {
			int numOpScheduledUser = database.getNumOpScheduledUser(date, user);
			if (numOpScheduledUser >= context.getMaxCountOpUser()) {
				LogUtil.info(this, String.format(context.language.getString("WorkerAdvMngr_Dont_have_session_available_user_count"), numOpScheduledUser, context.getMaxCountOpUser()));
				return false;
			}
		}

		// Check the user time
		if (context.getMaxShareTimeUser() > 0) {
			long timeOpScheduledUser = database.getTimeScheduledUser(date, user) + predDuration;
			if (timeOpScheduledUser >= context.getMaxShareTimeUser()) {
				LogUtil.info(this, String.format(context.language.getString("WorkerAdvMngr_Dont_have_session_available_user_time"), timeOpScheduledUser, context.getMaxShareTimeUser()));
				return false;
			}
		}

		// Check the session count
		if (context.getMaxCountOpSession() > 0) {
			int numOpScheduled = database.getNumOpScheduled(date);
			if (numOpScheduled >= context.getMaxCountOpSession()) {
				LogUtil.info(this, String.format(context.language.getString("WorkerAdvMngr_Dont_have_session_available_session_count"), numOpScheduled, context.getMaxCountOpSession()));
				return false;
			}
		}

		// Check the session time
		if (context.getMaxSharedTimeSession() > 0) {
			long timeOpScheduled = database.getTimeScheduled(date) + predDuration;
			if (timeOpScheduled >= context.getMaxSharedTimeSession()) {
				LogUtil.info(this, String.format(context.language.getString("WorkerAdvMngr_Dont_have_session_available_session_time"), timeOpScheduled, context.getMaxSharedTimeSession()));
				return false;
			}
		}

		LogUtil.info(this, String.format(context.language.getString("WorkerAdvMngr_Have_session_available"), date));
		return true;
	}
	
	/**
	 * Method to seek the slot to schedule an OP using the free slots iterator and the astronomical iterator.
	 * 
	 * @param timeFrameIter
	 *            The possible slots iterator.
	 * @param astrTfLocator
	 *            The astronomical time iterator.
	 * @param constraints
	 *            The constraints of the observing plan.
	 * @param predDuration
	 *            The execute predicted duration of the observing plan.
	 * @param user
	 *            The name of the user that makes this observing plan.
	 * 
	 * @return <b>null</b> if don't find a slot or the TimeFrame if find a slot.
	 */
	private TimeFrame iterateLookingForTheFrame(TimeFrameIterator timeFrameIter, IAstronomicalTimeFrameLocator astrTfLocator, Constraints constraints, long predDuration, String user) {
		TimeFrame timeFrame = null;
		TimeFrame astrTimeFrame = null;

		while (timeFrameIter.hasNext()) {
			// Iterate over available slots
			timeFrame = timeFrameIter.next();

			// Can be schedule in this date...
			if (hasSessionValuesAvailable(timeFrame.getInit(), predDuration, user)) {
				// Looking for in the fragments of the slot
				LogUtil.fine(this, String.format(context.language.getString("WorkerAdvMngr_Test_time_frame"), timeFrame.toString()));
				astrTimeFrame = astrTfLocator.getValidTimeFrame(constraints, timeFrame);

				// If the result are not null...
				if (astrTimeFrame != null) {
					// ...we have a good time frame
					LogUtil.fine(this, String.format(context.language.getString("WorkerAdvMngr_Acepted_in_time_frame"), timeFrame.toString()));
					LogUtil.fine(this, String.format(context.language.getString("WorkerAdvMngr_Acepted_in_astronomical_time_frame"), astrTimeFrame.toString()));
					return astrTimeFrame;
				}
			}
		}
		return null;
	}
	
	/**
	 * Method to save in the data base that one observing plan are scheduled.
	 * 
	 * @param op
	 *            The observing plan.
	 * @param timeFrame
	 *            The time frame when are scheduled.
	 * @param predDuration
	 *            The execute predicted duration.
	 * @throws ParseException 
	 */
	private void opAccepted(ObservingPlan op, TimeFrame timeFrame, long predDuration) throws ParseException {
		Timestamp predIni = new Timestamp(timeFrame.getInit().getTime());
		Timestamp predEnd = new Timestamp(timeFrame.getInit().getTime() + predDuration);
		LogUtil.info(this, String.format(context.language.getString("WorkerAdvMngr_Accepted"), op.getId(), predIni));

		// Save data in the OP
		if (context.getAdvertAcceptedToQueue()) {
			op.setState(ObservingPlanState.QUEUED);
		}else{
			op.setState(ObservingPlanState.ADVERT_ACCEPTED);
		}
		op.setPredDuration(predDuration);
		op.setScheduleDateIni(predIni);
		op.setScheduleDateEnd(predEnd);
		op.setPredAstr(predIni);

		// Save the execution dead line...
		if (context.getIsNightTelescope()) {
			// ...if the telescope is a night telescope
			op.setExecDeadline(getNightTimeExecDeadLine(predIni));
		} else {
			// ...if the telescope is a sun telescope
			op.setExecDeadline(getSunTimeExecDeadLine(predIni));
		}
		
		if (context.getTimeLimitExecString() != null && !context.getTimeLimitExecString().isEmpty()) {
			//Change the time to the execDeadLine
			Date deadline = DateTools.getDate(DateTools.getDate(op.getExecDeadline(), "yyyy-MM-dd") + " " + context.getTimeLimitExecString(), "yyyy-MM-dd HH:mm:ss");
			op.setExecDeadline(deadline);
		}

		// Save the data in a map
		Date now = new Date();
		Map<String, Object> modifies = new HashMap<String, Object>();
		modifies.put("scheduleDateIni", op.getScheduleDateIni());
		modifies.put("scheduleDateEnd", op.getScheduleDateEnd());
		modifies.put("advertOffshoreDeadline", now);
		modifies.put("advertDateIni", op.getAdvertDateIni());
		modifies.put("advertDateEnd", op.getAdvertDateEnd());
		modifies.put("execDeadline", op.getExecDeadline());
		modifies.put("predDuration", predDuration);
		modifies.put("predAstr", predIni);
		modifies.put("state", op.getState());

		// Save in the table ObservingPlan in the database
		database.setOpScheduled(BigInteger.valueOf(op.getId()), modifies);
		// Save in the table SchtimeFrame in the database
		database.setUuidOp(op.getUuid(), predIni, predEnd);
	}
	
	/**
	 * Method to save in the data base that one observing plan are rejected.
	 * 
	 * @param op
	 *            The observing plan rejected.
	 */
	private void opRejected(ObservingPlan op) {
		LogUtil.info(this, String.format(context.language.getString("WorkerAdvMngr_Rejected"), op.getId()));

		// Save data in the OP
		op.setState(ObservingPlanState.ADVERT_REJECTED);
		op.setComment(context.language.getString("WorkerAdvMngr_Rejected_comment_db"));

		// Save the data in a map
		Map<String, Object> modifies = new HashMap<String, Object>();
		modifies.put("advertDateIni", op.getAdvertDateIni());
		modifies.put("advertDateEnd", op.getAdvertDateEnd());
		modifies.put("comment", op.getComment());
		modifies.put("state", op.getState());

		// Save in the table ObservingPlan in the database
		database.setOpScheduled(BigInteger.valueOf(op.getId()), modifies);
	}
	
	/**
	 * Method to save in the data base that one observing plan are errored.
	 * 
	 * @param op
	 *            The observing plan errored.
	 * @param comment
	 *            The reason of the error.
	 */
	private void opError(ObservingPlan op, String comment) {
		LogUtil.info(this, String.format(context.language.getString("WorkerAdvMngr_Error"), op.getId()));

		// Save data in the OP
		op.setState(ObservingPlanState.ADVERT_ERROR);
		op.setComment(comment);

		// Save the data in a map
		Map<String, Object> modifies = new HashMap<String, Object>();
		modifies.put("advertDateIni", op.getAdvertDateIni());
		modifies.put("advertDateEnd", op.getAdvertDateEnd());
		modifies.put("comment", op.getComment());
		modifies.put("state", op.getState());

		// Save in the table ObservingPlan in the database
		database.setOpScheduled(BigInteger.valueOf(op.getId()), modifies);
	}
	
	/**
	 * Method to communicate to GLORIA "this observing plan are scheduled". TODO This method is pending to make.
	 * 
	 * @param op
	 *            The observing plan scheduled.
	 * 
	 * @throws Exception
	 *             In error case..
	 */
	private void communicateResultSchedulerToGloria(ObservingPlan op) throws Exception {
		// /////////////////////////////////////////////////////////////////////////////////////////////////
		// TODO: Communicate to GLORIA -> actually ONLY save into table ObservingPlan [method accepted()] //
		// /////////////////////////////////////////////////////////////////////////////////////////////////
	}
	
	
	/**
	 * Method to calculate the execute dead line by configuration.
	 * 
	 * @param timeExec
	 *            The time predicted to execute the observing plan.
	 * 
	 * @return The Timestamp to execute dead line.
	 */
	private Timestamp getTimeExecByString(Timestamp timeExec) {
		try {
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTimeInMillis(timeExec.getTime());
			gc.set(Calendar.HOUR_OF_DAY, context.getTimeLimitExec()[0]);
			gc.set(Calendar.MINUTE, context.getTimeLimitExec()[1]);
			gc.set(Calendar.SECOND, context.getTimeLimitExec()[2]);

			if (timeExec.getTime() > gc.getTimeInMillis()) {
				gc.add(Calendar.DAY_OF_YEAR, 1);
			}
			return new Timestamp(gc.getTimeInMillis());

		} catch (Exception ex) {
			LogUtil.severe(this, String.format(context.language.getString("WorkerAdvMngr_Error_exec_dead_line"), "getTimeExecByString", ex.getClass().getCanonicalName(), ex.getMessage()));
			return null;
		}
	}
	
	/**
	 * Method to calculate the execute dead line for a night telescope.
	 * 
	 * @param timeExec
	 *            The time predicted to execute the observing plan.
	 * 
	 * @return The Timestamp to execute dead line.
	 */
	private Timestamp getNightTimeExecDeadLine(Timestamp timeExec) {
		try {
			Date execDate = new Date(timeExec.getTime());
			RTSInfo sunRTS = CatalogueTools.getSunRTSInfo(context.getObserver(), execDate);

			if (execDate.compareTo(sunRTS.getSet()) >= 0) {
				// After sun set -> good time
				execDate = DateTools.trunk(execDate, "yyyyMMdd");
				Date tomorrow = DateTools.increment(execDate, Calendar.DATE, 1);
				sunRTS = CatalogueTools.getSunRTSInfo(context.getObserver(), tomorrow);
			}
			// In other case, it is before sun rise -> good time
			return new Timestamp(sunRTS.getRise().getTime());

		} catch (Exception ex) {
			LogUtil.severe(this, String.format(context.language.getString("WorkerAdvMngr_Error_exec_dead_line"), "getNightTimeExecDeadLine", ex.getClass().getCanonicalName(), ex.getMessage()));
			return null;
		}
	}
	
	/**
	 * Method to calculate the execute dead line for a sun telescope.
	 * 
	 * @param timeExec
	 *            The time predicted to execute the observing plan.
	 * 
	 * @return The Timestamp to execute dead line.
	 */
	private Timestamp getSunTimeExecDeadLine(Timestamp timeExec) {
		Date execDate = new Date(timeExec.getTime());
		try {
			RTSInfo sunRTS = CatalogueTools.getSunRTSInfo(context.getObserver(), execDate);
			return new Timestamp(sunRTS.getSet().getTime());
		} catch (Exception ex) {
			LogUtil.severe(this, String.format(context.language.getString("WorkerAdvMngr_Error_exec_dead_line"), "getSunTimeExecDeadLine", ex.getClass().getCanonicalName(), ex.getMessage()));
			return null;
		}
	}
	


}
