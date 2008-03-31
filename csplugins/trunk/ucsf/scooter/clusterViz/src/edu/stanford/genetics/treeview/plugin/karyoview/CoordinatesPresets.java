/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: CoordinatesPresets.java,v $
 * $Revision: 1.6 $
 * $Date: 2006/10/03 06:19:12 $
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

import java.awt.HeadlessException;
import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

import edu.stanford.genetics.treeview.*;

/**
 * This class encapsulates a list of Coordinates presets,
 * which is really just FileSets.
 * This is the class to edit the default presets in...
 */

public class CoordinatesPresets {
    private ConfigNode root = new DummyConfigNode("Coordinates");
	private final static int dIndex = -1; // which preset to use if not by confignode?
    /**
     * creates a new CoordinatesPresets object and binds it to the node
	 * 
	 * adds default Presets if  none are currently set.
     */
    public CoordinatesPresets (ConfigNode parent) {
	  super();
	  bindConfig(parent);
	  if (getPresetNames().length == 0) {
	    addDefaultPresets();
	  }
	  
    }
    public CoordinatesPresets () {
	  super();
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

	public FileSet getDefaultFileSet() {
	  int defaultPreset = getDefaultIndex();
	  try {
		return getFileSet(defaultPreset);
	  } catch (Exception e) {
		return getFileSet(0);
	  }
	}
	
	public void setDefaultIndex(int i) {
	  root.setAttribute("default", i, dIndex);
	}
	
    public void addDefaultPresets() {	  
    }

    /** 
     * returns String [] of preset names for display
     */
    public String[] getPresetNames()
    {
        ConfigNode aconfigNode[] = root.fetch("FileSet");
        String astring[] = new String[aconfigNode.length];
		for (int i = 0; i < aconfigNode.length; i++) {
		FileSet temp = new FileSet(aconfigNode[i]);
            astring[i] = temp.getName();
		}
		return astring;
    }
	public int getNumPresets() {
        ConfigNode aconfigNode[] = root.fetch("FileSet");
		return aconfigNode.length;
	}
	public String toString() {
        ConfigNode aconfigNode[] = root.fetch("FileSet");
		String ret = "";
		if (getDefaultIndex() < 0 ) {
			ret = "Default is to parse file\n";
		} else {
			ret = "Default is " + getPresetNames() [getDefaultIndex()] + " index " + getDefaultIndex() + "\n";
		}
		for (int index = 0; index < aconfigNode.length; index++) {
		FileSet tmp = new FileSet(aconfigNode[index]);
		  ret += tmp.getName() + " " + tmp.toString() +"\n";
		}
		return ret;
	}
    
    /**
     * returns the color set for the ith preset
     * or null, if any exceptions are thrown.
     */
    public FileSet getFileSet(int index) {
        ConfigNode aconfigNode[] = root.fetch("FileSet");
		try {
		  FileSet ret = new FileSet(aconfigNode[index]);
		  return ret;
		} catch (Exception e) {
		  return null;
		}
    }

    /**
     * returns the color set for this name
     * or null, if name not found in kids
     */
	 public FileSet getFileSet(String name) {
	   ConfigNode aconfigNode[] = root.fetch("FileSet");
	   for (int i = 0; i < aconfigNode.length; i++) {
		   FileSet ret = new FileSet(aconfigNode[i]);
		   if (name.equals(ret.getName()))  {
			   return ret;
		   }
	   }
	   return null;
	 }
	
    public void addFileSet(String name) {
	  FileSet preset = new FileSet(root.create("FileSet"));
	  preset.setName(name);
    }
	/**
	* actually copies state of colorset, does not add the colorset itself but a copy.
	*/
    public void addFileSet(FileSet set) {
	  FileSet preset = new FileSet(root.create("FileSet"));
	  preset.copyState(set);
	}
    public void bindConfig(ConfigNode configNode)
    {
        root = configNode;
    }

	public void removeFileSet(int i) {
	   ConfigNode aconfigNode[] = root.fetch("FileSet");
	   root.remove(aconfigNode[i]);
	}
    private ConfigNode createSubNode()
    {
        return root.create("FileSet");
    }
    /**
     * This method will populate the coordinates presets
     * by listing all files from the given url.
     * 
     * @param source
     */
    public void scanUrl(URL source) {
    	if (source.getProtocol().startsWith("file")) {
    		File dir = new File(
    				Util.URLtoFilePath(source.getPath())
    				);
    		FileFilter fileFilter = new FileFilter() {
    			public boolean accept(File file) {
    				return (file.getName().startsWith(".") == false);
    			}
    		};
    		File [] files = dir.listFiles(fileFilter);
    		if (files == null) {
    			JOptionPane.showMessageDialog(null, "Could not list " + dir.toString());
    		} else {
    		for (int i = 0; i< files.length; i++) {
    	    	try {
    	    		FileSet set = new FileSet(files[i].getName(),
	    					dir.getCanonicalPath()+ File.separator);
    	    		set.setName(set.getRoot());
	    			addFileSet(set);
				} catch (HeadlessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		}
    	}
    }
}
