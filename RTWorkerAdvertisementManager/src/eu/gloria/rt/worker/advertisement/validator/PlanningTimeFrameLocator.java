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
	private int sessionMaxOpCount;
	private long sessionMaxSharedTime;
	private Date initDate;
	private int riseOffsetSecs;
	private int setOffsetSecs;
	private int sessionMaxOpCountPerUser;
	private long sessionMaxSharedTimePerUser;
	private String user;
	
	public PlanningTimeFrameLocator(Observer observer, Date initDate, int days, boolean nightTime, boolean verbose, int sessionMaxOpCount, long sessionMaxSharedTime, int riseOffsetSecs, int setOffsetSecs, String user, int sessionMaxOpCountPerUser, long sessionMaxSharedTimePerUser) throws RTException{
		this.days = days;
		this.nightTime = nightTime;
//		if (!nightTime) throw new RTException("Unsupported SunShine mode.");
		this.observer = observer;
		this.verbose = verbose;
		this.sessionMaxOpCount = sessionMaxOpCount;
		this.sessionMaxSharedTime = sessionMaxSharedTime;
		this.initDate = initDate;
		this.riseOffsetSecs = riseOffsetSecs;
		this.setOffsetSecs = setOffsetSecs;
		this.user = user;
		this.sessionMaxOpCountPerUser = sessionMaxOpCountPerUser;
		this.sessionMaxSharedTimePerUser = sessionMaxSharedTimePerUser;
	}
	
	

	@Override
	public TimeFrameIterator getAvailableTimeFrameIterator(long seconds)
			throws RTException {
		try{
			
			/*Date initDate = DateTools.trunk(new Date(), "yyyyMMdd");
			Date endDate = DateTools.increment(initDate, Calendar.DATE, days);
			return new eu.gloria.rti.sch.impl.time.TimeFrameIterator(initDate, endDate, Calendar.DATE, 1);*/
			
			if (nightTime) {
				return new TimeFrameIteratorForNightObservationTime(observer, this.initDate, days, true, this.sessionMaxOpCount, this.sessionMaxSharedTime, this.riseOffsetSecs, this.setOffsetSecs, this.user, this.sessionMaxOpCountPerUser, this.sessionMaxSharedTimePerUser);
			} else{
				return new TimeFrameIteratorForSunshineObservationTime(observer, this.initDate, days, true, this.sessionMaxOpCount, this.sessionMaxSharedTime, this.riseOffsetSecs, this.setOffsetSecs, this.user, this.sessionMaxOpCountPerUser, this.sessionMaxSharedTimePerUser);
			}
			
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
