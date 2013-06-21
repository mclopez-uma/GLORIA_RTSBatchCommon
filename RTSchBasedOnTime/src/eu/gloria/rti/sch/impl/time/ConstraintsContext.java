package eu.gloria.rti.sch.impl.time;

import java.util.HashMap;

import eu.gloria.rt.catalogue.Observer;
import eu.gloria.rt.ephemeris.Ephemeris;
import eu.gloria.rt.unit.Radec;
import eu.gloria.rt.unit.Altaz;

public class ConstraintsContext {
	
	private Radec moonRadec;
	private Altaz moonAltaz;
	
	private Radec targetRadec;
	private Altaz targetAltaz;
	
	private Observer observer;
	
	private HashMap<String, Ephemeris> ephemeris;
	
	public void clear(){
		moonAltaz = null;
		moonRadec = null;
		targetRadec = null;
		targetAltaz = null;
		ephemeris = new HashMap<String, Ephemeris>();
	}

	public Radec getMoonRadec() {
		return moonRadec;
	}

	public void setMoonRadec(Radec moonRadec) {
		this.moonRadec = moonRadec;
	}

	public Altaz getMoonAltaz() {
		return moonAltaz;
	}

	public void setMoonAltaz(Altaz moonAltaz) {
		this.moonAltaz = moonAltaz;
	}

	public Radec getTargetRadec() {
		return targetRadec;
	}

	public void setTargetRadec(Radec targetRadec) {
		this.targetRadec = targetRadec;
	}

	public Altaz getTargetAltaz() {
		return targetAltaz;
	}

	public void setTargetAltaz(Altaz targetAltaz) {
		this.targetAltaz = targetAltaz;
	}
	
	public Ephemeris getEphemeris(String id){
		return ephemeris.get(id);
	}
	
	public void putEphemeris(String id, Ephemeris eph){
		ephemeris.put(id, eph);
	}

	public Observer getObserver() {
		return observer;
	}

	public void setObserver(Observer observer) {
		this.observer = observer;
	}

}
