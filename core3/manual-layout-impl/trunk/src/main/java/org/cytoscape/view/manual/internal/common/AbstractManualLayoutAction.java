
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

package org.cytoscape.view.manual.internal.common; 


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;

import org.cytoscape.view.CySwingApplication;
import org.cytoscape.view.CytoscapeAction;
import org.cytoscape.view.CytoPanel;
import org.cytoscape.view.CytoPanelState;
import org.cytoscape.view.CytoPanelName;
import org.cytoscape.view.CytoPanelListener;

import org.cytoscape.session.CyApplicationManager;

/**
 * Base class for displaying cytopanel menu items. This class primarily
 * manages the Layout Menu logic and tab selection of the tools cytopanel. 
 */
public abstract class AbstractManualLayoutAction 
	extends CytoscapeAction 
	implements CytoPanelListener {

    static protected CytoPanel manualLayoutPanel; 

	private static int selectedIndex = -1;

	private final CySwingApplication swingApp;

	int menuIndex; 

	private final static String preferredMenu = "Layout";
	private final String title;

	/**
	 * Base class for displaying cytopanel menu items. 
	 *
	 * @param title The title of the menu item. 
	 * @param menuIndex The desired menu index for the action. 
	 */
	public AbstractManualLayoutAction(String title, int menuIndex, CySwingApplication swingApp, CyApplicationManager appMgr) {
		super(title, appMgr);
		this.title = title;
		this.swingApp = swingApp;
    	manualLayoutPanel = swingApp.getCytoPanel(CytoPanelName.SOUTH_WEST);
		this.menuIndex = menuIndex;
		setPreferredMenu(preferredMenu);
		useCheckBoxMenuItem = true;
		manualLayoutPanel.addCytoPanelListener(this);
	}

	/**
	 * Selects the component and hides/unhides the cytopanel as necessary. 
	 *
	 * @param ev Triggering event - not used. 
	 */
	public void actionPerformed(ActionEvent ev) {

		// Check the state of the manual layout Panel
		CytoPanelState curState = manualLayoutPanel.getState();

		// Case 1: Panel is disabled
		if (curState == CytoPanelState.HIDE) {
			manualLayoutPanel.setState(CytoPanelState.DOCK);
			manualLayoutPanel.setSelectedIndex(menuIndex);
			selectedIndex = menuIndex;

		// Case 2: Panel is in the DOCK/FLOAT and a different panel is selected
		} else if ( manualLayoutPanel.getSelectedIndex() != menuIndex ) {
			manualLayoutPanel.setSelectedIndex(menuIndex);
			selectedIndex = menuIndex;

		// Case 3: The currently selected item is selected 
		} else { 
			manualLayoutPanel.setState(CytoPanelState.HIDE);
			selectedIndex = -1;
		}
	} 

	private JCheckBoxMenuItem getThisItem() {
		JMenu layouts = swingApp.getJMenu(preferredMenu);
		for ( int i = 0; i < layouts.getItemCount(); i++ ) {
			JMenuItem item = layouts.getItem(i);
			if ( item.getText().equals(title) && item instanceof JCheckBoxMenuItem) {
				return (JCheckBoxMenuItem)item;	
			}
		}
		return null;
	}

	/**
	 * Enables of disables the action based on system state. 
	 *
	 * @param ev Triggering event - not used. 
	 */
	public void menuSelected(MenuEvent e) {
		// set the check next to the menu item
		JCheckBoxMenuItem item = getThisItem(); 
		if ( item != null ) {
			if ( manualLayoutPanel.getSelectedIndex() != menuIndex || 
			     manualLayoutPanel.getState() == CytoPanelState.HIDE )
				item.setState(false);
			else 
				item.setState(true);
		}
	
		// enable the menu based on cytopanel state
		CytoPanelState parentState = swingApp.getCytoPanel(CytoPanelName.WEST).getState();
		if ( parentState == CytoPanelState.HIDE )
			setEnabled(false);
		else 
			setEnabled(true);

		// enable the menu based on presence of network 
		enableForNetworkAndView();
	}

	/**
	 * Makes sure the menu check stays in sync with the selections made in the cytopanel.
	 *
	 * @param componentIndex the index of the menu
	 */
	public void onComponentSelected(int componentIndex) {
		selectedIndex = componentIndex;
	}

    public void onStateChange(CytoPanelState newState) {}
	public void onComponentAdded(int count) {}
	public void onComponentRemoved(int count) {}
}
