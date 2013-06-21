package eu.gloria.rti.sch.impl.time;

import eu.gloria.rt.catalogue.Catalogue;
import eu.gloria.rt.catalogue.Observer;
import eu.gloria.rt.ephemeris.EphemerisCalculator;
import eu.gloria.rti.sch.core.ConstraintValidator;

public abstract class ConstraintValidatorBase implements ConstraintValidator {
	
	protected ConstraintsContext context;
	protected Catalogue catalogue;
	protected Observer observer;
	protected EphemerisCalculator ephemerisCalculator;
	protected int days;
	protected boolean verbose;
	

	public ConstraintValidatorBase(ConstraintsContext context, int days, boolean verbose){
		this.context = context;
		this.observer = context.getObserver();
		this.observer.setLatitude(37.2);
		this.observer.setLongitude(-7.216667);
		this.catalogue = new Catalogue(this.observer.getLongitude(), this.observer.getLatitude(), this.observer.getAltitude());
		this.ephemerisCalculator = new EphemerisCalculator(this.observer, days, verbose);
		this.days = days;
		this.verbose = verbose;
	}

}
