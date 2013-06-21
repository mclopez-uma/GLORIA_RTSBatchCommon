package eu.gloria.rti.sch.core;

import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.RTSchException;
import eu.gloria.rti.sch.core.plan.constraint.Constraints;

/**
 * Interface of a Constraint Validator component.
 * 
 * @author jcabello
 *
 */
public interface ConstraintValidator {
	
	public boolean isSatisfied(Constraints constraints, TimeFrame timeFrame) throws RTException;

}
