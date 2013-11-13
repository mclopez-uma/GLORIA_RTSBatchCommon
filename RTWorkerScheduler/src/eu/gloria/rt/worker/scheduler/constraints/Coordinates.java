package eu.gloria.rt.worker.scheduler.constraints;

/**
 * @author Alfredo
 * 
 *         Coordinates wrapper.
 */
public class Coordinates {
	private J2000 j2000;

	/**
	 * Getter method to access to J2000.
	 * 
	 * @return coordinates in j2000 format.
	 */
	public J2000 getJ2000() {
		return j2000;
	}

	/**
	 * Setter method to access to J2000.
	 * 
	 * @param j2000
	 *            The new coordinates.
	 */
	public void setJ2000(J2000 j2000) {
		this.j2000 = j2000;
	}
}
