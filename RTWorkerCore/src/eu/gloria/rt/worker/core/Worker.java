package eu.gloria.rt.worker.core;

import java.nio.channels.ClosedByInterruptException;
import java.util.List;

import eu.gloria.rt.db.task.TaskProperty;

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

				while (true) {

					// Executes the custom action....
					doAction();

					Thread.sleep(sleepTime);
				}

			} catch (InterruptedException iex) {

				state = WorkerState.STOP;

			} catch (ClosedByInterruptException ciex) {

				state = WorkerState.STOP;

			} catch (Exception e) {
				
				state = WorkerState.STOP;

				// LogUtil.info(this, getLogHead() +
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
	
	public int getPropertyIntValue(String key) throws Exception{
		try{
			return Integer.parseInt(getPropertyStringValue(key));
		}catch(Exception ex){
			throw new Exception("Error recovering an integer property:" + key + ". " + ex.getMessage());
		}
	}
	
	public double getPropertyDoubleValue(String key) throws Exception{
		try{
			return Double.parseDouble(getPropertyStringValue(key));
		}catch(Exception ex){
			throw new Exception("Error recovering a double property:" + key + ". " + ex.getMessage());
		}
	}

}
