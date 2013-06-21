package eu.gloria.rti.sch.impl.time;

import java.util.Date;

import eu.gloria.rt.catalogue.CatalogueTools;
import eu.gloria.rt.catalogue.ObjCategory;
import eu.gloria.rt.catalogue.ObjInfo;
import eu.gloria.rt.ephemeris.Ephemeris;
import eu.gloria.rt.ephemeris.EphemerisData;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.unit.Altaz;
import eu.gloria.rt.unit.Radec;
import eu.gloria.rti.sch.core.TimeFrame;
import eu.gloria.rti.sch.core.plan.constraint.ConstraintTarget;
import eu.gloria.rti.sch.core.plan.constraint.ConstraintTargetAltitude;
import eu.gloria.rti.sch.core.plan.constraint.Constraints;

public class ConstraintValidatorTargetVisible extends ConstraintValidatorBase {
	
	private int targetOrdinal;

	public ConstraintValidatorTargetVisible(ConstraintsContext context, int days, boolean verbose) {
		super(context, days, verbose);
	}

	@Override
	public boolean isSatisfied(Constraints constraints, TimeFrame timeFrame)
			throws RTException {
		
		try {

			ConstraintTarget constraintTarget = (ConstraintTarget) constraints.getTargets().get(targetOrdinal);
			Radec targetRadec = null;
			Altaz targetAltaz = null;
			
			//Clean context
			this.context.setTargetRadec(targetRadec);
			this.context.setTargetAltaz(targetAltaz);
			
			if (constraintTarget.getObjName() != null) {
				
				Ephemeris eph = context.getEphemeris(constraintTarget.getObjName());
				if (eph == null){
					eph = ephemerisCalculator.getEphemeris(constraintTarget.getObjName());
					context.putEphemeris(constraintTarget.getObjName(), eph);
				}
				
				EphemerisData data = eph.getObjectInfo(timeFrame.getInit());
				
				if (data == null) return false; // UNFOUND!!!
				
				targetRadec = data.getRadec();
				targetAltaz = data.getAltaz();
				
			} else {
				
				targetRadec = new Radec();
				targetRadec.setDec(constraintTarget.getCoordinates().getJ2000().getDec());
				targetRadec.setRa(constraintTarget.getCoordinates().getJ2000().getRa());
				
				targetAltaz = CatalogueTools.getAltazByRadec(observer, timeFrame.getInit(), targetRadec);
			}
			
			this.context.setTargetRadec(targetRadec);
			this.context.setTargetAltaz(targetAltaz);
			
			System.out.println((new Date()) + "      AstronomicalTimeFrameLocator::TARGET.RA:" + targetRadec.getRaDecimal());
			System.out.println((new Date()) + "      AstronomicalTimeFrameLocator::TARGET.DEC:" + targetRadec.getDecDecimal());
			System.out.println((new Date()) + "      AstronomicalTimeFrameLocator::TARGET.ALT:" + targetAltaz.getAltDecimal());

			return (0 < targetAltaz.getAltDecimal());
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	/*@Override
	public boolean isSatisfied(Constraints constraints, TimeFrame timeFrame)
			throws RTException {
		
		
		try {

			ConstraintTarget constraintTarget = (ConstraintTarget) constraints.getTargets().get(targetOrdinal);
			Radec targetRadec = null;
			if (constraintTarget.getObjName() != null) {
				ObjInfo info = this.catalogue.getObject(constraintTarget.getObjName(), ObjCategory.MajorPlanetAndMoon, timeFrame.getInit());
				targetRadec = info.getPosition();
			} else {
				targetRadec = new Radec();
				targetRadec.setDec(constraintTarget.getCoordinates().getJ2000().getDec());
				targetRadec.setRa(constraintTarget.getCoordinates().getJ2000().getRa());
			}
			
			this.context.setTargetRadec(targetRadec);
			Altaz targetAltaz = CatalogueTools.getAltazByRadec(observer, timeFrame.getInit(), targetRadec);
			System.out.println((new Date()) + "      AstronomicalTimeFrameLocator::TARGET.RA:" + targetRadec.getRaDecimal());
			System.out.println((new Date()) + "      AstronomicalTimeFrameLocator::TARGET.DEC:" + targetRadec.getDecDecimal());
			System.out.println((new Date()) + "      AstronomicalTimeFrameLocator::TARGET.ALT:" + targetAltaz.getAltDecimal());
			this.context.setTargetAltaz(targetAltaz);

			return (0 < targetAltaz.getAltDecimal());
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}*/

	public int getTargetOrdinal() {
		return targetOrdinal;
	}

	public void setTargetOrdinal(int targetOrdinal) {
		this.targetOrdinal = targetOrdinal;
	}

}
