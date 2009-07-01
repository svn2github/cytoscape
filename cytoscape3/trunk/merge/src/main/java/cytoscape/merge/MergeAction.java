/*
  File: MergeAction.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute of Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
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
package cytoscape.merge;

import org.cytoscape.session.CyNetworkNaming;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MergeAction extends AbstractAction {
	public MergeAction() {
		super("Merge networks");
	}

	/**
	 * This method is called when the user selects the menu item.
	 */
	public void actionPerformed(ActionEvent ae) {
		MergeDialog dialog = new MergeDialog();
		dialog.setVisible(true);

		if (!dialog.isCancelled()) {
			if (dialog.getOperation() == MergeDialog.UNION) {
				GraphSetUtils.createUnionGraph(dialog.getNetworkList(), true, 
		                CyNetworkNaming.getSuggestedNetworkTitle("Union"));
			} else if (dialog.getOperation() == MergeDialog.INTERSECTION) {
				GraphSetUtils.createIntersectionGraph(dialog.getNetworkList(), true,
					    CyNetworkNaming.getSuggestedNetworkTitle("Intersection"));
			} else if (dialog.getOperation() == MergeDialog.DIFFERENCE) {
				GraphSetUtils.createDifferenceGraph(dialog.getNetworkList(), true, 
		                CyNetworkNaming.getSuggestedNetworkTitle("Difference"));
			}
		}
	}
}
