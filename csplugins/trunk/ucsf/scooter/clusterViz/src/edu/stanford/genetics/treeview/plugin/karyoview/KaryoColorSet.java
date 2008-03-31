/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: KaryoColorSet.java,v $
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
package edu.stanford.genetics.treeview.plugin.karyoview;

import edu.stanford.genetics.treeview.*;

/**
* color set for karyoscope view
*/
class KaryoColorSet extends ConfigColorSet {
	private static final String [] types = new String [] {"Up", "Down", "Highlight", "Genome", "Background", "Line"};

	private static final String [] defaults = new String [] {"#FF0000","#00FF00","#FFFF00", "#FFFFFF", "#000000", "#A0A0A0"};
	private static final String defaultName = "KaryoColorSet";
	
	KaryoColorSet(String name, 
	String up, String down, String high, 
	String genome, String back, String line) {
		this(name);
		setColor(0, decodeColor(up));
		setColor(1, decodeColor(down));
		setColor(2, decodeColor(high));
		setColor(3, decodeColor(genome));
		setColor(4, decodeColor(back));
		setColor(5, decodeColor(line));
	}
	KaryoColorSet() {
		this("KaryoColorSet");
	}
	KaryoColorSet(String name) {
		super(defaultName, types, defaults);
		setName(name);
	}
	
	public void save(String file) {
		XmlConfig config = new XmlConfig(file, "KaryoColorSet");
		ConfigNode newNode = config.getNode("ConfigColorSet");
		KaryoColorSet tempSet = new KaryoColorSet();
		tempSet.bindConfig(newNode);
		tempSet.copyStateFrom(this);
		config.store();
	}
	
	public void load(String file) {
		XmlConfig config = new XmlConfig(file, "KaryoColorSet");
		ConfigNode newNode = config.getNode("ConfigColorSet");
		KaryoColorSet tempSet = new KaryoColorSet();
		tempSet.bindConfig(newNode);
		copyStateFrom(tempSet);
	}
}
