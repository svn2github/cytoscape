/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.viewmodel.internal;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.GraphObject;
import org.cytoscape.model.events.AddedEdgeEvent;
import org.cytoscape.model.events.AddedNodeEvent;
import org.cytoscape.model.events.AboutToRemoveEdgeEvent;
import org.cytoscape.model.events.AboutToRemoveNodeEvent;
import org.cytoscape.model.events.AddedEdgeListener;
import org.cytoscape.model.events.AddedNodeListener;
import org.cytoscape.model.events.AboutToRemoveEdgeListener;
import org.cytoscape.model.events.AboutToRemoveNodeListener;

import org.cytoscape.viewmodel.CyNetworkView;
import org.cytoscape.viewmodel.View;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
/**
 * 
 */
public class RowOrientedNetworkViewImpl implements CyNetworkView,
						   AddedEdgeListener, 
						   AddedNodeListener, 
						   AboutToRemoveEdgeListener, 
						   AboutToRemoveNodeListener {
    private CyEventHelper eventHelper;
    private CyNetwork network;
    private HashMap<CyNode, RowOrientedViewImpl<CyNode>> nodeViews;
    private HashMap<CyEdge, RowOrientedViewImpl<CyEdge>> edgeViews;
    private RowOrientedViewImpl<CyNetwork> networkView;

    public RowOrientedNetworkViewImpl(CyEventHelper eventHelper, CyNetwork network){
	this.eventHelper = eventHelper;
	this.network = network;
	nodeViews = new HashMap<CyNode, RowOrientedViewImpl<CyNode>>();
	edgeViews = new HashMap<CyEdge, RowOrientedViewImpl<CyEdge>>();
	for (CyNode node: network.getNodeList()){
	    nodeViews.put(node, new RowOrientedViewImpl<CyNode>(node));
	}

	for (CyEdge edge: network.getEdgeList()){
	    edgeViews.put(edge, new RowOrientedViewImpl<CyEdge>(edge));
	}
	networkView = new RowOrientedViewImpl<CyNetwork>(network);
    }
	/**
	 * Returns the network this view was created for.  The network is immutable for this
	 * view, so there is no way to set it.
	 *
	 * @return  DOCUMENT ME!
	 */
    public CyNetwork getNetwork(){
	return network;
    }

	/**
	 * Returns a View for a specified Node.
	 *
	 * @param n  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
    public View<CyNode> getCyNodeView(CyNode n){
	return nodeViews.get(n);
    }

	/**
	 * Returns a list of Views for all CyNodes in the network.
	 *
	 * @return  DOCUMENT ME!
	 */
    public List<View<CyNode>> getCyNodeViews(){
	return new ArrayList(nodeViews.values());
    }

	/**
	 * Returns a View for a specified Edge.
	 *
	 * @param e  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
    public View<CyEdge> getCyEdgeView(CyEdge e){
	return edgeViews.get(e);
    }

	/**
	 * Returns a list of Views for all CyEdges in the network.
	 *
	 * @return  DOCUMENT ME!
	 */
    public List<View<CyEdge>> getCyEdgeViews(){
	return new ArrayList(edgeViews.values());
    }

	/**
	 * Returns the view for this Network.
	 *
	 * @return  DOCUMENT ME!
	 */
    public View<CyNetwork> getNetworkView(){
	return networkView;
    }

	/**
	 * Returns a list of all View including those for Nodes, Edges, and Network.
	 *
	 * @return  DOCUMENT ME!
	 */
    public List<View<?extends GraphObject>> getAllViews(){
	List result = new ArrayList(nodeViews.values());
	result.addAll(edgeViews.values());
	result.add(networkView);
	return result;
    }
    /* Handle events in model and update accordingly: */
	public void handleEvent(AddedEdgeEvent e) {
	    System.out.println("handling event: "+e);
		if ( network != e.getSource() )
			return;

		CyEdge edge = e.getEdge();
		edgeViews.put(edge, new RowOrientedViewImpl<CyEdge>(edge));
		// FIXME: fire events!
	}

	public void handleEvent(AddedNodeEvent e) {
	    System.out.println("handling event: "+e);
		if ( network != e.getSource() )
			return;

		CyNode node = e.getNode();
		nodeViews.put(node, new RowOrientedViewImpl<CyNode>(node));
	}

	public void handleEvent(AboutToRemoveEdgeEvent e) {
	    System.out.println("handling event: "+e);
		if ( network != e.getSource() )
			return;

		CyEdge edge = e.getEdge();
		edgeViews.remove(edge);
	}

	public void handleEvent(AboutToRemoveNodeEvent e) {
	    System.out.println("handling event: "+e);
		if ( network != e.getSource() )
			return;

		CyNode node = e.getNode();
		edgeViews.remove(node);

	}
}
