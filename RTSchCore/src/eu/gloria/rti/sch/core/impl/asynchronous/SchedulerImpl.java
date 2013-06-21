package eu.gloria.rti.sch.core.impl.asynchronous;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import eu.gloria.rt.entity.scheduler.PlanCancelationInfo;
import eu.gloria.rt.entity.scheduler.PlanInfo;
import eu.gloria.rt.entity.scheduler.PlanOfferInfo;
import eu.gloria.rt.entity.scheduler.PlanSearchFilter;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rti.sch.core.Scheduler;

public class SchedulerImpl  implements Scheduler  {

	@Override
	public List<String> advertise(XMLGregorianCalendar deadline,
			List<String> plans) throws RTException {
		
		java.util.Date deadlineDate = deadline.toGregorianCalendar().getTime();
		
		
		
		return null;
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
