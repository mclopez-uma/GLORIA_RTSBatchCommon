package eu.gloria.rt.worker.scheduler.context;


import eu.gloria.tools.log.LogUtil;

/**
 * @author Alfredo
 * 
 *         Intermediate class to create a log.
 */
public class SchedulerLog {
	
	//public static final String logConfigFile = "scheduler.log.properties";
	//private Logger log;
	
	private Class source;
	
	/**
	 * Constructor.
	 * 
	 * @param clazz Class that request a log.
	 */
	public SchedulerLog(Class<?> clazz) {
		//PropertyConfigurator.configure(logConfigFile);
		//log = Logger.getLogger(clazz.getSimpleName());
		source = clazz;
	}
	
	/**
	 * Method to show a message with low important, debug mode.
	 * 
	 * @param msg
	 *            The message to show.
	 */
	public void debug(String msg) {
		LogUtil.fine(source, msg);
	}
	
	/**
	 * Method to show a message with normal important.
	 * 
	 * @param msg
	 *            The message to show.
	 */
	public void info(String msg) {
		LogUtil.info(source, msg);
	}
	
	/**
	 * Method to show a warning message.
	 * 
	 * @param msg
	 *            The message to show.
	 */
	public void warn(String msg) {
		LogUtil.info(source, msg);
	}
	
	/**
	 * Method to show a error message.
	 * 
	 * @param msg
	 *            The message to show.
	 */
	public void error(String msg) {
		LogUtil.severe(source, msg);
	}
	
	/**
	 * Method to show a fatal error message.
	 * 
	 * @param msg
	 *            The message to show.
	 */
	public void fatal(String msg) {
		LogUtil.severe(source, msg);
	}
}
