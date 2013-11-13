package eu.gloria.rt.worker.scheduler.core;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import eu.gloria.rt.catalogue.Catalogue;
import eu.gloria.rt.catalogue.CatalogueTools;
import eu.gloria.rt.catalogue.ObjInfo;
import eu.gloria.rt.catalogue.Observer;
import eu.gloria.rt.catalogue.RTSInfo;
import eu.gloria.rt.db.scheduler.ObservingPlan;
import eu.gloria.rt.db.scheduler.ObservingPlanState;
import eu.gloria.rt.db.scheduler.SchTimeFrame;
import eu.gloria.rt.ephemeris.EphemerisOutOfScopeException;
import eu.gloria.rt.worker.scheduler.constraints.ConstraintTarget;
import eu.gloria.rt.worker.scheduler.constraints.Constraints;
import eu.gloria.rt.worker.scheduler.context.SchedulerContext;
import eu.gloria.rt.worker.scheduler.context.SchedulerLog;
import eu.gloria.rt.worker.scheduler.interfaces.ConfigUpgradeable;
import eu.gloria.rt.worker.scheduler.interfaces.DataBaseInterface;
import eu.gloria.rt.worker.scheduler.iterator.AstronomicalTimeFrameLocator;
import eu.gloria.rt.worker.scheduler.iterator.IAstronomicalTimeFrameLocator;
import eu.gloria.rt.worker.scheduler.iterator.IteratorSunshineNightTime;
import eu.gloria.rt.worker.scheduler.iterator.TimeFrame;
import eu.gloria.rt.worker.scheduler.iterator.TimeFrameIterator;
import eu.gloria.rt.worker.scheduler.xmlop.XmlObservingPlan;
import eu.gloria.tools.log.LogUtil;
import eu.gloria.tools.time.DateTools;

/**
 * @author Alfredo
 * 
 *         Class that extends from Worker and can schedule an Observing Plan.
 */
public class WorkerAdvertisementManager extends Worker implements ConfigUpgradeable {
	private DataBaseInterface database;
	private SchedulerLog log;
	private int sessionMaxCountOpUser, sessionMaxTimeOpUser, sessionMaxOpCount, sessionMaxSharedTime;
	private long msecsMountMove, msecsFilterMove, msecsLooseness, msecsCameraSettings;
	private boolean isNightTelescope, advertAcceptedToQueue;
	private SortedSet<ObservingPlan> queueOp;
	private int maxInQueued, limitTries;
	private String xmlPath, xsdFile;
	private int[] timeLimitExec;
	private Observer observer;

	/**
	 * Constructor.
	 * 
	 * @param sc
	 *            The context of the scheduler.
	 * @param db
	 *            Object to access to the massive storage (data base).
	 * @param id
	 *            The identifier of the worker.
	 */
	public WorkerAdvertisementManager(SchedulerContext sc, DataBaseInterface db, String id) {
		super(sc, id);
		database = db;
		log = sc.logger(getClass());
		queueOp = new TreeSet<ObservingPlan>();
		sc.addConfigUpgradeable(this);
		updateConfig();
	}

	@Override
	protected void doAction() {
		int triesOp = 0;
		while (!queueOp.isEmpty()) {
			ObservingPlan op = queueOp.first();
			database.setOpScheduling(BigInteger.valueOf(op.getId()));
			op.setAdvertDateIni(new Timestamp(System.currentTimeMillis()));
			try {
				// Make the necessary object to schedule
				String xmlFile = xmlPath + op.getFile();
				Constraints constraints = XmlObservingPlan.getConstraints(xsdFile, xmlFile);
				// Verify if the all targets in the OP exists
				verifyTargetsExistence(constraints.getTargets(), observer);
				long predDuration = XmlObservingPlan.getPredictedExecTime(xsdFile, xmlFile, msecsLooseness, msecsMountMove, msecsFilterMove, msecsCameraSettings);

				// Make the iterators: astronomical iterator and free time iterator
				IAstronomicalTimeFrameLocator astronomicalTimeFrameLocator = new AstronomicalTimeFrameLocator(schContext);
				TimeFrameIterator timeFrameIterator = new IteratorSunshineNightTime(schContext, database, op.getPriority());
				// Looking for a slot where the iterators match the OP
				TimeFrame timeFrame = iterateLookingForTheFrame(timeFrameIterator, astronomicalTimeFrameLocator, constraints, predDuration, op.getUser());

				// Finished, save the advertisement date end
				op.setAdvertDateEnd(new Timestamp(System.currentTimeMillis()));
				// If the response of the seek is null...
				if (timeFrame == null) {
					// ...the OP is rejected
					opRejected(op);
				} else {
					// ...if not, the OP is accepted
					List<SchTimeFrame> stfCrasheds = database.searchSchTimeFramesCrasheds(timeFrame.getInit().getTime(), timeFrame.getEnd().getTime());
					// If the SchTimeFrame are used by other OP with minor priority...
					if (stfCrasheds.size() > 0) {
						// Reset the ObservingPlan affected
						database.resetOps(stfCrasheds);
						// Reset the SchTimeFrame affected
						database.resetSchTimeFrames(stfCrasheds);
					}
					// Save the OP in the database
					opAccepted(op, timeFrame, predDuration);
				}
				// Communicate the result to GLORIA
				communicateResultSchedulerToGloria(op);

				// After accept of reject, remove the OP from the queue
				queueOp.remove(op);
				triesOp = 0;
				log.debug(String.format(schContext.language.getString("WorkerAdvMngr_Processed_uuid"), op.getUuid()));

			} catch (EphemerisOutOfScopeException ex) {
				// This exception is caused by not finding free slots, show it and save in OP with rejected state
				ex.printStackTrace();
				log.error(String.format(schContext.language.getString("WorkerAdvMngr_Exception"), "EphemerisOutOfScopeException", ex.getMessage()));
				op.setAdvertDateEnd(new Timestamp(System.currentTimeMillis()));
				opRejected(op);
				triesOp++;

			} catch (Exception ex) {
				// This exception is caused by varied reasons, show it and save in OP with error state
				ex.printStackTrace();
				log.error(String.format(schContext.language.getString("WorkerAdvMngr_Exception"), ex.getClass().getCanonicalName(), ex.getMessage()));
				op.setAdvertDateEnd(new Timestamp(System.currentTimeMillis()));
				opError(op, ex.getMessage());
				triesOp++;

			} finally {
				if (triesOp >= limitTries) {
					// massiveStorage.setOpError(op.getId(), schContext.language.getString("WorkerAdvMngr_Many_scheduling_errors"));
					queueOp.remove(op);
					triesOp = 0;
				}
			}
		}
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
		log.info(schContext.language.getString("WorkerAdvMngr_Verify_targets_existence"));
		if (targets != null && targets.size() > 0) {
			Catalogue catalogue = new Catalogue(observer.getLongitude(), observer.getLatitude(), observer.getAltitude());
			for (ConstraintTarget target : targets) {
				if (target != null && target.getObjName() != null) {
					String objName = target.getObjName();
					log.info(String.format(schContext.language.getString("WorkerAdvMngr_Verify_target_exists"), objName));
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
		if (sessionMaxCountOpUser > 0) {
			int numOpScheduledUser = database.getNumOpScheduledUser(date, user);
			if (numOpScheduledUser >= sessionMaxCountOpUser) {
				log.info(String.format(schContext.language.getString("WorkerAdvMngr_Dont_have_session_available_user_count"), numOpScheduledUser, sessionMaxCountOpUser));
				return false;
			}
		}

		// Check the user time
		if (sessionMaxTimeOpUser > 0) {
			long timeOpScheduledUser = database.getTimeScheduledUser(date, user) + predDuration;
			if (timeOpScheduledUser >= sessionMaxTimeOpUser) {
				log.info(String.format(schContext.language.getString("WorkerAdvMngr_Dont_have_session_available_user_time"), timeOpScheduledUser, sessionMaxTimeOpUser));
				return false;
			}
		}

		// Check the session count
		if (sessionMaxOpCount > 0) {
			int numOpScheduled = database.getNumOpScheduled(date);
			if (numOpScheduled >= sessionMaxOpCount) {
				log.info(String.format(schContext.language.getString("WorkerAdvMngr_Dont_have_session_available_session_count"), numOpScheduled, sessionMaxOpCount));
				return false;
			}
		}

		// Check the session time
		if (sessionMaxSharedTime > 0) {
			long timeOpScheduled = database.getTimeScheduled(date) + predDuration;
			if (timeOpScheduled >= sessionMaxSharedTime) {
				log.info(String.format(schContext.language.getString("WorkerAdvMngr_Dont_have_session_available_session_time"), timeOpScheduled, sessionMaxSharedTime));
				return false;
			}
		}

		log.info(String.format(schContext.language.getString("WorkerAdvMngr_Have_session_available"), date));
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
				log.debug(String.format(schContext.language.getString("WorkerAdvMngr_Test_time_frame"), timeFrame.toString()));
				astrTimeFrame = astrTfLocator.getValidTimeFrame(constraints, timeFrame);

				// If the result are not null...
				if (astrTimeFrame != null) {
					// ...we have a good time frame
					log.debug(String.format(schContext.language.getString("WorkerAdvMngr_Acepted_in_time_frame"), timeFrame.toString()));
					log.debug(String.format(schContext.language.getString("WorkerAdvMngr_Acepted_in_astronomical_time_frame"), astrTimeFrame.toString()));
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
		log.info(String.format(schContext.language.getString("WorkerAdvMngr_Accepted"), op.getId(), predIni));

		// Save data in the OP
		if (advertAcceptedToQueue) {
			op.setState(ObservingPlanState.QUEUED);
		}else{
			op.setState(ObservingPlanState.ADVERT_ACCEPTED);
		}
		op.setPredDuration(predDuration);
		op.setScheduleDateIni(predIni);
		op.setScheduleDateEnd(predEnd);
		op.setPredAstr(predIni);

		// Save the execution dead line...
		if (schContext.getIsNightTelescope()) {
			// ...if the telescope is a night telescope
			op.setExecDeadline(getNightTimeExecDeadLine(predIni));
		} else {
			// ...if the telescope is a sun telescope
			op.setExecDeadline(getSunTimeExecDeadLine(predIni));
		}
		
		if (schContext.getTimeLimitExecString() != null && !schContext.getTimeLimitExecString().isEmpty()) {
			//Change the time to the execDeadLine
			Date deadline = DateTools.getDate(DateTools.getDate(op.getExecDeadline(), "yyyy-MM-dd") + " " + schContext.getTimeLimitExecString(), "yyyy-MM-dd HH:mm:ss");
			op.setExecDeadline(deadline);
		}

		// Save the data in a map
		Map<String, Object> modifies = new HashMap<String, Object>();
		modifies.put("scheduleDateIni", op.getScheduleDateIni());
		modifies.put("scheduleDateEnd", op.getScheduleDateEnd());
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
		log.info(String.format(schContext.language.getString("WorkerAdvMngr_Rejected"), op.getId()));

		// Save data in the OP
		op.setState(ObservingPlanState.ADVERT_REJECTED);
		op.setComment(schContext.language.getString("WorkerAdvMngr_Rejected_comment_db"));

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
		log.info(String.format(schContext.language.getString("WorkerAdvMngr_Error"), op.getId()));

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
	 * Method to send an observing plan to schedule by the worker.
	 * 
	 * @param op
	 *            An observing plan to schedule.
	 * @return <b>true</b> if the observing plan has accepted, <b>false</b> in other case.
	 */
	public boolean addObservingPlan(ObservingPlan op) {
		boolean added = false;
		if (queueOp.size() < maxInQueued && !queueOp.contains(op)) {
			added = queueOp.add(op);
		}
		return added;
	}

	@Override
	public void updateConfig() {
		limitTries = 1; // Maximums tries to scheduler an observing plan

		maxInQueued = schContext.getMaxInQueued();

		xmlPath = schContext.getXmlPath();
		xsdFile = schContext.getXsdFile();
		sessionMaxOpCount = schContext.getMaxCountOpSession();
		sessionMaxSharedTime = schContext.getMaxSharedTimeSession();
		sessionMaxCountOpUser = schContext.getMaxCountOpUser();
		sessionMaxTimeOpUser = schContext.getMaxShareTimeUser();

		msecsMountMove = schContext.getPredictionMsecMountMove();
		msecsFilterMove = schContext.getPredictionMsecFilterMove();
		msecsLooseness = schContext.getPredictionMsecLooseness();
		msecsCameraSettings = schContext.getPredictionMsecCameraSettings();

		observer = schContext.getObserver();
		isNightTelescope = schContext.getIsNightTelescope();

		timeLimitExec = schContext.getTimeLimitExec();
		
		advertAcceptedToQueue = schContext.getAdvertAcceptedToQueue();
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
			gc.set(Calendar.HOUR_OF_DAY, timeLimitExec[0]);
			gc.set(Calendar.MINUTE, timeLimitExec[1]);
			gc.set(Calendar.SECOND, timeLimitExec[2]);

			if (timeExec.getTime() > gc.getTimeInMillis()) {
				gc.add(Calendar.DAY_OF_YEAR, 1);
			}
			return new Timestamp(gc.getTimeInMillis());

		} catch (Exception ex) {
			log.error(String.format(schContext.language.getString("WorkerAdvMngr_Error_exec_dead_line"), "getTimeExecByString", ex.getClass().getCanonicalName(), ex.getMessage()));
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
			RTSInfo sunRTS = CatalogueTools.getSunRTSInfo(observer, execDate);

			if (execDate.compareTo(sunRTS.getSet()) >= 0) {
				// After sun set -> good time
				execDate = DateTools.trunk(execDate, "yyyyMMdd");
				Date tomorrow = DateTools.increment(execDate, Calendar.DATE, 1);
				sunRTS = CatalogueTools.getSunRTSInfo(observer, tomorrow);
			}
			// In other case, it is before sun rise -> good time
			return new Timestamp(sunRTS.getRise().getTime());

		} catch (Exception ex) {
			log.error(String.format(schContext.language.getString("WorkerAdvMngr_Error_exec_dead_line"), "getNightTimeExecDeadLine", ex.getClass().getCanonicalName(), ex.getMessage()));
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
			RTSInfo sunRTS = CatalogueTools.getSunRTSInfo(observer, execDate);
			return new Timestamp(sunRTS.getSet().getTime());
		} catch (Exception ex) {
			log.error(String.format(schContext.language.getString("WorkerAdvMngr_Error_exec_dead_line"), "getSunTimeExecDeadLine", ex.getClass().getCanonicalName(), ex.getMessage()));
			return null;
		}
	}
}
