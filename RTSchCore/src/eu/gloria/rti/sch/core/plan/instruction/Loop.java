package eu.gloria.rti.sch.core.plan.instruction;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Loop extends Instruction {
	
	protected Double repeatDuration;
    protected BigInteger repeatCount;
    protected List<Instruction> instructions;
    
    public Loop(){
    	instructions = new ArrayList<Instruction>();
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
	public List<Instruction> getInstructions() {
		return instructions;
	}
	public void setInstructions(List<Instruction> instructions) {
		this.instructions = instructions;
	}

}
