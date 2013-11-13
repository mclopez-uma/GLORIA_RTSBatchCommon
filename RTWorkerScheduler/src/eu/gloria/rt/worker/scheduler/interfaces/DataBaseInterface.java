package eu.gloria.rt.worker.scheduler.interfaces;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import eu.gloria.rt.db.scheduler.ObservingPlan;
import eu.gloria.rt.db.scheduler.SchTimeFrame;
import eu.gloria.rt.worker.scheduler.iterator.TimeFrame;

public interface DataBaseInterface {
	/**
	 * This method select the observing plan more appropriate to schedule.
	 * 
	 * @return The OP to schedule.
	 */
	public ObservingPlan getNextOpToProcess();

	/**
	 * Reset all observing plan that has state equals from RUNNING to QUEUED.
	 */
	public void resetAllOpsRunningToQueued();

	/**
	 * Reject all OP that his dead line date to schedule has passed and the OP is not scheduled.
	 */
	public void rejectOpsPassedAdvertDeadLine();

	/**
	 * Change the state of one scheduler to RUNNING, this means that the OP are scheduling.
	 * 
	 * @param id
	 *            The identifier of the observing plan.
	 */
	public void setOpScheduling(BigInteger id);

	/**
	 * Set the observing plan as finished state (ACCEPTED, REJECTED, ERROR or ABORTED).
	 * 
	 * @param id
	 *            The identifier of the observing plan.
	 * @param modifies
	 *            It is a map with the values to modify in the database.
	 */
	public void setOpScheduled(BigInteger id, Map<String, Object> modifies);

	/**
	 * Merge a scheduled time frame with a observing plan in the database to execute it in this time.
	 * 
	 * @param uuidOp
	 *            The uuid of the observing plan.
	 * @param iniOp
	 *            Time to initiation the execution.
	 * @param endOp
	 *            Time to finish the execution.
	 */
	public void setUuidOp(String uuidOp, Timestamp iniOp, Timestamp endOp);

	/**
	 * This method set an observing plan to state ERROR in the database.
	 * 
	 * @param id
	 *            The identifier of the observing plan.
	 * @param comment
	 *            The comment of the set state=ERROR.
	 */
	public void setOpError(BigInteger id, String comment);

	/**
	 * This method return the SchtimeFrame that them date end are highest in the database.
	 * 
	 * @return the SchTimerFrame highest.
	 */
	public SchTimeFrame getMaxSlotSchTimeFrame();

	/**
	 * This method delete the Slots previous to now.
	 */
	public void deleteOldsSlots();

	/**
	 * this method save all SchTimeFrame in a list.
	 * 
	 * @param lstTimeFrame
	 *            The list of SchTimeFrame.
	 */
	public void saveSchedulerTimeFrames(List<TimeFrame> lstTimeFrame);

	/**
	 * This method calculated the number of observing plan are scheduling by an user.
	 * 
	 * @param date
	 *            The day to make the count.
	 * @param user
	 *            The user makes the OPs.
	 * 
	 * @return The count of OP in this day.
	 */
	public int getNumOpScheduledUser(Date date, String user);

	/**
	 * This method calculated the number of observing plan are scheduling in a day.
	 * 
	 * @param date
	 *            The day to make the count.
	 * 
	 * @return The count of OP in this day.
	 */
	public int getNumOpScheduled(Date date);

	/**
	 * This method calculated the time (predicted) to execute of the observing plan by an user.
	 * 
	 * @param date
	 *            The day to make the count.
	 * @param user
	 *            The user makes the OPs.
	 * 
	 * @return The time to execute (predicted) in this day.
	 */
	public long getTimeScheduledUser(Date date, String user);

	/**
	 * This method calculated the time (predicted) to execute of the observing plan in a day.
	 * 
	 * @param date
	 *            The day to make the count.
	 * 
	 * @return The time to execute (predicted) in this day.
	 */
	public long getTimeScheduled(Date date);

	/**
	 * Get the Slots free and the Slots with a priority minor to a number.
	 * 
	 * @param priority
	 *            Maximum of priority of observing plan to get.
	 * 
	 * @return The list of SchTimeFrame with this conditions.
	 */
	public List<SchTimeFrame> getSlotsWithPriority(Timestamp init, int priority);

	/**
	 * This method merge a list of SchTimeFrame if the times are united.
	 * 
	 * @param stfs
	 *            The list of SchTimeFrame.
	 * 
	 * @return The list of SchTimeFrame complying this conditions.
	 */
	public List<SchTimeFrame> mergeSchTimeFrames(List<SchTimeFrame> stfs);

	/**
	 * This method search the SchTimeFrame with and OP assigned and they are between in two times.
	 * 
	 * @param ini
	 *            The time in long to initialization.
	 * @param end
	 *            The time in long to end.
	 * 
	 * @return The list of SchTimeFrame complying this conditions.
	 */
	public List<SchTimeFrame> searchSchTimeFramesCrasheds(long ini, long end);

	/**
	 * This method reset all observing plan assigned to a some SchTimeFrames.
	 * 
	 * @param stfs
	 *            The SchTimeFrames with the ObservinPlan assigned.
	 */
	public void resetOps(List<SchTimeFrame> stfs);

	/**
	 * This method reset and merge the SchTimeFrames in a list.
	 * 
	 * @param stfs
	 *            The list of SchTimeFrames.
	 */
	public void resetSchTimeFrames(List<SchTimeFrame> stfs);

	/**
	 * This method reset the observing plan canceled by GLORIA and liberate the SchTimeFrame corresponding.
	 */
	public void updateOpCanceledByGloria();

	/**
	 * This method reject an observing plan.
	 * 
	 * @param op
	 *            the observing plan to reject.
	 */
	public void rejectForOverbooking(ObservingPlan op);

	/**
	 * This method make a list with all SchTimeFrames free in the database.
	 * 
	 * @return The list of SchTimeFrame.
	 */
	public List<SchTimeFrame> getSlotsFree();
}
