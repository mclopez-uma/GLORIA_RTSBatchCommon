package eu.gloria.rt.worker.scheduler.iterator;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import eu.gloria.rt.catalogue.CatalogueTools;
import eu.gloria.rt.catalogue.Observer;
import eu.gloria.rt.catalogue.RTSInfo;
import eu.gloria.rt.db.scheduler.SchTimeFrame;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.worker.scheduler.context.SchedulerContext;
import eu.gloria.rt.worker.scheduler.context.SchedulerLog;
import eu.gloria.rt.worker.scheduler.interfaces.DataBaseInterface;

public class IteratorSunshineNightTime extends TimeFrameIterator {
	protected SchedulerContext schContext;
	private Iterator<SchTimeFrame> iterator;
	private SchedulerLog log;

	public IteratorSunshineNightTime(SchedulerContext sc, DataBaseInterface db, int priority) throws RTException {
		this.log = sc.logger(getClass());
		this.schContext = sc;
		List<SchTimeFrame> mergeSchTimeFrames = db.getSlotsWithPriority(initScheduling(), priority);
		this.iterator = mergeSchTimeFrames.iterator();
	}
	
	public Timestamp initScheduling() {
		Observer observer = schContext.getObserver();
		boolean isNightTelescope = schContext.getIsNightTelescope();
		int[] timesLimit = schContext.getTimeLimitToday();
		
		try {
			long now = System.currentTimeMillis();
			RTSInfo sunToday  = CatalogueTools.getSunRTSInfo(observer, new Date());
			RTSInfo sunTomorr = CatalogueTools.getSunRTSInfo(observer, new Date(now +     24 * 60 * 60 * 1000));
			RTSInfo sunAftTom = CatalogueTools.getSunRTSInfo(observer, new Date(now + 2 * 24 * 60 * 60 * 1000));
			
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTimeInMillis(now);
			gc.set(Calendar.SECOND, timesLimit[2]);
			gc.set(Calendar.MINUTE, timesLimit[1]);
			gc.set(Calendar.HOUR_OF_DAY, timesLimit[0]);
			long limitT = gc.getTimeInMillis();
			
			if (isNightTelescope) {
				if (limitT < sunToday.getSet().getTime()) {
					// Nocturno con T antes del anochecer (madrugada o dia)
					if (now < limitT) {
						// Antes de T
						return new Timestamp(sunToday.getSet().getTime());
					}else{
						// Despues de T
						return new Timestamp(sunTomorr.getSet().getTime());
					}
				}else{
					// Nocturno con T después del anochecer (noche)
					if (now < limitT) {
						// Antes de T
						return new Timestamp(sunTomorr.getSet().getTime());
					}else{
						// Despues de T
						return new Timestamp(sunAftTom.getSet().getTime());
					}
				}
			}else{
				if (limitT < sunToday.getRise().getTime()) {
					// Diurno con T antes del amanecer (madrugada)
					if (now < limitT) {
						// Antes de T
						return new Timestamp(sunToday.getRise().getTime());
					}else{
						// Despues de T
						return new Timestamp(sunTomorr.getRise().getTime());
					}
				}else{
					// Diurno con T después del amanecer (dia o noche)
					if (now < limitT) {
						// Antes de T
						return new Timestamp(sunTomorr.getRise().getTime());
					}else{
						// Despues de T
						return new Timestamp(sunAftTom.getRise().getTime());
					}
				}
			}
		} catch (Exception e) {
			return new Timestamp(new Date().getTime());
		}
	}

	@Override
	public boolean hasNext() {
		boolean hasNext = iterator.hasNext();
		log.debug(String.format(schContext.language.getString("IterSunNightTime_Has_next"), hasNext));
		return hasNext;
	}

	@Override
	public TimeFrame next() {
		SchTimeFrame stf = iterator.next();
		TimeFrame tf = new TimeFrame();
		if (stf.getDateIni().getTime() < System.currentTimeMillis()) {
			tf.setInit(new Date());
		}else{
			tf.setInit(stf.getDateIni());
		}
		tf.setEnd(stf.getDateEnd());
		log.debug(String.format(schContext.language.getString("IterSunNightTime_Next"), tf.toString()));
		return tf;
	}

	@Override
	public void remove() {
		log.debug(schContext.language.getString("IterSunNightTime_Remove"));
	}
}
