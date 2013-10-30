package eu.gloria.rti.sch.core;

import eu.gloria.rt.exception.RTSchException;

public interface OffshorePublisher {
	
	public void publish(long idOp) throws RTSchException;

}
