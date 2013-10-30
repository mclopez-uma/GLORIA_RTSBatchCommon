package eu.gloria.rt.worker.advertisement.validator;

import java.text.ParseException;
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
import eu.gloria.tools.time.DateTools;

public class TimeFrameIteratorForNightObservationTime extends
		eu.gloria.rti.sch.core.TimeFrameIterator {

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
	private int sessionMaxOpCountPerUser;
	private long sessionMaxSharedTimePerUser;
	private int riseOffsetSecs;
	private int setOffsetSecs;
	private String user;

	public TimeFrameIteratorForNightObservationTime(Observer observer, Date initDate, int days, boolean verbose, int sessionMaxOpCount, long sessionMaxSharedTime, int riseOffsetSecs, int setOffsetSecs, String user, int sessionMaxOpCountPerUser, long sessionMaxSharedTimePerUser) throws RTException {

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
			this.riseOffsetSecs = riseOffsetSecs;
			this.setOffsetSecs = setOffsetSecs;
			this.user = user;
			this.sessionMaxOpCountPerUser = sessionMaxOpCountPerUser;
			this.sessionMaxSharedTimePerUser = sessionMaxSharedTimePerUser;
			
			if (verbose) LogUtil.info(this, "constructor. user=" + this.user);
			if (verbose) LogUtil.info(this, "constructor. sessionMaxOpCountPerUser=" + this.sessionMaxOpCountPerUser);
			if (verbose) LogUtil.info(this, "constructor. sessionMaxSharedTimePerUser=" + this.sessionMaxSharedTimePerUser);

			this.sun = CatalogueTools.getSunRTSInfo(observer, initDate, riseOffsetSecs, setOffsetSecs);

			if (verbose) LogUtil.info(this, "Constructor: SUN RTS:" + sun.toString());

			if (initDate.compareTo(this.sun.getSet()) >= 0) { // after set -> good time
				this.calendar.setTime(initDate);
			} else if (initDate.compareTo(this.sun.getRise()) >= 0) { // after rise -> Bad time
				this.calendar.setTime(this.sun.getSet()); // before raise -> good time
			} else {
				this.calendar.setTime(this.initDate);
			}

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
	public void remove() {
	}

	@Override
	public TimeFrame next() {

		return this.next;
	}

	private TimeFrame calculateNext() {

		try {

			this.sun = CatalogueTools.getSunRTSInfo(observer, this.calendar.getTime(), this.riseOffsetSecs, this.setOffsetSecs);

			if (calendar.getTime().compareTo(sun.getRise()) <= 0) { // before raise -> [calendar.time, sun.rise]

				this.timeFrame.setInit(calendar.getTime());
				this.timeFrame.setEnd(sun.getRise());

				this.calendar.setTime(sun.getSet());

			} else { // Second part of the day -> [sun.set, (enOfDay || nextday.sun.rise)]

				daysProvided++;

				this.timeFrame.setInit(calendar.getTime());

				if (daysProvided >= days) { // up to 23:59:59 of current day

					String dayPrefixYYYYMMDD = null;
					try {
						dayPrefixYYYYMMDD = DateTools.getDate(
								this.calendar.getTime(), "yyyyMMdd");
					} catch (ParseException e) {
						e.printStackTrace();
					}
					String endOfTheDay = dayPrefixYYYYMMDD + "235959";
					try {
						this.timeFrame.setEnd(DateTools.getDate(endOfTheDay, "yyyyMMddHHmmss"));
					} catch (ParseException e) {
						e.printStackTrace();
					}

					Date today = DateTools.trunk(this.calendar.getTime(), "yyyyMMdd");
					Date tomorrow = DateTools.increment(today, Calendar.DATE, 1);
					this.calendar.setTime(tomorrow); // It will never be used,  because there is not  next TimeFrame

				} else { // up to the sun rise of the next day

					// Look for the nextday.sun.rise
					this.calendar.add(Calendar.DATE, 1);
					this.sun = CatalogueTools.getSunRTSInfo(observer, this.calendar.getTime(), this.riseOffsetSecs, this.setOffsetSecs);
					this.timeFrame.setEnd(sun.getRise());

					this.calendar.setTime(sun.getSet());
				}

			}
			
			if (verbose) LogUtil.info(this, "Potential TimeFrame: " + this.timeFrame);
			if (verbose) LogUtil.info(this, "Next iteration initial time: " + this.calendar.getTime());

			// Check D.B.
			TimeFrame observationSession = getObservationSessionTimeFrame(this.timeFrame.getInit());

			if (verbose) LogUtil.info(this, "NextTimeFrame: " + this.timeFrame);
			if (verbose) LogUtil.info(this, "NextTimeFrame belongs to the ObservationSession: " + observationSession);

			ObservingPlanManager manager = new ObservingPlanManager();
			if (sessionMaxSharedTime > 0){
				
				long observingPlanSharedTimeForObservationSession = 1 + manager.getObservationTimeByScheduleDate(null, observationSession.getInit(), observationSession.getEnd());
				if (verbose) LogUtil.info(this, "Observing Plans Shared Time for the ObservationSession: " + observingPlanSharedTimeForObservationSession);
				if (observingPlanSharedTimeForObservationSession > sessionMaxSharedTime) {
					return null;
				}
				
			}else if (sessionMaxOpCount > 0){
				
				long observingPlanCountForObservationSession = 1 + manager.getCountByScheduleDate(null, observationSession.getInit(), observationSession.getEnd());
				if (verbose) LogUtil.info(this, "Observing Plans Count for the ObservationSession: " + observingPlanCountForObservationSession);
				if (observingPlanCountForObservationSession > sessionMaxOpCount) {
					return null;
				}
			}
			
			if (sessionMaxSharedTimePerUser > 0 && user != null){
				
				long observingPlanSharedTimeForObservationSession = 1 +manager.getObservationTimeByScheduleDate(null, observationSession.getInit(), observationSession.getEnd(), user);
				if (verbose) LogUtil.info(this, "Observing Plans Shared Time for the user[" + user + "]. ObservationSession: " + observingPlanSharedTimeForObservationSession);
				if (observingPlanSharedTimeForObservationSession > sessionMaxSharedTimePerUser) {
					return null;
				}
				
			}else if (sessionMaxOpCountPerUser > 0 && user != null){
				
				long observingPlanCountForObservationSession = 1 + manager.getCountByScheduleDate(null, observationSession.getInit(), observationSession.getEnd(), user);
				if (verbose) LogUtil.info(this, "Observing Plans Count for the user[" + user + "]. ObservationSession: " + observingPlanCountForObservationSession);
				if (observingPlanCountForObservationSession > sessionMaxOpCountPerUser) {
					return null;
				}
				
			}
			
			return this.timeFrame;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	private TimeFrame getObservationSessionTimeFrame(Date date)
			throws RTException {

		try {

			TimeFrame result = null;

			RTSInfo sunRTS = CatalogueTools.getSunRTSInfo(observer, date, this.riseOffsetSecs, this.setOffsetSecs);

			if (date.compareTo(sunRTS.getSet()) >= 0) { // after sun set ->
														// good time

				result = new TimeFrame();

				// INIT
				result.setInit(sunRTS.getSet());

				// END
				date = DateTools.trunk(date, "yyyyMMdd");
				Date tomorrow = DateTools.increment(date, Calendar.DATE, 1);
				sunRTS = CatalogueTools.getSunRTSInfo(observer, tomorrow, this.riseOffsetSecs, this.setOffsetSecs);
				result.setEnd(sunRTS.getRise());

			} else if (date.compareTo(sunRTS.getRise()) <= 0) { // before
																// sun rise
																// -> good
																// time

				result = new TimeFrame();

				// INIT
				date = DateTools.trunk(date, "yyyyMMdd");
				Date yesterday = DateTools.increment(date, Calendar.DATE, -1);
				sunRTS = CatalogueTools.getSunRTSInfo(observer, yesterday, this.riseOffsetSecs, this.setOffsetSecs);
				result.setInit(sunRTS.getSet());

				// END
				result.setEnd(sunRTS.getRise());
			}

			return result;

		} catch (Exception ex) {
			throw new RTException(ex);
		}

	}
	
}
