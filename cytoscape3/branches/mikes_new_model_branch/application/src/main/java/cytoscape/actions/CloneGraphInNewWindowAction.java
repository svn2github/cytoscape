/*
 File: CloneGraphInNewWindowAction.java

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

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.EdgeView;
import org.cytoscape.view.GraphView;
import org.cytoscape.view.NodeView;
import org.cytoscape.vizmap.VisualStyle;

import javax.swing.event.MenuEvent;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.Iterator;

/**
 *
 */
public class CloneGraphInNewWindowAction extends CytoscapeAction {
	private final static long serialVersionUID = 1202339869106208L;
	/**
	 * Creates a new CloneGraphInNewWindowAction object.
	 */
	public CloneGraphInNewWindowAction() {
		super("Clone current network");
		setPreferredMenu("File.New.Network");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
	// TODO
	System.out.println("NOT implemented");
	/*
		CyNetwork origNet = Cytoscape.getCurrentNetwork();
		GraphView origView = Cytoscape.getCurrentNetworkView();
		VisualStyle vs = Cytoscape.getVisualMappingManager().getVisualStyle(); 

		CyNetwork new_network = Cytoscape.createNetwork(origNet.getNodeList(),
		                                                origNet.getEdgeList(),
		                                                origNet.attrs().get("name",String.class) + " copy", 
														null,
														true);

		// only clone the view if one actually exists
		if ( origView == null || origView == Cytoscape.getNullNetworkView() )
			return;

		GraphView newView = Cytoscape.getNetworkView(new_network.getSUID());
		if ( newView != null || newView != Cytoscape.getNullNetworkView() ) {

        	// Use nodes as keys because they are less volatile than views...
			for ( CyNode n : origView.getGraphPerspective().getNodeList() ) {

				NodeView onv = origView.getNodeView(n);
				NodeView nnv = newView.getNodeView(n);

				nnv.setXPosition(onv.getXPosition());
				nnv.setYPosition(onv.getYPosition());
			}

			newView.setZoom(origView.getZoom());
			Point2D origCenter = origView.getCenter();
			newView.setCenter(origCenter.getX(), origCenter.getY());

			// set edge anchors and bends
			for ( CyEdge ee : origView.getGraphPerspective().getEdgeList() ) {
				EdgeView oev = origView.getEdgeView(ee);
				EdgeView nev = newView.getEdgeView(ee);

				nev.getBend().setHandles(oev.getBend().getHandles());
				nev.getBend().setHandles(oev.getBend().getHandles());

				nev.setLineType( oev.getLineType() );
			}

			Cytoscape.getVisualMappingManager().setVisualStyle(vs);
		}
		*/
	}

	public void menuSelected(MenuEvent e) {
		enableForNetwork();
	}
}
