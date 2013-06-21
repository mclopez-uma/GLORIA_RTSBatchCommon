package eu.gloria.rti.sch.core.plan.constraint;


/**
 * Constraint - Target.
 * 
 * @author jcabello
 *
 */
public class ConstraintTarget extends Constraint {
	
	private Coordinates coordinates;
	
	protected String objName;

	/**
	 * Constructor.
	 */
	public ConstraintTarget() {
		super(ConstraintType.TARGET);
	}

	/**
	 * Access method.
	 * @return value.
	 */
	public Coordinates getCoordinates() {
		return coordinates;
	}

	/**
	 * Access method.
	 * @param coordinates value.
	 */
	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}
	
	/**
	 * Access method.
	 * @return value.
	 */
	public String getObjName() {
		return objName;
	}

	/**
	 * Access method.
	 * @param objName value.
	 */
	public void setObjName(String objName) {
		this.objName = objName;
	}

}
