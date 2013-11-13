package eu.gloria.rt.worker.scheduler.constraints;

import java.util.ArrayList;
import java.util.List;

/**
 * Constraints of an Observing Plan.
 * 
 * @author Alfredo
 */
public class Constraints {
	private List<ConstraintTarget> targets;
	private Constraint targetAltitude;
	private Constraint moonDistance;
	private Constraint moonAltitude;
	
	/**
	 * Default constructor.
	 */
	public Constraints() {
		targets = new ArrayList<ConstraintTarget>();
	}
	
	/**
	 * Getter method to the list of targets.
	 * 
	 * @return The list of targets altitude constraint class.
	 */
	public List<ConstraintTarget> getTargets() {
		return targets;
	}
	
	/**
	 * Setter method to the list of targets.
	 * 
	 * @param targets The new list of targets altitude constraint class.
	 */
	public void setTargets(List<ConstraintTarget> targets) {
		this.targets = targets;
	}
	
	/**
	 * Getter method to target altitude.
	 * 
	 * @return The target altitude constraint class.
	 */
	public Constraint getTargetAltitude() {
		return targetAltitude;
	}
	
	/**
	 * Setter method to target altitude.
	 * 
	 * @param targetAltitude The new target altitude constraint class.
	 */
	public void setTargetAltitude(Constraint targetAltitude) {
		this.targetAltitude = targetAltitude;
	}
	
	/**
	 * Getter method to moon distance.
	 * 
	 * @return The moon distance constraint class.
	 */
	public Constraint getMoonDistance() {
		return moonDistance;
	}
	
	/**
	 * Setter method to moon distance.
	 * 
	 * @param moonDistance The new moon distance constraint class.
	 */
	public void setMoonDistance(Constraint moonDistance) {
		this.moonDistance = moonDistance;
	}
	
	/**
	 * Getter method to moon altitude.
	 * 
	 * @return The moon altitude constraint class.
	 */
	public Constraint getMoonAltitude() {
		return moonAltitude;
	}
	
	/**
	 * Setter method to moon altitude.
	 * 
	 * @param moonAltitude The new moon altitude constraint class.
	 */
	public void setMoonAltitude(Constraint moonAltitude) {
		this.moonAltitude = moonAltitude;
	}
	
	@Override
	public String toString() {
		String str = "ConstraintTarget: ";
		for (ConstraintTarget ct : targets) {
			str += ct + "\t";
		}
		
		str += "\nTarget altitude: " + targetAltitude;
		str += "\nMoon distance: " + moonDistance;
		str += "\nMoon altitude: " + moonAltitude;
		
		return str;
	}
}
