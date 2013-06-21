package eu.gloria.rt.worker;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import eu.gloria.rt.db.task.TaskManager;
import eu.gloria.rt.db.util.DBUtil;
import eu.gloria.rt.worker.core.Worker;


public class WorkerStarter {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		WorkerStarter starter = new WorkerStarter();
		starter.start();

	}
	
	public WorkerStarter(){
	}
	
	public void start() throws Exception{
		
		try{
			
			EntityManager em = DBUtil.getEntityManager();
			
			TaskManager manager = new TaskManager();
			List<eu.gloria.rt.db.task.Task> tasks = manager.getTasks(em);
			
			List<Worker> runnableTasks = new ArrayList<Worker>();
			if (tasks != null){
				for (eu.gloria.rt.db.task.Task task : tasks) {
					runnableTasks.add(getTask(task));
				}
			}
			
			for (Worker task : runnableTasks) {
				task.start();
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			throw ex;
		}
	}
	
	
	private Worker getTask(eu.gloria.rt.db.task.Task taskIn) throws Exception{
		
		try{
			Class<?> cls = Class.forName(taskIn.getProvider());
			Constructor<?> ct = cls.getConstructor();
			
			Worker task = (Worker) ct.newInstance();
			task.init(taskIn.getId(), taskIn.getSleepTime(), taskIn.getTaskProperties());
			
			return task;
			
		}catch(Exception ex){
			ex.printStackTrace();
			throw new Exception("Error creating task: " + taskIn.getProvider() + ". " + ex.getMessage());
		}
		
		
	}

}
