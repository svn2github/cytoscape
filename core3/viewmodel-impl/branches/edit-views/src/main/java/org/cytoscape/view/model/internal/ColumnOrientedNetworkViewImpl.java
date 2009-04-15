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
package org.cytoscape.view.model.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.GraphObject;
import org.cytoscape.model.events.AboutToRemoveEdgeEvent;
import org.cytoscape.model.events.AboutToRemoveEdgeListener;
import org.cytoscape.model.events.AboutToRemoveNodeEvent;
import org.cytoscape.model.events.AboutToRemoveNodeListener;
import org.cytoscape.model.events.AddedEdgeEvent;
import org.cytoscape.model.events.AddedEdgeListener;
import org.cytoscape.model.events.AddedNodeEvent;
import org.cytoscape.model.events.AddedNodeListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.ViewChangeListener;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.events.SubsetChangedListener;
import org.cytoscape.view.model.events.SubsetCreatedListener;
import org.cytoscape.view.model.events.SubsetDestroyedListener;
import org.cytoscape.view.model.events.NetworkViewChangedEvent;
import org.cytoscape.view.model.events.NetworkViewChangedListener;
import org.cytoscape.view.model.internal.events.SubsetChangedEventImpl;
import org.cytoscape.view.model.internal.events.SubsetCreatedEventImpl;
import org.cytoscape.view.model.internal.events.SubsetDestroyedEventImpl;

/**
 *
 */
public class ColumnOrientedNetworkViewImpl implements CyNetworkView,
		AddedEdgeListener, AddedNodeListener, AboutToRemoveEdgeListener,
		AboutToRemoveNodeListener {

	private CyEventHelper eventHelper;
	private CyNetwork network;
	private HashMap<CyNode, ColumnOrientedViewImpl<CyNode>> nodeViews;
	private HashMap<CyEdge, ColumnOrientedViewImpl<CyEdge>> edgeViews;
	private HashMap<String, Set<View<? extends GraphObject>>> subsets;
	private ColumnOrientedViewImpl<CyNetwork> viewCyNetwork;
	private HashMap<VisualProperty<?>, ColumnOrientedViewColumn<?>> columns;

	/**
	 * Creates a new ColumnOrientedNetworkViewImpl object.
	 * 
	 * @param eventHelper
	 *            DOCUMENT ME!
	 * @param network
	 *            DOCUMENT ME!
	 * @param bc
	 *            DOCUMENT ME!
	 */
	public ColumnOrientedNetworkViewImpl(final CyEventHelper eventHelper,
			final CyNetwork network) {
		this.eventHelper = eventHelper;
		this.network = network;

		nodeViews = new HashMap<CyNode, ColumnOrientedViewImpl<CyNode>>();
		edgeViews = new HashMap<CyEdge, ColumnOrientedViewImpl<CyEdge>>();
		subsets = new HashMap<String, Set<View<? extends GraphObject>>>();
		columns = new HashMap<VisualProperty<?>, ColumnOrientedViewColumn<?>>();

		for (CyNode node : network.getNodeList()) {
			nodeViews.put(node, new ColumnOrientedViewImpl<CyNode>(node, this));
		}

		for (CyEdge edge : network.getEdgeList()) {
			edgeViews.put(edge, new ColumnOrientedViewImpl<CyEdge>(edge, this));
		}

		viewCyNetwork = new ColumnOrientedViewImpl<CyNetwork>(network, this);
	}

	/**
	 * Returns a View for a specified Node.
	 * 
	 * @param n
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public View<CyNode> getNodeView(final CyNode n) {
		return nodeViews.get(n);
	}

	/**
	 * Returns a list of Views for all CyNodes in the network.
	 * 
	 * @return DOCUMENT ME!
	 */
	public List<View<CyNode>> getNodeViews() {
		return new ArrayList<View<CyNode>>(nodeViews.values());
	}

	/**
	 * Returns a View for a specified Edge.
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public View<CyEdge> getEdgeView(final CyEdge e) {
		return edgeViews.get(e);
	}

	/**
	 * Returns a list of Views for all CyEdges in the network.
	 * 
	 * @return DOCUMENT ME!
	 */
	public List<View<CyEdge>> getEdgeViews() {
		return new ArrayList<View<CyEdge>>(edgeViews.values());
	}

	/**
	 * Returns a list of all View including those for Nodes, Edges, and Network.
	 * 
	 * @return DOCUMENT ME!
	 */
	public List<View<? extends GraphObject>> getAllViews() {
		final List<View<? extends GraphObject>> result = new ArrayList<View<? extends GraphObject>>(
				nodeViews.values());
		result.addAll(edgeViews.values());
		result.add(this);

		return result;
	}

	/* Handle events in model and update accordingly: */
	/**
	 * DOCUMENT ME!
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void handleEvent(final AddedEdgeEvent e) {
		if (network != e.getSource())
			return;

		final CyEdge edge = e.getEdge();
		edgeViews.put(edge, new ColumnOrientedViewImpl<CyEdge>(edge, this)); // FIXME:
																				// View
																				// creation
																				// here
																				// and
																				// in
																				// initializer:
																				// should
																				// be
																				// in
																				// one
																				// place

		// FIXME: fire events!
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void handleEvent(final AddedNodeEvent e) {
		if (network != e.getSource())
			return;

		final CyNode node = e.getNode();
		nodeViews.put(node, new ColumnOrientedViewImpl<CyNode>(node, this));
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void handleEvent(final AboutToRemoveEdgeEvent e) {
		System.out.println("handling event: " + e);

		if (network != e.getSource())
			return;

		edgeViews.remove(e.getEdge());
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void handleEvent(final AboutToRemoveNodeEvent e) {
		if (network != e.getSource())
			return;

		edgeViews.remove(e.getNode());
	}

	public <T> ColumnOrientedViewColumn<T> getColumn(final VisualProperty<? extends T> vp) {
		if (vp == null)
			throw new NullPointerException("VisualProperty must not be null");
		if (columns.containsKey(vp)) {
			return (ColumnOrientedViewColumn<T>) columns.get(vp);
		} else { // create column
			ColumnOrientedViewColumn<T> column = new ColumnOrientedViewColumn<T>((VisualProperty<T>) vp);
			columns.put(vp, column);
			return column;
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param name
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Set<View<? extends GraphObject>> getSubset(final String name) {
		return subsets.get(name);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param name
	 *            DOCUMENT ME!
	 * @param subset
	 *            DOCUMENT ME!
	 */
	public void createSubset(final String name,
			final Set<View<? extends GraphObject>> subset) {
		subsets.put(name, subset);
		eventHelper.fireSynchronousEvent(
				new SubsetCreatedEventImpl(this, name),
				SubsetCreatedListener.class);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param name
	 *            DOCUMENT ME!
	 * @param toAdd
	 *            DOCUMENT ME!
	 */
	public void addToSubset(final String name,
			final Set<View<? extends GraphObject>> toAdd) {
		final Set<View<? extends GraphObject>> subset = subsets.get(name);

		if (subset == null)
			throw new NullPointerException("non-existent subset");

		subset.addAll(toAdd);
		eventHelper.fireSynchronousEvent(
				new SubsetChangedEventImpl(this, name),
				SubsetChangedListener.class);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param name
	 *            DOCUMENT ME!
	 * @param toRemove
	 *            DOCUMENT ME!
	 */
	public void removeFromSubset(final String name,
			final Set<View<? extends GraphObject>> toRemove) {
		final Set<View<? extends GraphObject>> subset = subsets.get(name);

		if (subset == null)
			throw new NullPointerException("non-existent subset");

		subset.removeAll(toRemove);
		eventHelper.fireSynchronousEvent(
				new SubsetChangedEventImpl(this, name),
				SubsetChangedListener.class);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param name
	 *            DOCUMENT ME!
	 */
	public void deleteSubset(final String name) {
		subsets.remove(name);
		eventHelper.fireSynchronousEvent(new SubsetDestroyedEventImpl(this,
				name), SubsetDestroyedListener.class);
	}
	// The following are the View<CyNetwork> methods, implemented as proxies to viewCyNetwork.methods

	/** {@inheritDoc}
	 */
	public <P, V extends P> void setVisualProperty(VisualProperty<? extends P> vp, V o){
		viewCyNetwork.setVisualProperty(vp, o);
	}
	/** {@inheritDoc}
	 */
	public <T> T getVisualProperty(VisualProperty<T> vp){
		return viewCyNetwork.getVisualProperty(vp);
	}
	/** {@inheritDoc}
	 */
	public <P, V extends P> void setLockedValue(VisualProperty<? extends P> vp, V value){
		viewCyNetwork.setLockedValue(vp, value);
	}

	/** {@inheritDoc}
	 */
	public boolean isValueLocked(VisualProperty<?> vp){
		return viewCyNetwork.isValueLocked(vp);
	}

	/** {@inheritDoc}
	 */
	public void clearValueLock(VisualProperty<?> vp){
		viewCyNetwork.clearValueLock(vp);
	}

	/** {@inheritDoc}
	 */
	public CyNetwork getSource(){
		return viewCyNetwork.getSource();
	}

	/** {@inheritDoc}
	 */
	public long getSUID() {
		return viewCyNetwork.getSUID();
	}

	public void addViewChangeListener(ViewChangeListener vcl) {
		viewCyNetwork.addViewChangeListener(vcl);
	}

	public void removeViewChangeListener(ViewChangeListener vcl) {
		viewCyNetwork.removeViewChangeListener(vcl);
	}

	public void fitContent() {
		System.out.println("running dummy fitContent");
	}
	public void fitSelected() {
		System.out.println("running dummy fitSelected");
	}
	public void updateView() {
		eventHelper.fireAsynchronousEvent(
				new NetworkViewChangedEvent() {
					public Object getSource() {
						return ColumnOrientedNetworkViewImpl.this; 
					} 
					public CyNetworkView getNetworkView() { 
						return ColumnOrientedNetworkViewImpl.this; 
					} 
				},  NetworkViewChangedListener.class);
	}
}
