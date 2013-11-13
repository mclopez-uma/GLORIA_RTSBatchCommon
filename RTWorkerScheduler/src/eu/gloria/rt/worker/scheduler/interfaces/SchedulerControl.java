package eu.gloria.rt.worker.scheduler.interfaces;

/**
 * Communication interface with the scheduler.
 * 
 * @author Alfredo
 */
public interface SchedulerControl {
	
	/**
	 * Method to reload the new scheduler configuration, all changes replace the old values.
	 */
	public void eventConfigScheduler();
	
	/**
	 * Method to pause the scheduling. If the scheduler are scheduling a observing plan, it will finish this OP.
	 */
	public void eventPauseScheduler();

	/**
	 * Method to resume the scheduling after a pause order.
	 */
	public void eventContinueScheduler();

	/**
	 * Method to finish the scheduling. If the scheduler are scheduling a observing plan, it will finish this OP.
	 */
	public void eventFinishScheduler();
}
