
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

import cytoscape.Cytoscape;

import cytoscape.util.CytoscapeAction;

import cytoscape.view.cytopanels.CytoPanelState;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.SwingConstants;


/**
 *
 * This class is enabled only when browser plugin is loaded.
 * 3/6/2005 KONO: This action is now under "View" menu.
 *
 * @author kono
 *
 */
public class DisplayAdvancedWindowAction extends CytoscapeAction {
	/**
	 * Creates a new DisplayAdvancedWindowAction object.
	 */
	public DisplayAdvancedWindowAction() {
		super("Show/Hide advanced window");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param ev DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent ev) {
		// Check the state of the browser Panel
		CytoPanelState curState = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).getState();

		int targetIndex = 0;

		// Case 1: Panel is disabled
		if (curState == CytoPanelState.HIDE) {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).setState(CytoPanelState.FLOAT);
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).setSelectedIndex(targetIndex);
			// Need to sync. Desktop menu item
			syncCheckbox(true, 3);

			// Case 2: Panel is in the Dock
		} else if (curState == CytoPanelState.DOCK) {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).setState(CytoPanelState.HIDE);
			syncCheckbox(false, 3);

			// Case 3: Panel is FLOAT
		} else {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).setState(CytoPanelState.HIDE);
			syncCheckbox(false, 3);
		}
	} // action performed

	/**
	 * Find menu item, and sync check box.
	 *
	 * @param on
	 * @param menuItem
	 */
	protected static void syncCheckbox(boolean on, int cytopanelIndex) {
		JCheckBoxMenuItem targetCheckbox = null;
		JMenu targetMenu = Cytoscape.getDesktop().getCyMenus().getViewMenu();
		int menuCount = targetMenu.getMenuComponentCount();

		// Find the location of menu item
		for (int i = 0; i < menuCount; i++) {
			Object component = targetMenu.getMenuComponent(i);

			if (component.getClass().equals(JMenu.class)) {
				if (((JMenu) component).getText().equals("Desktop")) {
					targetCheckbox = (JCheckBoxMenuItem) ((JMenu) component).getMenuComponent(cytopanelIndex
					                                                                          - 1);
				}
			}
		}

		if (targetCheckbox == null) {
			return;
		}

		if (on == true) {
			targetCheckbox.setSelected(true);
		} else {
			targetCheckbox.setSelected(false);
		}
	}
}
