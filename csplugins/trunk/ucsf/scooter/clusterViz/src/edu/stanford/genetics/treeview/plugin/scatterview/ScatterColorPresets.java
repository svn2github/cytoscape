/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: ScatterColorPresets.java,v $
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

import edu.stanford.genetics.treeview.ConfigNode;
import edu.stanford.genetics.treeview.DummyConfigNode;
/**
 * This class encapsulates a list of ScatterColorSet presets.
 */
public class ScatterColorPresets {
	private ConfigNode root;
	private final static int dIndex = 0; // which preset to use if not by confignode?
    /**
     * creates a new ColorPresets object and binds it to the node
	 * 
	 * adds default Presets if  none are currently set.
     */
    public ScatterColorPresets (ConfigNode parent) {
	  super();
	  bindConfig(parent);
	  int nNames = getPresetNames().length;
	  if (nNames == 0) {
	    addDefaultPresets();
	  }
	  
    }
    public ScatterColorPresets () {
	  this(new DummyConfigNode("ScatterColorPresets"));
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

	public ScatterColorSet getDefaultColorSet() {
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

	public static ScatterColorSet [] defaultColorSets;
	
	static {
		defaultColorSets  = new ScatterColorSet[2];
		defaultColorSets[0] = new ScatterColorSet("BlackBG",
		"#000000","#00FF00", "#FFFF00", 
		"#FFFFFF");
		defaultColorSets[1] = new ScatterColorSet("WhiteBG",
		"#FFFFFF","#00FF00","#999900",
		"#000000");
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
        ConfigNode aconfigNode[] = root.fetch("ScatterColorSet");
        String astring[] = new String[aconfigNode.length];
		ScatterColorSet temp = new ScatterColorSet("TempColorSet");
		for (int i = 0; i < aconfigNode.length; i++) {
		  temp.bindConfig(aconfigNode[i]);
            astring[i] = temp.getName();
		}
		return astring;
    }
	public int getNumPresets() {
        ConfigNode aconfigNode[] = root.fetch("ScatterColorSet");
		return aconfigNode.length;
	}
	public String toString() {
        ConfigNode aconfigNode[] = root.fetch("ScatterColorSet");
		ScatterColorSet tmp = new ScatterColorSet();
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
    public ScatterColorSet getColorSet(int index) {
        ConfigNode aconfigNode[] = root.fetch("ScatterColorSet");
		try {
		  ScatterColorSet ret = new ScatterColorSet();
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
	 public ScatterColorSet getColorSet(String name) {
	   ConfigNode aconfigNode[] = root.fetch("ScatterColorSet");
	   ScatterColorSet ret = new ScatterColorSet();
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
    public void addColorSet(ScatterColorSet set) {
		ScatterColorSet preset = new ScatterColorSet("AddingColorSet");
	  if (root != null) preset.bindConfig(root.create("ScatterColorSet"));
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
	   ConfigNode aconfigNode[] = root.fetch("ScatterColorSet");
	   root.remove(aconfigNode[i]);
	}
    private ConfigNode createSubNode()
    {
        return root.create("ScatterColorSet");
    }
    
}
