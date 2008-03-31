/* BEGIN_HEADER                                              Java TreeView
*
* $Author: alokito $
* $RCSfile: Registration.java,v $
* $Revision: 1.6 $
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
package edu.stanford.genetics.treeview.reg;


import edu.stanford.genetics.treeview.*;

/**
 * @author aloksaldanha
 *
 * This class keeps track of the registration information for Java Treeview
 * It should be bound to the Registration config node of the global xml config file.
 * 
 */
public class Registration implements ConfigNodePersistent {
	private ConfigNode configNode  = null;
	
	/**
	 * @param node
	 */
	public Registration(ConfigNode node) {
		bindConfig(node);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.ConfigNodePersistent#bindConfig(edu.stanford.genetics.treeview.ConfigNode)
	 */
	public void bindConfig(ConfigNode configNode) {
		this.configNode = configNode;
	}

	/**
	 * @param versionTag
	 * @return returns entry corresponding to version tag. Returns null if no corresponding Entry.
	 */
	public Entry getEntry(String versionTag) {
		for (int i = 0; i < getNumEntries(); i++) {
			Entry entry = getEntry(i);
			if (entry.getVersionTag().equals(versionTag))
				return entry;
		}
		return null;
	}

	/**
	 * @param versionTag
	 * @return creates entry corresponding to version tag.
	 */
	public Entry createEntry(String versionTag) {
		Entry oldEntry = getLastEntry();
		ConfigNode newNode = configNode.create("Entry");
		Entry newEntry = new Entry(newNode);
		newEntry.initialize(oldEntry);
		return newEntry;
	}

	/**
	 * @return Number of entries in Registration confignode.
	 */
	public int getNumEntries() {
		ConfigNode [] entries = configNode.fetch("Entry");
		return entries.length;
	}

	/**
	 * @param i
	 * @return i'th entry in ConfigNode.
	 */
	public Entry getEntry(int i) {
		ConfigNode [] entries = configNode.fetch("Entry");
		return new Entry(entries[i]);
	}

	/**
	 * @return current entry
	 */
	public Entry getCurrentEntry() {
		String versionTag = TreeViewApp.getVersionTag();
		return getEntry(versionTag);
	}

	/**
	 * @return
	 */
	public Entry getLastEntry() {
		int numEntries = getNumEntries();
		if (numEntries > 0 ) {
			return getEntry(numEntries-1);
		} else {
			return null;
		}
	}
}
