package eu.gloria.rti.sch.core.plan.constraint;

/**
 * Coordinates J2000
 * 
 * @author jcabello
 *
 */
public class J2000 {
	
	private double ra;
	private double dec;
	
	/**
	 * Access method.
	 * @return value.
	 */
	public double getRa() {
		return ra;
	}
	
	/**
	 * Access method.
	 * @param ra value
	 */
	public void setRa(double ra) {
		this.ra = ra;
	}
	
	/**
	 * Access method.
	 * @return value.
	 */
	public double getDec() {
		return dec;
	}
	
	/**
	 * Access method.
	 * @param dec value.
	 */
	public void setDec(double dec) {
		this.dec = dec;
	}

}
