
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

package org.cytoscape.view.vizmap.gui.internal.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;


/**
 *
 */
public class RemoveStyleAction extends AbstractVizMapperAction {
	/**
	 * Creates a new RemoveStyleAction object.
	 */
	public RemoveStyleAction() {
		super();
	}

	private static final long serialVersionUID = -916650015995966595L;

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {

		VisualStyle currentStyle = this.vizMapperMainPanel.getSelectedVisualStyle();

		if (currentStyle.equals(vizMapperMainPanel.getDefaultVisualStyle())) {
			JOptionPane.showMessageDialog(vizMapperMainPanel, "You cannot delete default style.",
			                              "Cannot remove defalut style!", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// make sure the user really wants to do this
		final String styleName = currentStyle.getTitle();
		final String checkString = "Are you sure you want to permanently delete"
		                           + " the visual style '" + styleName + "'?";
		int ich = JOptionPane.showConfirmDialog(vizMapperMainPanel, checkString,
		                                        "Confirm Delete Style", JOptionPane.YES_NO_OPTION);

		if (ich == JOptionPane.YES_OPTION) {
			
			vmm.removeVisualStyle(currentStyle);
			// try to switch to the default style
			currentStyle = vizMapperMainPanel.getDefaultVisualStyle();

			vizMapperMainPanel.switchVS(currentStyle);
			vizMapperMainPanel.getDefaultImageManager().remove(currentStyle);
			vizMapPropertySheetBuilder.getPropertyMap().remove(currentStyle);

			// Apply to the current view
			final CyNetworkView view = cyNetworkManager.getCurrentNetworkView();
			if(view != null)
				vmm.setVisualStyle(currentStyle, view);
		}
	}
}
