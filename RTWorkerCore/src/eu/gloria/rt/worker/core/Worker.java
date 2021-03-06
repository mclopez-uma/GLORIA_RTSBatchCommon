package eu.gloria.rt.worker.core;

import java.nio.channels.ClosedByInterruptException;
import java.util.List;

import eu.gloria.rt.db.task.TaskProperty;
import eu.gloria.tools.log.LogUtil;

public abstract class Worker {
	
	private List<TaskProperty> properties;

	/**
	 * Internal Runnable class containing the business logic of the operation.
	 */
	private ThreadImpl threadImpl;

	/**
	 * The java API thread for <threadImpl>.
	 */
	private Thread thread;

	private WorkerState state;

	private long sleepTime;
	
	private String id;
	
	private boolean init;

	/**
	 * Constructor.
	 */
	public Worker() {

		
		this.threadImpl = new ThreadImpl();
		this.thread = new Thread(threadImpl);
		this.init = false;
		this.state = WorkerState.STOP;
	}
	
	public void init(String id, long sleepTime, List<TaskProperty> properties){
		this.id = id;
		this.sleepTime = sleepTime;
		this.init = true;
		this.properties = properties;
	}

	/**
	 * Real business logic for the custom operation.
	 * 
	 * @throws InterruptedException
	 *             In error case (abort).
	 * @throws ClosedByInterruptException
	 *             In error case (abort).
	 */
	protected abstract void doAction() throws InterruptedException, ClosedByInterruptException, Exception;
	
	/**
	 * Executes the Operation in other thread.
	 * @throws Exception 
	 */
	public synchronized void start() throws Exception {
		
		if (!init) throw new Exception("Task non-initialized.");

		state = WorkerState.RUN;

		this.thread.start();

	}

	/**
	 * Abort the operation execution.
	 * @throws Exception 
	 */
	public synchronized void stop() throws Exception{
		
		if (!init) throw new Exception("Task non-initialized.");

		if (thread != null && state == WorkerState.RUN) {
			thread.interrupt();
		}

		state = WorkerState.STOP;

	}

	/**
	 * Internal class to execute the task in other thread.
	 * 
	 * @author jcabello
	 * 
	 */
	class ThreadImpl implements Runnable {

		@Override
		public void run() {

			try {
				
				if (sleepTime > 0){

					while (true) {

						// Executes the custom action....
						doAction();

						Thread.sleep(sleepTime);
					}
					
				}else{
					
					// Executes the custom action....only once
					doAction();
					
				}

			} catch (InterruptedException iex) {

				state = WorkerState.STOP;
				
				LogUtil.info(this, "Worker. Error running task: " + iex.getMessage());
				iex.printStackTrace();

			} catch (ClosedByInterruptException ciex) {

				state = WorkerState.STOP;
				
				LogUtil.info(this, "Worker. Error running task: " + ciex.getMessage());
				ciex.printStackTrace();

			} catch (Exception e) {
				
				state = WorkerState.STOP;
				
				LogUtil.info(this, "Worker. Error running task: " + e.getMessage());
				e.printStackTrace();

				//LogUtil.info(this, getLogHead() +
				// "TaskTimeout aborted. Exception thrown Exception....");

			} finally {
				// NOTHING....
			}

		}
	}

	public WorkerState getState() {
		return state;
	}

	public void setState(WorkerState state) {
		this.state = state;
	}

	public List<TaskProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<TaskProperty> properties) {
		this.properties = properties;
	}
	
	public TaskProperty getProperty(String key){
		
		TaskProperty result = null;
		if (properties != null){
			for (TaskProperty prop : properties) {
				if (prop.getName().equals(key)){
					result = prop;
					break;
				}
			}
		}
		return result;
	}
	
	public String getPropertyStringValue(String key){
		String result  = null;
		TaskProperty prop =  getProperty(key);
		if (prop != null) result = prop.getValue();
		return result;
	}
	
	public String getPropertyStringValue(String key, String defaultValue){
		String result  = getPropertyStringValue(key);
		if (result == null) {
			result = defaultValue;
			LogUtil.info(this, "Worker[ID=" + id + "]. Param[key=" + key + "]. Returning the default value=" + result);
		}else{
			LogUtil.info(this, "Worker[ID=" + id + "]. Param[key=" + key + "]. Returning the found value=" + result);
		}
		return result;
	}
	
	public int getPropertyIntValue(String key) throws Exception{
		try{
			return Integer.parseInt(getPropertyStringValue(key));
		}catch(Exception ex){
			throw new Exception("Error recovering an integer property:" + key + ". " + ex.getMessage());
		}
	}
	
	public int getPropertyIntValue(String key, int defaultValue) {
		try{
			int result =  Integer.parseInt(getPropertyStringValue(key));
			LogUtil.info(this, "Worker[ID=" + id + "]. Param[key=" + key + "]. Returning the found value=" + result);
			return result;
		}catch(Exception ex){
			LogUtil.info(this, "Worker[ID=" + id + "]. Param[key=" + key + "]. Returning the default value=" + defaultValue);
			return defaultValue;
		}
	}
	
	public double getPropertyDoubleValue(String key) throws Exception{
		try{
			return Double.parseDouble(getPropertyStringValue(key));
		}catch(Exception ex){
			throw new Exception("Error recovering a double property:" + key + ". " + ex.getMessage());
		}
	}
	
	public double getPropertyDoubleValue(String key, double defaultValue) {
		try{
			double result =  Double.parseDouble(getPropertyStringValue(key));
			LogUtil.info(this, "Worker[ID=" + id + "]. Param[key=" + key + "]. Returning the found value=" + result);
			return result;
		}catch(Exception ex){
			LogUtil.info(this, "Worker[ID=" + id + "]. Param[key=" + key + "]. Returning the default value=" + defaultValue);
			return defaultValue;
		}
	}
	
	public long getPropertyLongValue(String key) throws Exception{
		try{
			return Long.parseLong(getPropertyStringValue(key));
		}catch(Exception ex){
			throw new Exception("Error recovering an long property:" + key + ". " + ex.getMessage());
		}
	}
	
	public long getPropertyLongValue(String key, long defaultValue) {
		try{
			long result =  Long.parseLong(getPropertyStringValue(key));
			LogUtil.info(this, "Worker[ID=" + id + "]. Param[key=" + key + "]. Returning the found value=" + result);
			return result;
		}catch(Exception ex){
			LogUtil.info(this, "Worker[ID=" + id + "]. Param[key=" + key + "]. Returning the default value=" + defaultValue);
			return defaultValue;
		}
	}
	
	public boolean getPropertyBooleanValue(String key) throws Exception{
		try{
			return Boolean.parseBoolean(getPropertyStringValue(key));
		}catch(Exception ex){
			throw new Exception("Error recovering an boolean property:" + key + ". " + ex.getMessage());
		}
	}
	
	public boolean getPropertyBooleanValue(String key, boolean defaultValue) {
		try{
			boolean result = Boolean.parseBoolean(getPropertyStringValue(key));
			LogUtil.info(this, "Worker[ID=" + id + "]. Param[key=" + key + "]. Returning the found value=" + result);
			return result;
		}catch(Exception ex){
			LogUtil.info(this, "Worker[ID=" + id + "]. Param[key=" + key + "]. Returning the default value=" + defaultValue);
			return defaultValue;
		}
	}
	
	

}
