package eu.gloria.rti.sch.core;

import java.util.List;

import eu.gloria.rt.exception.RTException;
import eu.gloria.rti.sch.core.plan.constraint.Constraints;
import eu.gloria.rti.sch.core.plan.instruction.Instruction;

/**
 * Observing plan super class.
 * 
 * @author jcabello
 *
 */
public abstract class ObservingPlanBase {
	
	private Object plan;
	
	abstract public long getPredictedExecTime(IObservingPlanExecTimePredictor predictor) throws RTException;
	
	abstract public Constraints getConstraints();
	
	abstract public Metadata getMetadata();
	
	abstract public List<Instruction> getInstructions();

	public Object getPlan() {
		return plan;
	}

	public void setPlan(Object plan) {
		this.plan = plan;
	}
	

}
