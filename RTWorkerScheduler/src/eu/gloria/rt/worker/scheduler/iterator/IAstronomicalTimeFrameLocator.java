package eu.gloria.rt.worker.scheduler.iterator;

import eu.gloria.rt.worker.scheduler.constraints.Constraints;

/**
 * @author Alfredo
 * 
 *         Interface to check the constraint of the observing plan in a specified time.
 */
public interface IAstronomicalTimeFrameLocator {
	/**
	 * This method return the time frame where the astronomical constraints are satisfied.
	 * 
	 * @param constraints
	 *            The constraints of the observing plan.
	 * @param timeFrame
	 *            The great time frame from planning slots to check.
	 * 
	 * @return <b>null</b> if the constraints are unsatisfied, <b>Timeframe</b> The specify time frame when the constraints are satisfied.
	 */
	public TimeFrame getValidTimeFrame(Constraints constraints, TimeFrame timeFrame);
}
