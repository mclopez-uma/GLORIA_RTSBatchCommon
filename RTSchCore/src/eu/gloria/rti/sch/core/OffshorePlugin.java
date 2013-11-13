package eu.gloria.rti.sch.core;

import java.util.List;

import eu.gloria.rt.db.task.TaskProperty;

public abstract class OffshorePlugin {
	
	protected List<TaskProperty> properties;
	
	public OffshorePlugin(){
	}
	
	public void init(List<TaskProperty> properties){
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
	
	public String getPropertyValueString(String key){
		String result  = null;
		TaskProperty prop =  getProperty(key);
		if (prop != null) result = prop.getValue();
		return result;
	}
	
	public double getPropertyValueDouble(String key){
		String result  = null;
		TaskProperty prop =  getProperty(key);
		if (prop != null) result = prop.getValue();
		return Double.parseDouble(result);
	}
	
	public int getPropertyValueInt(String key){
		String result  = null;
		TaskProperty prop =  getProperty(key);
		if (prop != null) result = prop.getValue();
		return Integer.parseInt(result);
	}
	
	public boolean getPropertyValueBoolean(String key){
		String result  = null;
		TaskProperty prop =  getProperty(key);
		if (prop != null) result = prop.getValue();
		return Boolean.parseBoolean(result);
	}
	
}
