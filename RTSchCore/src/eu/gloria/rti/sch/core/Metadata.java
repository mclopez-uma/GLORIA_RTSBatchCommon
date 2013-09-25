package eu.gloria.rti.sch.core;

import java.util.GregorianCalendar;

public class Metadata {
	
	private String uuid;
	private String user;
	private String priority;
	private String description;
	private GregorianCalendar predictedExecIni;
	private GregorianCalendar predictedExecEnd;
	private double predictedExecTime;
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public GregorianCalendar getPredictedExecIni() {
		return predictedExecIni;
	}
	public void setPredictedExecIni(GregorianCalendar predictedExecIni) {
		this.predictedExecIni = predictedExecIni;
	}
	public GregorianCalendar getPredictedExecEnd() {
		return predictedExecEnd;
	}
	public void setPredictedExecEnd(GregorianCalendar predictedExecEnd) {
		this.predictedExecEnd = predictedExecEnd;
	}
	public double getPredictedExecTime() {
		return predictedExecTime;
	}
	public void setPredictedExecTime(double predictedExecTime) {
		this.predictedExecTime = predictedExecTime;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
