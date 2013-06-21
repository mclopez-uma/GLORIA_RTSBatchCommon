package eu.gloria.rti.sch.impl.time;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import eu.gloria.rt.catalogue.CatalogueTools;
import eu.gloria.rt.catalogue.Observer;
import eu.gloria.rt.catalogue.RTSInfo;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rti.sch.core.TimeFrame;
import eu.gloria.tools.time.DateTools;

public class TimeFrameIteratorForNightObservationTime extends eu.gloria.rti.sch.core.TimeFrameIterator {
	
	private TimeFrame timeFrame;
	
	private GregorianCalendar calendar;
	private Date initDate;
	private int days;
	private RTSInfo sun;
	private int daysProvided;
	private Observer observer;
	
	public TimeFrameIteratorForNightObservationTime(Observer observer, Date initDate, int days) throws RTException{
		
		try{
			
			this.days = days;
			this.daysProvided = 0;
			this.initDate = initDate;
			this.calendar = new GregorianCalendar();
			this.timeFrame = new TimeFrame();
			this.observer = observer;
			
			this.sun = CatalogueTools.getSunRTSInfo(observer, initDate);
			
			System.out.println("SUN RTS: " + sun.toString());
			
			if (initDate.compareTo(this.sun.getSet()) >= 0){ //after set -> good time
				this.calendar.setTime(initDate);
			}else if (initDate.compareTo(this.sun.getRise()) >= 0){ //after rise -> Bad time
				this.calendar.setTime(this.sun.getSet()); //before raise -> good time
			}else{
				this.calendar.setTime(this.initDate);
			}
			
			
		}catch(Exception ex){
			throw new RTException(ex);
		}
		
	}
	
	@Override
	public boolean hasNext() {
		return daysProvided < days;
	}
	
	
	@Override
	public void remove() {
	}
	
	@Override
	public TimeFrame next() {
		
		try{
			
			this.sun = CatalogueTools.getSunRTSInfo(observer, this.calendar.getTime());
			
			if (calendar.getTime().compareTo(sun.getRise()) <= 0){ //before raise -> [calendar.time, sun.rise]
				
				this.timeFrame.setInit(calendar.getTime());
				this.timeFrame.setEnd(sun.getRise());
				
				this.calendar.setTime(sun.getSet());
				
			}else{ //Second part of the day -> [sun.set, (enOfDay || nextday.sun.rise)]
				
				daysProvided++;
				
				this.timeFrame.setInit(sun.getSet());
				
				if (daysProvided >= days){ //up to 23:59:59 of current day
					
					String dayPrefixYYYYMMDD = null;
					try {
						dayPrefixYYYYMMDD = DateTools.getDate(this.calendar.getTime(), "yyyyMMdd");
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String endOfTheDay = dayPrefixYYYYMMDD + "235959";
					try {
						this.timeFrame.setEnd(DateTools.getDate(endOfTheDay, "yyyyMMddHHmmss"));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Date today = DateTools.trunk(this.calendar.getTime(), "yyyyMMdd");
					Date tomorrow = DateTools.increment(today, Calendar.DATE, 1);
					this.calendar.setTime(tomorrow); //It will never be used, because there is not next TimeFrame
					
				}else { //up to the sun rise of the next day
					
					//Look for the nextday.sun.rise
					this.calendar.add(Calendar.DATE, 1);
					this.sun = CatalogueTools.getSunRTSInfo(observer, this.calendar.getTime());
					this.timeFrame.setEnd(sun.getRise());
					
					this.calendar.setTime(sun.getSet());
				}
				
			}
			
			return this.timeFrame;
			
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
		

		
	}

}
