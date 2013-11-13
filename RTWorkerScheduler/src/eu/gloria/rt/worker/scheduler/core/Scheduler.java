package eu.gloria.rt.worker.scheduler.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import eu.gloria.rt.db.scheduler.ObservingPlan;
import eu.gloria.rt.worker.core.WorkerState;
import eu.gloria.rt.worker.scheduler.context.SchedulerContext;
import eu.gloria.rt.worker.scheduler.context.SchedulerLog;
import eu.gloria.rt.worker.scheduler.enums.SchedulerState;
import eu.gloria.rt.worker.scheduler.interfaces.ConfigUpgradeable;
import eu.gloria.rt.worker.scheduler.interfaces.DataBaseInterface;
import eu.gloria.rt.worker.scheduler.interfaces.SchedulerControl;
import eu.gloria.rt.worker.scheduler.times.GeneratorSlots;

/**
 * @author Alfredo
 * 
 *         Main class to scheduler an observing plan.
 */
public class Scheduler implements SchedulerControl, ConfigUpgradeable, Runnable {
	private final String gloriaXsdFile = "gloria_rti_plan.xsd";
	private WorkerAdvertisementManager worker;
	private SchedulerContext schContext;
	private DataBaseInterface database;
	private GeneratorSlots genSlots;
	private SchedulerState schState;
	private SchedulerLog log;
	private int schWait;

	/**
	 * Default constructor.
	 */
	public Scheduler() {
		// Set the state
		schState = SchedulerState.INITIALIZING;

		// Check if the necessary files exists
		//if (notExistsLogConfigFile()) {
		//	createLogConfigFile();
		//}
		if (notExistsGloriaXsdFile()) {
			createGloriaXsdFile();
		}

		// Create the objects
		schContext = new SchedulerContext();
		database = new MassiveStorageDbMysql(schContext);
		log = schContext.logger(getClass());
		
		////////////////////////////////////////////////////////////////////
		/*
		genSlots = new GeneratorSlots(schContext, database);
		genSlots.start();
		
		try {
			Thread.sleep(2500);
		}catch (Exception e) { }
		
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(System.currentTimeMillis());
		gc.set(Calendar.DAY_OF_MONTH, 3);
//		gc.set(Calendar.MILLISECOND, 0);


//		gc.set(Calendar.SECOND, 59);
//		gc.set(Calendar.MINUTE, 13);
//		gc.set(Calendar.HOUR_OF_DAY, 8);

		gc.set(Calendar.SECOND, 1);
		gc.set(Calendar.MINUTE, 14);
		gc.set(Calendar.HOUR_OF_DAY, 8);

//		gc.set(Calendar.SECOND, 0);
//		gc.set(Calendar.MINUTE, 0);
//		gc.set(Calendar.HOUR_OF_DAY, 18);

		Timestamp ini = new Timestamp(gc.getTimeInMillis());


//		gc.set(Calendar.SECOND, 0);
//		gc.set(Calendar.MINUTE, 0);
//		gc.set(Calendar.HOUR_OF_DAY, 19);

		gc.set(Calendar.SECOND, 52);
		gc.set(Calendar.MINUTE, 57);
		gc.set(Calendar.HOUR_OF_DAY, 19);

//		gc.set(Calendar.SECOND, 54);
//		gc.set(Calendar.MINUTE, 57);
//		gc.set(Calendar.HOUR_OF_DAY, 19);

		Timestamp end = new Timestamp(gc.getTimeInMillis());

		database.setUuidOp("1", ini, end);
		
		
		// */
		
		//*

		// Modify the OPs RUNNING to QUEUED
		database.resetAllOpsRunningToQueued();
		// Set ERROR the OPs with their dead line are passed
		database.rejectOpsPassedAdvertDeadLine();

		// Create and start the generator slots
		genSlots = new GeneratorSlots(schContext, database);
		genSlots.start();

		// Add this class in the context to update if the configuration changed
		schContext.addConfigUpgradeable(this);
		updateConfig();

		// Create the worker
		worker = new WorkerAdvertisementManager(schContext, database, "MyWorker");
//		*/
//		System.exit(0);
	}

	@Override
	public void run() {
		schState = SchedulerState.RUNNING;
		// While the state are not finish...
		while (schState != SchedulerState.FINISHING) {
			boolean added = false;
			// If the state are running...
			if (schState == SchedulerState.RUNNING) {
				// Set ERROR the OPs with their dead line are passed
				database.rejectOpsPassedAdvertDeadLine();
				// Update the OP canceled by GLORIA
				database.updateOpCanceledByGloria();
				// Search one OP to scheduler
				ObservingPlan op = database.getNextOpToProcess();
				// If op!=null, exists an OP to schedule
				if (op != null) {
					// The worker can schedule it
					added = worker.addObservingPlan(op);
					// If this has accepted to schedule and the worker state are not RUN...
					if (added && worker.getState() != WorkerState.RUN) {
						// ...start the scheduling
						worker.start();
					} else {
						// In other case, nothing (rejected)
						// store.rejectForOverbooking(op);
					}
				}
			}

			try {
				// If don't add...
				if (!added) {
					// ...wait to search other OP
					Thread.sleep(schWait);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}

	@Override
	public void eventConfigScheduler() {
		log.info(schContext.language.getString("Scheduler_Event_config"));
		schContext.loadConfigFile();
	}

	@Override
	public void eventPauseScheduler() {
		log.info(schContext.language.getString("Scheduler_Event_pause"));
		schState = SchedulerState.PAUSED;
	}

	@Override
	public void eventContinueScheduler() {
		log.info(schContext.language.getString("Scheduler_Event_continue"));
		schState = SchedulerState.RUNNING;
	}

	@Override
	public void eventFinishScheduler() {
		log.info(schContext.language.getString("Scheduler_Event_finish"));
		schState = SchedulerState.FINISHING;
	}

	@Override
	public void updateConfig() {
		schWait = schContext.getSchedulerWait();
	}

	/**
	 * Method to check if the log configuration file exists.
	 * 
	 * @return <b>true</b> if the log configuration file exists, <b>false</b> in other case.
	 */
	/*private boolean notExistsLogConfigFile() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(SchedulerLog.logConfigFile));
			br.close();
		} catch (Exception e) {
			return true;
		}
		return false;
	}*/

	/**
	 * Method to check if the GLORIA XSD file exists.
	 * 
	 * @return <b>true</b> if the GLORIA XSD file exists, <b>false</b> in other case.
	 */
	private boolean notExistsGloriaXsdFile() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(gloriaXsdFile));
			br.close();
		} catch (Exception e) {
			return true;
		}
		return false;
	}

	/**
	 * Method to create a default log configuration file.
	 */
	/*private void createLogConfigFile() {
		copyFile(SchedulerLog.logConfigFile);
	}*/

	/**
	 * Method to create a default log configuration file.
	 */
	private void createGloriaXsdFile() {
		copyFile(gloriaXsdFile);
	}

	/**
	 * Method to copy one file from the packet to the execution directory.
	 * 
	 * @param file
	 *            Name of file to copy.
	 */
	private void copyFile(String file) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/eu/gloria/scheduler/txt/" + file)));
			PrintWriter pw = new PrintWriter(new FileWriter(file));

			while (br.ready()) {
				pw.println(br.readLine());
			}

			pw.close();
			br.close();
		} catch (Exception e) {
			log.error("Exception: " + e.getClass().getCanonicalName() + " -> " + e.getLocalizedMessage() + "\n");
			e.printStackTrace();
		}
	}
}
