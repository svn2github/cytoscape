/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: KaryoColorPresets.java,v $
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

import edu.stanford.genetics.treeview.ConfigNode;
import edu.stanford.genetics.treeview.DummyConfigNode;
/**
 * This class encapsulates a list of KaryoColorSet presets.
 */
public class KaryoColorPresets {
	private ConfigNode root;
	private final static int dIndex = 0; // which preset to use if not by confignode?
    /**
     * creates a new ColorPresets object and binds it to the node
	 * 
	 * adds default Presets if  none are currently set.
     */
    public KaryoColorPresets (ConfigNode parent) {
	  super();
	  bindConfig(parent);
	  int nNames = getPresetNames().length;
	  if (nNames == 0) {
	    addDefaultPresets();
	  }
	  
    }
    public KaryoColorPresets () {
	  this(new DummyConfigNode("KaryoColorPresets"));
	}

	/**
	* returns default preset, for use when opening a new file which has no color settings
	*/
	public int getDefaultIndex() {
	  return root.getAttribute("default", dIndex);
	}

	public boolean isDefaultEnabled() {
	  return (getDefaultIndex() != -1);
	}

	public KaryoColorSet getDefaultColorSet() {
	  int defaultPreset = getDefaultIndex();
	  try {
		return getColorSet(defaultPreset);
	  } catch (Exception e) {
		return getColorSet(0);
	  }
	}
	
	public void setDefaultIndex(int i) {
	  root.setAttribute("default", i, dIndex);
	}

	public static KaryoColorSet [] defaultColorSets;
	
	static {
		defaultColorSets  = new KaryoColorSet[2];
		defaultColorSets[0] = new KaryoColorSet("BlackBG",
		"#FF0000","#00FF00", "#FFFF00", 
		"#FFFFFF", "#000000", "#A0A0A0");
		defaultColorSets[1] = new KaryoColorSet("WhiteBG",
		"#FF0000","#00FF00","#FFFF00",
		"#000000", "#FFFFFF", "#A0A0A0");
	}

    public void addDefaultPresets() {	  
		for (int i = 0; i < defaultColorSets.length; i++) {
			addColorSet(defaultColorSets[i]);
		}
    }

    /** 
     * returns String [] of preset names for display
     */
    public String[] getPresetNames()
    {
        ConfigNode aconfigNode[] = root.fetch("KaryoColorSet");
        String astring[] = new String[aconfigNode.length];
		KaryoColorSet temp = new KaryoColorSet("TempColorSet");
		for (int i = 0; i < aconfigNode.length; i++) {
		  temp.bindConfig(aconfigNode[i]);
            astring[i] = temp.getName();
		}
		return astring;
    }
	public int getNumPresets() {
        ConfigNode aconfigNode[] = root.fetch("KaryoColorSet");
		return aconfigNode.length;
	}
	public String toString() {
        ConfigNode aconfigNode[] = root.fetch("KaryoColorSet");
		KaryoColorSet tmp = new KaryoColorSet();
		String ret = "Default is " + getPresetNames() [getDefaultIndex()] + " index " + getDefaultIndex() + "\n";
		for (int index = 0; index < aconfigNode.length; index++) {
		  tmp.bindConfig(aconfigNode[index]);
		  ret += tmp.toString() +"\n";
		}
		return ret;
	}
    
    /**
     * returns the color set for the ith preset
     * or null, if any exceptions are thrown.
     */
    public KaryoColorSet getColorSet(int index) {
        ConfigNode aconfigNode[] = root.fetch("KaryoColorSet");
		try {
		  KaryoColorSet ret = new KaryoColorSet();
		  ret.bindConfig(aconfigNode[index]);
		  return ret;
		} catch (Exception e) {
		  return null;
		}
    }

    /**
     * returns the color set for this name
     * or null, if name not found in kids
     */
	 public KaryoColorSet getColorSet(String name) {
	   ConfigNode aconfigNode[] = root.fetch("KaryoColorSet");
	   KaryoColorSet ret = new KaryoColorSet();
	   for (int i = 0; i < aconfigNode.length; i++) {
		 ret.bindConfig(aconfigNode[i]);
		 if (name.equals(ret.getName()))  {
		   return ret;
		 }
	   }
	   return null;
	 }
	
	/**
	* actually copies state of colorset, does not add the colorset itself but a copy.
	*/
    public void addColorSet(KaryoColorSet set) {
    	  KaryoColorSet preset = new KaryoColorSet("AddingColorSet");
	  preset.bindConfig(root.create("KaryoColorSet"));
	  preset.copyStateFrom(set);
	}
    public void bindConfig(ConfigNode configNode)
    {
		root = configNode;
		int nNames = getPresetNames().length;
		if (nNames == 0) {
			addDefaultPresets();
		}

    }

	public void removeColorSet(int i) {
	   ConfigNode aconfigNode[] = root.fetch("KaryoColorSet");
	   root.remove(aconfigNode[i]);
	}
    private ConfigNode createSubNode()
    {
        return root.create("KaryoColorSet");
    }
    
}
