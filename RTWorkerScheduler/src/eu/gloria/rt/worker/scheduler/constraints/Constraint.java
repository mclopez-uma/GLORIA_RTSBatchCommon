package eu.gloria.rt.worker.scheduler.constraints;

/**
 * @author Alfredo
 * 
 *         Base class of the constraints.
 */
public abstract class Constraint {
	private ConstraintType type;

	/**
	 * Constructor.
	 * 
	 * @param type
	 *            Constraint type
	 */
	public Constraint(ConstraintType type) {
		this.type = type;
	}

	/**
	 * Access method to the type of constraint.
	 * 
	 * @return The type of constraint.
	 */
	public ConstraintType getType() {
		return type;
	}

	/**
	 * Access method to change the type of constraint.
	 * 
	 * @param type
	 *            The new type of constraint.
	 */
	public void setType(ConstraintType type) {
		this.type = type;
	}
}
