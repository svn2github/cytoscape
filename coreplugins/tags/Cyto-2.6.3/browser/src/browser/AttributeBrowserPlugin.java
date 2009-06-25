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
package browser;

import static browser.DataObjectType.EDGES;
import static browser.DataObjectType.NETWORK;
import static browser.DataObjectType.NODES;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;

import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;


/**
 * Attribute browser's main class.<br>
 *
 * @version 0.9
 * @since 2.2
 * @author xmas kono
 *
 */
public class AttributeBrowserPlugin extends CytoscapePlugin {
	
	// Name of browser's property file.
	private static final String PROP_FILE_NAME = "attributeBrowser.props";

	// Header message in the prop file.
	private static final String PROP_HEADER = "Cytoscape Attribute Browser Plugin Properties";

	/*
	 * Static instances of browsers.
	 */
	private static final AttributeBrowser nodeAttributeBrowser;
	private static final AttributeBrowser edgeAttributeBrowser;
	private static final AttributeBrowser networkAttributeBrowser;

	private static CyLogger logger;

	static {
		nodeAttributeBrowser = AttributeBrowser.getBrowser(NODES);
		edgeAttributeBrowser = AttributeBrowser.getBrowser(EDGES);
		networkAttributeBrowser = AttributeBrowser.getBrowser(NETWORK);
		logger = CyLogger.getLogger(AttributeBrowserPlugin.class);
	}

	// Global properties for this plugin.
	private final Properties prop;

	/**
	 * Constructor for this plugin.
	 * Call 3 tables, nodes, edges and network.<br>
	 *  The DataTable class actually creates all CytoPanels.<br>
	 *  Filter functions are implemented in Advanced Window.
	 *
	 */
	public AttributeBrowserPlugin() {
		prop = new Properties();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param targetType DOCUMENT ME!
	 * @param menuItem DOCUMENT ME!
	 */
	public static void addMenuItem(final DataObjectType targetType, final Component menuItem) {
		if (targetType == NODES) {
			nodeAttributeBrowser.addMenuItem(menuItem);
		} else if (targetType == EDGES) {
			edgeAttributeBrowser.addMenuItem(menuItem);
		} else {
			networkAttributeBrowser.addMenuItem(menuItem);
		}
	}

	public static AttributeBrowser getAttributeBrowser(browser.DataObjectType pObjectType){
		if (pObjectType == NODES)
			return nodeAttributeBrowser;
		else if (pObjectType == EDGES)
			return edgeAttributeBrowser;
			
		return networkAttributeBrowser;
	}
	/**
	 *  DOCUMENT ME!
	 */
	public void onCytoscapeExit() {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param pStateFileList DOCUMENT ME!
	 */
	public void restoreSessionState(List<File> pStateFileList) {
		if ((pStateFileList == null) || (pStateFileList.size() == 0)) {
			logger.error("Could not find Browser property file.  Use defaults.");

			return;
		}

		try {
			// Pick the first one since currently there is only one prop file.
			final File browserPropfile = pStateFileList.get(0);
			prop.clear();
			prop.load(new FileInputStream(browserPropfile));

			final List<String> nodeAttrSelected = new ArrayList<String>();
			final List<String> edgeAttrSelected = new ArrayList<String>();
			final List<String> networkAttrSelected = new ArrayList<String>();

			final List<String> nodeKeys = new ArrayList<String>();
			final List<String> edgeKeys = new ArrayList<String>();
			final List<String> networkKeys = new ArrayList<String>();

			String key;
			String val;

			for (Entry<Object, Object> set : prop.entrySet()) {
				key = set.getKey().toString();
				val = set.getValue().toString();

				if (key.contains("node"))
					nodeKeys.add(key);
				else if (key.contains("edge"))
					edgeKeys.add(key);
				else
					networkKeys.add(key);
			}

			Collections.sort(nodeKeys);
			Collections.sort(edgeKeys);
			Collections.sort(networkKeys);

			for (String targetKey : nodeKeys)
				nodeAttrSelected.add(prop.getProperty(targetKey));

			for (String targetKey : edgeKeys)
				edgeAttrSelected.add(prop.getProperty(targetKey));

			for (String targetKey : networkKeys)
				networkAttrSelected.add(prop.getProperty(targetKey));

			nodeAttributeBrowser.setSelectedAttributes(nodeAttrSelected);
			edgeAttributeBrowser.setSelectedAttributes(edgeAttrSelected);
			networkAttributeBrowser.setSelectedAttributes(networkAttrSelected);
		} catch (IOException e) {
			logger.error("Could not restore browser state.  Use defaults...", e);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param fileList DOCUMENT ME!
	 */
	public void saveSessionStateFiles(List<File> fileList) {
		final String tmpDir = System.getProperty("java.io.tmpdir");
		final File browserPropfile = new File(tmpDir, PROP_FILE_NAME);
		prop.clear();

		try {
			int idx = 0;
			List<String> nodeAttr = nodeAttributeBrowser.getSelectedAttributes();

			for (String name : nodeAttr) {
				prop.setProperty("attributeBrowser.node.selectedAttr" + idx, name);
				idx++;
			}

			idx = 0;

			List<String> edgeAttr = edgeAttributeBrowser.getSelectedAttributes();

			for (String name : edgeAttr) {
				prop.setProperty("attributeBrowser.edge.selectedAttr" + idx, name);
				idx++;
			}

			idx = 0;

			for (String name : networkAttributeBrowser.getSelectedAttributes()) {
				prop.setProperty("attributeBrowser.network.selectedAttr" + idx, name);
				idx++;
			}

			String key;
			String val;

			for (Entry<Object, Object> set : prop.entrySet()) {
				key = set.getKey().toString();
				val = set.getValue().toString();
				// logger.debug("KEY and VAL = " + key + ", " + val);
			}

			prop.store(new FileOutputStream(browserPropfile), PROP_HEADER);
		} catch (IOException e) {
			logger.error("Could not save attribute browser property file.", e);
		}

		fileList.add(browserPropfile);
	}
}
