package eu.gloria.rti.sch.core;

import eu.gloria.rt.exception.RTException;

/**
 * Interface of the planning time frame locator component.
 * 
 * @author jcabello
 *
 */
public interface PlanningTimeFrameLocator {
	
	public TimeFrameIterator getAvailableTimeFrameIterator(long seconds) throws RTException;
	
	public boolean isAvailableTimeFrame (TimeFrame timeFrame) throws RTException;
	

}
