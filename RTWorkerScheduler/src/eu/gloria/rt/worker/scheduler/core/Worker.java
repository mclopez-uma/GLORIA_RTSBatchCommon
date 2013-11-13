package eu.gloria.rt.worker.scheduler.core;

import java.nio.channels.ClosedByInterruptException;

import eu.gloria.rt.worker.core.WorkerState;
import eu.gloria.rt.worker.scheduler.context.SchedulerContext;

/**
 * @author Alfredo
 * 
 *         Abstract class to make a daemon to schedule OPs.
 */
public abstract class Worker {
	protected SchedulerContext schContext;
	private ThreadImpl threadImpl;
	private Thread thread;
	private WorkerState state;
	private String id;

	/**
	 * Constructor.
	 */
	public Worker(SchedulerContext sc, String id) {
		this.state = WorkerState.STOP;
		this.schContext = sc;
		this.id = id;
	}

	/**
	 * Real business logic for the custom operation.
	 * 
	 * @throws InterruptedException
	 *             In abort case.
	 * @throws ClosedByInterruptException
	 *             In abort case.
	 * @throws Exception
	 *             In error case.
	 */
	protected abstract void doAction();

	/**
	 * Executes the Operation in other thread.
	 * 
	 * @throws Exception
	 */
	public synchronized void start() {
		threadImpl = new ThreadImpl();
		thread = new Thread(threadImpl);

		state = WorkerState.RUN;
		thread.start();
	}

	/**
	 * Abort the operation execution.
	 * 
	 * @throws Exception
	 */
	public synchronized void stop() throws Exception {
		if (thread != null && state == WorkerState.RUN) {
			thread.interrupt();
		}
		state = WorkerState.STOP;
	}

	/**
	 * Internal class to execute the task in other thread.
	 */
	class ThreadImpl implements Runnable {
		@Override
		public void run() {
			try {
				// Executes the custom action....
				doAction();

			} catch (Exception e) {
				e.printStackTrace();

			} finally {
				state = WorkerState.STOP;
			}
		}
	}

	/**
	 * Getter method to request the identifier of a worker.
	 * 
	 * @return The identifier.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Getter method to request the state of a worker.
	 * 
	 * @return The state.
	 */
	public WorkerState getState() {
		return state;
	}
}
