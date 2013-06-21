package eu.gloria.rt.worker.advertisement.validator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import eu.gloria.rt.catalogue.Catalogue;
import eu.gloria.rt.catalogue.CatalogueTools;
import eu.gloria.rt.catalogue.Observer;
import eu.gloria.rt.catalogue.RTSInfo;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rti.sch.core.TimeFrame;
import eu.gloria.tools.time.DateTools;

public class TimeFrameIteratorForFullNightTime extends eu.gloria.rti.sch.core.TimeFrameIterator {
	
	private TimeFrame timeFrame;
	
	private GregorianCalendar calendar;
	private Date initDate;
	private int days;
	private RTSInfo sun;
	private int daysProvided;
	private Observer observer;
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		Observer observer = new Observer();
		observer.setLatitude(37.2);
		observer.setLongitude(-7.216667);
		
		TimeFrameIteratorForFullNightTime iterator = new TimeFrameIteratorForFullNightTime(observer, new Date(), 5);
		while (iterator.hasNext()){
			System.out.println(iterator.next().toString());
		}
		
	}
	
	public TimeFrameIteratorForFullNightTime(Observer observer, Date initDate, int days) throws RTException{
		
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
	public TimeFrame next() {
		
		try{
			
			this.sun = CatalogueTools.getSunRTSInfo(observer, this.calendar.getTime());
			
			if (calendar.getTime().compareTo(sun.getRise()) <= 0){ //before raise -> [calendar.time, sun.rise]
				
				this.timeFrame.setInit(calendar.getTime());
				this.timeFrame.setEnd(sun.getRise());
				
				this.calendar.setTime(sun.getSet());
				
			}else{ //Second part of the day -> [sun.rise, enOfDay]
				
				this.timeFrame.setInit(sun.getSet());
				
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
				
				this.calendar.setTime(tomorrow);
				
				daysProvided++;
			}
			
			return this.timeFrame;
			
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
		

		
	}

	@Override
	public void remove() {
}
	
	

}
