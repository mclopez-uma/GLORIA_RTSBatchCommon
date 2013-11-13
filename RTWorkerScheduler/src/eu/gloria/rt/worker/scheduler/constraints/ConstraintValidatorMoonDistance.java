package eu.gloria.rt.worker.scheduler.constraints;

import eu.gloria.rt.catalogue.CatalogueTools;
import eu.gloria.rt.catalogue.ObjCategory;
import eu.gloria.rt.catalogue.ObjInfo;
import eu.gloria.rt.unit.Radec;
import eu.gloria.rt.worker.scheduler.context.ConstraintsContext;
import eu.gloria.rt.worker.scheduler.context.SchedulerContext;
import eu.gloria.rt.worker.scheduler.iterator.TimeFrame;

/**
 * Class to check the moon distance.
 * 
 * @author Alfredo
 */
public class ConstraintValidatorMoonDistance extends ConstraintValidatorBase {
	/**
	 * Default constructor.
	 * 
	 * @param sc The scheduler context.
	 * @param cc The constraints context.
	 * @param days The number of days to scheduler.
	 */
	public ConstraintValidatorMoonDistance(SchedulerContext sc, ConstraintsContext cc) {
		super(sc, cc);
	}

	@Override
	public boolean isSatisfied(Constraints constraints, TimeFrame timeFrame) {
		try {
			Radec moonRadec = context.getMoonRadec();
			if (moonRadec == null){
				ObjInfo info = this.catalogue.getObject("moon", ObjCategory.MajorPlanetAndMoon, timeFrame.getInit());
				moonRadec = info.getPosition();
				this.context.setMoonRadec(moonRadec);
			}
			
			Radec targetRadec = context.getTargetRadec();
			if (targetRadec == null) {
				return false;
			}
			
			ConstraintMoonDistance constraintMoonDistance = (ConstraintMoonDistance) constraints.getMoonDistance();
			double calculated = CatalogueTools.getAngularDistance(moonRadec, targetRadec);
			boolean check = constraintMoonDistance.getDistance() <= calculated;
	
			log.info(String.format(schContext.language.getString("ConsValMoonDistance_Moon_distance"), constraintMoonDistance.getDistance()));
			log.info(String.format(schContext.language.getString("ConsValMoonDistance_Distance_calculated"), calculated));
			log.info(String.format(schContext.language.getString("ConsValMoonDistance_Result_check"), check));
	
			return check;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
