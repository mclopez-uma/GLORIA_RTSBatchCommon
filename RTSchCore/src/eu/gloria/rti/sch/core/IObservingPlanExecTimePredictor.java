package eu.gloria.rti.sch.core;

import eu.gloria.rt.exception.RTSchException;

public interface IObservingPlanExecTimePredictor {
	
	public long getPredictExecTime(ObservingPlan op) throws RTSchException;

}
