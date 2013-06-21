package eu.gloria.rti.sch.core.plan.constraint;

/**
 * Constraint - Moon Altitude.
 * 
 * @author jcabello
 *
 */
public class ConstraintMoonAltitude extends Constraint {
	
	public double altitude;

	/**
	 * Constructor.
	 */
	public ConstraintMoonAltitude() {
		super(ConstraintType.MOON_ALTITUDE);
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
