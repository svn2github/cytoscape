/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: FileMru.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/09/25 22:02:02 $
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
package edu.stanford.genetics.treeview.core;

import edu.stanford.genetics.treeview.*;


/*
 *  Decompiled by Mocha from FileMru.class
 */
/*
 *  Originally compiled from FileMru.java
 */

/**
 *  This class encapsulates an xml-based most recently used list of files
 *
 * @author     Alok Saldanha <alok@genome.stanford.edu>
 * @version $Revision: 1.1 $ $Date: 2006/09/25 22:02:02 $
 */
public class FileMru extends java.util.Observable implements ConfigNodePersistent {
	private ConfigNode root;


	/**
	 *  Binds FileMru to a ConfigNode
	 *
	 * @param  configNode  Node to be bound to
	 */
	public synchronized void bindConfig(ConfigNode configNode) {
		root = configNode;
		setChanged();
	}


	/**
	 *  Create subnode of current confignode
	 *
	 * @return    Newly created subnode
	 */
	public synchronized ConfigNode createSubNode() {
		setChanged();
		return root.create("File");
	}


	/**
	 *  Gets the ConfigNode of the ith file
	 *
	 * @param  i  Index of file to get node for
	 * @return    The corresponding ConfigNdoe
	 */
	public ConfigNode getConfig(int i) {
	ConfigNode aconfigNode[]  = root.fetch("File");
		if ((i < aconfigNode.length) && (i >= 0)) {
			return aconfigNode[i];
		} else {
			return null;
		}
	}


	/**
	 *  Gets the configs of all files
	 *
	 * @return    Array of all ConfigNodes
	 */
	public ConfigNode[] getConfigs() {
		return root.fetch("File");
	}


	/**
	 *  Gets names of all recently used files
	 *
	 *  @return String [] of file names for display
	 */
	public String[] getFileNames() {
	ConfigNode aconfigNode[]  = root.fetch("File");
	String astring[]          = new String[aconfigNode.length];
		for (int i = 0; i < aconfigNode.length; i++) {
			astring[i] = aconfigNode[i].getAttribute("root", "");
		}
		return astring;
	}


	/**
	 *  returns dir of most recently used file or null
	 *
	 * @return    The Most Recent Dir or null
	 */
	public String getMostRecentDir() {
	ConfigNode aconfigNode[]  = root.fetch("File");
		if (aconfigNode.length == 0) {
			return null;
		}
	ConfigNode configNode   = aconfigNode[aconfigNode.length - 1];
		return configNode.getAttribute("dir", null);
	}
	public boolean getParseQuotedStrings() {
		return (root.getAttribute("quotes", FileSet.PARSE_QUOTED) == 1);
	}
	public void setParseQuotedStrings(boolean parse) {
		if (parse)
			root.setAttribute("quotes",1, FileSet.PARSE_QUOTED);
		else
			root.setAttribute("quotes",0, FileSet.PARSE_QUOTED);
	}
	
	public int getStyle() {
		return root.getAttribute("style", FileSet.LINKED_STYLE);
	}
	public void setStyle(int style) {
		root.setAttribute("style", style,FileSet.LINKED_STYLE);
	}
	/**
	 *  Delete the nth file in the list
	 *
	 * @param  i  The the index of the file to delete.
	 */
	public synchronized void removeFile(int i) {
	ConfigNode aconfigNode[]  = root.fetch("File");
		root.remove(aconfigNode[i]);
		setChanged();
	}


	/**
	 *  Removes particular FileSet from Mru
	 *
	 * @param  fileSet  FileSet to remove
	 */
	public synchronized void removeFileSet(FileSet fileSet) {
		root.remove(fileSet.getConfigNode());
		setChanged();
	}


	/**
	 *  Sets confignode to be last in list
	 *
	 * @param  configNode  Node to move to end
	 */
	public synchronized void setLast(ConfigNode configNode) {
	ConfigNode[] all  = root.fetch("File");
		if (!configNode.equals(all[all.length - 1])) {
			root.setLast(configNode);
			setChanged();
		}
	}


	/**
	 *  Must notify explicitly when a managed fileset is modified (perhaps should
	 *  pass modifications through Mru?
	 */
	public void notifyFileSetModified() {
		setChanged();
	}


	/**
	 *  Move FileSet to end of list
	 *
	 * @param  fileSet  FileSet to move
	 */
	public synchronized void setLast(FileSet fileSet) {
		setLast(fileSet.getConfigNode());
	}

	/**
	* add a fileset if it's not already in the list.
	* Or, if it is in the list, create a fileset with the correct confignode.
	*
	* @return the fileset corresponding to the correct config node
	*/
	public synchronized FileSet addUnique(FileSet inSet) {
		// check existing file nodes...
		ConfigNode aconfigNode[] = getConfigs();
		for (int i = 0; i < aconfigNode.length; i++) {
			FileSet fileSet2 = new FileSet(aconfigNode[i]);
			if (fileSet2.equals(inSet)) {
				LogBuffer.println("Found Existing node in MRU list for " + inSet);
				fileSet2.copyState(inSet);
				return fileSet2;
			}
		}


		 ConfigNode configNode = createSubNode();
		 FileSet fileSet3 = new FileSet(configNode);
		 fileSet3.copyState(inSet);
		 LogBuffer.println("Creating new fileset " + fileSet3);
		 return fileSet3;
		 /*

			 loadFileSet(fileSet);
		 fileMru.setLast(fileSet);
		 fileMru.notifyObservers();
		 setLoaded(true);
		 fileMru.setLast(configNode);
		 fileMru.notifyObservers();
		 */
	}


	/**
	 *  Delete all but the last i files from the list
	 *
	 * @param  i  The number of files to delete
	 */
	public synchronized void trimToLength(int i) {
	ConfigNode aconfigNode[]  = root.fetch("File");
	int j                   = aconfigNode.length - i;
		for (int k = 0; k < j; k++) {
			root.remove(aconfigNode[k]);
		}
		setChanged();
	}
	
	public synchronized void removeMoved() {
		ConfigNode [] nodes = getConfigs();
		for (int i = nodes.length ; i > 0; i--) {
			FileSet fileSet = new FileSet(nodes[i-1]);
			if (fileSet.hasMoved()) {
				LogBuffer.println("Could not find " + fileSet.getCdt() + ", removing from mru...");
				removeFile(i-1);
				setChanged();
			}
		}
	}
}

