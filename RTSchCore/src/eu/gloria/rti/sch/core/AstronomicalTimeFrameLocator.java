package eu.gloria.rti.sch.core;

import eu.gloria.rt.exception.RTException;
import eu.gloria.rti.sch.core.plan.constraint.Constraints;

/**
 * Interface of the Astronomical Time Frame locator component.
 * 
 * @author jcabello
 *
 */
public interface AstronomicalTimeFrameLocator {
	
	/**
	 * Return the time frame where the astronomical constraints are satisfied.
	 * @param constraints
	 * @param timeFrame Original time frame (from Planning slots list)
	 * @return null=>unsatisfied, subTimeframe=>The original Time frame is decreased, OriginalTimeFrame=>maintains the original time frame.
	 * @throws RTSchException In error case.
	 */
	public TimeFrame getValidTimeFrame(Constraints constraints, TimeFrame timeFrame)  throws RTException; 
	
}
