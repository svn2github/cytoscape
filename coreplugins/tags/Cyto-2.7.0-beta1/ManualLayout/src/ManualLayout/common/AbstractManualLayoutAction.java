
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

package ManualLayout.common; 


import cytoscape.Cytoscape;
import cytoscape.data.SelectEvent;
import cytoscape.data.SelectEventListener;
import cytoscape.graph.layout.algorithm.MutablePolyEdgeGraphLayout;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.BiModalJSplitPane;
import cytoscape.view.cytopanels.CytoPanelImp;
import cytoscape.view.cytopanels.CytoPanelListener;
import cytoscape.view.cytopanels.CytoPanelState;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;


/**
 * Base class for displaying cytopanel menu items. 
 */
public abstract class AbstractManualLayoutAction extends CytoscapeAction implements CytoPanelListener {

	// set up the cytopanel
    static protected CytoPanelImp cytoPanel1;
    static protected CytoPanelImp manualLayoutPanel;
    static protected BiModalJSplitPane split;
	static {
        cytoPanel1 = (CytoPanelImp) Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
        manualLayoutPanel = (CytoPanelImp) Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH_WEST);
        split = new BiModalJSplitPane(Cytoscape.getDesktop(), JSplitPane.VERTICAL_SPLIT,
                                                      BiModalJSplitPane.MODE_HIDE_SPLIT, new JPanel(),
                                                    manualLayoutPanel);
        split.setResizeWeight(0);
        manualLayoutPanel.setCytoPanelContainer(split);
        manualLayoutPanel.setMinimumSize(new Dimension(180, 230));
        manualLayoutPanel.setMaximumSize(new Dimension(180, 230));
        manualLayoutPanel.setPreferredSize(new Dimension(180, 230));
	}
	private static int selectedIndex = -1;

	int menuIndex; 

	/**
	 * Base class for displaying cytopanel menu items. 
	 *
	 * @param title The title of the menu item. 
	 * @param menuIndex The desired menu index for the action. 
	 */
	public AbstractManualLayoutAction(String title, int menuIndex) {
		super(title);
		this.menuIndex = menuIndex;
		setPreferredMenu("Layout");
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

			cytoPanel1.addComponentToSouth(split);

		// Case 2: Panel is in the DOCK/FLOAT and a different panel is selected
		} else if ( manualLayoutPanel.getSelectedIndex() != menuIndex ) {
			manualLayoutPanel.setSelectedIndex(menuIndex);
			selectedIndex = menuIndex;

		// Case 3: The currently selected item is selected 
		} else { 
			manualLayoutPanel.setState(CytoPanelState.HIDE);
			selectedIndex = -1;
			
			//Remove the manuallayoutPanel
			//removeComponentAtSouth(split) does not work, overwrite it is a workaround
			cytoPanel1.addComponentToSouth(new javax.swing.JLabel());
		}

		cytoPanel1.validate();
	} 

	/**
	 * Enables of disables the action based on system state. 
	 *
	 * @param ev Triggering event - not used. 
	 */
	public void menuSelected(MenuEvent e) {
		enableForNetworkAndView();
		JCheckBoxMenuItem item = (JCheckBoxMenuItem)Cytoscape.getDesktop().getCyMenus().getLayoutMenu().getItem(menuIndex);
		if ( selectedIndex != menuIndex )
			item.setState(false);
		else 
			item.setState(true);
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
