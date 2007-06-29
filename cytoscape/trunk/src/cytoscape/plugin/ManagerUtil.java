
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.plugin;

import java.util.List;
import java.util.Map;
import cytoscape.plugin.PluginTracker.PluginStatus;


/**
 *
 */
public class ManagerUtil {
	// get the list sorted the way we want to display it, I'd like to do these in one method somehow
	// where you just give it the PluginInfo method to sort by.  I'm sure there's a way, I just don't know it yet
	/**
	 *  DOCUMENT ME!
	 *
	 * @param Plugins DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static Map<String, List<PluginInfo>> sortByCategory(List<PluginInfo> Plugins) {
		Map<String, List<PluginInfo>> Categories = new java.util.HashMap<String, List<PluginInfo>>();

		for (PluginInfo Current : Plugins) {
			if (Categories.containsKey(Current.getCategory())) {
				Categories.get(Current.getCategory()).add(Current);
			} else {
				List<PluginInfo> List = new java.util.ArrayList<PluginInfo>();
				List.add(Current);
				Categories.put(Current.getCategory(), List);
			}
		}
		return Categories;
	}

	// cheap and hacky I know....
	public static Map<String, List<PluginInfo>> sortByClass(List<PluginInfo> Plugins)
		{
		Map<String, List<PluginInfo>> Classes = new java.util.HashMap<String, List<PluginInfo>>();

		for (PluginInfo Current : Plugins) {
			if (Classes.containsKey(Current.getPluginClassName())) {
				Classes.get(Current.getPluginClassName()).add(Current);
			} else {
				List<PluginInfo> List = new java.util.ArrayList<PluginInfo>();
				List.add(Current);
				Classes.put(Current.getPluginClassName(), List);
			}
		}
		return Classes;
	}

	public static Map<String, List<PluginInfo>> sortByID(List<PluginInfo> Plugins) {
		Map<String, List<PluginInfo>> Ids = new java.util.HashMap<String, List<PluginInfo>>();

		for (PluginInfo Current : Plugins) {
			if (Ids.containsKey(Current.getID())) {
				Ids.get(Current.getID()).add(Current);
			} else {
				List<PluginInfo> List = new java.util.ArrayList<PluginInfo>();
				List.add(Current);
				Ids.put(Current.getID(), List);
			}
		}
		return Ids;
	}
	
	/**
	 * Returns a list of available plugins minus any currently installed
	 * @param Current
	 * @param Available
	 */
	public static List<PluginInfo> getUnique(List<PluginInfo> Current, List<PluginInfo> Available) {
		List<PluginInfo> UniqueAvail = new java.util.ArrayList<PluginInfo>(Available);

		if (Current == null) {
			return Available;
		}
		
		for (PluginInfo infoAvail: Available) {
			for (PluginInfo infoCur: Current) {
				if ( infoCur.getID().equals(infoAvail.getID()) && 
					infoCur.getDownloadUrl().equals(infoAvail.getDownloadUrl())) {
					UniqueAvail.remove(infoAvail);
				}
					
			}
		}
		return UniqueAvail;
	}
	
	/**
	 * Takes a Class object for a CytoscapePlugin and returns the PluginInfo object
	 * associated
	 * @param pluginClass
	 * @return PluginInfo object
	 */
	public static PluginInfo getInfoObject(Class pluginClass) {
		PluginManager mgr = PluginManager.getPluginManager();
		List<PluginInfo> Plugins = mgr.getPlugins(PluginStatus.CURRENT);
	
		for (PluginInfo Current : Plugins) {
			if (Current.getPluginClassName().equals(pluginClass.getName())) {
				return Current;
			}
		}
		return null;
	}
	
}
