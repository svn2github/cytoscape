// $Id: PathwayCommonsPlugin.java,v 1.8 2007/04/27 19:18:48 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2007 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander, Benjamin Gross
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathway_commons.plugin;

// imports
import cytoscape.plugin.PluginInfo;
import cytoscape.plugin.CytoscapePlugin;

import org.mskcc.pathway_commons.http.HTTPServer;
import org.mskcc.pathway_commons.mapping.MapPathwayCommonsToCytoscape;

/**
 * The pathway commons plugin class.  It gets called by Cytoscape's plugin manager
 * to install inself.  The main job of this guy is to instantiate our http server.
 *
 * @author Benjamin Gross.
 */
public class PathwayCommonsPlugin extends CytoscapePlugin {

    /**
     * Version Major Number.
     */
    public static final int VERSION_MAJOR_NUM = 0;

    /**
     * Version Minor Number.
     */
    public static final int VERSION_MINOR_NUM = 1;

    /**
     * Name of Plugin.
     */
    public static final String PLUGIN_NAME = "Pathway Commons Plugin";

	/**
	 * Our implementation of CytoscapePlugin.getPluginInfoObject()
	 *
	 * @return PluginInfo
	 */
	public PluginInfo getPluginInfoObject() {
		PluginInfo info = new PluginInfo();
		info.setName(PLUGIN_NAME);
		info.setDescription("Provides connectivity to Pathway Commons Repository (www.pathwaycommons.org).");
		info.setCategory("Connectivity");
		info.setPluginVersion(VERSION_MAJOR_NUM + "." + VERSION_MINOR_NUM);
		info.setProjectUrl("http://www.pathwaycommons.org");
		info.addAuthor("Benjamin Gross", "Computational Biology Center, Memorial Sloan-Kettering Cancer Center");

		// outta here
		return info;
	}

    /**
     * Constructor.
     */
    public PathwayCommonsPlugin() {

		String debugProperty = System.getProperty("DEBUG");
		Boolean debug  = (debugProperty != null && debugProperty.length() > 0) &&
			new Boolean(debugProperty.toLowerCase());

		// create our http server and start its thread
		new HTTPServer(HTTPServer.DEFAULT_PORT,
					   new MapPathwayCommonsToCytoscape(), debug).start();
    }
}
