package eu.gloria.rt.worker.advertisement.validator;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import eu.gloria.rt.catalogue.CatalogueTools;
import eu.gloria.rt.catalogue.Observer;
import eu.gloria.rt.catalogue.RTSInfo;
import eu.gloria.rt.db.scheduler.ObservingPlanManager;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rti.sch.core.TimeFrame;
import eu.gloria.tools.log.LogUtil;

public class TimeFrameIteratorForSunshineObservationTime extends eu.gloria.rti.sch.core.TimeFrameIterator {
	
	private TimeFrame timeFrame;

	private GregorianCalendar calendar;
	private Date initDate;
	private int days;
	private RTSInfo sun;
	private int daysProvided;
	private Observer observer;
	private TimeFrame next;
	private boolean verbose;
	private int sessionMaxOpCount;
	private long sessionMaxSharedTime;

	public TimeFrameIteratorForSunshineObservationTime(Observer observer, Date initDate, int days, boolean verbose, int sessionMaxOpCount, long sessionMaxSharedTime) throws RTException {

		try {

			this.days = days;
			this.daysProvided = 0;
			this.initDate = initDate;
			this.calendar = new GregorianCalendar();
			this.timeFrame = new TimeFrame();
			this.observer = observer;
			this.next = null;
			this.verbose = verbose;
			this.sessionMaxOpCount = sessionMaxOpCount;
			this.sessionMaxSharedTime = sessionMaxSharedTime;

			this.sun = CatalogueTools.getSunRTSInfo(observer, initDate);

			if (verbose) LogUtil.info(this, "Constructor: SUN RTS:" + sun.toString());

			if (initDate.compareTo(this.sun.getSet()) >= 0) { // today after set -> wrong time, put sunrise of next day
				this.calendar.setTime(initDate);
				this.calendar.add(Calendar.DATE, 1);
				this.sun = CatalogueTools.getSunRTSInfo(observer, this.calendar.getTime());
				this.calendar.setTime(this.sun.getRise());
			} else if (initDate.compareTo(this.sun.getRise()) >= 0) { // after rise -> good time
				this.calendar.setTime(this.initDate); 
			} else { //Today before sunrise -> Put today-sunrise
				this.calendar.setTime(this.sun.getRise());
			}

		} catch (Exception ex) {
			throw new RTException(ex);
		}

	}
	
	private TimeFrame calculateNext() {

		try {
			
			daysProvided++;
			
			this.sun = CatalogueTools.getSunRTSInfo(observer, this.calendar.getTime());
			RTSInfo currentRTSInfo = this.sun;
			
			this.timeFrame.setInit(calendar.getTime());
			this.timeFrame.setEnd(sun.getSet());
			
			this.calendar.add(Calendar.DATE, 1);
			this.sun = CatalogueTools.getSunRTSInfo(observer, this.calendar.getTime());

			this.calendar.setTime(sun.getRise());
			
			if (verbose) LogUtil.info(this, "Potential TimeFrame: " + this.timeFrame);
			if (verbose) LogUtil.info(this, "Next iteration initial time: " + this.calendar.getTime());

			// Check D.B.
			TimeFrame observationSession = getObservationSessionTimeFrame(this.timeFrame.getInit());

			if (verbose) LogUtil.info(this, "NextTimeFrame: " + this.timeFrame);
			if (verbose) LogUtil.info(this, "NextTimeFrame belongs to the ObservationSession: " + observationSession);

			ObservingPlanManager manager = new ObservingPlanManager();
			if (sessionMaxSharedTime > 0){
				
				long observingPlanSharedTimeForObservationSession = manager.getObservationTimeByScheduleDate(null, observationSession.getInit(), observationSession.getEnd());
				if (verbose) LogUtil.info(this, "Observing Plans Shared Time for the ObservationSession: " + observingPlanSharedTimeForObservationSession + " [" + observationSession.getInit() + "->" +  observationSession.getEnd() + "]");
				if (observingPlanSharedTimeForObservationSession < sessionMaxSharedTime) {
					return this.timeFrame;
				} 
				
			}else if (sessionMaxOpCount > 0){
				
				long observingPlanCountForObservationSession = manager.getCountByScheduleDate(null, observationSession.getInit(), observationSession.getEnd());
				if (verbose) LogUtil.info(this, "Observing Plans Count for the ObservationSession: " + observingPlanCountForObservationSession + " [" + observationSession.getInit() + "->" +  observationSession.getEnd() + "]");
				if (observingPlanCountForObservationSession < sessionMaxOpCount) {
					return this.timeFrame;
				}
			}
			
			return null;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}
	
	private TimeFrame getObservationSessionTimeFrame(Date date)
			throws RTException {

		try {
			
			RTSInfo sunRTS = CatalogueTools.getSunRTSInfo(observer, date);

			TimeFrame result = new TimeFrame();
			
			// INIT
			result.setInit(sunRTS.getRise());
			
			// END
			result.setEnd(sunRTS.getSet());

			return result;

		} catch (Exception ex) {
			throw new RTException(ex);
		}

	}

	@Override
	public boolean hasNext() {
		
		this.next = null;

		while (daysProvided < days && next == null) {
			next = calculateNext();
		}

		return (next != null);
	}

	@Override
	public TimeFrame next() {
		
		return this.next;
	}

	@Override
	public void remove() {
	}

}
