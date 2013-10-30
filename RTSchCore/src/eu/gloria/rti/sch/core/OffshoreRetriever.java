package eu.gloria.rti.sch.core;

import eu.gloria.rt.exception.RTSchException;

public interface OffshoreRetriever {
	
	public void retrieve(long idOp) throws RTSchException;

}
