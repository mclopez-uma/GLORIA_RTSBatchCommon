package eu.gloria.rt.worker.scheduler.iterator;

import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.worker.scheduler.constraints.ConstraintTarget;
import eu.gloria.rt.worker.scheduler.constraints.ConstraintValidatorBase;
import eu.gloria.rt.worker.scheduler.constraints.ConstraintValidatorMoonAltitude;
import eu.gloria.rt.worker.scheduler.constraints.ConstraintValidatorMoonDistance;
import eu.gloria.rt.worker.scheduler.constraints.ConstraintValidatorTargetAltitude;
import eu.gloria.rt.worker.scheduler.constraints.ConstraintValidatorTargetVisible;
import eu.gloria.rt.worker.scheduler.constraints.Constraints;
import eu.gloria.rt.worker.scheduler.context.ConstraintsContext;
import eu.gloria.rt.worker.scheduler.context.SchedulerContext;
import eu.gloria.rt.worker.scheduler.context.SchedulerLog;

public class AstronomicalTimeFrameLocator implements IAstronomicalTimeFrameLocator {
	private ConstraintValidatorTargetAltitude targetAltitude;
	private ConstraintValidatorTargetVisible targetVisible;
	private ConstraintValidatorBase moonAltitude;
	private ConstraintValidatorBase moonDistance;
	private SchedulerContext schContext;
	private SchedulerLog log;
	
	protected ConstraintsContext context;
	
	public AstronomicalTimeFrameLocator(SchedulerContext sc) throws RTException {
		this.schContext = sc;
		this.log = sc.logger(getClass());
		context = new ConstraintsContext();
		context.setObserver(sc.getObserver());
		
		moonAltitude = new ConstraintValidatorMoonAltitude(sc, context);
		moonDistance = new ConstraintValidatorMoonDistance(sc, context);
		targetAltitude = new ConstraintValidatorTargetAltitude(sc, context);
		targetVisible = new ConstraintValidatorTargetVisible(sc, context);	
	}
	
	@Override
	public TimeFrame getValidTimeFrame(Constraints constraints, TimeFrame timeFrame) {
		context.clear();
		TimeFrameIteratorAnyTime iterator = new TimeFrameIteratorAnyTime(timeFrame.getInit(), timeFrame.getEnd(), schContext.getUnitType(), schContext.getAmount());
		log.debug(String.format(schContext.language.getString("AstroTimeFrameLoc_Planning_init"), timeFrame.getInit()));
		log.debug(String.format(schContext.language.getString("AstroTimeFrameLoc_Planning_end"), timeFrame.getEnd()));
		
		while (iterator.hasNext()) {
			boolean satisfied = true;
			TimeFrame internalTimeFrame = iterator.next();

			log.info(String.format(schContext.language.getString("AstroTimeFrameLoc_Astronomical_init"), internalTimeFrame.getInit()));
			log.info(String.format(schContext.language.getString("AstroTimeFrameLoc_Astronomical_end"), internalTimeFrame.getEnd()));
			
			// Moon altitude
			if (constraints.getMoonAltitude() != null) {
				log.debug(schContext.language.getString("AstroTimeFrameLoc_Check_moon_altitude"));
				satisfied = moonAltitude.isSatisfied(constraints, internalTimeFrame);
				log.info(String.format(schContext.language.getString("AstroTimeFrameLoc_Moon_altitude"), satisfied));
			}
			
			log.info(String.format(schContext.language.getString("AstroTimeFrameLoc_About_targets"), constraints.getTargets().size()));
			for (int x = 0   ;  satisfied  &&  x < constraints.getTargets().size()   ;   x++) {
				ConstraintTarget target = (ConstraintTarget) constraints.getTargets().get(x);
				log.info(String.format(schContext.language.getString("AstroTimeFrameLoc_Target_to_check"), target.getObjName()));

				// Visible
				targetVisible.setTargetOrdinal(x);
				log.debug(schContext.language.getString("AstroTimeFrameLoc_Check_target_visibility"));
				satisfied = targetVisible.isSatisfied(constraints, internalTimeFrame);
				log.info(String.format(schContext.language.getString("AstroTimeFrameLoc_Target_visibility"), satisfied));
				
				// Altitude
				if (satisfied  &&  constraints.getTargetAltitude() != null) {
					log.debug(schContext.language.getString("AstroTimeFrameLoc_Check_target_altitude"));
					satisfied = targetAltitude.isSatisfied(constraints, internalTimeFrame);
					log.info(String.format(schContext.language.getString("AstroTimeFrameLoc_Target_altitude"), satisfied));
				}
				
				// Moon distance
				if (satisfied  &&  constraints.getMoonDistance() != null) {
					log.debug(schContext.language.getString("AstroTimeFrameLoc_Check_moon_distance"));
					satisfied = moonDistance.isSatisfied(constraints, internalTimeFrame);
					log.info(String.format(schContext.language.getString("AstroTimeFrameLoc_Moon_distance"), satisfied));
				}
			}
			
			if (satisfied) {
				return internalTimeFrame;
			}
		}
		return null;
	}
}
