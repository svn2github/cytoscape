/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: ConfigColorSet.java,v $
 * $Revision: 1.5 $
 * $Date: 2004/12/21 03:28:14 $
 * $Name:  $
 *
 * This file is part of Java TreeView
 * Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
 *
 * This software is provided under the GNU GPL Version 2. In particular,
 *
 * 1) If you modify a source file, make a comment in it containing your name and the date.
 * 2) If you distribute a modified version, you must do it under the GPL 2.
 * 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
 *
 * A full copy of the license can be found in gpl.txt or online at
 * http://www.gnu.org/licenses/gpl.txt
 *
 * END_HEADER
 */
package edu.stanford.genetics.treeview;

import java.awt.*;
/**
 *  a color set which can be stored in an ConfigNode.
 *
 * @author     Alok Saldanha <alok@genome.stanford.edu>
 * @version    @version $Revision: 1.5 $ $Date: 2004/12/21 03:28:14 $
 */
public class ConfigColorSet implements ColorSetI, ConfigNodePersistent {
	private ConfigNode configNode;

	private Color[] colors;

	private String[] types;

	/*inherit description */
	public String[] getTypes() {
		return types;
	}


	/*inherit description */
	private void setTypes(String[] types) {
		this.types = types;
	}


	private String[] defaultColors;

	/**
	* used to reset the colors to their defaults when requested.
	*/
	public String[] getDefaultColors() {
		return defaultColors;
	}


	/**
	* used by subclasses to set what they want their default colors to be.
	 *
	 * @param  defaultColors  The new defaultColors value
	 */
	private void setDefaultColors(String[] defaultColors) {
		this.defaultColors = defaultColors;
	}


	private String name;
	/**
	 *  Setter for name of the ColorSet
	 *
	 * @param  name  The new name value
	 */
	public void setName(String name) {
		if (configNode != null) {
			configNode.setAttribute("name", name, defaultName);
		}
		this.name = name;
	}

	/* inherit description */
	public String getName() {
		return name;
	}


	private String defaultName;


	/**
	*  Sets the default name of all color sets within the class.
	* This should not be monkeyed with, except in the constructor of a subclass.
	 */
	protected void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}


	/**
	 *  Getter for defaultName
	 *
	 * @return    The defaultName value
	 */
	public String getDefaultName() {
		return defaultName;
	}


	/**
	 *  Constructor for the ConfigColorSet object
	 *
	 * @param  defaultName    The name of this kind of color set.
	 * @param  types          types supported by this color set.
	 * @param  defaultColors  default colors for this color set.
	 */
	public ConfigColorSet(String defaultName, String[] types, String[] defaultColors) {
		setDefaultName(defaultName);
		setTypes(types);
		setDefaultColors(defaultColors);
		colors = new Color[types.length];
		setupDefaults();
	}


	/**
	 *  Copies entires state from another ConfigColorSet.
	 * In general, only do this with things that are actually the same class, not between subclasses.
	 *
	 * @param  other  The color set to copy state from.
	 */
	public void copyStateFrom(ConfigColorSet other) {
		if (other == null) {
			return;
		}
		for (int i = 0; i < colors.length; i++) {
			Color otherC  = other.getColor(getType(i));
			if (otherC != null) {
				setColor(i, otherC);
			}
		}
		setName(other.getName());
	}


	/*inherit description */
	public void bindConfig(ConfigNode configNode) {
		this.configNode = configNode;
		// first, init existing...
		Color[] oldColors        = new Color[types.length];
		for (int i = 0; i < types.length; i++) {
			oldColors[i] = colors[i];
			colors[i] = null;
		}

		// copy over the new...
		ConfigNode[] colorNodes  = configNode.fetch("Color");
		for (int i = 0; i < colorNodes.length; i++) {
			int type  = getIndex(colorNodes[i].getAttribute("type", "none"));
			if (type == -1) {
				continue;
			}
			colors[type] = decodeColor(colorNodes[i].getAttribute("hex", defaultColors[type]));
		}
		setName(configNode.getAttribute("name", defaultName));

		// finally, make any new nodes which are required...
		for (int i = 0; i < types.length; i++) {
			if (colors[i] == null) {
				ConfigNode colorNode  = configNode.create("Color");
				colorNode.setAttribute("type", getType(i), "none");
				if (oldColors[i] == null) {
					System.out.println("In ConfigColorSet.bindConfig(), Oldcolors " + i + "was null, should never happen!");
					colorNode.setAttribute("hex", defaultColors[i], defaultColors[i]);
				} else {
					colorNode.setAttribute("hex", encodeColor(oldColors[i]), defaultColors[i]);
				}
			}
		}
	}


	/*inherit description */
	public String toString() {
		String ret      = "ConfigColorSet " + getName() + "\n";
		String[] types  = getTypes();
		for (int i = 0; i < types.length; i++) {
			ret += types[i] + " " + getColor(i).toString() + "\t";
		}
		return ret;
	}


	/**  set colors to their default values. */
	public void setupDefaults() {
		for (int i = 0; i < colors.length; i++) {
			setColor(i, decodeColor(defaultColors[i]));
		}
		setName(defaultName);
	}


	/*inherit description */
	public Color getColor(int i) {
		if (i == -1) {
			return null;
		} else {
			return colors[i];
		}
	}


	/*inherit description */
	public Color getColor(String type) {
		int index  = getIndex(type);
		if (index == -1) {
			System.out.println("ConfigColorSet Asked for color " + type + " which doesn't exist.");
		}
		return getColor(index);
	}


	/*inherit description */
	public void setColor(int i, Color newColor) {
		colors[i] = newColor;
		if (configNode != null) {
			ConfigNode[] colors  = configNode.fetch("Color");
			colors[i].setAttribute("type", getType(i), (String) "none");
			colors[i].setAttribute("hex", encodeColor(newColor), defaultColors[i]);
		}
	}


	/*inherit description */
	public String getType(int i) {
		String[] types  = getTypes();
		return types[i];
	}


	/**
	 *  Get an index given a type.
	 *
	 * @param  type  The exact string specifying the type
	 * @return       Returns the index of the type, or -1 if there is no such type
	 */
	public int getIndex(String type) {
		if (type == null) {
			return -1;
		}
		String[] types  = getTypes();
		for (int i = 0; i < types.length; i++) {
			if (type.equals(types[i])) {
				return i;
			}
		}
		return -1;
	}


	/**
	 *  utility routine to which converts a string to a color
	 *
	 * @param  colorString  a string to be converted
	 * @return              the cognate <code>Color</code>
	 */
	public final static Color decodeColor(String colorString) {
		return Color.decode(colorString);//will this work?
	}


	/**
	 *  converts a color to a string
	 *
	 * @param  color  a <code>Color</code> to be converted.
	 * @return        the cognate string.
	 */
	public final static String encodeColor(Color color) {
		int red    = color.getRed();
		int green  = color.getGreen();
		int blue   = color.getBlue();

		return "#" + hex(red) + hex(green) + hex(blue);
	}


	private final static String hex(int buf) {
		int hi   = buf / 16;
		int low  = buf % 16;
		return hexChar(hi) + hexChar(low);
	}


	private final static String hexChar(int i) {
		switch (i) {
						case 0:
							return "0";
						case 1:
							return "1";
						case 2:
							return "2";
						case 3:
							return "3";
						case 4:
							return "4";
						case 5:
							return "5";
						case 6:
							return "6";
						case 7:
							return "7";
						case 8:
							return "8";
						case 9:
							return "9";
						case 10:
							return "A";
						case 11:
							return "B";
						case 12:
							return "C";
						case 13:
							return "D";
						case 14:
							return "E";
						case 15:
							return "F";
		}
		return "F";
	}
}

