package eu.gloria.rt.worker.scheduler.constraints;

import eu.gloria.rt.catalogue.Catalogue;
import eu.gloria.rt.catalogue.Observer;
import eu.gloria.rt.ephemeris.EphemerisCalculator;
import eu.gloria.rt.worker.scheduler.context.ConstraintsContext;
import eu.gloria.rt.worker.scheduler.context.SchedulerContext;
import eu.gloria.rt.worker.scheduler.context.SchedulerLog;

/**
 * Abstract class that implements the interface to check the specified astronomics constraints.
 * 
 * @author Alfredo
 */
public abstract class ConstraintValidatorBase implements ConstraintValidator {
	protected EphemerisCalculator ephemerisCalculator;
	protected SchedulerContext schContext;
	protected ConstraintsContext context;
	protected Catalogue catalogue;
	protected Observer observer;
	protected SchedulerLog log;
	protected int daysSch;

	/**
	 * Default constructor.
	 * 
	 * @param sc The scheduler context.
	 * @param cc The constraints context.
	 * @param daysSch The number of days to scheduler.
	 */
	public ConstraintValidatorBase(SchedulerContext sc, ConstraintsContext cc) {
		this.schContext = sc;
		this.log = sc.logger(getClass());
		this.context = cc;
		this.observer = cc.getObserver();
		double longitude = this.observer.getLongitude();
		double latitude = this.observer.getLatitude();
		double altitude = this.observer.getAltitude();
		this.catalogue = new Catalogue(longitude, latitude, altitude);
		this.daysSch = sc.getDaysScheduling();
		this.ephemerisCalculator = new EphemerisCalculator(this.observer, daysSch, false);
	}
}
