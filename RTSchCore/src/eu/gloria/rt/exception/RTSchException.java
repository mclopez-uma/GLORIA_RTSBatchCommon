package eu.gloria.rt.exception;

import java.math.BigInteger;

public class RTSchException extends RTException {
	
	/**
	 * Error code. 1 by default.
	 */
	private BigInteger errorCode;

	/**
	 * Constructor.
	 */
	public RTSchException(){
		errorCode = new BigInteger("1");
		
		//this.printStackTrace();
	} 
	
	/**
	 * @param message Error message
	 */
	public RTSchException(String message){
		super(message); 
		errorCode = new BigInteger("1");
		
		//this.printStackTrace();
	} 
	
	/**
	 * Constructor.
	 * @param cause internal Exception.
	 */
	public RTSchException(Throwable cause) { 
		super(cause); 
		errorCode = new BigInteger("1");
		
		//this.printStackTrace();
	}
	
	/**
	 * Constructor.
	 * @param message Error message.
	 * @param errCode Error code.
	 */
	public RTSchException(String message, int errCode){
		super(message); 
		errorCode = new BigInteger(String.valueOf(errCode));
		
		//this.printStackTrace();
	}
	
	/**
	 * Constructor.
	 * @param message Error message.
	 * @param errCode Error code.
	 */
	public RTSchException(String message, BigInteger errCode){
		super(message); 
		errorCode = errCode;
		
		//this.printStackTrace();
	}
	
	/**
	 * Constructor.
	 * @param cause Internal Exception.
	 * @param errCode Error code.
	 */
	public RTSchException(Throwable cause, int errCode) { 
		super(cause); 
		errorCode = new BigInteger(String.valueOf(errCode));
		
		//this.printStackTrace();
	}
	
	/**
	 * Constructor.
	 * @param cause Internal Exception.
	 * @param errCode Error code.
	 */
	public RTSchException(Throwable cause, BigInteger errCode) { 
		super(cause); 
		errorCode = errCode;
		
		//this.printStackTrace();
	}

	/**
	 * Access method
	 * @return Error code.
	 */
	public BigInteger getErrorCode() {
		return errorCode;
	}

	/**
	 * Access method
	 * @param errorCode error code.
	 */
	public void setErrorCode(BigInteger errorCode) {
		this.errorCode = errorCode;
	}
}