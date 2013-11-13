package eu.gloria.rt.worker.scheduler.constraints;

/**
 * Class to check a specified constraint: Target.
 * 
 * @author Alfredo
 */
public class ConstraintTarget extends Constraint {
	private Coordinates coordinates;
	private String objName;

	/**
	 * Default constructor.
	 */
	public ConstraintTarget() {
		super(ConstraintType.TARGET);
	}

	/**
	 * Getter method to target localization using coordinates.
	 * 
	 * @return The coodinates.
	 */
	public Coordinates getCoordinates() {
		return coordinates;
	}

	/**
	 * Setter method to target localization using coordinates.
	 * 
	 * @param coordinates The new coordinates.
	 */
	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}
	
	/**
	 * Getter method to target localization using the object name.
	 * 
	 * @return The name.
	 */
	public String getObjName() {
		return objName;
	}

	/**
	 * Setter method to target localization using the object name.
	 * 
	 * @param objName The new name.
	 */
	public void setObjName(String objName) {
		this.objName = objName;
	}
	
	@Override
	public String toString() {
		String str = "";
		if (coordinates == null) {
			str = "ObjName: " + objName;
		}else{
			str = "Coordinates: [" + coordinates.getJ2000().getRa();
			str += ", " + coordinates.getJ2000().getDec() + "]";
		}
		return str;
	}
}
