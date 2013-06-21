package eu.gloria.rti.sch.core;

import java.util.Date;

/**
 * Time frame class.
 * 
 * @author jcabello
 *
 */
public class TimeFrame {
	
	private Date init;
	private Date end;
	
	/**
	 * Access method
	 * @return value.
	 */
	public Date getInit() {
		return init;
	}
	
	/**
	 * Access method.
	 * @param init Value.
	 */
	public void setInit(Date init) {
		this.init = init;
	}
	
	/**
	 * Access method.
	 * @return value.
	 */
	public Date getEnd() {
		return end;
	}
	
	/**
	 * Access method.
	 * @param end Value.
	 */
	public void setEnd(Date end) {
		this.end = end;
	}
	
	public String toString(){
		return "TimeFrame:[" + init + ", " + end + "]";
	}
	

}
