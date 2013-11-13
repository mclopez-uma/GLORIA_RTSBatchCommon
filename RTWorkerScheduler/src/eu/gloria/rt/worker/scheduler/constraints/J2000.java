package eu.gloria.rt.worker.scheduler.constraints;

/**
 * @author Alfredo
 * 
 *         Coordinates J2000.
 */
public class J2000 {
	private double ra;
	private double dec;

	/**
	 * Default constructor.
	 */
	public J2000() {
	}

	/**
	 * Constructor with specify data.
	 * 
	 * @param ra
	 *            Right ascension.
	 * @param dec
	 *            Declination.
	 */
	public J2000(double ra, double dec) {
		this.dec = dec;
		this.ra = ra;
	}

	/**
	 * Getter method to access to RA.
	 * 
	 * @return right ascension.
	 */
	public double getRa() {
		return ra;
	}

	/**
	 * Setter method to access to RA.
	 * 
	 * @param ra
	 *            The new value.
	 */
	public void setRa(double ra) {
		this.ra = ra;
	}

	/**
	 * Getter method to access to DEC.
	 * 
	 * @return declination.
	 */
	public double getDec() {
		return dec;
	}

	/**
	 * Getter method to access to RA.
	 * 
	 * @param dec
	 *            The new value.
	 */
	public void setDec(double dec) {
		this.dec = dec;
	}
}
