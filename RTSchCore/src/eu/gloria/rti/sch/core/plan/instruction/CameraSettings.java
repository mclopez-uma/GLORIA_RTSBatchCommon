package eu.gloria.rti.sch.core.plan.instruction;


public class CameraSettings extends Instruction {
	
	protected Binning binning;

	public Binning getBinning() {
		return binning;
	}

	public void setBinning(Binning binning) {
		this.binning = binning;
	}

}
