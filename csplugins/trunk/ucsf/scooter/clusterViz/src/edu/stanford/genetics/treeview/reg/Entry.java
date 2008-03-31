/* BEGIN_HEADER                                              Java TreeView
*
* $Author: alokito $
* $RCSfile: Entry.java,v $
* $Revision: 1.8 $
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

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.stanford.genetics.treeview.*;

/**
 * @author aloksaldanha
 *
 * This is intended to represent an Entry in the Registration section of the global XML config.
 * an implementation should be defined as an inner class of Registration.
 * 
 * The Entry is simply a list of key-value pairs which correspond to the information for a particular registration.
 * The XML config may contain several of them because the user has registered multiple versions.
 * 
 * The valid key names are statically defined within this class.
 * Note that if the key ends with Okay, it will be treated as a boolean for display and editing purposes.
 */
public class Entry implements ConfigNodePersistent {
	/** keys to send over.
	 * 
	 * If you edit this, make sure to edit initialize()
	 * 
	 * Actual URL and connection details are handled in RegEngine.
	 *  
	 */
	
	private final static String [] regKeys = new String [] {
		"first_name",
		"last_name",
		"email",
		"institution",
		"contactOkay",
		"jtv_version",
		"java_version",
		"java_vendor",
		"os_name",
		"os_arch",
		"os_version",
		"install_ip",
		"install_host",
		"install_date"
	};
	/**
	 * should be subset off regKeys
	 */
	private final static String [] editableRegKeys = new String [] {
			"first_name",
			"last_name",
			"email",
			"institution",
			"contactOkay"
	};
	private boolean [] isEditable = null;

	public int getNumRegKeys() {
		return regKeys.length;
	}
	public String getRegKey(int i) {
		return regKeys[i];
	}
	public int getNumEditableRegKeys() {
		return editableRegKeys.length;
	}
	public String getEditableRegKey(int i) {
		return editableRegKeys[i];
	}

	private ConfigNode configNode  = null;
	
	public Entry(ConfigNode configNode) {
		bindConfig(configNode);
	}

	/**
	 * @param cur_ver
	 * @return first number in version string
	 */
	private static int getPrimary(String cur_ver) {
		int firstDot = cur_ver.indexOf(".");
	
		String between = cur_ver;
		if (firstDot > 0) {
			between  = cur_ver.substring(0, firstDot);
		}
		return Integer.parseInt(between);
	}

	/**
	 * @param cur_ver
	 * @return second number of version string
	 */
	private static int getSecondary(String cur_ver) {
		int firstDot = cur_ver.indexOf(".");
		int secondDot = cur_ver.indexOf(".", firstDot+1);
	
		String between = cur_ver.substring(firstDot+1);
		if (secondDot > firstDot) {
			between  = cur_ver.substring(firstDot+1, secondDot);
		}
		return Integer.parseInt(between);
	}

	/**
	 * @param node_ver
	 * @return Returns true if this version is newer than the last registered, otherwise returns false.
	 */
	private static boolean isOld(String node_ver,String cur_ver) {
		if (getPrimary(cur_ver) > getPrimary(node_ver)) {
			return true;
		}
		if (getPrimary(cur_ver) < getPrimary(node_ver)) {
			return false;
		}
		if (getSecondary(cur_ver) > getSecondary(node_ver)) {
			return true;
		}
		if (getSecondary(cur_ver) < getSecondary(node_ver)) {
			return false;
		}
		if (getTertiary(cur_ver) > getTertiary(node_ver)) {
			return true;
		}
		if (getTertiary(cur_ver) < getTertiary(node_ver)) {
			return false;
		}
		return false;
	}

	/**
	 * @param cur_ver
	 * @return third number of version string.
	 */
	private static int getTertiary(String cur_ver) {
		int firstDot = cur_ver.indexOf(".");
		int secondDot = cur_ver.indexOf(".", firstDot+1);
		int thirdDot = cur_ver.indexOf(".", secondDot+1);
		String between = cur_ver.substring(secondDot+1);
		if (thirdDot > secondDot) {
			between  = cur_ver.substring(secondDot+1, thirdDot);
		}
		return Integer.parseInt(between);
	}

	
	/**
	 * Determine whether version tag is entirely numeric.
	 * Only stable releases should have entirely numeric version tags.
	 * By convention, beta version will have Beta appended to their names.
	 * @return true if version tag is entirely numeric.
	 */
	private boolean hasNumericVersion() {
		return isNumericVersion(getVersionTag());
	}
	/**
	 * populate values of node
	 * takes editable values from supplied Entry
	 */
	void initialize(Entry oldEntry) {
		try {
			if (oldEntry != null) {
				setAttribute("first_name", oldEntry.getAttribute("first_name"));
				setAttribute("last_name", oldEntry.getAttribute("last_name"));
				setAttribute("email", oldEntry.getAttribute("email"));
				setAttribute("institution", oldEntry.getAttribute("institution"));
				setAttribute("contactOkay", oldEntry.getAttribute("contactOkay"));
			}
		} catch (Exception e) {
			LogBuffer.println("Exception in Entry.initialize(Entry): " + e);
			e.printStackTrace();
		}
		initialize();
	}
	/**
	 * populate fixed (non-editable) values of node
	 */
	void initialize() {
		setAttribute("jtv_version", TreeViewApp.getVersionTag());
		setAttribute("java_version", System.getProperty("java.version"));
		setAttribute("java_vendor",System.getProperty("java.vendor"));
		setAttribute("os_name",System.getProperty("os.name"));
		setAttribute("os_arch",System.getProperty("os.arch"));
		setAttribute("os_version",System.getProperty("os.version"));
		try{
			InetAddress addr = InetAddress.getLocalHost();
			setAttribute("install_ip",addr.getHostAddress());
			setAttribute("install_host",addr.getHostName());
		} catch (Exception e) {
			setAttribute("install_ip","Failed:"+e);
			setAttribute("install_host","Failed:"+e);
		}
		try {
			Date current = new Date() ;
			SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss z");
			setAttribute("install_date",formatter.format(current));
		} catch (Exception e) {
			LogBuffer.println("error formatting data while registering.");
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.ConfigNodePersistent#bindConfig(edu.stanford.genetics.treeview.ConfigNode)
	 */
	public void bindConfig(ConfigNode configNode) {
		this.configNode = configNode;
	}

	/**
	 * @param versionTag
	 * @return true if the version is entirely numeric, indicating released version.
	 */
	public static boolean isNumericVersion(String versionTag) {
		/* position of last dot */
		int last = -1;
		int cur = versionTag.indexOf(".",last+1);
		while (cur > 0) {
			try {
				String between  = versionTag.substring(last+1, cur);
				Integer.parseInt(between);
			} catch (Exception e) {
				return false;
			}
			last = cur;
			cur = versionTag.indexOf(".",last+1);
		}
		return true;
	}

	/**
	 * @param string - Key of attribute to retrieve
	 * @return value of attribute, defaults to empty string, not null.
	 */
	private String getAttribute(String string) {
		return configNode.getAttribute(string, "");
	}

	/**
	 * @param attribute - attribute to set
	 * @param newVal - new value of attribute
	 */
	private void setAttribute(String attribute, String newVal) {
		configNode.setAttribute(attribute, newVal, "");
	}

	/**
	 * Status can equal one of
	 * 
	 *  deferred, declined, pending, complete.
	 * 
	 * @return status of this entry
	 */
	public String getStatus() {
		return getAttribute("status");
	}
	public void setStatus(String newStatus) {
		setAttribute("status", newStatus);
	}
	/**
	 * @param i - index of reg key, from getRegKey()
	 * @return return attribute for i'th key.
	 */
	public String getRegValue(int i) {
		return getAttribute(regKeys[i]);
	}
	
	/**
	 * @param i - index of reg key, corresponding to getEditableRegKey()
	 * @return Value of reg key.
	 */
	public String getEditableRegValue(int i) {
		return getAttribute(editableRegKeys[i]);
	}
	/**
	 *  set value of the ith editable reg key
	 * 
	 * @param i index into editableRegKeys array (from getEditableRegKey())
	 * @param val new value for reg key
	 */
	public void setEditableRegValue(int i, String val) {
		setAttribute(editableRegKeys[i], val);
	}
	/**
	 * @param index index of Registration Key to query.
	 * @return true if ith reg key is editable.
	 */
	public boolean isEditable(int index) {
		if (isEditable == null) {
			isEditable = new boolean[getNumRegKeys()];
			for (int i = 0; i < isEditable.length; i++) {
				boolean editable = false;
				String key = getRegKey(i);
				for (int j = 0; j < getNumEditableRegKeys(); j++) {
					if (key.equals(editableRegKeys[j]))
						editable = true;
				}
				isEditable[i] = editable;
			}
		}
		return isEditable[index];
	}
	/**
	 * @return version tag of entry
	 */
	public String getVersionTag() {
		return configNode.getAttribute("jtv_version", "");
	}
	/**
	 * @return summary of values in entry.
	 */
	public String getSummary() {
		String ret= getRegKey(0) +"="+getRegValue(0);
		for (int i = 1; i< getNumRegKeys(); i++)
			ret += "; " + getRegKey(i) + "="+getRegValue(i);
		return ret;
	}

}
