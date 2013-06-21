package eu.gloria.rti.sch.impl.time;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import eu.gloria.rti.sch.core.TimeFrame;
import eu.gloria.tools.time.DateTools;

public class TimeFrameIterator extends eu.gloria.rti.sch.core.TimeFrameIterator {
	
	private TimeFrame timeFrame;
	
	private GregorianCalendar calendar;
	private int mode01_dayCount;
	private int mode01_days;
	
	private int mode02_unitType;
	private int mode02_amount;
	private Date mode02_initDate;
	private Date mode02_endDate;
	
	private int mode; //1 days number, 2 upto date.
	
	public TimeFrameIterator(int days){
		this.mode=1;
		this.mode01_days = days;
		this.mode01_dayCount = 0;
		this.calendar = new GregorianCalendar();
		this.calendar.setTime(new Date());
		
		this.timeFrame = new TimeFrame();
	}
	
	public TimeFrameIterator(Date initDate, Date endDate, int unitType, int amount){
		
		this.mode=2;
		this.mode02_unitType = unitType;
		this.mode02_amount = amount;
		this.mode02_initDate = initDate;
		this.mode02_endDate = endDate;
		this.calendar = new GregorianCalendar();
		this.calendar.setTime(mode02_initDate);
		
		this.timeFrame = new TimeFrame();
	}

	@Override
	public boolean hasNext() {
		
		if (mode == 1){ //number of days
			return (mode01_dayCount < mode01_days);
		}else{
			calendar.add(mode02_unitType, mode02_amount);
			boolean result = calendar.getTime().compareTo(mode02_endDate) <= 0;
			calendar.add(mode02_unitType, - mode02_amount);
			return result;
		}
		
	}

	@Override
	public TimeFrame next() {
		
		if (mode==1){ //number of days
			timeFrame.setInit(calendar.getTime());
			calendar.add(Calendar.HOUR, 24);
			timeFrame.setEnd(calendar.getTime());
			mode01_dayCount++;
			
		}else{
			
			timeFrame.setInit(calendar.getTime());
			calendar.add(mode02_unitType, mode02_amount);
			timeFrame.setEnd(calendar.getTime());
		}
		
		return timeFrame;
		
	}

	@Override
	public void remove() {
		//NOTHING
	}

}
