package eu.gloria.rt.worker.scheduler.constraints;

/**
 * Class to check a specified constraint: Moon Altitude.
 * 
 * @author Alfredo
 */
public class ConstraintMoonAltitude extends Constraint {
	public double altitude;

	/**
	 * Default constructor.
	 */
	public ConstraintMoonAltitude() {
		super(ConstraintType.MOON_ALTITUDE);
	}

	/**
	 * Getter method to moon altitude.
	 * 
	 * @return The altitude.
	 */
	public double getAltitude() {
		return altitude;
	}

	/**
	 * Setter method to moon altitude.
	 * 
	 * @param altitude The new moon altitude.
	 */
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	
	@Override
	public String toString() {
		return "ConstraintMoonAltitude: altitude=" + altitude;
	}
}
