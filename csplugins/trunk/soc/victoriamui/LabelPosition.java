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

import giny.view.Label;

import java.text.DecimalFormat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cytoscape.logger.CyLogger;


/**
 * This class represents the positioning of a label.  Each LabelPosition
 * object specifies its target's position, the label's position, the
 * justification of the text in the label, the X offset of the label,
 * and the Y offset of the label.  Each position parameter can be one of the
 * following: Northwest, North, Norteast, West, Center, East, Southwest, South,
 * or Southeast.  The justification of the label can be one of the following:
 * Center Justified, Left Justified, or Right Justified.
 */
public class LabelPosition {
	
	/**
	 * 
	 */
	public static final String northWestName = "Northwest";

	/**
	 *
	 */
	public static final String northName = "North";

	/**
	 *
	 */
	public static final String northEastName = "Northeast";

	/**
	 *
	 */
	public static final String westName = "West";

	/**
	 *
	 */
	public static final String centerName = "Center";

	/**
	 *
	 */
	public static final String eastName = "East";

	/**
	 *
	 */
	public static final String southWestName = "Southwest";

	/**
	 *
	 */
	public static final String southName = "South";

	/**
	 *
	 */
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

	/**
	 *
	 */
	public static final String noName = "none";

	/**
	 *
	 */
	public static final String justifyCenterName = "Center Justified";

	/**
	 *
	 */
	public static final String justifyLeftName = "Left Justified";

	/**
	 *
	 */
	public static final String justifyRightName = "Right Justified";
	protected static final String justifyCName = "c";
	protected static final String justifyLName = "l";
	protected static final String justifyRName = "r";
	protected int labelAnchor;
	protected int targetAnchor;
	protected int justify;
	protected double xOffset;
	protected double yOffset;

	/**
	 * Creates a new LabelPosition object.  The new LabelPosition object
	 * has the following default settings: 
	 * - Target anchor: Center
	 * - Label anchor: Center
	 * - Label justification: Center
	 * - Offset (x, y): (0.0, 0.0)
	 */
	public LabelPosition() {
		this(Label.CENTER, Label.CENTER, Label.JUSTIFY_CENTER, 0.0, 0.0);
	}

	/**
	 * Creates a new LabelPosition object.  The new LabelPosition object has
	 * the same anchors, offset, and justification as lp.
	 *
	 * @param lp  an existing LabelPosition object.
	 */
	public LabelPosition(LabelPosition lp) {
		targetAnchor = lp.getTargetAnchor();
		labelAnchor = lp.getLabelAnchor();
		xOffset = lp.getOffsetX();
		yOffset = lp.getOffsetY();
		justify = lp.getJustify();
	}

	/**
	 * Creates a new LabelPosition object.
	 *
	 * @param targ  the anchor of the target
	 * @param lab  the anchor of the label
	 * @param just  the justification of the label
	 * @param x  the horizontal offset of the label
	 * @param y  the vertical offset of the label
	 */
	public LabelPosition(int targ, int lab, int just, double x, double y) {
		targetAnchor = targ;
		labelAnchor = lab;
		justify = just;
		xOffset = x;
		yOffset = y;
	}

	/**
	 *  Returns the label anchor of this LabelPosition object.
	 *
	 * @return  the label anchor of this LabelPosition object
	 */
	public int getLabelAnchor() {
		return labelAnchor;
	}

	/**
	 *  Returns the target anchor of this LabelPosition object.
	 *
	 * @return  the target anchor of this Label Position object.
	 */
	public int getTargetAnchor() {
		return targetAnchor;
	}

	/**
	 *  Returns the justification of the label.
	 *
	 * @return  the justification of the label
	 */
	public int getJustify() {
		return justify;
	}

	/**
	 *  Returns the horizontal offset, x, of the label.
	 *
	 * @return  the horizontal offset of the label
	 */
	public double getOffsetX() {
		return xOffset;
	}

	/**
	 *  Returns the vertical offset, y, of the label.
	 *
	 * @return  the vertical offset of the label
	 */
	public double getOffsetY() {
		return yOffset;
	}

	/**
	 *  Sets the label anchor of this LabelPosition object to b.
	 *
	 * @param b the int value representing the desired label anchor position
	 */
	public void setLabelAnchor(int b) {
		labelAnchor = b;
	}

	/**
	 *  Sets the target anchor of this LabelPosition object to b.
	 *
	 * @param b the int value representing the desired target anchor position
	 */
	public void setTargetAnchor(int b) {
		targetAnchor = b;
	}

	/**
	 *  Sets label justification to b.
	 *
	 * @param b the int value representing the desired label justification
	 */
	public void setJustify(int b) {
		justify = b;
	}

	/**
	 *  Sets the horizontal offset, x, to d.
	 *
	 * @param d the desired horizontal offset value
	 */
	public void setOffsetX(double d) {
		xOffset = d;
	}

	/**
	 *  Sets the vertical offset, y, to d.
	 *
	 * @param d the desired vertical offset value
	 */
	public void setOffsetY(double d) {
		yOffset = d;
	}

	/**
	 *  Compares this LabelPosition object with the specified LabelPosition
	 *  object lp.  Returns true if and only if lp is not null, lp is a
	 *  LabelPosition object, and lp represents the same label position
	 * 	(i.e., the two LabelPosition objects have the same set of positioning
	 *  parameter values).
	 *
	 * @param lp the LabelPosition object to compare this LabelPosition object
	 * with
	 *
	 * @return true if this LabelPosition object is the same as lp; returns
	 * false otherwise
	 */
	public boolean equals(Object lp) {
		if (lp == null)
			return false;

		if (lp instanceof LabelPosition) {
			LabelPosition LP = (LabelPosition) lp;

			if (Math.abs(LP.getOffsetX() - xOffset) > 0.0000001) {
				CyLogger.getLogger().info("xoff");

				return false;
			}

			if (Math.abs(LP.getOffsetY() - yOffset) > 0.0000001) {
				CyLogger.getLogger().info("yoff");

				return false;
			}

			if (LP.getLabelAnchor() != labelAnchor) {
				CyLogger.getLogger().info("label");

				return false;
			}

			if (LP.getTargetAnchor() != targetAnchor) {
				CyLogger.getLogger().info("taret");

				return false;
			}

			if (LP.getJustify() != justify) {
				CyLogger.getLogger().info("justify");

				return false;
			}

			return true;
		} else {
			CyLogger.getLogger().info("not lp");

			return false;
		}
	}

	/**
	 *  Given position b as an int, this method returns the corresponding
	 *  String representation of this position.  Returns null if b is
	 *  an invalid position.
	 *
	 * @param b the position that is to be converted from an int to its
	 * corresponding String
	 *
	 * @return  the corresponding String representation of position b if b is
	 * a valid int position; returns null otherwise
	 */
	public static String convert(int b) {
		switch (b) {
			case (Label.NORTH):
				return northName;

			case (Label.SOUTH):
				return southName;

			case (Label.EAST):
				return eastName;

			case (Label.WEST):
				return westName;

			case (Label.NORTHWEST):
				return northWestName;

			case (Label.NORTHEAST):
				return northEastName;

			case (Label.SOUTHWEST):
				return southWestName;

			case (Label.SOUTHEAST):
				return southEastName;

			case (Label.CENTER):
				return centerName;

			case (Label.NONE):
				return noName;

			case (Label.JUSTIFY_CENTER):
				return justifyCenterName;

			case (Label.JUSTIFY_LEFT):
				return justifyLeftName;

			case (Label.JUSTIFY_RIGHT):
				return justifyRightName;

			default:
				return null;
		}
	}

	/**
	 *  Given position s as a String, this method returns the corresponding
	 *  int representation of this position.  Returns -1 if s is an invalid
	 *  String position.
	 *
	 * @param s the position that is to be converted from an String to its
	 * corresponding int
	 *
	 * @return  the corresponding int representation of position s if s is 
	 * a valid String position; returns -1 otherwise
	 */
	public static int convert(String s) {
		if (northName.equals(s) || nName.equals(s))
			return Label.NORTH;
		else if (southName.equals(s) || sName.equals(s))
			return Label.SOUTH;
		else if (eastName.equals(s) || eName.equals(s))
			return Label.EAST;
		else if (westName.equals(s) || wName.equals(s))
			return Label.WEST;
		else if (northWestName.equals(s) || nwName.equals(s))
			return Label.NORTHWEST;
		else if (northEastName.equals(s) || neName.equals(s))
			return Label.NORTHEAST;
		else if (southWestName.equals(s) || swName.equals(s))
			return Label.SOUTHWEST;
		else if (southEastName.equals(s) || seName.equals(s))
			return Label.SOUTHEAST;
		else if (centerName.equals(s) || cName.equals(s))
			return Label.CENTER;
		else if (justifyCenterName.equals(s) || justifyCName.equals(s))
			return Label.JUSTIFY_CENTER;
		else if (justifyLeftName.equals(s) || justifyLName.equals(s))
			return Label.JUSTIFY_LEFT;
		else if (justifyRightName.equals(s) || justifyRName.equals(s))
			return Label.JUSTIFY_RIGHT;
		else

			return -1;
	}

	/**
	 *  Returns the names of the 3 possible justifications as a String array.
	 *
	 * @return the names of the 3 possible justifications as a String array
	 */
	public static String[] getJustifyNames() {
		String[] s = { justifyLeftName, justifyCenterName, justifyRightName };

		return s;
	}

	/**
	 *  Returns the names of the 9 possible name anchoring positions as a
	 *  String array.
	 *
	 * @return  the names of the 9 possible name anchoring positions as a
	 * String array
	 */
	public static String[] getAnchorNames() {
		String[] s = {
		                 northWestName, northName, northEastName, westName, centerName, eastName,
		                 southWestName, southName, southEastName
		             };

		return s;
	}

	/**
	 *  Returns the String representation of this LabelPosition object.  This
	 *  includes returning the target and label position, the justification
	 *  of the label, and the X and Y offset of this label.
	 *
	 * @return  the String representation of this LabelPosition object
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("target: ").append(convert(targetAnchor));
		sb.append("  label: ").append(convert(labelAnchor));
		sb.append("  justify: ").append(convert(justify));
		sb.append("  X offset: ").append(Double.toString(xOffset));
		sb.append("  Y offset: ").append(Double.toString(yOffset));

		return sb.toString();
	}

	/**
	 *  Returns a shortened String representation of this LabelPosition object.
	 *  The returned String consists of the target anchor position, label anchor
	 *  position, the label justification, the x offset, and the y offset
	 *  respectively, separated by a single comma.
	 *
	 * @return  the shortened String representation of this LabelPosition object
	 */
	public String shortString() {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);

		StringBuffer sb = new StringBuffer();
		sb.append(getShortName(targetAnchor));
		sb.append(",");
		sb.append(getShortName(labelAnchor));
		sb.append(",");
		sb.append(getShortName(justify));
		sb.append(",");
		sb.append(df.format(xOffset));
		sb.append(",");
		sb.append(df.format(yOffset));

		return sb.toString();
	}


	/**
	 *  Returns a new LabelPosition object, which is constructed based
	 *  on the position parameters specified in String value.  The String
	 *  value must be of the form:
	 *  
	 *  "<target position anchor>,<label position anchor>,<label justification>,<X offset>,<Y offset>"
	 *  
	 *  If String value does not have the above format, null will be returned.
	 *
	 * @param value the String specifying the values of the anchoring position
	 * of the target, the anchoring position of the label, the justification
	 * of the label, the X offset of the label, and the Y offset of the label,
	 * respectively 
	 *
	 * @return a new LabelPosition object constructed based on the values
	 * specified in String value; returns null if String value is not in the
	 * correct format
	 */
	public static LabelPosition parse(String value) {
		Pattern p = Pattern.compile("^([NSEWC]{1,2}+),([NSEWC]{1,2}+),([clr]{1}+),(-?\\d+(.\\d+)?),(-?\\d+(.\\d+)?)$");
		Matcher m = p.matcher(value);

		if (m.matches()) {
			LabelPosition lp = new LabelPosition();
			lp.setTargetAnchor(convert(m.group(1)));
			lp.setLabelAnchor(convert(m.group(2)));
			lp.setJustify(convert(m.group(3)));
			lp.setOffsetX(Double.parseDouble(m.group(4)));
			lp.setOffsetY(Double.parseDouble(m.group(6)));

			return lp;
		}

		return null;
	}

	/* Helper method called by shortString().  Given a position value as an
	 * int, this method does the conversion and returns the corresponding
	 * position as a String.  If position x is not recognized, String "x"
	 * will be returned.
	 */
	protected static String getShortName(int x) {
		switch (x) {
			case (Label.NORTH):
				return nName;

			case (Label.SOUTH):
				return sName;

			case (Label.EAST):
				return eName;

			case (Label.WEST):
				return wName;

			case (Label.NORTHWEST):
				return nwName;

			case (Label.NORTHEAST):
				return neName;

			case (Label.SOUTHWEST):
				return swName;

			case (Label.SOUTHEAST):
				return seName;

			case (Label.CENTER):
				return cName;

			case (Label.JUSTIFY_CENTER):
				return justifyCName;

			case (Label.JUSTIFY_LEFT):
				return justifyLName;

			case (Label.JUSTIFY_RIGHT):
				return justifyRName;

			default:
				CyLogger.getLogger().info("don't recognize type: " + x);

				return "x";
		}
	}

	/**
	 * Constant representing the default LabelPosition object with:
	 * - target anchor set to Center
	 * - label anchor set to Center
	 * - label justification set to Center
	 * - X offset set to 0.0
	 * - Y offset set to 0.0
	 */
	public static final LabelPosition DEFAULT = new LabelPosition();
}
