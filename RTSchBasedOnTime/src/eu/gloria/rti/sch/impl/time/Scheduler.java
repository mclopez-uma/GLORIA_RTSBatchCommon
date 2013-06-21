package eu.gloria.rti.sch.impl.time;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import eu.gloria.rt.catalogue.Catalogue;
import eu.gloria.rt.catalogue.ObjInfo;
import eu.gloria.rt.catalogue.Observer;
import eu.gloria.rt.entity.scheduler.PlanCancelationInfo;
import eu.gloria.rt.entity.scheduler.PlanInfo;
import eu.gloria.rt.entity.scheduler.PlanOfferInfo;
import eu.gloria.rt.entity.scheduler.PlanSearchFilter;
import eu.gloria.rti.sch.core.plan.constraint.Constraints;
import eu.gloria.rti.sch.core.ObservingPlan;
import eu.gloria.rti.sch.core.TimeFrame;
import eu.gloria.rti.sch.core.TimeFrameIterator;
import eu.gloria.rt.exception.RTException;

public class Scheduler implements eu.gloria.rti.sch.core.Scheduler {
	
	public boolean logs = true;
	
	private int days = 3;
	

	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		List<String> lista = new ArrayList<String>();
		lista.add("ll");
		Scheduler sch = new Scheduler();
		//sch.advertise(lista);
		
	}
	

	@Override
	public List<String> advertise(XMLGregorianCalendar deadline, List<String> plans) throws RTException {
		
		if (logs) System.out.println("[" + new Date() + "]::Scheduler::Advertise.START");
		
		ArrayList<String> result = new ArrayList<String>();
		
		/*Observer observer = new Observer();
		observer.setLatitude(37.2);
		observer.setLongitude(-7.216667);
		Catalogue catalogue = new Catalogue(observer.getLongitude(), observer.getLatitude());
		
		ObjInfo obj = catalogue.getObject("HIP42662");
		
		if (logs) System.out.println("[" + new Date() + "]::Scheduler::Advertise. Obj= " + obj);
		
		return result;*/
		
		Observer observer = new Observer();
		observer.setLatitude(37.2);
		observer.setLongitude(-7.216667);
		
		PlanningTimeFrameLocator planningTimeFrameLocator = new PlanningTimeFrameLocator(observer, days, true);
		AstronomicalTimeFrameLocator astronomicalTimeFrameLocator = new AstronomicalTimeFrameLocator(observer, days, false);
		
		for (int x = 0; x < plans.size(); x++){
			
			String file = "/mnt/default/worker/" + "TODO";
			String xsdFile = "/mnt/default/worker/gloria_rti_plan.xsd";
			ObservingPlan plan = new ObservingPlan(file, xsdFile);
			
			Constraints constraints = plan.getConstraints();
			TimeFrameIterator timeFrameIterator =  planningTimeFrameLocator.getAvailableTimeFrameIterator(plan.getPredictedExecTime());
			
			TimeFrame timeFrame = null;
			if (logs) System.out.println("[" + new Date() + "]::Scheduler::Advertise.BEGIN.Plan:" + x + ", fileName=" + file);
			boolean accepted = false;
			while (timeFrameIterator.hasNext()){
				timeFrame = timeFrameIterator.next();
				if (logs) System.out.println("[" + new Date() + "]::Scheduler::Advertise.Plan:" + x + ", TimeFrame:" + timeFrame.toString());
				timeFrame = astronomicalTimeFrameLocator.getValidTimeFrame(constraints, timeFrame);
				if (timeFrame != null){
					accepted = true;
					break;
				}
			}
			
			if (accepted){
				if (logs) System.out.println("[" + new Date() + "]::Scheduler::Advertise.ACCEPTED.Plan:" + x + ", TimeFrame:" + timeFrame.toString());
				result.add(file);
			}else{
				if (logs) System.out.println("[" + new Date() + "]::Scheduler::Advertise.REJECTED.Plan:" + x);
			}
			
			if (logs) System.out.println("[" + new Date() + "]::Scheduler::Advertise.END.Plan:" + x);
			
			
			
			if (x + 1 < plans.size()){
			
				if (logs) System.out.println("[" + new Date() + "]::Scheduler::Advertise.END. Sleeping 3 minutes");
				try {
					Thread.sleep(120000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (logs) System.out.println("[" + new Date() + "]::Scheduler::Advertise.END. wake up!!!");
			
			}
		}
		
		
		
		return result;
	}

	@Override
	public List<PlanOfferInfo> offer(List<String> plans) throws RTException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PlanCancelationInfo> cancel(List<String> planUuids)
			throws RTException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PlanInfo> searchByUuid(List<String> planUuids)
			throws RTException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PlanInfo> searchByFilter(PlanSearchFilter filter)
			throws RTException {
		// TODO Auto-generated method stub
		return null;
	}

}
