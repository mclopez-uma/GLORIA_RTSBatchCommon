package eu.gloria.rti.sch.core.plan.constraint;

/**
 * Base class of the constraints.
 * 
 * @author jcabello
 *
 */
public class Constraint {
	
	/**
	 * Constructor.
	 * @param type Constraint type
	 */
	public Constraint(ConstraintType type){
		this.type = type;
	}
	
	/**
	 * Constraint type.
	 */
	private ConstraintType type;

	/**
	 * Access method.
	 * @return value.
	 */
	public ConstraintType getType() {
		return type;
	}

	/**
	 * Access method.
	 * @param type value.
	 */
	public void setType(ConstraintType type) {
		this.type = type;
	}

}
