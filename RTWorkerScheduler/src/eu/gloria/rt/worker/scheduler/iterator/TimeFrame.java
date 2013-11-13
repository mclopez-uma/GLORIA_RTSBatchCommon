package eu.gloria.rt.worker.scheduler.iterator;

import java.util.Date;

@SuppressWarnings("rawtypes")
public class TimeFrame implements Comparable {
	private Date init;
	private Date end;
	
	public Date getInit() {
		return init;
	}
	
	public void setInit(Date init) {
		this.init = init;
	}
	
	public Date getEnd() {
		return end;
	}
	
	public void setEnd(Date end) {
		this.end = end;
	}

	@Override
	public int compareTo(Object obj) {
		if (obj instanceof TimeFrame) {
			return init.compareTo(((TimeFrame) obj).getInit());
		}
		return 0;
	}
	
	public String toString(){
		return "TimeFrame:[" + init + ", " + end + "]";
	}
}
