package eu.gloria.rti.sch.impl.time;

import java.util.Date;

import eu.gloria.rt.catalogue.CatalogueTools;
import eu.gloria.rt.catalogue.ObjCategory;
import eu.gloria.rt.catalogue.ObjInfo;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.RTSchException;
import eu.gloria.rt.unit.Altaz;
import eu.gloria.rt.unit.Radec;
import eu.gloria.rti.sch.core.ConstraintValidator;
import eu.gloria.rti.sch.core.TimeFrame;
import eu.gloria.rti.sch.core.plan.constraint.ConstraintMoonDistance;
import eu.gloria.rti.sch.core.plan.constraint.ConstraintTargetAltitude;
import eu.gloria.rti.sch.core.plan.constraint.Constraints;
import eu.gloria.tools.conversion.DegreeFormat;
import eu.gloria.tools.log.LogUtil;

public class ConstraintValidatorMoonDistance  extends ConstraintValidatorBase  {
	
	private boolean logs = false;

	public ConstraintValidatorMoonDistance(ConstraintsContext context, int days, boolean verbose) {
		super(context, days, verbose);
	}

	@Override
	public boolean isSatisfied(Constraints constraints, TimeFrame timeFrame)
			throws RTException {
		
		Radec moonRadec = context.getMoonRadec();
		if (moonRadec == null){
			ObjInfo info = this.catalogue.getObject("moon", ObjCategory.MajorPlanetAndMoon, timeFrame.getInit());
			moonRadec = info.getPosition();
			this.context.setMoonRadec(moonRadec);
		}
		
		Radec targetRadec = context.getTargetRadec();
		
		if (targetRadec == null) return false;
		
		if (logs){
			
			String[] names = {
					"date",
					"PosRa",
					"PosDec",
					"obs.latitude",
					"obs.longitude"
			};
			
			String[] values = {
					timeFrame.getInit().toString(),
					targetRadec.getRaString(DegreeFormat.HHMMSS),
					targetRadec.getDecString(DegreeFormat.DDMMSS),
					String.valueOf(observer.getLatitude()),
					String.valueOf(observer.getLongitude())
			};
			
			System.out.println((new Date()) + "      AstronomicalTimeFrameLocator::Constraint.MoonDistance->Target info:" + LogUtil.getLog(names, values));
			
		}
		
		ConstraintMoonDistance constraintMoonDistance = (ConstraintMoonDistance) constraints.getMoonDistance();
		
		return (constraintMoonDistance.getDistance() <=  CatalogueTools.getAngularDistance(moonRadec, targetRadec));
		
	}

}
