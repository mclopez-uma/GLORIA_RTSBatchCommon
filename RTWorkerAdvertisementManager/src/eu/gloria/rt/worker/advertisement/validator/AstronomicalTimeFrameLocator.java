package eu.gloria.rt.worker.advertisement.validator;

import java.util.Calendar;
import java.util.Date;

import eu.gloria.rt.catalogue.Observer;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rti.sch.core.ConstraintValidator;
import eu.gloria.rti.sch.core.TimeFrame;
import eu.gloria.rti.sch.core.plan.constraint.ConstraintTarget;
import eu.gloria.rti.sch.core.plan.constraint.Constraints;
import eu.gloria.tools.log.LogUtil;

public class AstronomicalTimeFrameLocator implements
		eu.gloria.rti.sch.core.AstronomicalTimeFrameLocator {
	
	private ConstraintValidator moonAltitude;
	private ConstraintValidator moonDistance;
	private ConstraintValidatorTargetAltitude targetAltitude;
	private ConstraintValidatorTargetVisible targetVisible;
	private int days;
	private boolean verbose;
	private Observer observer;
	
	protected ConstraintsContext context;
	
	public AstronomicalTimeFrameLocator(Observer observer, int days, boolean verbose) throws RTException{
		
		this.days = days;
		this.verbose = verbose;
		this.observer = observer;
		
		context = new ConstraintsContext();
		context.setObserver(observer);
		
		moonAltitude = new ConstraintValidatorMoonAltitude(context, days, verbose);
		moonDistance = new ConstraintValidatorMoonDistance(context, days, verbose);
		targetAltitude = new ConstraintValidatorTargetAltitude(context, days, verbose);
		targetVisible = new ConstraintValidatorTargetVisible(context, days, verbose);
		
	}
	
	@Override
	public TimeFrame getValidTimeFrame(Constraints constraints,
			TimeFrame timeFrame) throws RTException {
		
		context.clear();
		
		TimeFrameIterator iterator = new TimeFrameIterator(timeFrame.getInit(), timeFrame.getEnd(), Calendar.MINUTE, 10);
		
		if (verbose) LogUtil.info(this, "AstronomicalTimeFrameLocator::TimeFrame.Planning:->init:" + timeFrame.getInit());
		if (verbose) LogUtil.info(this, "AstronomicalTimeFrameLocator::TimeFrame.Planning:->end :" + timeFrame.getEnd());
		
		while (iterator.hasNext()){
			
			boolean satisfied;
			
			TimeFrame internalTimeFrame = iterator.next();
			
			if (verbose) LogUtil.info(this,  "   AstronomicalTimeFrameLocator::TimeFrame.Astronomical:->init:" + internalTimeFrame.getInit());
			if (verbose) LogUtil.info(this, "   AstronomicalTimeFrameLocator::TimeFrame.Astronomical:->end :" + internalTimeFrame.getEnd());
			
			//Moon altitude
			if (constraints.getMoonAltitude() != null){
				if (verbose) LogUtil.info(this, "      AstronomicalTimeFrameLocator::Constraint->MoonAltitude");
				satisfied = moonAltitude.isSatisfied(constraints, internalTimeFrame);
				if (verbose) LogUtil.info(this, "      AstronomicalTimeFrameLocator::Constraint->MoonAltitude: " + satisfied);
				//satisfied = true;					
				if (!satisfied) continue;
			}
			
			if (verbose) LogUtil.info(this, "      AstronomicalTimeFrameLocator::Constraint->About Targets: " + constraints.getTargets().size());
			boolean satisfiedTarget = true;
			for (int x = 0; x < constraints.getTargets().size(); x++){
				
				ConstraintTarget target = (ConstraintTarget) constraints.getTargets().get(x);
				if (verbose) LogUtil.info(this, "      AstronomicalTimeFrameLocator::Constraint->****TARGET****: " + target.getObjName());
				
				//Visible
				targetVisible.setTargetOrdinal(x);
				if (verbose) LogUtil.info(this, "      AstronomicalTimeFrameLocator::Constraint->Target Visibility");
				satisfied = targetVisible.isSatisfied(constraints, internalTimeFrame);
				if (verbose) LogUtil.info(this, "      AstronomicalTimeFrameLocator::Constraint->Target Visibility:" + satisfied);
				//satisfied = true;				
				if (!satisfied) {
					satisfiedTarget = false;
					break;
				}
				
				//Altitude
				if (constraints.getTargetAltitude() != null){
					targetAltitude.setTargetOrdinal(x);
					if (verbose) LogUtil.info(this, "      AstronomicalTimeFrameLocator::Constraint->Target Altitude");
					satisfied = targetAltitude.isSatisfied(constraints, internalTimeFrame);
					if (verbose) LogUtil.info(this, "      AstronomicalTimeFrameLocator::Constraint->Target Altitude: " + satisfied);
					//satisfied = true;					
					if (!satisfied) {
						satisfiedTarget = false;
						break;
					}
				}
				
				//moon distance
				if (constraints.getMoonDistance() != null){
					if (verbose) LogUtil.info(this, "      AstronomicalTimeFrameLocator::Constraint->Moon Distance");
					satisfied = moonDistance.isSatisfied(constraints, internalTimeFrame);
					if (verbose) LogUtil.info(this, "      AstronomicalTimeFrameLocator::Constraint->Moon Distance: " + satisfied);
					//satisfied = true;					
					if (!satisfied) {
						satisfiedTarget = false;
						break;
					}
				}
				
			} //for
			
			//satisfiedTarget = false;
			if (satisfiedTarget) {
				
				return internalTimeFrame;
			}
			
		}
		
		return null;
	}
	
	
	
}
