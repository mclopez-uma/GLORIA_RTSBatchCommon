package eu.gloria.rti.sch.core.plan.instruction;

import java.math.BigInteger;

public class Expose extends Instruction {
	
    protected double expositionTime;
    protected Double repeatDuration;
    protected BigInteger repeatCount;
    protected String filter;
    
	public double getExpositionTime() {
		return expositionTime;
	}
	public void setExpositionTime(double expositionTime) {
		this.expositionTime = expositionTime;
	}
	public Double getRepeatDuration() {
		return repeatDuration;
	}
	public void setRepeatDuration(Double repeatDuration) {
		this.repeatDuration = repeatDuration;
	}
	public BigInteger getRepeatCount() {
		return repeatCount;
	}
	public void setRepeatCount(BigInteger repeatCount) {
		this.repeatCount = repeatCount;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}

}
