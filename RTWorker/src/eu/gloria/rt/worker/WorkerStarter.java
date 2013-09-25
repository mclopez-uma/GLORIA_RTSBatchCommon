package eu.gloria.rt.worker;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import eu.gloria.rt.db.task.TaskManager;
import eu.gloria.rt.db.util.DBUtil;
import eu.gloria.rt.worker.core.Worker;
import eu.gloria.tools.log.LogUtil;


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
				
				LogUtil.info(this, "WorkerStarter. Recovered Tasks: " + tasks.size());
				
				for (eu.gloria.rt.db.task.Task task : tasks) {
					if (task.getEnable() > 0){
						runnableTasks.add(getTask(task));
					}
				}
			}else{
				LogUtil.info(this, "WorkerStarter. Recovered Tasks: 0");
			}
			
			for (Worker task : runnableTasks) {
				task.start();
			}
			
		}catch(Exception ex){
			LogUtil.info(this, "WorkerStarter. Error: " + ex.getMessage());
			ex.printStackTrace();
			throw ex;
		}
	}
	
	
	private Worker getTask(eu.gloria.rt.db.task.Task taskIn) throws Exception{
		
		try{
			
			LogUtil.info(this, "WorkerStarter. Creating task: " + taskIn.getProvider());
			
			Class<?> cls = Class.forName(taskIn.getProvider());
			Constructor<?> ct = cls.getConstructor();
			
			Worker task = (Worker) ct.newInstance();
			task.init(taskIn.getId(), taskIn.getSleepTime(), taskIn.getTaskProperties());
			
			LogUtil.info(this, "WorkerStarter. Created task: " + taskIn.getProvider());
			
			return task;
			
		}catch(Exception ex){
			LogUtil.info(this, "Error creating task: " + taskIn.getProvider() + ". " + ex.getMessage());
			ex.printStackTrace();
			throw new Exception("Error creating task: " + taskIn.getProvider() + ". " + ex.getMessage());
		}
		
		
	}

}
