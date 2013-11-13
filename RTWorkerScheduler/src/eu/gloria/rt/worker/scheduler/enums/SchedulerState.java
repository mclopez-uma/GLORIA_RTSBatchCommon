package eu.gloria.rt.worker.scheduler.enums;

/**
 * This class have the different state that the scheduler can be.
 * 
 * @author Alfredo
 */
public enum SchedulerState {
	/**
	 * The scheduler are initializing his attributes.
	 */
	INITIALIZING,		// 0

	/**
	 * The scheduler are ready to scheduler.
	 */
	RUNNING,			// 1

	/**
	 * The scheduler are waiting to star the scheduler.
	 */
	PAUSED,				// 2

	/**
	 * The scheduler received a finish request.
	 */
	FINISHING,			// 3

	/**
	 * The scheduler are finished.
	 */
	FINISHED,			// 4

}
