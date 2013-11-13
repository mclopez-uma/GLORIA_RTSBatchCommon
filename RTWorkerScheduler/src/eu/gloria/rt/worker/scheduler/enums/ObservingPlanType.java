package eu.gloria.rt.worker.scheduler.enums;

/**
 * Enumeration to specify the type of observing plan.
 * 
 * @author Alfredo
 */
public enum ObservingPlanType {
	/**
	 * A dark observation.
	 */
	DARK,
	
	/**
	 * A flat observation.
	 */
    FLAT,
 
	/**
	 * A normal observation.
	 */
    OBSERVATION,
    
	/**
	 * A bias observation.
	 */
    BIAS,
}
