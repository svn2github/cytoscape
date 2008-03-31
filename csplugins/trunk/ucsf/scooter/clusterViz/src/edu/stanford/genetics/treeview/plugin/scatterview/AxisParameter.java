/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: AxisParameter.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/08/16 19:13:49 $
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
package edu.stanford.genetics.treeview.plugin.scatterview;

import edu.stanford.genetics.treeview.*;

class AxisParameter {
	private ConfigNode configNode;
	/** Setter for configNode */
	public void setConfigNode(ConfigNode configNode) {
		this.configNode = configNode;
	}
	/** Getter for configNode */
	public ConfigNode getConfigNode() {
		return configNode;
	}
	
	public static final int MIN   = 0;
	public static final int MAX   = 1;
	public static final int MINOR = 2;
	public static final int MAJOR = 3;

	/**
	* the type of this parameter
	*/
	private int defaultType = 0;
	public int getType() {
		return configNode.getAttribute("type", defaultType);
	}
	public void setType(int type) {
		configNode.setAttribute("type", type, defaultType);
	}

	/**
	* the name of this parameter
	*/
	public String getName() {
		switch(getType()) {
			case MIN:
				return "Min";
			case MAX:
				return "Max";
			case MINOR:
				return "Minor";
			case MAJOR:
				return "Major";
		}
		return "Unknown";
	}

	/**
	* is this parameter enabled?
	*/
	private int defaultEnabled = 0;
	public boolean getEnabled() {
		return (configNode.getAttribute("enabled", defaultEnabled) == 1);
	}
	public void setEnabled(boolean b) {
		int val = (b)?1:0;
		configNode.setAttribute("enabled", val, defaultEnabled);
	}
	
	/**
	* what is the value for the parameter?
	*/
	private double defaultValue = 1.0;
	public double getValue() {
		return configNode.getAttribute("value", defaultValue);
	}
	public void setValue(double value) {
		configNode.setAttribute("value", value, defaultValue);
	}
	
	AxisParameter(ConfigNode config) {
		setConfigNode(config);
		
	}
	public void copyStateFrom(AxisParameter other) {
		setType(other.getType());
		setEnabled(other.getEnabled());
		setValue(other.getValue());
	}
}
