// XYMetaData


package csplugins.isb.dante.plot2d;

import java.util.regex.*;

/**
 * Represents the data from a specific plot point.
 * 
 * @author Dan Tenenbaum
 */
public class XYMetaData {
   
	private String x; // these are strings and not doubles
	private String y; // because of problems with Double.parseInt()
					  // let the client deal with it. ;)
	private String row;
	private String xTickName = null;

	
	/**
	 * Creates a new XYMetaData. This class uses Strings instead of doubles
	 * for the x and y values, because Double.parseDouble(String) was giving
	 * incorrect results in the parseToolTip() method. 
	 * 
	 * @param x The x data point, expressed as a String
	 * @param y The y data point, expressed as a String
	 * @param row The Name Of The Row
	 * @param xTickName The name of the tick at this data point
	 */
	public XYMetaData(String x, String y, String row,
	  String xTickName) {
	  	this.x = x;
	  	this.y = y;
	  	this.row = row;
	  	this.xTickName = xTickName;
	} //ctor
	
	/**
	 * @return The x value for this data point expressed as a String.
	 */
	public String getX() {
		return x;
	} //getX
	
	
	/**
	 * @return The y value for this data point expressed as a String
	 */
	public String getY() {
		return y;
	} // getY
	
	
	/**
	 * @return The name of the row represented by this data point
	 */
	public String getRow() {
		return row;
	}
	
	
	/**
	 * @return The name of the tick at this data point
	 * @throws NullPointerException If no name is set
	 */
	public String getXTickName()  throws NullPointerException {
		if (null==xTickName)throw new NullPointerException();
		return xTickName;
	} //getXTickName
	



	/**
	 * Generates an XYMetaData object by parsing the tool tip
	 * shown when the user mouses over a data point. This is the only way
	 * to get this data, apparently. If there is no name for the X axis
	 * tick at the current data point, the corresponding member in this
	 * object will be set to null. <P>
	 * 
	 * @param tooltipText The text of the tooltip for this data point.
	 * @return An XYMetaData containing information about this data point 
	 */
	public static XYMetaData parseToolTip(String tooltipText) {
		// TODO - localize this method to handle 
		// foreign representations of doubles?		
		String x = null;
		String y = null;
		String row = null;
		String xTickName = null; 
		
		Pattern p = Pattern.compile("(.*?)=(.*?),");
		Matcher m = p.matcher(tooltipText+",");
		while (m.find()) {
			String m1 = m.group(1);
			if(null == m1)continue;
			m1 = m1.trim();
			if ("x".equals(m1)) {
				x=m.group(2);
			} else if ("y".equals(m1)) {
				y=m.group(2);
			} else if ("row".equals(m1)) {
				row = m.group(2);
			} else {
				xTickName = m.group(2);
			}
		}
		System.out.println();
		return new XYMetaData(x, y, row, xTickName);
	}
} // XYMetaData