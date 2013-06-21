package eu.gloria.rti.sch.core;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import eu.gloria.rt.entity.scheduler.PlanCancelationInfo;
import eu.gloria.rt.entity.scheduler.PlanInfo;
import eu.gloria.rt.entity.scheduler.PlanOfferInfo;
import eu.gloria.rt.entity.scheduler.PlanSearchFilter;
import eu.gloria.rt.exception.RTException;


/**
 * Scheduler - Interface.
 * @author jcabello
 *
 */
public interface Scheduler {
	
	public List<String> advertise(XMLGregorianCalendar deadline, List<String> plans) throws RTException;
	
	public List<PlanOfferInfo> offer(List<String> plans)  throws RTException;
	
	public List<PlanCancelationInfo> cancel(List<String> planUuids)  throws RTException;
	
	public List<PlanInfo> searchByUuid(List<String> planUuids)  throws RTException;
	
	public List<PlanInfo> searchByFilter(PlanSearchFilter filter)  throws RTException;
	

}
