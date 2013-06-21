package eu.gloria.rti.sch.impl.time;

import java.util.Calendar;
import java.util.Date;

import org.apache.catalina.util.DateTool;

import eu.gloria.rt.catalogue.Observer;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.RTSchException;
import eu.gloria.rti.sch.core.TimeFrame;
import eu.gloria.rti.sch.core.TimeFrameIterator;
import eu.gloria.tools.time.DateTools;

public class PlanningTimeFrameLocator implements
		eu.gloria.rti.sch.core.PlanningTimeFrameLocator {
	
	private int days;
	private boolean nightTime;
	private Observer observer;
	
	public PlanningTimeFrameLocator(Observer observer, int days, boolean nightTime) throws RTException{
		this.days = days;
		this.nightTime = nightTime;
		if (!nightTime) throw new RTException("Unsupported SunShine mode.");
		this.observer = observer;
	}

	@Override
	public TimeFrameIterator getAvailableTimeFrameIterator(long seconds)
			throws RTException {
		try{
			
			/*Date initDate = DateTools.trunk(new Date(), "yyyyMMdd");
			Date endDate = DateTools.increment(initDate, Calendar.DATE, days);
			return new eu.gloria.rti.sch.impl.time.TimeFrameIterator(initDate, endDate, Calendar.DATE, 1);*/
			
			return new TimeFrameIteratorForFullNightTime(observer, new Date(), days);
			
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
