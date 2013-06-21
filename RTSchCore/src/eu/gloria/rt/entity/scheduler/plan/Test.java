package eu.gloria.rt.entity.scheduler.plan;

import java.io.File;
import java.math.BigInteger;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import eu.gloria.rt.entity.scheduler.plan.Binning;
import eu.gloria.rt.entity.scheduler.plan.CameraSettings;
import eu.gloria.rt.entity.scheduler.plan.Constraints;
import eu.gloria.rt.entity.scheduler.plan.Expose;
import eu.gloria.rt.entity.scheduler.plan.FilterType;
import eu.gloria.rt.entity.scheduler.plan.Filters;
import eu.gloria.rt.entity.scheduler.plan.Instructions;
import eu.gloria.rt.entity.scheduler.plan.Loop;
import eu.gloria.rt.entity.scheduler.plan.Mode;
import eu.gloria.rt.entity.scheduler.plan.Plan;
import eu.gloria.rt.entity.scheduler.plan.Target;
import eu.gloria.rt.entity.scheduler.plan.Targets;
import eu.gloria.rt.entity.scheduler.plan.TrackingRateType;

public class Test {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		generateXml();
		//readXml();
		
	}
	
	private static void readXml()  throws Exception{
		
		File schemaFile = new File("c:\\repositorio\\workspace\\eclipsews\\RTD_RTS2\\xml\\rts2_error_management.xsd");
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(schemaFile);

		JAXBContext context = JAXBContext.newInstance(Plan.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setSchema(schema);
		File file = new File("c:\\repositorio\\workspace\\eclipsews\\RTD_RTS2\\config\\BOOTES02_rtd_rts2_error_management.xml");
		Plan root = (Plan) unmarshaller.unmarshal(file);
		
		int x = 0;
		x++;
	}
	
	private static void generateXml() throws Exception{
		
		Target target = new Target();
		target.setObjName("Jupiter");
		
		Constraints constraints = new Constraints();
		Filters filters = new Filters();
		constraints.setFilters(filters);
		constraints.getFilters().getFilter().add(FilterType.BESSEL_R);
		constraints.getFilters().getFilter().add(FilterType.JOHNSON_R);
		constraints.tracking = TrackingRateType.DRIVE_SIDEREAL;
		constraints.moonAltitude = 1.1;
		constraints.targetAltitude = 30.0;
		constraints.moonDistance = 20.0;
		constraints.setMode(Mode.BATCH);
		constraints.setTracking(TrackingRateType.DRIVE_SIDEREAL);
		
		Targets targets = new Targets();
		targets.getTarget().add(target);
		targets.getTarget().add(target);
		constraints.setTargets(targets);
		

		
		Binning binning = new Binning();
		binning.setBinX(new BigInteger("1"));
		binning.setBinY(new BigInteger("1"));
		
		CameraSettings camSet = new CameraSettings();
		camSet.setBinning(binning);
		
		Expose exp = new Expose();
		exp.setFilter(FilterType.BESSEL_R);
		//exp.setRepeatCount(new BigInteger("4"));
		exp.setExpositionTime(22);
		exp.setRepeatDuration(2.2);
		
		Loop loop1 = new Loop();
		loop1.setRepeatCount(new BigInteger("4"));
		loop1.getTargetOrCameraSettingsOrLoop().add(target);
		loop1.getTargetOrCameraSettingsOrLoop().add(camSet);
		loop1.getTargetOrCameraSettingsOrLoop().add(exp);
		loop1.getTargetOrCameraSettingsOrLoop().add(exp);
		
		Instructions instructions = new Instructions();
		instructions.getTargetOrCameraSettingsOrLoop().add(target);
		instructions.getTargetOrCameraSettingsOrLoop().add(camSet);
		instructions.getTargetOrCameraSettingsOrLoop().add(exp);
		instructions.getTargetOrCameraSettingsOrLoop().add(exp);
		instructions.getTargetOrCameraSettingsOrLoop().add(loop1);
		
		Plan root = new Plan();
		root.setConstraints(constraints);
		root.setInstructions(instructions);
		
		JAXBContext ctx = JAXBContext.newInstance(Plan.class);
		
		File schemaFile = new File("C:\\repositorio\\workspace\\eclipsews\\RTCore\\xml\\gloria_rti_plan.xsd");
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(schemaFile);

		File outputFile = new File("C:\\repositorio\\workspace\\eclipsews\\RTSchCore\\xml\\example01.xml");
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setSchema(schema);
		marshaller.marshal(root, outputFile);
	}

}
