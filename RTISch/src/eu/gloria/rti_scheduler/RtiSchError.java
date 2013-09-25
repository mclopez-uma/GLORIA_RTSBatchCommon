
package eu.gloria.rti_scheduler;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.6.1
 * 2013-09-17T11:59:13.363+02:00
 * Generated source version: 2.6.1
 */

@WebFault(name = "errorDetail", targetNamespace = "http://gloria.eu/rti_scheduler")
public class RtiSchError extends Exception {
    
    private eu.gloria.rti_scheduler.ErrorDetail errorDetail;

    public RtiSchError() {
        super();
    }
    
    public RtiSchError(String message) {
        super(message);
    }
    
    public RtiSchError(String message, Throwable cause) {
        super(message, cause);
    }

    public RtiSchError(String message, eu.gloria.rti_scheduler.ErrorDetail errorDetail) {
        super(message);
        this.errorDetail = errorDetail;
    }

    public RtiSchError(String message, eu.gloria.rti_scheduler.ErrorDetail errorDetail, Throwable cause) {
        super(message, cause);
        this.errorDetail = errorDetail;
    }

    public eu.gloria.rti_scheduler.ErrorDetail getFaultInfo() {
        return this.errorDetail;
    }
}
