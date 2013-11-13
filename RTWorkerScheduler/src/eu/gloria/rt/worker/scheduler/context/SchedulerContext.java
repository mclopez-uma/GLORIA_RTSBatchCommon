package eu.gloria.rt.worker.scheduler.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import eu.gloria.rt.catalogue.Observer;
import eu.gloria.rt.worker.scheduler.interfaces.ConfigUpgradeable;
import eu.gloria.rt.worker.scheduler.times.SharedTimePortion;

public class SchedulerContext {
	public ResourceBundle language;
	private SchedulerLog log;
	private List<ConfigUpgradeable> classesConfig;
	
	private long predictionMsecMountMove, predictionMsecFilterMove, predictionMsecLooseness, predictionMsecCameraSettings;
	private String xmlPath, xsdFile, databaseIp, databasePort, databaseDb, databaseUser, databasePass;
	private int maxSharedTimeSession, maxCountOpSession, maxCountOpUser, maxTimeUser;
	private int daysScheduling, daysFutures, maxInQueued, schedulerWait;
	private boolean isNightTelescope, advertAcceptedToQueue;
	private List<SharedTimePortion> sharedTimeFrame;
	private int[] timeLimitToday, timeLimitExec;
	private Observer observer;
	private String timeLimitExecString;
	
	public SchedulerContext() {
		language = ResourceBundle.getBundle("eu.gloria.rt.worker.scheduler.txt.languages");
		log = this.logger(getClass());
		classesConfig = new LinkedList<ConfigUpgradeable>();
		loadConfigFile();
	}
	
	public SchedulerContext(boolean readFile){
		if (readFile){
			language = ResourceBundle.getBundle("eu.gloria.rt.worker.scheduler.txt.languages");
			log = this.logger(getClass());
			classesConfig = new LinkedList<ConfigUpgradeable>();
			loadConfigFile();
		}else{
			classesConfig = new LinkedList<ConfigUpgradeable>();
			language = ResourceBundle.getBundle("eu.gloria.rt.worker.scheduler.txt.languages");
		}
	}
	
	public SchedulerLog logger(Class<?> clase) {
		return new SchedulerLog(clase);
	}
	
	public void loadConfigFile() {
		double latitude=0, longitude=0, altitude=0;
		String sharedTimeFrame="";
		
		log.info(language.getString("SchCtxt_Loading_config_file"));
		Properties prop = new Properties();
		try {
			InputStream is = new FileInputStream("scheduler.properties");
			prop.load(is);
			is.close();
		} catch (IOException e) {
			log.error(String.format(language.getString("SchCtxt_Error_loading_config_file"), e.getMessage()));
		}
		
		try {
			databaseIp = prop.getProperty("DatabaseIp", "127.0.0.1");
		} catch (Exception e) {
			databaseIp = "127.0.0.1";
		}
		
		try {
			databasePort = prop.getProperty("DatabasePort", "3306");
			int port = Integer.valueOf(databasePort);
			if (port <= 0  ||  port >= 65536) {
				databasePort = "3306";
			}
		} catch (Exception e) {
			databasePort = "3306";
		}
		
		try {
			databaseDb = prop.getProperty("DatabaseDb", "gloria");
		} catch (Exception e) {
			databaseDb = "gloria";
		}
		
		try {
			databaseUser = prop.getProperty("DatabaseUser", "gloria");
		} catch (Exception e) {
			databaseUser = "gloria";
		}
		
		try {
			databasePass = prop.getProperty("DatabasePass", "");
		} catch (Exception e) {
			databasePass = "";
		}
		
		try {
			latitude  = Double.valueOf(prop.getProperty("Latitude",  "0"));
			longitude = Double.valueOf(prop.getProperty("Longitude", "0"));
			altitude  = Double.valueOf(prop.getProperty("Altitude",  "0"));
		} catch (Exception e) {
			latitude  = 0;
			longitude = 0;
			altitude  = 0;
		} finally {
			observer = new Observer();
			observer.setAltitude(altitude);
			observer.setLatitude(latitude);
			observer.setLongitude(longitude);
		}

		try {
			sharedTimeFrame = prop.getProperty("SharedTimeFrame", "monday,*;tuesday,*;wednesday,*;thursday,*;friday,*;saturday,*;sunday,*");
		} catch (Exception e) {
			sharedTimeFrame = "monday,*;tuesday,*;wednesday,*;thursday,*;friday,*;saturday,*;sunday,*";
		} finally {
			makeShareTime(sharedTimeFrame);
		}
		
		try {
			maxSharedTimeSession = Integer.parseInt(prop.getProperty("MaxSharedTimeSession", "0"));
		} catch (RuntimeException e) {
			maxSharedTimeSession = 0;
		}
		try {
			maxCountOpSession = Integer.parseInt(prop.getProperty("MaxCountOpSession", "0"));
		} catch (RuntimeException e) {
			maxCountOpSession = 0;
		}
		try {
			maxCountOpUser  = Integer.parseInt(prop.getProperty("MaxCountOpUser", "0"));
		} catch (Exception e) {
			maxCountOpUser  = 0;
		}
		try {
			maxTimeUser     = Integer.parseInt(prop.getProperty("MaxShareTimeUser", "0"));
		} catch (Exception e) {
			maxTimeUser     = 0;
		}

		try {
			maxInQueued = Integer.valueOf(prop.getProperty("MaxInQueued", "1"));
			if (maxInQueued <= 0) {
				maxInQueued = 1;
			}
		} catch (Exception e) {
			maxInQueued = 1;
		}
		
		try {
			xmlPath = prop.getProperty("XmlPath", "./xml/");
			xsdFile = prop.getProperty("XsdFile", "./gloria_rti_plan.xsd");
		} catch (Exception e) {
			xmlPath = "./xml/";
			xsdFile = "./gloria_rti_plan.xsd";
		}
		File xmlDir = new File(xmlPath);
		if (!xmlDir.exists()) {
			xmlDir.mkdirs();
		}
		
		try {
			daysScheduling = Integer.valueOf(prop.getProperty("DaysScheduling", "3"));
			daysFutures    = Integer.valueOf(prop.getProperty("daysFutures", "7"));
		} catch (Exception e) {
			daysScheduling = 3;
			daysFutures    = 7;
		}
		if (daysScheduling < 1) {
			daysScheduling = 3;
		}
		if (daysFutures <= daysScheduling) {
			daysFutures = daysScheduling + 1;
		}
		
		try {
			predictionMsecMountMove      = Long.valueOf(prop.getProperty("PredictionMsecMountMove",     "60000"));
			predictionMsecFilterMove     = Long.valueOf(prop.getProperty("PredictionMsecFilterMove",    "15000")); 
			predictionMsecLooseness      = Long.valueOf(prop.getProperty("PredictionMsecLooseness",     "10000"));
			predictionMsecCameraSettings = Long.valueOf(prop.getProperty("PredictionMsecCameraSettings", "5000"));
		} catch (Exception e) {
			predictionMsecMountMove      = 60000;
			predictionMsecFilterMove     = 15000; 
			predictionMsecLooseness      = 10000;
			predictionMsecCameraSettings =  5000;
		}
		
		try {
			isNightTelescope = Boolean.valueOf(prop.getProperty("IsNightTelescope", "true"));
		} catch (Exception e) {
			isNightTelescope = true;
		}
		
		try {
			schedulerWait = Integer.valueOf(prop.getProperty("SchedulerWait", "5000"));
		} catch (Exception e) {
			schedulerWait = 5000;
		}
		
		try {
			String timeLimitTodayStr = prop.getProperty("TimeLimitToday", "");
			StringTokenizer st = new StringTokenizer(timeLimitTodayStr, ":");
			timeLimitToday = new int[3];
			timeLimitToday[0] = Integer.parseInt(st.nextToken());
			timeLimitToday[1] = Integer.parseInt(st.nextToken());
			timeLimitToday[2] = Integer.parseInt(st.nextToken());
		} catch (Exception e) {
			timeLimitToday = null;
		}
		
		try {
			timeLimitExecString = prop.getProperty("TimeLimitExec", "");
			StringTokenizer st = new StringTokenizer(timeLimitExecString, ":");
			timeLimitExec = new int[3];
			timeLimitExec[0] = Integer.parseInt(st.nextToken());
			timeLimitExec[1] = Integer.parseInt(st.nextToken());
			timeLimitExec[2] = Integer.parseInt(st.nextToken());
		} catch (Exception e) {
			timeLimitExec = null;
		}
		
		try {
			advertAcceptedToQueue = Boolean.parseBoolean(prop.getProperty("AdvertAcceptedToQueue", "false"));
		} catch (Exception e) {
			advertAcceptedToQueue = false;
		}
		
		upgradeConfig();
	}

	public void addConfigUpgradeable(ConfigUpgradeable clazz) {
		classesConfig.add(clazz);
	}

	public void upgradeConfig() {
		for (ConfigUpgradeable clazz : classesConfig) {
			clazz.updateConfig();
		}
	}

	public List<SharedTimePortion> getSharedTimeFrame() {
		return sharedTimeFrame;
	}

	private void makeShareTime(String share) {
		sharedTimeFrame = new LinkedList<SharedTimePortion>();
		StringTokenizer st1 = new StringTokenizer(share, ";");
		while (st1.hasMoreTokens()) {
			StringTokenizer st2 = new StringTokenizer(st1.nextToken(), ",");
			String moment = st2.nextToken();
			String ini = st2.nextToken();
			if (st2.hasMoreTokens()) {
				String end = st2.nextToken();
				sharedTimeFrame.add(new SharedTimePortion(moment, ini, end));
			}else{
				sharedTimeFrame.add(new SharedTimePortion(moment));
			}
		}
	}
	
	public int getMaxInQueued() {
		return maxInQueued;
	}

	public Observer getObserver() {
		return observer;
	}

	public int getMaxSharedTimeSession() {
		return maxSharedTimeSession;
	}
	
	public int getMaxCountOpSession() {
		return maxCountOpSession;
	}
	
	public int getMaxCountOpUser() {
		return maxCountOpUser;
	}
	
	public int getMaxShareTimeUser() {
		return maxTimeUser;
	}
		
	public String getXmlPath() {
		return xmlPath;
	}
	
	public String getXsdFile() {
		return xsdFile;
	}
	
	public int getDaysScheduling() {
		return daysScheduling;
	}
	
	public int getDaysFutures() {
		return daysFutures;
	}
	
	public long getPredictionMsecMountMove() {
		return predictionMsecMountMove;
	}
	
	public long getPredictionMsecFilterMove() {
		return predictionMsecFilterMove;
	}
	
	public long getPredictionMsecLooseness() {
		return predictionMsecLooseness;
	}
	
	public long getPredictionMsecCameraSettings() {
		return predictionMsecCameraSettings;
	}
	
	public boolean getIsNightTelescope() {
		return isNightTelescope;
	}
	
	public int[] getTimeLimitToday() {
		return timeLimitToday;
	}
	
	public int[] getTimeLimitExec() {
		return timeLimitExec;
	}

	public int getSchedulerWait() {
		return schedulerWait;
	}
	
	public String getDatabaseIp() {
		return databaseIp;
	}
	
	public String getDatabasePort() {
		return databasePort;
	}
	
	public String getDatabaseDb() {
		return databaseDb;
	}
	
	public String getDatabaseUser() {
		return databaseUser;
	}
	
	public String getDatabasePass() {
		return databasePass;
	}
	
	public boolean getAdvertAcceptedToQueue() {
		return advertAcceptedToQueue;
	}
	
	public int getUnitType() {
		return Calendar.MINUTE;
	}
	
	public int getAmount() {
		return 10;
	}
	
	@Override
	public String toString() {
		String str = "observer: " + observer + "\n";
		str += "sharedTimeFrame: " + sharedTimeFrame + "\n";
		str += "maxSharedTimeSession: " + maxSharedTimeSession + "\n";
		str += "maxCountOpSession: " + maxCountOpSession + "\n";
		str += "maxInQueued: " + maxInQueued + "\n";
		
		str += "xmlPath: " + xmlPath + "\n";
		str += "xsdFile: " + xsdFile + "\n";
		str += "daysScheduling: " + daysScheduling + "\n";
		str += "daysFutures: " + daysFutures + "\n";
		str += "predictionMsecMountMove: " + predictionMsecMountMove + "\n";
		str += "predictionMsecFilterMove: " + predictionMsecFilterMove + "\n";
		str += "predictionMsecLooseness: " + predictionMsecLooseness + "\n";
		str += "predictionMsecCameraSettings: " + predictionMsecCameraSettings + "\n";
		str += "isNightTelescope: " + isNightTelescope + "\n";
		
		str += "schedulerWait: " + schedulerWait + "\n";
		return str;
	}

	public int getMaxTimeUser() {
		return maxTimeUser;
	}

	public void setMaxTimeUser(int maxTimeUser) {
		this.maxTimeUser = maxTimeUser;
	}

	public void setPredictionMsecMountMove(long predictionMsecMountMove) {
		this.predictionMsecMountMove = predictionMsecMountMove;
	}

	public void setPredictionMsecFilterMove(long predictionMsecFilterMove) {
		this.predictionMsecFilterMove = predictionMsecFilterMove;
	}

	public void setPredictionMsecLooseness(long predictionMsecLooseness) {
		this.predictionMsecLooseness = predictionMsecLooseness;
	}

	public void setPredictionMsecCameraSettings(long predictionMsecCameraSettings) {
		this.predictionMsecCameraSettings = predictionMsecCameraSettings;
	}

	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}

	public void setXsdFile(String xsdFile) {
		this.xsdFile = xsdFile;
	}

	public void setDatabaseIp(String databaseIp) {
		this.databaseIp = databaseIp;
	}

	public void setDatabasePort(String databasePort) {
		this.databasePort = databasePort;
	}

	public void setDatabaseDb(String databaseDb) {
		this.databaseDb = databaseDb;
	}

	public void setDatabaseUser(String databaseUser) {
		this.databaseUser = databaseUser;
	}

	public void setDatabasePass(String databasePass) {
		this.databasePass = databasePass;
	}

	public void setMaxSharedTimeSession(int maxSharedTimeSession) {
		this.maxSharedTimeSession = maxSharedTimeSession;
	}

	public void setMaxCountOpSession(int maxCountOpSession) {
		this.maxCountOpSession = maxCountOpSession;
	}

	public void setMaxCountOpUser(int maxCountOpUser) {
		this.maxCountOpUser = maxCountOpUser;
	}

	public void setDaysScheduling(int daysScheduling) {
		this.daysScheduling = daysScheduling;
	}

	public void setDaysFutures(int daysFutures) {
		this.daysFutures = daysFutures;
	}

	public void setMaxInQueued(int maxInQueued) {
		this.maxInQueued = maxInQueued;
	}

	public void setSchedulerWait(int schedulerWait) {
		this.schedulerWait = schedulerWait;
	}

	public void setNightTelescope(boolean isNightTelescope) {
		this.isNightTelescope = isNightTelescope;
	}

	public void setAdvertAcceptedToQueue(boolean advertAcceptedToQueue) {
		this.advertAcceptedToQueue = advertAcceptedToQueue;
	}

	public void setSharedTimeFrame(List<SharedTimePortion> sharedTimeFrame) {
		this.sharedTimeFrame = sharedTimeFrame;
	}

	public void setTimeLimitToday(int[] timeLimitToday) {
		this.timeLimitToday = timeLimitToday;
	}

	public void setTimeLimitExec(int[] timeLimitExec) {
		this.timeLimitExec = timeLimitExec;
	}

	public void setObserver(Observer observer) {
		this.observer = observer;
	}

	public String getTimeLimitExecString() {
		return timeLimitExecString;
	}

	public void setTimeLimitExecString(String timeLimitExecString) {
		this.timeLimitExecString = timeLimitExecString;
	}
	
}
