package eu.gloria.rti.sch.impl.time;

import java.util.Date;

import eu.gloria.rt.catalogue.CatalogueTools;
import eu.gloria.rt.catalogue.ObjCategory;
import eu.gloria.rt.catalogue.ObjInfo;
import eu.gloria.rt.ephemeris.EphemerisCalculator;
import eu.gloria.rt.ephemeris.EphemerisData;
import eu.gloria.rt.ephemeris.EphemerisOutOfScopeException;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.RTSchException;
import eu.gloria.rt.unit.Altaz;
import eu.gloria.rt.unit.Radec;
import eu.gloria.rti.sch.core.ConstraintValidator;
import eu.gloria.rti.sch.core.TimeFrame;
import eu.gloria.rti.sch.core.plan.constraint.ConstraintMoonAltitude;
import eu.gloria.rti.sch.core.plan.constraint.Constraints;
import eu.gloria.tools.conversion.DegreeFormat;
import eu.gloria.tools.log.LogUtil;

public class ConstraintValidatorMoonAltitude  extends ConstraintValidatorBase {
	
	private boolean logs = false;
	

	public ConstraintValidatorMoonAltitude(ConstraintsContext context, int days, boolean verbose) {
		super(context, days, verbose);
	}

	@Override
	public boolean isSatisfied(Constraints constraints, TimeFrame timeFrame)
			throws RTException {
		
		boolean result = false;
		
		EphemerisData data = ephemerisCalculator.getObjectInfo("moon", ObjCategory.MajorPlanetAndMoon, timeFrame.getInit());
		if (data != null) {
			
			Radec moonRadec = data.getRadec();
			Altaz moonAltaz = data.getAltaz();
			
			this.context.setMoonRadec(moonRadec);
			this.context.setMoonAltaz(moonAltaz);
			
			if (logs){
				
				String[] names = {
						"date",
						"alt",
						"az",
						"ra",
						"dec",
						"obs.latitude",
						"obs.longitude"
				};
			
				String[] values = {
						timeFrame.getInit().toString(),
						moonAltaz.getAltString(DegreeFormat.DDMMSS),
						moonAltaz.getAzString(DegreeFormat.DDMMSS),
						moonRadec.getRaString(DegreeFormat.HHMMSS),
						moonRadec.getDecString(DegreeFormat.DDMMSS),
						String.valueOf(observer.getLatitude()),
						String.valueOf(observer.getLongitude())
				};
			
				System.out.println((new Date()) + "      AstronomicalTimeFrameLocator::Constraint.MoonAltitude->Target info:" + LogUtil.getLog(names, values));
			
			}
			
			ConstraintMoonAltitude constraint = (ConstraintMoonAltitude) constraints.getMoonAltitude();
			
			result =  (constraint.getAltitude() >= moonAltaz.getAltDecimal());
			
		}
		
		return result;
	}
	
	/*@Override
	public boolean isSatisfied(Constraints constraints, TimeFrame timeFrame)
			throws RTException {
		
		//Altaz moonAltaz = context.getMoonAltaz();
		Altaz moonAltaz = null;
		if (moonAltaz == null){
			ObjInfo info = this.catalogue.getObject("moon", ObjCategory.MajorPlanetAndMoon, timeFrame.getInit());
			Radec moonRadec = info.getPosition();
			this.context.setMoonRadec(moonRadec);
			moonAltaz = CatalogueTools.getAltazByRadec(observer, timeFrame.getInit(), moonRadec);
			this.context.setMoonAltaz(moonAltaz);
			
			if (logs){
				
				String[] names = {
						"date",
						"alt",
						"az",
						"ra",
						"dec",
						"obs.latitude",
						"obs.longitude"
				};
			
				String[] values = {
						timeFrame.getInit().toString(),
						moonAltaz.getAltString(DegreeFormat.DDMMSS),
						moonAltaz.getAzString(DegreeFormat.DDMMSS),
						moonRadec.getRaString(DegreeFormat.HHMMSS),
						moonRadec.getDecString(DegreeFormat.DDMMSS),
						String.valueOf(observer.getLatitude()),
						String.valueOf(observer.getLongitude())
				};
			
				System.out.println((new Date()) + "      AstronomicalTimeFrameLocator::Constraint.MoonAltitude->Target info:" + LogUtil.getLog(names, values));
			
			}
			
		} else{
		
			if (logs){
			
				String[] names = {
						"date",
						"alt",
						"az",
						"obs.latitude",
						"obs.longitude"
				};
			
				String[] values = {
						timeFrame.getInit().toString(),
						moonAltaz.getAltString(DegreeFormat.DDMMSS),
						moonAltaz.getAzString(DegreeFormat.DDMMSS),
						String.valueOf(observer.getLatitude()),
						String.valueOf(observer.getLongitude())
				};
			
				System.out.println((new Date()) + "      AstronomicalTimeFrameLocator::Constraint.MoonAltitude->Target info:" + LogUtil.getLog(names, values));
			
			}
		}
		
		ConstraintMoonAltitude constraint = (ConstraintMoonAltitude) constraints.getMoonAltitude();
		
		return (constraint.getAltitude() >= moonAltaz.getAltDecimal());
	}*/

}
