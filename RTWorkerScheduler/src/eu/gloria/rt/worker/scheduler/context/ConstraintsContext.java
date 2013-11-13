package eu.gloria.rt.worker.scheduler.context;

import java.util.HashMap;

import eu.gloria.rt.catalogue.Observer;
import eu.gloria.rt.ephemeris.Ephemeris;
import eu.gloria.rt.unit.Radec;
import eu.gloria.rt.unit.Altaz;

/**
 * Class to save the information about the astronomic constraints.
 * 
 * @author Alfredo
 */
public class ConstraintsContext {
	private Radec moonRadec;
	private Altaz moonAltaz;
	private Radec targetRadec;
	private Altaz targetAltaz;
	private Observer observer;
	private HashMap<String, Ephemeris> ephemeris;
	
	/**
	 * Method to clear the internal data.
	 */
	public void clear() {
		moonAltaz = null;
		moonRadec = null;
		targetRadec = null;
		targetAltaz = null;
		ephemeris = new HashMap<String, Ephemeris>();
	}

	/**
	 * Getter method to access to the moon right ascension & declination.
	 * 
	 * @return The moon right ascension & declination.
	 */
	public Radec getMoonRadec() {
		return moonRadec;
	}

	/**
	 * Setter method to access to the moon right ascension & declination.
	 * 
	 * @param moonRadec The new moon right ascension & declination data class.
	 */
	public void setMoonRadec(Radec moonRadec) {
		this.moonRadec = moonRadec;
	}

	/**
	 * Getter method to access to the moon altitude & azimut.
	 * 
	 * @return The moon altitude & azimut.
	 */
	public Altaz getMoonAltaz() {
		return moonAltaz;
	}

	/**
	 * Setter method to access to the moon altitude & azimut.
	 * 
	 * @param moonAltaz The new moon altitude & azimut data class.
	 */
	public void setMoonAltaz(Altaz moonAltaz) {
		this.moonAltaz = moonAltaz;
	}

	/**
	 * Getter method to access to the target right ascension & declination.
	 * 
	 * @return The target right ascension & declination.
	 */
	public Radec getTargetRadec() {
		return targetRadec;
	}

	/**
	 * Setter method to access to the target right ascension & declination.
	 * 
	 * @param targetRadec The new target right ascension & declination data class.
	 */
	public void setTargetRadec(Radec targetRadec) {
		this.targetRadec = targetRadec;
	}

	/**
	 * Getter method to access to the target altitude & azimut.
	 * 
	 * @return The target altitude & azimut.
	 */
	public Altaz getTargetAltaz() {
		return targetAltaz;
	}

	/**
	 * Setter method to access to the target altitude & azimut.
	 * 
	 * @param targetAltaz The new target altitude & azimut data class.
	 */
	public void setTargetAltaz(Altaz targetAltaz) {
		this.targetAltaz = targetAltaz;
	}
	
	/**
	 * Getter method to ephemeris data of a object.
	 * 
	 * @return The ephemeris data in a Ephemeris class.
	 */
	public Ephemeris getEphemeris(String id) {
		return ephemeris.get(id);
	}
	
	/**
	 * Setter method to ephemeris data of a object.
	 * 
	 * @param id The new object name.
	 * @param eph The new ephemeris data class.
	 */
	public void putEphemeris(String id, Ephemeris eph) {
		ephemeris.put(id, eph);
	}

	/**
	 * Getter method to observer localization.
	 * 
	 * @return The observer localization.
	 */
	public Observer getObserver() {
		return observer;
	}

	/**
	 * Setter method to observer localization.
	 * 
	 * @param observer The new observer localization.
	 */
	public void setObserver(Observer observer) {
		this.observer = observer;
	}
}
