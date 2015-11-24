package com.addongaming.hcessentials.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
	private Date date;

	public TimeUtils(long time) {
		this.date = new Date(time);
	}

	@Override
	public String toString() {
		long diff = date.getTime();
		StringBuilder sb = new StringBuilder();
		long secondInMillis = 1000;
		long minuteInMillis = secondInMillis * 60;
		long hourInMillis = minuteInMillis * 60;
		long dayInMillis = hourInMillis * 24;
		long weekInMillis = dayInMillis*7;
		long elapsedWeeks = diff/weekInMillis;
		diff=diff%weekInMillis;
		long elapsedDays = diff / dayInMillis;
		diff = diff % dayInMillis;
		long elapsedHours = diff / hourInMillis;
		diff = diff % hourInMillis;
		long elapsedMinutes = diff / minuteInMillis;
		diff = diff % minuteInMillis;
		long elapsedSeconds = diff / secondInMillis;
		//Week
		if(elapsedWeeks==1){
			sb.append("1 week, ");
		}else if(elapsedWeeks>1)
			sb.append(elapsedWeeks + " weeks, ");
		//Day
		if(elapsedDays==1){
			sb.append("1 day, ");
		}else if(elapsedDays>1)
			sb.append(elapsedDays + " days, ");
		//Hour
		if(elapsedHours==1){
			sb.append("1 hour, ");
		}else if(elapsedHours>1)
			sb.append(elapsedHours + " hours, ");
		if(elapsedMinutes==1){
			sb.append("1 minute, ");
		}else if(elapsedMinutes>1)
			sb.append(elapsedMinutes + " minutes, ");
		if(elapsedSeconds==1){
			sb.append("1 second, ");
		}else if(elapsedSeconds>1)
			sb.append(elapsedSeconds + " seconds, ");
		/**
		if (hasHours())
			sb.append(getHours() + (getHours() > 1 ? " hours, " : " hour, "));
		if (hasMinutes())
			sb.append(getMinutes()
					+ (getMinutes() > 1 ? " minutes, " : " minute, "));
		if (hasSeconds())
			sb.append(getSeconds()
					+ (getSeconds() > 1 ? " seconds, " : " second, "));*/
		
		if (sb.length() > 4) {
			sb.deleteCharAt(sb.length() - 1);
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public boolean hasMinutes() {
		return getMinutes() > 0;
	}

	public boolean hasSeconds() {
		return getSeconds() > 0;
	}

	public boolean hasHours() {
		return getHours() > 0;
	}

	public int getMinutes() {
		return Integer.parseInt(new SimpleDateFormat("mm").format(date));

	}

	public int getSeconds() {
		return Integer.parseInt(new SimpleDateFormat("ss").format(date));

	}

	public int getHours() {
		return Integer.parseInt(new SimpleDateFormat("HH").format(date)) - 1;
	}
}
