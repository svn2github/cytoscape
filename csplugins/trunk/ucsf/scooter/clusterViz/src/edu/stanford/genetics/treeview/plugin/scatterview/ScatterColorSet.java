/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: ScatterColorSet.java,v $
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

/**
* color set for scatterplot view
*/
class ScatterColorSet extends ConfigColorSet {
	private static final String [] types = new String [] {"Background", "Axis", "Data", "Selected"};

	private static final String [] defaults = new String [] {"#000000","#00FF00","#FFFF00", "#FFFFFF"};
	private static final String defaultName = "ScatterColorSet";
	
	ScatterColorSet(String name, 
	String back, String axis, String data, 
	String sel) {
		this(name);
		setColor(0, decodeColor(back));
		setColor(1, decodeColor(axis));
		setColor(2, decodeColor(data));
		setColor(3, decodeColor(sel));
	}
	ScatterColorSet() {
		this("ScatterColorSet");
	}
	ScatterColorSet(String name) {
		super(defaultName, types, defaults);
		setName(name);
	}
	
	public void save(String file) {
		XmlConfig config = new XmlConfig(file, "ScatterColorSet");
		ConfigNode newNode = config.getNode("ConfigColorSet");
		ScatterColorSet tempSet = new ScatterColorSet();
		tempSet.bindConfig(newNode);
		tempSet.copyStateFrom(this);
		config.store();
	}
	
	public void load(String file) {
		XmlConfig config = new XmlConfig(file, "ScatterColorSet");
		ConfigNode newNode = config.getNode("ConfigColorSet");
		ScatterColorSet tempSet = new ScatterColorSet();
		tempSet.bindConfig(newNode);
		copyStateFrom(tempSet);
	}
}
