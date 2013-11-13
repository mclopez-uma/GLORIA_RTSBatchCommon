package eu.gloria.rt.worker.scheduler.times;

import java.util.Calendar;

public class SharedTimePortion {
	private String[] strMoments = new String[] { /*"year", "month",*/ "monday", "tuesday",
			"wednesday", "thursday", "friday", "saturday", "sunday", };
	private int[] calMoments = new int[] { /*Calendar.YEAR, Calendar.MONTH,*/ Calendar.MONDAY, Calendar.TUESDAY,
			Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY, };
	private int moment;
	private int[] init;  // init[0]=Hours, init[1]=Minutes
	private int[] end;   //  end[0]=Hours,  end[1]=Minutes
	
	public SharedTimePortion(String moment) {
		this.moment = convertMomentToConstant(moment);
		this.init = new int[] { 0, 0};
		this.end  = new int[] {24, 0};
	}
	
	public SharedTimePortion(String moment, String init, String end) {
		this.moment = convertMomentToConstant(moment);
		
		String[] strTime = init.split(":");
		this.init = new int[2];
		this.init[0] = Integer.parseInt(strTime[0]);
		this.init[1] = Integer.parseInt(strTime[1]);
		
		strTime = end.split(":");
		this.end = new int[2];
		this.end[0] = Integer.parseInt(strTime[0]);
		this.end[1] = Integer.parseInt(strTime[1]);
	}
	
	private int convertMomentToConstant(String moment) {
		int i;
		for (i=0 ; i<calMoments.length ; i++) {
			if (strMoments[i].equalsIgnoreCase(moment)) {
				return i;
			}
		}
		throw new RuntimeException("SharedTimePortion:: ShareTimeFrame '" + moment + "' not valid");
	}

	public String getMomentStr() {
		return strMoments[moment];
	}

	public int getMomentIndex() {
		return moment;
	}

	public int getMomentIntCalendar() {
		return calMoments[moment];
	}

	public int[] getInit() {
		return init;
	}

	public int[] getEnd() {
		return end;
	}

	@Override
	public int hashCode() {
		String str = (init[0]<=9 ? "0" : "") + init[0];
		str += (init[1]<=9 ? "0" : "") + init[1];
		return str.hashCode();
	}

	@Override
	public String toString() {
		return "{" + getMomentStr() + "," + init[0] + ":" + init[1] + "->" + end[0] + ":" + end[1] + "}";
	}
}
