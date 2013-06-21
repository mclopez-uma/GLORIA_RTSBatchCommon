package eu.gloria.rti.sch.core;

import eu.gloria.rt.exception.RTSchException;

public interface OffshorePublisher {
	
	public void publish(String idOp) throws RTSchException;

}
