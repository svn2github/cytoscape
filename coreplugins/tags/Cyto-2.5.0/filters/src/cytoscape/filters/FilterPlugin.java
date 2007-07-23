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
package cytoscape.filters;

import cytoscape.*;

import cytoscape.data.*;

import cytoscape.filters.util.FilterUtil;

import cytoscape.filters.view.FilterMainPanel;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.plugin.PluginInfo;

import cytoscape.util.*;

import cytoscape.view.*;

import cytoscape.view.cytopanels.CytoPanelImp;
import cytoscape.view.cytopanels.CytoPanelState;

import java.awt.event.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.*;


/**
 *
 */
public class FilterPlugin extends CytoscapePlugin {
	private CytoPanelImp cytoPanelWest = (CytoPanelImp) Cytoscape.getDesktop()
	                                                             .getCytoPanel(SwingConstants.WEST);
	private static FilterMainPanel filterMainPanel = null;
	private static Vector<CompositeFilter> allFilterVect = null;

	//Used to pass values to other plugin, the BrowserPlugin
	public static Vector<CompositeFilter> getAllFilterVect() {
		if (allFilterVect == null) {
			allFilterVect = new Vector<CompositeFilter>();
		}
		return allFilterVect;
	}
	
	public static FilterMainPanel getFilterMainPanel() {
		if (filterMainPanel == null) {
			filterMainPanel = new FilterMainPanel();
		}
		return filterMainPanel;
	}
	/**
	 * Creates a new FilterPlugin object.
	 *
	 * @param icon  DOCUMENT ME!
	 * @param csfilter  DOCUMENT ME!
	 */
	public FilterPlugin() {
		init();

		// for debug only
		//System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&");
		//System.out.println("New filters plugin");
		//System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&");
		//int indexInCytoPanel = cytoPanelWest.indexOfComponent("Filters");
		//cytoPanelWest.setSelectedIndex(indexInCytoPanel);						
	}

	private void init() {
		ImageIcon icon = new ImageIcon(getClass()
		                                   .getResource("/cytoscape/images/ximian/stock_filter-data-by-criteria.png"));
		ImageIcon icon2 = new ImageIcon(getClass()
		                                    .getResource("/cytoscape/images/ximian/stock_filter-data-by-criteria-16.png"));

		// Add a menuItem on "select" menu
		FilterMenuItemAction menuAction = new FilterMenuItemAction(icon2, this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) menuAction);

		// Add an icon on toolbar
		FilterPluginToolBarAction toolbarAction = new FilterPluginToolBarAction(icon, this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) toolbarAction);

		//initialize the filterMainPanel and add it to the CytoPanelWEST, i.e. the control (management) panel
		File global_filter_file = CytoscapeInit.getConfigFile("filters.props");

		if (allFilterVect == null) {
			allFilterVect = new Vector<CompositeFilter>();
		}

		if (filterMainPanel == null) {
			filterMainPanel = new FilterMainPanel();
		}

		allFilterVect.addAll(getFilterVectFromPropFile(global_filter_file));

		System.out.println("FilterPlugin.init(): load " + allFilterVect.size()
		                   + " filters from filters.prop");

		filterMainPanel.setAllFilterVect(allFilterVect);
		cytoPanelWest.add("Filters", filterMainPanel);
	}

	// Read the filter property file and construct the filter objects
	//based on the string representation of each filter
	private Vector<CompositeFilter> getFilterVectFromPropFile(File pPropFile) {
		Vector<CompositeFilter> retVect = new Vector<CompositeFilter>();

		Vector<String> tmpVect = new Vector<String>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(pPropFile));

			String oneLine = in.readLine();

			while (oneLine != null) {
				// ignore comment or empty line
				if (oneLine.startsWith("#") || oneLine.trim().equals("")) {
					oneLine = in.readLine();

					continue;
				}

				if (oneLine.trim().startsWith("Filter_name")) {
					if (tmpVect.size() == 0) { // start of another Filter string
						tmpVect.add(oneLine);
					} else {
						CompositeFilter theFilter = FilterUtil.createFilterFromString(tmpVect);
						retVect.add(theFilter);

						tmpVect = new Vector<String>();
						tmpVect.add(oneLine);
					}
				} else {
					tmpVect.add(oneLine);
				}

				oneLine = in.readLine();
			} // while loop

			in.close();
		} catch (Exception ex) {
			System.out.println("Filter Read error");
			ex.printStackTrace();
		}

		// The last filter string
		if (tmpVect.size() > 0) {
			CompositeFilter theFilter = FilterUtil.createFilterFromString(tmpVect);
			retVect.add(theFilter);
		}

		return retVect;
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void onCytoscapeExit() {
		//System.out.println("onCytoscapeExit() ...");
		// Save global filter to "filters.prop"
		File filter_file = CytoscapeInit.getConfigFile("filters.props");
		Vector<CompositeFilter> allFilterVect = filterMainPanel.getAllFilterVect();

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filter_file));

			for (int i = 0; i < allFilterVect.size(); i++) {
				CompositeFilter theFilter = (CompositeFilter) allFilterVect.elementAt(i);
				AdvancedSetting advSetting = theFilter.getAdvancedSetting();

				if (advSetting.isGlobalChecked()) {
					writer.write(theFilter.toString());
					writer.newLine();
				}
			}

			writer.close();
		} catch (Exception ex) {
			System.out.println("Global filter Write error");
			ex.printStackTrace();
		}
	}

	// override the following two methods to save state.
	/**
	 * DOCUMENT ME!
	 *
	 * @param pStateFileList
	 *            DOCUMENT ME!
	 */
	public void restoreSessionState(List<File> pStateFileList) {
		System.out.println("FilterPlugin.restoreSessionState()");

		if ((pStateFileList == null) || (pStateFileList.size() == 0)) {
			System.out.println("\tNo previous filter state to restore.");

			return;
		}

		try {
			File session_filter_file = pStateFileList.get(0);

			Vector<CompositeFilter> sessionFilterVect = getFilterVectFromPropFile(session_filter_file);

			System.out.println("\tLoad " + sessionFilterVect.size() + " session filters");
			allFilterVect = filterMainPanel.getAllFilterVect();

			int currentFilterCount = allFilterVect.size();
			System.out.println("\tcurrentFilterCount=" + currentFilterCount);

			for (int i = 0; i < sessionFilterVect.size(); i++) {
				// Exclude duplicated filter
				boolean isDuplicated = false;

				for (int j = 0; j < currentFilterCount; j++) {
					if (sessionFilterVect.elementAt(i).toString()
					                     .equalsIgnoreCase(allFilterVect.elementAt(j).toString())) {
						isDuplicated = true;

						break;
					}
				}

				if (isDuplicated) {
					continue;
				}

				allFilterVect.add(sessionFilterVect.elementAt(i));
			}

			System.out.println("\t"
			                   + ((currentFilterCount + sessionFilterVect.size())
			                     - allFilterVect.size()) + " duplicated filters are not added");
		} catch (Throwable ee) {
			System.out.println("Failed to restore Filters from session!");
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param pFileList
	 *            DOCUMENT ME!
	 */
	public void saveSessionStateFiles(List<File> pFileList) {
		System.out.println("saveSessionStateFiles() ...");

		//Create an empty file on system temp directory
		String tmpDir = System.getProperty("java.io.tmpdir");
		System.out.println("java.io.tmpdir: [" + tmpDir + "]");

		File session_filter_file = new File(tmpDir, "session_filters.props");
		Vector<CompositeFilter> allFilterVect = filterMainPanel.getAllFilterVect();

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(session_filter_file));

			for (int i = 0; i < allFilterVect.size(); i++) {
				CompositeFilter theFilter = (CompositeFilter) allFilterVect.elementAt(i);
				AdvancedSetting advSetting = theFilter.getAdvancedSetting();

				if (advSetting.isSessionChecked()) {
					writer.write(theFilter.toString());
					writer.newLine();
				}
			}

			writer.close();
		} catch (Exception ex) {
			System.out.println("Session filter Write error");
			ex.printStackTrace();
		}

		pFileList.add(session_filter_file);
	}
}
