package eu.gloria.rt.worker.scheduler.constraints;

import eu.gloria.rt.catalogue.CatalogueTools;
import eu.gloria.rt.ephemeris.Ephemeris;
import eu.gloria.rt.ephemeris.EphemerisData;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.unit.Altaz;
import eu.gloria.rt.unit.Radec;
import eu.gloria.rt.worker.scheduler.context.ConstraintsContext;
import eu.gloria.rt.worker.scheduler.context.SchedulerContext;
import eu.gloria.rt.worker.scheduler.iterator.TimeFrame;

/**
 * Class to check target visibility.
 * 
 * @author Alfredo
 */
public class ConstraintValidatorTargetVisible extends ConstraintValidatorBase {
	private int targetOrdinal;

	/**
	 * Default constructor.
	 * 
	 * @param sc The scheduler context.
	 * @param cc The constraints context.
	 * @param days The number of days to scheduler.
	 */
	public ConstraintValidatorTargetVisible(SchedulerContext sc, ConstraintsContext cc) {
		super(sc, cc);
	}

	@Override
	public boolean isSatisfied(Constraints constraints, TimeFrame timeFrame) {
		try {
			ConstraintTarget constraintTarget = (ConstraintTarget) constraints.getTargets().get(targetOrdinal);
			Radec targetRadec = null;
			Altaz targetAltaz = null;
			
			// Clean context
			this.context.setTargetRadec(targetRadec);
			this.context.setTargetAltaz(targetAltaz);
			
			EphemerisData data = null;
			
			if (constraintTarget.getObjName() != null) {
				Ephemeris eph = context.getEphemeris(constraintTarget.getObjName());
				if (eph == null) {
					eph = ephemerisCalculator.getEphemeris(constraintTarget.getObjName());
					if (eph == null) {
						throw new RTException("Unfound object: " + constraintTarget.getObjName());
					}else{
						context.putEphemeris(constraintTarget.getObjName(), eph);
					}
				}
				
				data = eph.getObjectInfo(timeFrame.getInit());
				if (data == null) {
					// UNFOUND!!!
					return false;
				}
				
				targetRadec = data.getRadec();
				targetAltaz = data.getAltaz();
				
			}else{
				targetRadec = new Radec();
				targetRadec.setDec(constraintTarget.getCoordinates().getJ2000().getDec());
				targetRadec.setRa(constraintTarget.getCoordinates().getJ2000().getRa());
				
				targetAltaz = CatalogueTools.getAltazByRadec(observer, timeFrame.getInit(), targetRadec);
			}
			
			this.context.setTargetRadec(targetRadec);
			this.context.setTargetAltaz(targetAltaz);
			
			log.info(String.format(schContext.language.getString("ConsValTargetVisible_Object_name"), constraintTarget.getObjName()));
			log.info(String.format(schContext.language.getString("ConsValTargetVisible_Time_interval_init"), timeFrame.getInit()));
			if (data != null) {
				log.info(String.format(schContext.language.getString("ConsValTargetVisible_Time_ephemeris"), data.getDate()));
			}
			log.info(String.format(schContext.language.getString("ConsValTargetVisible_Target_ra"), targetRadec.getRaDecimal()));
			log.info(String.format(schContext.language.getString("ConsValTargetVisible_Target_dec"), targetRadec.getDecDecimal()));
			log.info(String.format(schContext.language.getString("ConsValTargetVisible_Target_alt"), targetAltaz.getAltDecimal()));
			log.info(String.format(schContext.language.getString("ConsValTargetVisible_Observer"), this.context.getObserver().toString()));
			
			boolean check = 0 < targetAltaz.getAltDecimal();
			log.info(String.format(schContext.language.getString("ConsValTargetVisible_Result_check"), check));
			return check;

        } catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * Setter method to request the index target.
	 * 
	 * @param targetOrdinal The new number of the target.
	 */
	public void setTargetOrdinal(int targetOrdinal) {
		this.targetOrdinal = targetOrdinal;
	}
}
