package eu.gloria.rt.worker.scheduler.constraints;

import eu.gloria.rt.catalogue.ObjCategory;
import eu.gloria.rt.ephemeris.EphemerisData;
import eu.gloria.rt.unit.Altaz;
import eu.gloria.rt.unit.Radec;
import eu.gloria.rt.worker.scheduler.context.ConstraintsContext;
import eu.gloria.rt.worker.scheduler.context.SchedulerContext;
import eu.gloria.rt.worker.scheduler.iterator.TimeFrame;

/**
 * Class to check the moon altitude.
 * 
 * @author Alfredo
 */
public class ConstraintValidatorMoonAltitude extends ConstraintValidatorBase {
	/**
	 * Default constructor.
	 * 
	 * @param sc The scheduler context.
	 * @param cc The constraints context.
	 * @param days The number of days to scheduler.
	 */
	public ConstraintValidatorMoonAltitude(SchedulerContext sc, ConstraintsContext cc) {
		super(sc, cc);
	}

	@Override
	public boolean isSatisfied(Constraints constraints, TimeFrame timeFrame) {
		boolean check = false;
		try {
			EphemerisData data = ephemerisCalculator.getObjectInfo("moon", ObjCategory.MajorPlanetAndMoon, timeFrame.getInit());
			if (data != null) {
				Radec moonRadec = data.getRadec();
				Altaz moonAltaz = data.getAltaz();
				
				this.context.setMoonRadec(moonRadec);
				this.context.setMoonAltaz(moonAltaz);
				
				ConstraintMoonAltitude constraint = (ConstraintMoonAltitude) constraints.getMoonAltitude();
				check = constraint.getAltitude() >= moonAltaz.getAltDecimal();
	
				log.info(String.format(schContext.language.getString("ConsValMoonAltitude_Maximum_moon_altitude"), constraint.getAltitude()));
				log.info(String.format(schContext.language.getString("ConsValMoonAltitude_Moon_altitude"), moonAltaz.getAltDecimal()));
				log.info(String.format(schContext.language.getString("ConsValMoonAltitude_Result_check"), check));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return check;
	}
}
