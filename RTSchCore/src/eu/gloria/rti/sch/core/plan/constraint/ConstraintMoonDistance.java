package eu.gloria.rti.sch.core.plan.constraint;

/**
 * Constraint - Moon Distance.
 * 
 * @author jcabello
 *
 */
public class ConstraintMoonDistance extends Constraint {
	
	private double distance;

	/**
	 * Constructor.
	 */
	public ConstraintMoonDistance() {
		super(ConstraintType.MOON_DISTANCE);
	}

	/**
	 * Access method.
	 * @return value.
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * Access method.
	 * @param distance value.
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}

}
