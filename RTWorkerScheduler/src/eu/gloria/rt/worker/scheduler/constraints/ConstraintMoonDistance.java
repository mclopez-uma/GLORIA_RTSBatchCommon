package eu.gloria.rt.worker.scheduler.constraints;

/**
 * Class to check a specified constraint: Moon Distance.
 * 
 * @author Alfredo
 */
public class ConstraintMoonDistance extends Constraint {
	private double distance;

	/**
	 * Default constructor.
	 */
	public ConstraintMoonDistance() {
		super(ConstraintType.MOON_DISTANCE);
	}

	/**
	 * Getter method to moon distance.
	 * 
	 * @return The altitude.
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * Setter method to moon distance.
	 * 
	 * @param distance The new moon distance.
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	@Override
	public String toString() {
		return "ConstraintMoonDistance: distance=" + distance;
	}
}
