/**
 * 
 */
package cytoscape.plugin;

import java.util.*;

/*
 * I think this will read and write an xml file pretty similar to the one expected from a website describing
 * plugins.  A list of the files/locations installed will be added.  A tag for plugin type (core/custom) will also
 * be added for the user?
 */


/**
 * @author skillcoy
 * Tracks all installed plugins and the files required for each.
 * Writes out a file when cytoscape is closed describing installed plugins, reads in
 * at load time (check that plugins are still there?)
 */
public class PluginTracker
	{

	/**
	 * 
	 * @return List of PluginInfo objects for installed plugins
	 */
	public static List<PluginInfo> getInstalledPlugins()
		{
		return null;
		}
	
	}
