package eu.gloria.rti.sch.core.plan.instruction;


public class Target extends Instruction {
	
	protected Coordinates coordinates;
	protected String objName;
	  
	public Coordinates getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}
	public String getObjName() {
		return objName;
	}
	public void setObjName(String objName) {
		this.objName = objName;
	}

}
