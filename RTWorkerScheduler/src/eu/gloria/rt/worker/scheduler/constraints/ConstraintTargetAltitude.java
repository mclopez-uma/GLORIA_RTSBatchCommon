package eu.gloria.rt.worker.scheduler.constraints;

/**
 * Class to check a specified constraint: Target altitude.
 * 
 * @author Alfredo
 */
public class ConstraintTargetAltitude extends Constraint {
	private double altitude;

	/**
	 * Default constructor.
	 */
	public ConstraintTargetAltitude() {
		super(ConstraintType.TARGET_ALTITUDE);
	}

	/**
	 * Getter method to target altitude.
	 * 
	 * @return The altitude.
	 */
	public double getAltitude() {
		return altitude;
	}

	/**
	 * Setter method to altitude.
	 * 
	 * @param altitude The new altitude.
	 */
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	
	@Override
	public String toString() {
		return "ConstraintTargetAltitude: altitude=" + altitude;
	}
}
