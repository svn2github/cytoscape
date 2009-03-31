/*
  File: HideSelectedAction.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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

//-------------------------------------------------------------------------
// $Revision: 13022 $
// $Date: 2008-02-11 13:59:26 -0800 (Mon, 11 Feb 2008) $
// $Author: mes $
//-------------------------------------------------------------------------
package cytoscape.actions;

import cytoscape.CyNetworkManager;
import cytoscape.util.CytoscapeAction;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyDataTableUtil;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;

import java.awt.event.ActionEvent;
import java.util.Set;
import java.util.List;


//-------------------------------------------------------------------------
/**
 *
 */
public class HideSelectedAction extends CytoscapeAction {
	private final static long serialVersionUID = 1202339869713059L;
	/**
	 * Creates a new HideSelectedAction object.
	 */
	public HideSelectedAction(CyNetworkManager netmgr) {
		super("Hide Selected",netmgr);
		setPreferredMenu("Select.Edges");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
	
		final CyNetwork curr = netmgr.getCurrentNetwork();
		final CyNetworkView view = netmgr.getNetworkView( curr.getSUID() );
		final List<CyNode> selectedNodes = CyDataTableUtil.getNodesInState(curr,"selected",true);
		final List<CyEdge> selectedEdges = CyDataTableUtil.getEdgesInState(curr,"selected",true); 

		HideUtils.setVisibleNodes( selectedNodes, false, view );
		HideUtils.setVisibleEdges( selectedEdges, false, view );

        if ( view != null )
            view.updateView();
	} 
}
