package eu.gloria.rt.worker.scheduler.constraints;

import eu.gloria.rt.worker.scheduler.iterator.TimeFrame;

/**
 * @author Alfredo
 * 
 *         Interface to define the method to check astronomic constraints.
 */
public interface ConstraintValidator {
	/**
	 * Method to check a generic constraint in a specified time frame.
	 * 
	 * @param constraints
	 *            The data constraint to check.
	 * @param timeFrame
	 *            The time to check.
	 * 
	 * @return <b>true</b> if the constraint is OK in this time frame, <b>false</b> in other case (including error cases).
	 */
	public boolean isSatisfied(Constraints constraints, TimeFrame timeFrame);
}