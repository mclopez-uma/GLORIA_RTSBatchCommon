package eu.gloria.rt.worker.advertisement.validator;

import java.util.Date;

import eu.gloria.rt.catalogue.Observer;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.RTSchException;
import eu.gloria.rti.sch.core.TimeFrame;
import eu.gloria.rti.sch.core.TimeFrameIterator;

public class PlanningTimeFrameLocator implements
		eu.gloria.rti.sch.core.PlanningTimeFrameLocator {
	
	private int days;
	private boolean nightTime;
	private Observer observer;
	private boolean verbose;
	private int opPerDay;
	
	public PlanningTimeFrameLocator(Observer observer, int days, boolean nightTime, boolean verbose, int opPerDay) throws RTException{
		this.days = days;
		this.nightTime = nightTime;
		if (!nightTime) throw new RTException("Unsupported SunShine mode.");
		this.observer = observer;
		this.verbose = verbose;
		this.opPerDay = opPerDay;
	}

	@Override
	public TimeFrameIterator getAvailableTimeFrameIterator(long seconds)
			throws RTException {
		try{
			
			/*Date initDate = DateTools.trunk(new Date(), "yyyyMMdd");
			Date endDate = DateTools.increment(initDate, Calendar.DATE, days);
			return new eu.gloria.rti.sch.impl.time.TimeFrameIterator(initDate, endDate, Calendar.DATE, 1);*/
			
			return new TimeFrameIteratorForNightObservationTime(observer, new Date(), days, true, opPerDay);
			
		}catch(Exception ex){
			throw new RTException(ex);
		}
		
	}

	@Override
	public boolean isAvailableTimeFrame(TimeFrame timeFrame)
			throws RTSchException {
		return false;
	}
	


}
