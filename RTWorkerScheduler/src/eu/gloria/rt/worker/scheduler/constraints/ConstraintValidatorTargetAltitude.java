package eu.gloria.rt.worker.scheduler.constraints;

import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.unit.Altaz;
import eu.gloria.rt.worker.scheduler.context.ConstraintsContext;
import eu.gloria.rt.worker.scheduler.context.SchedulerContext;
import eu.gloria.rt.worker.scheduler.iterator.TimeFrame;

/**
 * Class to check target altitude.
 * 
 * @author Alfredo
 */
public class ConstraintValidatorTargetAltitude extends ConstraintValidatorBase {
	/**
	 * Default constructor.
	 * 
	 * @param sc The scheduler context.
	 * @param cc The constraints context.
	 * @param days The number of days to scheduler.
	 */
	public ConstraintValidatorTargetAltitude(SchedulerContext sc, ConstraintsContext cc) throws RTException {
		super(sc, cc);
	}

	@Override
	public boolean isSatisfied(Constraints constraints, TimeFrame timeFrame) {
		try {
			// Calculated by TargetVisibility Validator
			Altaz targetAltaz = context.getTargetAltaz();
			
			if (targetAltaz == null) {
				return false;
			}

			ConstraintTargetAltitude constraintTargetAltitude = (ConstraintTargetAltitude) constraints.getTargetAltitude();
			boolean check = constraintTargetAltitude.getAltitude() <= targetAltaz.getAltDecimal();

			log.info(String.format(schContext.language.getString("ConsValTargetAltitude_Minimum_target_altitude"), constraintTargetAltitude.getAltitude()));
			log.info(String.format(schContext.language.getString("ConsValTargetAltitude_Target_altitude"), targetAltaz.getAltDecimal()));
			log.info(String.format(schContext.language.getString("ConsValTargetAltitude_Result_check"), check));

			return check;

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
}
