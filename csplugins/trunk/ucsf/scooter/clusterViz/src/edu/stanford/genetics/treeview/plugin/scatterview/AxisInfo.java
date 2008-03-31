/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: AxisInfo.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/08/16 19:13:48 $
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
class AxisInfo {
	private ConfigNode configNode;
	/** Setter for configNode */
	public void setConfigNode(ConfigNode configNode) {
		this.configNode = configNode;
		initParameters();
	}
	/** Getter for configNode */
	public ConfigNode getConfigNode() {
		return configNode;
	}
	
	private String title;
	/** Setter for title */
	public void setTitle(String title) {
		this.title = title;
	}
	/** Getter for title */
	public String getTitle() {
		return title;
	}
	
	/**
	* The type of this axis, either x or y.
	*/
	private String defaultType = "No Type";
	public String getType() {
		return configNode.getAttribute("type", defaultType);
	}
	public void setType(String type) {
		configNode.setAttribute("type", type, defaultType);
	}
	private AxisParameter [] axisParameters;
	AxisInfo(ConfigNode config) {
		setConfigNode(config);
	}

	private void initParameters() {
		// initialize parameters
		axisParameters = new AxisParameter[4];
		for (int i = 0; i < axisParameters.length; i++) {
			axisParameters[i] = null;
		}
		// copy over existing
		ConfigNode [] existing = configNode.fetch("AxisParameter");
		for (int i = 0; i < existing.length; i++) {
			AxisParameter temp = new AxisParameter(existing[i]);
			axisParameters[temp.getType()] = temp;
		}
		// fill in blanks...
		for (int i = 0; i < axisParameters.length; i++) {
			if (axisParameters[i] == null) {
				ConfigNode newNode = configNode.create("AxisParameter");
				axisParameters[i] = new AxisParameter(newNode);
				axisParameters[i].setType(i);
			}
		}
	}

	public AxisParameter getAxisParameter(int type) {
		return axisParameters[type];
	}
	
	public void copyStateFrom(AxisInfo other) {
		setType(other.getType());
		for (int i =0; i < axisParameters.length; i++) {
			axisParameters[i].copyStateFrom(other.getAxisParameter(i));
		}
	}
}
