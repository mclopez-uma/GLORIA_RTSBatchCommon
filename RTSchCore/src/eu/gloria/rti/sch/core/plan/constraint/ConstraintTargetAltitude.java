package eu.gloria.rti.sch.core.plan.constraint;

/**
 * Constraint - Target altitude.
 * 
 * @author jcabello
 *
 */
public class ConstraintTargetAltitude extends Constraint {
	
	private double altitude;

	/**
	 * Constructor.
	 */
	public ConstraintTargetAltitude() {
		super(ConstraintType.TARGET_ALTITUDE);
	}

	/**
	 * Access method.
	 * @return value.
	 */
	public double getAltitude() {
		return altitude;
	}

	/**
	 * Access method.
	 * @param altitude value.
	 */
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

}
