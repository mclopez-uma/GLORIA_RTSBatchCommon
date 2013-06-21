package eu.gloria.rti.sch.core.plan.constraint;

import java.util.ArrayList;
import java.util.List;

/**
 * Constraints of an Obseving Plan.
 * 
 * @author jcabello
 *
 */
public class Constraints {
	
	/**
	 * Constructor
	 */
	public Constraints(){
		targets = new ArrayList<Constraint>();
	}
	
	private Constraint targetAltitude;
	private List<Constraint> targets;
	private Constraint moonDistance;
	private Constraint moonAltitude;
	
	public Constraint getTargetAltitude() {
		return targetAltitude;
	}
	public void setTargetAltitude(Constraint targetAltitude) {
		this.targetAltitude = targetAltitude;
	}
	public List<Constraint> getTargets() {
		return targets;
	}
	public void setTargets(List<Constraint> targets) {
		this.targets = targets;
	}
	public Constraint getMoonDistance() {
		return moonDistance;
	}
	public void setMoonDistance(Constraint moonDistance) {
		this.moonDistance = moonDistance;
	}
	public Constraint getMoonAltitude() {
		return moonAltitude;
	}
	public void setMoonAltitude(Constraint moonAltitude) {
		this.moonAltitude = moonAltitude;
	}

}
