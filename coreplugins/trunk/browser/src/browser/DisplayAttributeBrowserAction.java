
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

import javax.swing.SwingConstants;


/**
 *
 * This class is enabled only when browser plugin is loaded. User can on/off
 * browser panel (CytoPanel3) by using f5 key.
 *
 * @author kono
 *
 */
public class DisplayAttributeBrowserAction extends CytoscapeAction {
	/**
	 * Creates a new DisplayAttributeBrowserAction object.
	 */
	public DisplayAttributeBrowserAction() {
		super("Show/Hide attribute browser");

		// setPreferredMenu("Data");
		// setAcceleratorCombo(KeyEvent.VK_F5, 0);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param ev DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent ev) {
		// Check the state of the browser Panel
		CytoPanelState curState = Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)
		                                   .getState();
		int panelCount = Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)
		                          .getCytoPanelComponentCount();

		int targetIndex = 0;
		String curName = null;

		for (int i = 0; i < panelCount; i++) {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setSelectedIndex(i);
			curName = Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)
			                   .getSelectedComponent().getName();

			// System.out.println("CurName = " + curName);
			if (curName.equals("NodeAttributeBrowser")) {
				targetIndex = i;
				Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)
				         .setSelectedIndex(targetIndex);

				break;
			}
		}

		// Check panel state and switch panel state.
		if (curState == CytoPanelState.HIDE) {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setState(CytoPanelState.DOCK);
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setSelectedIndex(targetIndex);
			DisplayAdvancedWindowAction.syncCheckbox(true, 2);
		} else {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setState(CytoPanelState.HIDE);
			DisplayAdvancedWindowAction.syncCheckbox(false, 2);
		}
	} // action performed
}
