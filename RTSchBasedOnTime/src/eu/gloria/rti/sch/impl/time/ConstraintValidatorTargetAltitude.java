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
import eu.gloria.rti.sch.core.plan.constraint.ConstraintMoonAltitude;
import eu.gloria.rti.sch.core.plan.constraint.ConstraintTarget;
import eu.gloria.rti.sch.core.plan.constraint.ConstraintTargetAltitude;
import eu.gloria.rti.sch.core.plan.constraint.Constraints;
import eu.gloria.tools.conversion.DegreeFormat;
import eu.gloria.tools.log.LogUtil;

public class ConstraintValidatorTargetAltitude extends ConstraintValidatorBase {

	private int targetOrdinal;
	
	private boolean logs = false;

	public ConstraintValidatorTargetAltitude(ConstraintsContext context, int days, boolean verbose) throws RTException {
		super(context, days, verbose);
	}

	@Override
	public boolean isSatisfied(Constraints constraints, TimeFrame timeFrame)
			throws RTException {

		try {

			//Calculated by TargetVisibility Validator
			Radec targetRadec = context.getTargetRadec();
			Altaz targetAltaz = context.getTargetAltaz();
			
			if (targetRadec == null || targetAltaz == null) return false;
			
			if (logs){
				
				String[] names = {
						"date",
						"PosRa",
						"PosDec",
						"alt",
						"obs.latitude",
						"obs.longitude"
				};
				
				String[] values = {
						timeFrame.getInit().toString(),
						targetRadec.getRaString(DegreeFormat.HHMMSS),
						targetRadec.getDecString(DegreeFormat.DDMMSS),
						targetAltaz.getAltString(DegreeFormat.DDMMSS),
						String.valueOf(observer.getLatitude()),
						String.valueOf(observer.getLongitude())
				};
				
				System.out.println((new Date()) + "      AstronomicalTimeFrameLocator::Constraint.TargetAltitude->Target info:" + LogUtil.getLog(names, values));
				
			}

			ConstraintTargetAltitude constraintTargetAltitude = (ConstraintTargetAltitude) constraints.getTargetAltitude();
			
			return (constraintTargetAltitude.getAltitude() <= targetAltaz.getAltDecimal());
			
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
			this.context.setTargetAltaz(targetAltaz);
			
			if (logs){
				
				String[] names = {
						"date",
						"PosRa",
						"PosDec",
						"alt",
						"obs.latitude",
						"obs.longitude"
				};
				
				String[] values = {
						timeFrame.getInit().toString(),
						targetRadec.getRaString(DegreeFormat.HHMMSS),
						targetRadec.getDecString(DegreeFormat.DDMMSS),
						targetAltaz.getAltString(DegreeFormat.DDMMSS),
						String.valueOf(observer.getLatitude()),
						String.valueOf(observer.getLongitude())
				};
				
				System.out.println((new Date()) + "      AstronomicalTimeFrameLocator::Constraint.TargetAltitude->Target info:" + LogUtil.getLog(names, values));
				
			}

			ConstraintTargetAltitude constraintTargetAltitude = (ConstraintTargetAltitude) constraints.getTargetAltitude();
			
			return (constraintTargetAltitude.getAltitude() <= targetAltaz.getAltDecimal());
			
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
