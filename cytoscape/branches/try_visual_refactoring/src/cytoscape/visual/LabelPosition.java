/*
 File: LabelPosition.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package cytoscape.visual;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LabelPosition {

	public static final int NONE = 9;

	public static final int NORTHWEST = 0;
	public static final int NORTH = 1;
	public static final int NORTHEAST = 2;

	public static final int WEST = 3;
	public static final int CENTER = 4;
	public static final int EAST = 5;

	public static final int SOUTHWEST = 6;
	public static final int SOUTH = 7;
	public static final int SOUTHEAST = 8;

	public static final int JUSTIFY_CENTER = 64;
	public static final int JUSTIFY_LEFT = 65;
	public static final int JUSTIFY_RIGHT = 66;

	public static final String northWestName = "Northwest";
	public static final String northName = "North";
	public static final String northEastName = "Northeast";

	public static final String westName = "West";
	public static final String centerName = "Center";
	public static final String eastName = "East";

	public static final String southWestName = "Southwest";
	public static final String southName = "South";
	public static final String southEastName = "Southeast";

	protected static final String nwName = "NW";
	protected static final String nName = "N";
	protected static final String neName = "NE";

	protected static final String wName = "W";
	protected static final String cName = "C";
	protected static final String eName = "E";

	protected static final String swName = "SW";
	protected static final String sName = "S";
	protected static final String seName = "SE";

	public static final String noName = "none";

	public static final String justifyCenterName = "Center Justified";
	public static final String justifyLeftName = "Left Justified";
	public static final String justifyRightName = "Right Justified";

	protected static final String justifyCName = "C";
	protected static final String justifyLName = "L";
	protected static final String justifyRName = "R";

	protected int labelAnchor;
	protected int targetAnchor;
	protected int justify;
	protected double xOffset;
	protected double yOffset;

	public LabelPosition() {
		this(CENTER,CENTER,JUSTIFY_CENTER,0.0,0.0);
	}

	public LabelPosition(LabelPosition lp) {
		labelAnchor = lp.getLabelAnchor();
		targetAnchor = lp.getTargetAnchor();
		xOffset = lp.getOffsetX();
		yOffset = lp.getOffsetY();
		justify = lp.getJustify();
	}

	public LabelPosition(int lab, int targ, int just, double x, double y) {
		labelAnchor = lab;
		targetAnchor = targ;
		justify = just;
		xOffset = x;
		yOffset = y;
	}

	public int getLabelAnchor() {
		return labelAnchor;
	}
	public int getTargetAnchor() {
		return targetAnchor;
	}
	public int getJustify() {
		return justify;
	}
	public double getOffsetX() {
		return xOffset;
	}
	public double getOffsetY() {
		return yOffset;
	}
	public void setLabelAnchor(int b) {
		labelAnchor = b;
	}
	public void setTargetAnchor(int b) {
		targetAnchor = b;
	}
	public void setJustify(int b) {
		justify = b;
	}
	public void setOffsetX(double d) {
		xOffset = d;
	}
	public void setOffsetY(double d) {
		yOffset = d;
	}

	public static String convert(int b) {
		switch(b) {
			case(NORTH):
				return northName;
			case(SOUTH):
				return southName;
			case(EAST):
				return eastName;
			case(WEST):
				return westName;
			case(NORTHWEST):
				return northEastName;
			case(NORTHEAST):
				return northWestName;
			case(SOUTHWEST):
				return southEastName;
			case(SOUTHEAST):
				return southWestName;
			case(CENTER):
				return centerName;
			case(NONE):
				return noName;
			case(JUSTIFY_CENTER):
				return justifyCenterName;
			case(JUSTIFY_LEFT):
				return justifyLeftName;
			case(JUSTIFY_RIGHT):
				return justifyRightName;
			default:
				return null;
		}
	}

	public static int convert(String s) {
		if ( northName.equals(s) )
			return NORTH;
		else if ( southName.equals(s) )
			return SOUTH;
		else if ( eastName.equals(s) )
			return EAST;
		else if ( westName.equals(s) )
			return WEST;
		else if ( northWestName.equals(s) )
			return NORTHWEST;
		else if ( northEastName.equals(s) )
			return NORTHEAST;
		else if ( southWestName.equals(s) )
			return SOUTHWEST;
		else if ( southEastName.equals(s) )
			return SOUTHEAST;
		else if ( centerName.equals(s) )
			return CENTER;
		if ( justifyCenterName.equals(s) )
			return JUSTIFY_CENTER;
		else if ( justifyLeftName.equals(s) )
			return JUSTIFY_LEFT;
		else if ( justifyRightName.equals(s) )
			return JUSTIFY_RIGHT;
		else
			return -1;
	}

	public static String[] getJustifyNames() {
		String[] s = {justifyLeftName,justifyCenterName,justifyRightName};
		return s;
	}

	public static String[] getAnchorNames() {
		String[] s = { northWestName,  northName,  northEastName ,
			       westName,       centerName, eastName ,
			       southWestName , southName,  southEastName };
		return s;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" label ").append(Integer.toString(labelAnchor)).append("  ").append(convert(labelAnchor)).append("\n");
		sb.append(" target ").append(Integer.toString(targetAnchor)).append("  ").append(convert(targetAnchor)).append("\n");
		sb.append(" justify ").append(Integer.toString(justify)).append("  ").append(convert(justify)).append("\n");
		sb.append(" xoffset ").append(Double.toString(xOffset)).append("\n");
		sb.append(" yoffset ").append(Double.toString(yOffset)).append("\n");
		return sb.toString();

	}

	public String shortString() {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		StringBuffer sb = new StringBuffer();
		sb.append(getShortName(labelAnchor));
		sb.append(",");
		sb.append(getShortName(targetAnchor));
		sb.append(",");
		sb.append(getShortName(justify));
		sb.append(",");
		sb.append(df.format(xOffset));
		sb.append(",");
		sb.append(df.format(yOffset));

		return sb.toString();
	}

	public static LabelPosition parse(String value) {
		Pattern p = Pattern.compile("^([NSEWC]{1,2}+),([NSEWC]{1,2}+),([CLR]{1}+),(-?\\d+.\\d+),(-?\\d+.\\d+)$");
		Matcher m = p.matcher(value);
		LabelPosition lp = new LabelPosition();
		lp.setLabelAnchor( convert(m.group(0)) );
		lp.setTargetAnchor( convert(m.group(1)) );
		lp.setJustify( convert(m.group(2)) );
		lp.setOffsetX( Double.parseDouble(m.group(3)) );
		lp.setOffsetY( Double.parseDouble(m.group(4)) );

		return lp;
	}

	protected static String getShortName(int x) {
		switch(x) {
			case(NORTH):
				return nName;
			case(SOUTH):
				return sName;
			case(EAST):
				return eName;
			case(WEST):
				return wName;
			case(NORTHWEST):
				return neName;
			case(NORTHEAST):
				return nwName;
			case(SOUTHWEST):
				return seName;
			case(SOUTHEAST):
				return swName;
			case(CENTER):
				return cName;
			case(JUSTIFY_CENTER):
				return justifyCName;
			case(JUSTIFY_LEFT):
				return justifyLName;
			case(JUSTIFY_RIGHT):
				return justifyRName;
			default:
				return "x";
		}
	}

	public static final LabelPosition DEFAULT = new LabelPosition();
}

