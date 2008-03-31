/* 
* Created on 11. December 2007
 * 
 */
package de.layclust.taskmanaging;

import java.util.Vector;

/**
 * A collection of static methods for task managing.
 * @author sita
 */
public class TaskUtility {
	
	/**
	 * Converts the time given in long by the System methods into a human readable form.
	 * @param timeInMillis The time as a long.
	 * @return The time in a readable form.
	 */
	public static String convertTime(long timeInMillis) {
		
		int h = 0;
		int m = 0;
		int s = 0;
		
		s = (int) Math.rint(timeInMillis/1000);
		m = (int) Math.rint(s/60);
		h = (int) Math.rint(m/60);
		
		String str = new String();
		
		if ((h == 0) && (m == 0)) {
			str = s + "s";
		} else if (h == 0) {
			s = s - (m*60);
			str = m + "min " + s + "s";
		} else {
			s = s - (m*60);
			m = m - (h*60);
			str = h + "h " + m + "min " + s + "s";
		}
		
		return str;
	}

}
