package eu.gloria.rti.sch.core;

import java.util.List;

import eu.gloria.rt.entity.scheduler.plan.CameraSettings;
import eu.gloria.rt.entity.scheduler.plan.Expose;
import eu.gloria.rt.entity.scheduler.plan.Loop;
import eu.gloria.rt.entity.scheduler.plan.Target;
import eu.gloria.rt.exception.RTSchException;
import eu.gloria.rti.sch.core.ObservingPlan;
import eu.gloria.rti.sch.core.plan.instruction.Instruction;
import eu.gloria.tools.log.LogUtil;


/**
 * Predictor of OP Execution time.
 * 
 * @author jcabello
 *
 */
public class ObservingPlanExecTimePredictor implements IObservingPlanExecTimePredictor {
	
	private long millisecondsMountMove = 10000;
	private long millisecondsFilterMove = 3000;
	private long millisecondsLooseness = 5000;
	private long millisecondsCameraSettings = 500;
	
	public ObservingPlanExecTimePredictor(long millisecondsMountMove, long millisecondsFilterMove, long millisecondsLooseness, long millisecondsCameraSettings){
		
		this.millisecondsMountMove = millisecondsMountMove;
		this.millisecondsFilterMove = millisecondsFilterMove;
		this.millisecondsLooseness = millisecondsLooseness;
		this.millisecondsCameraSettings = millisecondsCameraSettings;
		
	}
	
	public ObservingPlanExecTimePredictor(){
		
	}
	
	public long getPredictExecTime(ObservingPlan op) throws RTSchException {
		
		long result = millisecondsLooseness;
		
		result = result + getPredictExecTime(op.getInstructions());
		
		return result;
		
	}
	
	
	private long getPredictExecTime(Object item) throws RTSchException {
		
		long result = 0;
		
		if (item instanceof List){
			
			LogUtil.info(this, "getPredictExecTime(). Processing List item.");
			
			List<Instruction> list = (List<Instruction>) item;
		
			for (int x = 0; x < list.size(); x++){
			
				Object obj = list.get(x);
				
				result = result + getPredictExecTime(obj);
			}
			
		} else if (item instanceof eu.gloria.rti.sch.core.plan.instruction.Target){
			LogUtil.info(this, "getPredictExecTime(). Processing Target item.");
			
			result = result + millisecondsMountMove;
			
		} else if (item instanceof eu.gloria.rti.sch.core.plan.instruction.CameraSettings){
			
			LogUtil.info(this, "getPredictExecTime(). Processing CameraSettings item.");
			
			result = result + millisecondsCameraSettings;
			
		}else if (item instanceof eu.gloria.rti.sch.core.plan.instruction.Loop){
			
			LogUtil.info(this, "getPredictExecTime(). Processing Loop item.");
			
			eu.gloria.rti.sch.core.plan.instruction.Loop tmpItemSource = (eu.gloria.rti.sch.core.plan.instruction.Loop) item;
			
			if (tmpItemSource.getRepeatCount() != null){
				
				result = result + (getPredictExecTime(tmpItemSource.getInstructions()) * tmpItemSource.getRepeatCount().intValue());
				
			} else if (tmpItemSource.getRepeatDuration() != null){
				
				result = result + (new Double(tmpItemSource.getRepeatDuration()*1000)).longValue();
			}
			
		}else if (item instanceof eu.gloria.rti.sch.core.plan.instruction.Expose){
			
			LogUtil.info(this, "getPredictExecTime(). Processing Expose item.");
			
			eu.gloria.rti.sch.core.plan.instruction.Expose tmpItemSource = (eu.gloria.rti.sch.core.plan.instruction.Expose) item;
			
			if (tmpItemSource.getFilter() != null){
				result = result + millisecondsFilterMove;
			}
			
			result = result + (new Double(tmpItemSource.getExpositionTime()*1000)).longValue();
			
		} else{
			
			LogUtil.info(this, "getPredictExecTime(). Unknown item. class::" + item.getClass().getName());
			
		}
		
		return result;
	}

}
