package eu.gloria.rt.worker.scheduler.xmlop;

import java.io.File;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;


import eu.gloria.rt.entity.scheduler.plan.CameraSettings;
import eu.gloria.rt.entity.scheduler.plan.Expose;
import eu.gloria.rt.entity.scheduler.plan.Loop;
import eu.gloria.rt.entity.scheduler.plan.Plan;
import eu.gloria.rt.entity.scheduler.plan.Target;
import eu.gloria.rt.worker.scheduler.constraints.ConstraintMoonAltitude;
import eu.gloria.rt.worker.scheduler.constraints.ConstraintMoonDistance;
import eu.gloria.rt.worker.scheduler.constraints.ConstraintTarget;
import eu.gloria.rt.worker.scheduler.constraints.ConstraintTargetAltitude;
import eu.gloria.rt.worker.scheduler.constraints.Constraints;
import eu.gloria.rt.worker.scheduler.constraints.Coordinates;
import eu.gloria.rt.worker.scheduler.constraints.J2000;
import eu.gloria.rti.sch.core.plan.instruction.Instruction;

public class XmlObservingPlan {
	private static Plan getPlanXml(String xsdFile, String xmlFile) throws Exception {
		File schemaFile = new File(xsdFile);
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(schemaFile);
		JAXBContext context = JAXBContext.newInstance(Plan.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setSchema(schema);
		File file = new File(xmlFile);
		Plan plan = (Plan) unmarshaller.unmarshal(file);
		return plan;
	}

	public static Constraints getConstraints(String xsdFile, String xmlFile) throws Exception {
		Plan plan = getPlanXml(xsdFile, xmlFile);
		Constraints cnstr = new Constraints();

		// ----- Destinies -----
		// I get the list of destinies
		for (Target target : plan.getConstraints().getTargets().getTarget()) {
			// Add the destiny to the list
			ConstraintTarget constraintTarget = new ConstraintTarget();
			if (target.getCoordinates() == null) {
				constraintTarget.setObjName(target.getObjName());
			}else{
				constraintTarget.setCoordinates(convertCoodinates(target.getCoordinates()));
			}
			cnstr.getTargets().add(constraintTarget);
		}
		
		// ----- Moon Distance -----
		if (plan.getConstraints().getMoonDistance() != null) {
			ConstraintMoonDistance moonDistance = new ConstraintMoonDistance();
			moonDistance.setDistance(plan.getConstraints().getMoonDistance());
			cnstr.setMoonDistance(moonDistance);
		}
		
		// ----- Moon Altitude -----
		if (plan.getConstraints().getMoonAltitude() != null) {
			ConstraintMoonAltitude moonAltitude = new ConstraintMoonAltitude();
			moonAltitude.setAltitude(plan.getConstraints().getMoonAltitude());
			cnstr.setMoonAltitude(moonAltitude);
		}
		
		// ----- Target Distance -----
		if (plan.getConstraints().getTargetAltitude() != null) {
			ConstraintTargetAltitude targetAltitude = new ConstraintTargetAltitude();
			targetAltitude.setAltitude(plan.getConstraints().getTargetAltitude());
			cnstr.setTargetAltitude(targetAltitude);
		}

		return cnstr;
	}

	private static Coordinates convertCoodinates(eu.gloria.rt.entity.scheduler.plan.Coordinates coordinates) {
		Coordinates coord = new Coordinates();
		J2000 j2000 = new J2000(coordinates.getJ2000().getRA(), coordinates.getJ2000().getDEC());
		coord.setJ2000(j2000);
		return coord;
	}

	public static long getPredictedExecTime(String xsdFile, String xmlFile, long msecsLooseness, long msecsMountMove, long msecsFilterMove, long msecsCameraSettings) {
		long time = 0;
		try {
			Plan plan = getPlanXml(xsdFile, xmlFile);
			time = msecsLooseness + getPredictExecTime(plan.getInstructions().getTargetOrCameraSettingsOrLoop(), msecsMountMove, msecsFilterMove, msecsCameraSettings);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return time;
	}

	private static long getPredictExecTime(Object item, long msecsMountMove, long msecsFilterMove, long msecsCameraSettings) {
		long result = 0;

		if (item instanceof List) {
			// Processing List item
			@SuppressWarnings("unchecked")
			List<Instruction> list = (List<Instruction>) item;
			for (int x = 0; x < list.size(); x++) {
				Object obj = list.get(x);
				result = result + getPredictExecTime(obj, msecsMountMove, msecsFilterMove, msecsCameraSettings);
			}

		} else if (item instanceof Target) {
			// Processing Target item
			result = result + msecsMountMove;

		} else if (item instanceof CameraSettings) {
			// Processing CameraSettings item
			result = result + msecsCameraSettings;

		} else if (item instanceof Loop) {
			// Processing Loop item
			Loop tmpItemSource = (Loop) item;
			if (tmpItemSource.getRepeatCount() != null) {
				result = result + (getPredictExecTime(tmpItemSource.getTargetOrCameraSettingsOrLoop(), msecsMountMove, msecsFilterMove, msecsCameraSettings)) * tmpItemSource.getRepeatCount().intValue();

			} else if (tmpItemSource.getRepeatDuration() != null) {
				result = result + (new Double(tmpItemSource.getRepeatDuration() * 1000)).longValue();
			}

		} else if (item instanceof Expose) {
			// Processing Expose item
			Expose tmpItemSource = (Expose) item;
			if (tmpItemSource.getFilter() != null) {
				result = result + msecsFilterMove;
			}
			result = result + (new Double(tmpItemSource.getExpositionTime() * 1000)).longValue();

		} else {
			// Unknown item
			System.out.println("Unknown item. class::" + item.getClass().getName());

		}
		return result;
	}
}
