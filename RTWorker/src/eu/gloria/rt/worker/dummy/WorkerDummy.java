package eu.gloria.rt.worker.dummy;

import java.nio.channels.ClosedByInterruptException;
import java.util.Date;

import eu.gloria.rt.worker.core.Worker;

public class WorkerDummy extends Worker {

	public WorkerDummy(){
		super();
	}

	@Override
	protected void doAction() throws InterruptedException,
			ClosedByInterruptException {
		
		System.out.println((new Date()) + "TaskDummy.....doAction...");
		
	}

}
