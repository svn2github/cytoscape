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
	private FilterIO filterIO = new FilterIO();

	// Used to pass values to other plugin, the BrowserPlugin
	public static Vector<CompositeFilter> getAllFilterVect() {
		if (allFilterVect == null) {
			allFilterVect = new Vector<CompositeFilter>();
		}
		return allFilterVect;
	}
	public static FilterMainPanel getFilterMainPanel() {
		if (filterMainPanel == null) {
			filterMainPanel = new FilterMainPanel(getAllFilterVect());
		}
		return filterMainPanel;
	}

	/**
	 * Creates a new FilterPlugin object.
	 * 
	 * @param icon
	 *            DOCUMENT ME!
	 * @param csfilter
	 *            DOCUMENT ME!
	 */
	public FilterPlugin() {

		ImageIcon icon = new ImageIcon(getClass().getResource(
				"/cytoscape/images/ximian/stock_filter-data-by-criteria.png"));
		ImageIcon icon2 = new ImageIcon(
				getClass()
						.getResource(
								"/cytoscape/images/ximian/stock_filter-data-by-criteria-16.png"));

		// Add a menuItem on "select" menu
		FilterMenuItemAction menuAction = new FilterMenuItemAction(icon2, this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction(
				(CytoscapeAction) menuAction);

		// Add an icon on toolbar
		FilterPluginToolBarAction toolbarAction = new FilterPluginToolBarAction(
				icon, this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction(
				(CytoscapeAction) toolbarAction);

		// initialize the filterMainPanel and add it to the CytoPanelWEST, i.e.
		// the control (management) panel

		if (allFilterVect == null) {
			allFilterVect = new Vector<CompositeFilter>();
		}

		if (filterMainPanel == null) {
			filterMainPanel = new FilterMainPanel(allFilterVect);
		}
		
		restoreInitState();
		
		cytoPanelWest.add("Filters", filterMainPanel);

		// The following two lines are for debug only
		int indexInCytoPanel = cytoPanelWest.indexOfComponent("Filters");
		cytoPanelWest.setSelectedIndex(indexInCytoPanel);
	}


	/**
	 * DOCUMENT ME!
	 */
	public void onCytoscapeExit() {
		// Save global filter to "filters.prop"
		filterIO.saveGlobalPropFile(CytoscapeInit.getConfigFile("filters.props"), allFilterVect);
	}

	public void restoreInitState() {
		File global_filter_file = CytoscapeInit.getConfigFile("filters.props");

		Vector<CompositeFilter> restoredFilters =filterIO.getFilterVectFromPropFile(global_filter_file);
		
		if (restoredFilters != null && restoredFilters.size()>0) {
			allFilterVect.addAll(restoredFilters);			
		}

		if (restoredFilters != null) {
			System.out.println("FilterPlugin: load " + restoredFilters.size() + " filters from filters.prop");			
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
		filterIO.restoreSessionState(pStateFileList, allFilterVect);	
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param pFileList
	 *            DOCUMENT ME!
	 */
	public void saveSessionStateFiles(List<File> pFileList) {
		filterIO.saveSessionStateFiles(pFileList, allFilterVect);
	}
}
