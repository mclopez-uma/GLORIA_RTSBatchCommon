package eu.gloria.rt.worker.scheduler.times;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import eu.gloria.rt.catalogue.CatalogueTools;
import eu.gloria.rt.catalogue.Observer;
import eu.gloria.rt.catalogue.RTSInfo;
import eu.gloria.rt.db.scheduler.SchTimeFrame;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.worker.scheduler.context.SchedulerContext;
import eu.gloria.rt.worker.scheduler.context.SchedulerLog;
import eu.gloria.rt.worker.scheduler.interfaces.ConfigUpgradeable;
import eu.gloria.rt.worker.scheduler.interfaces.DataBaseInterface;
import eu.gloria.rt.worker.scheduler.iterator.TimeFrame;
import eu.gloria.tools.log.LogUtil;
import eu.gloria.tools.time.DateTools;

public class GeneratorSlots extends Thread implements ConfigUpgradeable {
	private SchedulerContext schContext;
	private DataBaseInterface database;
	private SchedulerLog log;
	private List<SharedTimePortion> sharedTimePortions;
	private int daysFutures, daysScheduler;
	private boolean isNightTelescope;
	private Observer observer;

	public GeneratorSlots(SchedulerContext sc, DataBaseInterface db) {
		schContext = sc;
		database = db;
		log = sc.logger(getClass());
		sc.addConfigUpgradeable(this);
		updateConfig();
	}
	
	@Override
	public void updateConfig() {
		isNightTelescope   = schContext.getIsNightTelescope();
		sharedTimePortions = schContext.getSharedTimeFrame();
		daysScheduler      = schContext.getDaysScheduling();
		daysFutures        = schContext.getDaysFutures();
		observer           = schContext.getObserver();
	}
	
	public void run() {
		long timeToSleep = calculateTimeToSleep();
		while (true) {
			try {
				log.info(schContext.language.getString("GenSlots_Sleeping"));
				// Espero ese tiempo calculado
				Thread.sleep(timeToSleep);
			}catch (Exception e) {
				e.printStackTrace();
			}

			checkSlots();
			
			timeToSleep = daysScheduler * 24 * 60 * 60 * 1000;
		}
	}
	
	public void checkSlots() {
		
		log.debug(schContext.language.getString("GenSlots_Deleting_old_slots"));
		// Borro los huecos antiguos
		deleteOldsSlots();

		log.debug(schContext.language.getString("GenSlots_Creating_new_slots"));
		// Genero huecos nuevos
		try {
			generateSlots();
		} catch (Exception ex) {
			log.error("checkSlots(). Error: " + ex.getMessage());
		}
		
	}

	private long calculateTimeToSleep() {
		long timeToSleep = 0;
		// Calculo 3 días antes de la fecha del ultimo trozo planificable
		GregorianCalendar gCal = new GregorianCalendar();
		SchTimeFrame maxSchTimeFrame = database.getMaxSlotSchTimeFrame();
		if (maxSchTimeFrame != null) {
			gCal.setTime(maxSchTimeFrame.getDateEnd());
			gCal.add(Calendar.DAY_OF_YEAR, -1 * daysScheduler);
			log.debug(String.format(schContext.language.getString("GenSlots_Previous_n_days"), daysScheduler, gCal.getTime()));

			// Calculo los milisegundos hasta ese momento
			timeToSleep = gCal.getTime().getTime() - System.currentTimeMillis();
			log.debug(String.format(schContext.language.getString("GenSlots_Time_to_sleep"), timeToSleep));

			// Si es negativo...
			if (timeToSleep < 0) {
				log.debug(schContext.language.getString("GenSlots_Time_truncate_to_zero"));
				// ...queda en el pasado, pongo 0 para que no espere más
				timeToSleep = 0;
			}
		}
		return timeToSleep;
	}

	public void deleteOldsSlots() {
		database.deleteOldsSlots();
	}
	
	public void generateSlots() throws Exception {
		
		Date init;
		SchTimeFrame stf = database.getMaxSlotSchTimeFrame();
		
		Date lastGeneratedDate = null;
		if (stf != null){
			lastGeneratedDate = stf.getDateIni();
		}
		
		int daysToGenerate = getNeededDays(lastGeneratedDate);
		log.info("DaysToGenerate=" + daysToGenerate);
		
		if (daysToGenerate > 0){
			
			if (stf == null) {
				init = new Date();
			}else{
				/*GregorianCalendar gCal = new GregorianCalendar();
				gCal.setTime(stf.getDateIni());
				gCal.add(Calendar.DAY_OF_YEAR, 1);
				init = gCal.getTime();*/
				init = stf.getDateEnd();
				
			}
			
			SortedSet<TimeFrame> set = generatorTimesDays(daysToGenerate, init);
			database.saveSchedulerTimeFrames(mergeTimeFrame(set));
		}
	}
	
	private int getNeededDays(Date lastGeneratedDate) throws ParseException{
		
		int result = 0;
		Date now = new Date();
		
		if (lastGeneratedDate == null){ //Not generated date
			
			result = daysFutures; //All days
			
		}else{
			
			Date today = DateTools.trunk(now, "yyyyMMdd");
			lastGeneratedDate = DateTools.trunk(lastGeneratedDate, "yyyyMMdd");
			log.info("Today=" + today);
			log.info("lastGeneratedDate=" + lastGeneratedDate);
			
			if (today.compareTo(lastGeneratedDate) >= 0){ //now >= lastGeneratedDate -> all days, because the generated slots are past.
				result = daysFutures; //All days
				
			}else{
				
				long difms = lastGeneratedDate.getTime() - today.getTime();
				Long diffDays =  new Long(difms / (1000 * 60 * 60 * 24));
				result = (daysFutures - diffDays.intValue());
				if (result < 0) result = 0;
			}
		}
		
		return result;
	}

	private SortedSet<TimeFrame> generatorTimesDays(int days, Date dateInit) {
		SortedSet<TimeFrame> setResult = new TreeSet<TimeFrame>();
		List<TimeFrame> lstInit = findDatesValid(dateInit, days);
		
		for (TimeFrame timeFrame : lstInit) {
			TimeFrame[] tf = updateSunshineNightMoment(timeFrame);
			
			if (tf != null) {
				setResult.add(tf[0]);
				if (tf[1] != null) {
					setResult.add(tf[1]);
				}
			}
		}
		return setResult;
	}

	private List<TimeFrame> mergeTimeFrame(SortedSet<TimeFrame> set) {
		List<TimeFrame> result = new LinkedList<TimeFrame>();
		SortedSet<TimeFrame> auxSet = new TreeSet<TimeFrame>();
		
		if (set.size() == 0) {
			return result;
		}
		
		// The set has one or more elements
		TimeFrame first = set.first();
		set.remove(first);
		
		while (set.size() > 0) {
			TimeFrame second = set.first();
			set.remove(second);
			
			if (second.getInit().getTime() <= first.getEnd().getTime()) {
				first.getEnd().setTime(second.getEnd().getTime());
			}else{
				auxSet.add(first);
				first = second;
			}
		}
		auxSet.add(first);
		
		if (isNightTelescope) {
			while (auxSet.size() > 1) {
				TimeFrame tf = auxSet.first();
				auxSet.remove(tf);
				
				GregorianCalendar gc = new GregorianCalendar();
				gc.setTimeInMillis(tf.getInit().getTime());
				int dayIni = gc.get(Calendar.DAY_OF_YEAR);
				gc.setTimeInMillis(tf.getEnd().getTime());
				int dayEnd = gc.get(Calendar.DAY_OF_YEAR);
				
				gc.set(Calendar.HOUR_OF_DAY, 0);
				gc.set(Calendar.MINUTE, 0);
				gc.set(Calendar.SECOND, 0);
				gc.set(Calendar.MILLISECOND, 0);
				long midnight = gc.getTimeInMillis();
				
				if (dayIni == dayEnd) {
					result.add(tf);
				}else{
					TimeFrame tf1 = new TimeFrame();
					tf1.setInit(tf.getInit());
					tf1.setEnd(new Date(midnight));
					result.add(tf1);
					
					TimeFrame tf2 = new TimeFrame();
					tf2.setInit(new Date(midnight));
					tf2.setEnd(tf.getEnd());
					result.add(tf2);
				}
			}
			if (auxSet.size() == 1) {
				result.add(auxSet.first());
			}
		}else{
			for (TimeFrame tf : auxSet) {
				result.add(tf);
			}
		}
		return result;
	}
	
	private TimeFrame[] updateSunshineNightMoment(TimeFrame timeFrame) {
		try {
			RTSInfo sun = CatalogueTools.getSunRTSInfo(observer, timeFrame.getInit());
			Date sunRise = sun.getRise();
			Date sunSet = sun.getSet();
			
			TimeFrame[] tfRes = new TimeFrame[2];
			tfRes[0] = new TimeFrame();
			tfRes[1] = new TimeFrame();

			if (isNightTelescope) {	// Night telescope
				/* timeFrame		 ------
				 * sun				========
				 * result			  null			*/
				if (sunRise.getTime() <= timeFrame.getInit().getTime()  &&  timeFrame.getEnd().getTime() <= sunSet.getTime()) {
					return null;
				}
				/* timeFrame	----------------
				 * sun			    ========
				 * result		····		····	*/
				if (timeFrame.getInit().getTime() < sunRise.getTime()  &&  sunSet.getTime() < timeFrame.getEnd().getTime()) {
					tfRes[0].setInit(timeFrame.getInit());
					tfRes[0].setEnd(sunRise);
					tfRes[1].setInit(sunSet);
					tfRes[1].setEnd(timeFrame.getEnd());
				}
				/* timeFrame	--
				 * sun			    ========
				 * result		··  				*/
				if (timeFrame.getEnd().getTime() <= sunRise.getTime()) {
					tfRes[0] = timeFrame;
					tfRes[1] = null;
				}
				/* timeFrame				  --
				 * sun			    ========
				 * result					  ··	*/
				if (sunSet.getTime() <= timeFrame.getInit().getTime()) {
					tfRes[0] = timeFrame;
					tfRes[1] = null;
				}
				/* timeFrame	------
				 * sun			    ========
				 * result		····				*/
				if (sunRise.getTime() <= timeFrame.getEnd().getTime()  &&  timeFrame.getEnd().getTime() <= sunSet.getTime()) {
					tfRes[0].setInit(timeFrame.getInit());
					tfRes[0].setEnd(sunRise);
					tfRes[1] = null;
				}
				/* timeFrame			  ------
				 * sun			    ========
				 * result					····	*/
				if (sunRise.getTime() <= timeFrame.getInit().getTime()  &&  timeFrame.getInit().getTime() <= sunSet.getTime()  &&  sunSet.getTime() < timeFrame.getEnd().getTime()) {
					tfRes[0].setInit(sunSet);
					tfRes[0].setEnd(timeFrame.getEnd());
					tfRes[1] = null;
				}
				
			}else{ // Solar telescope
				/* timeFrame	----
				 * sun			      ========
				 * result		null 				*/
				if (timeFrame.getEnd().getTime() < sunRise.getTime()) {
					return null;
				}
				
				/* timeFrame					----
				 * sun				  ========
				 * result						null	*/
				if (sunSet.getTime() < timeFrame.getInit().getTime()) {
					return null;
				}
				
				/* timeFrame	-------------*
				 * sun				  =======*
				 * result			  ·······*		*/
				if (timeFrame.getInit().getTime() < sunRise.getTime()) {
					timeFrame.setInit(sunRise);
				}
				
				/* timeFrame	*----------
				 * sun			*=======
				 * result		*·······	 	*/
				if (timeFrame.getEnd().getTime() > sunSet.getTime()) {
					timeFrame.setEnd(sunSet);
				}
				tfRes[0] = timeFrame;
				tfRes[1] = null;
			}
			return tfRes;
		} catch (RTException e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<TimeFrame> findDatesValid(Date dateInit, int days) {
		List<TimeFrame> lst = new LinkedList<TimeFrame>();
		GregorianCalendar gCal = new GregorianCalendar();
		gCal.setTime(dateInit);
		for (int d=0 ; d<days ; d++) {
			for (SharedTimePortion stp : sharedTimePortions) {
				// Check the days of the week
				TimeFrame tf = isValidDaysOfWeek(gCal, stp);
				
				if (tf != null) {
					lst.add(tf);
				}
				
				// Check the mounths
				// ... to do
				
				// ...
			}
			gCal.add(Calendar.DAY_OF_YEAR, 1);
			gCal.set(Calendar.SECOND, 0);
			gCal.set(Calendar.MILLISECOND, 0);
			gCal.set(Calendar.MINUTE, 0);
			gCal.set(Calendar.HOUR_OF_DAY, 0);
		}
		return lst;
	}
	
	private TimeFrame isValidDaysOfWeek(GregorianCalendar gregCal, SharedTimePortion sharedTimeP) {
		GregorianCalendar gcIni = new GregorianCalendar();
		GregorianCalendar gcEnd = new GregorianCalendar();
		if (sharedTimeP.getMomentIntCalendar() == gregCal.get(Calendar.DAY_OF_WEEK)) {
			TimeFrame tf = new TimeFrame();
			
			gcIni.setTime(gregCal.getTime());
			gcIni.set(Calendar.SECOND, 0);
			gcIni.set(Calendar.MILLISECOND, 0);
			gcIni.set(Calendar.MINUTE, sharedTimeP.getInit()[1]);
			gcIni.set(Calendar.HOUR_OF_DAY, sharedTimeP.getInit()[0]);

			gcEnd.setTime(gregCal.getTime());
			gcEnd.set(Calendar.SECOND, 0);
			gcEnd.set(Calendar.MILLISECOND, 0);
			int[] timeEnd = sharedTimeP.getEnd();
			gcEnd.set(Calendar.MINUTE, timeEnd[1]);
			gcEnd.set(Calendar.HOUR_OF_DAY, timeEnd[0]);
			if (timeEnd[0] == 0  &&  timeEnd[1] == 0) {
				gcEnd.add(Calendar.DAY_OF_YEAR, 1);
			}
			
			// gregCal=g  init=i  end=e
			if (gregCal.getTimeInMillis() <= gcIni.getTimeInMillis()) {			// g <= i <= e  ->  [i, e]
				tf.setInit(gcIni.getTime());
				tf.setEnd(gcEnd.getTime());
				return tf;
				
			}else if (gregCal.getTimeInMillis() <= gcEnd.getTimeInMillis()) {		// i <= g <= e  ->  [g, e]
				tf.setInit(gregCal.getTime());
				tf.setEnd(gcEnd.getTime());
				return tf;
				
			}
			// else: i <= e <= g  ->  not
		}
		return null;
	}
}
