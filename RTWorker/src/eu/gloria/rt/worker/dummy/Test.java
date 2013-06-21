package eu.gloria.rt.worker.dummy;

public class Test {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		WorkerDummy task = new WorkerDummy();
		System.out.println("State=" + task.getState());
		task.start();
		System.out.println("State=" + task.getState());
		
		Thread.sleep(20000);
		System.out.println("State=" + task.getState());
		task.stop();
		
		System.out.println("State=" + task.getState());
		

	}

}
