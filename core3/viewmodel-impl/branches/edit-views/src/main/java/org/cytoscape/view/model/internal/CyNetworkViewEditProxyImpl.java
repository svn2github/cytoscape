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

import java.util.List;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.GraphObject;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewEditProxy;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.ViewChangeListener;
import org.cytoscape.view.model.ViewEditProxy;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.events.NetworkViewChangedEvent;
import org.cytoscape.view.model.events.NetworkViewChangedListener;

public class CyNetworkViewEditProxyImpl implements CyNetworkViewEditProxy {
	private final CyNetworkView underlying;
	private final ViewEditProxy<CyNetwork> viewEditProxyOfSelf;

	public CyNetworkViewEditProxyImpl(CyNetworkView underlying) {
		this.underlying = underlying;
	}

	public void mergeEdits() {
		// FIXME
	}

	// CyNetworkView methods:
	/**
	 * Returns a View for a specified Node.
	 * 
	 * @param n
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public View<CyNode> getNodeView(CyNode n) {

	}

	/**
	 * Returns a list of Views for all CyNodes in the network.
	 * 
	 * @return DOCUMENT ME!
	 */
	public List<View<CyNode>> getNodeViews() {

	}

	/**
	 * Returns a View for a specified Edge.
	 * 
	 * @param n
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public View<CyEdge> getEdgeView(CyEdge n) {

	}

	/**
	 * Returns a list of Views for all CyEdges in the network.
	 * 
	 * @return DOCUMENT ME!
	 */
	public List<View<CyEdge>> getEdgeViews() {

	}

	/**
	 * Returns a list of all View including those for Nodes, Edges, and Network.
	 * 
	 * @return DOCUMENT ME!
	 */
	public List<View<? extends GraphObject>> getAllViews() {

	}

	/**
	 * Returns an EditProxy bound to this instance
	 * 
	 * This method can't be called getEditProxy(), since a CyNetworkView is a
	 * View as well, and the two getEditProxy() methods would clash. Thus have
	 * to put return type in method name.
	 * 
	 * @return EditProxy bound to this instance
	 */
	public CyNetworkViewEditProxy getCyNetworkViewEditProxy() {
		return this;
	}

	// temp methods
	// FIXME: should mergeEdits before doing these?
	public void fitContent() {
		underlying.fitContent();
	}

	public void fitSelected() {
		underlying.fitSelected();
	}

	public void updateView() {
		underlying.updateView();
	}

	/**
	 * Returns the given subset.
	 * 
	 * @param name
	 *            name of the subset to return
	 * @return the subset
	 */
	public Set<View<? extends GraphObject>> getSubset(String name) {

	}

	/**
	 * If subset already exists, replaces it with given Set.
	 * 
	 * @param name
	 *            name of the subset
	 * @param subset
	 *            the Views the subset will contain
	 */
	public void createSubset(String name,
			Set<View<? extends GraphObject>> subset) {

	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param name
	 *            DOCUMENT ME!
	 * @param toAdd
	 *            DOCUMENT ME!
	 */
	public void addToSubset(String name, Set<View<? extends GraphObject>> toAdd) {

	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param name
	 *            DOCUMENT ME!
	 * @param toRemove
	 *            DOCUMENT ME!
	 */
	public void removeFromSubset(String name,
			Set<View<? extends GraphObject>> toRemove) {

	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param name
	 *            DOCUMENT ME!
	 */
	public void deleteSubset(String name) {

	}

	// The following are the View<CyNetwork> (and thus ViewEditProxy<CyNetwork>
	// ) methods, implemented as proxies to viewEditProxyOfSelf.methods

	/**
	 * {@inheritDoc}
	 */
	public <P, V extends P> void setVisualProperty(
			VisualProperty<? extends P> vp, V o) {
		viewEditProxyOfSelf.setVisualProperty(vp, o);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T getVisualProperty(VisualProperty<T> vp) {
		return viewEditProxyOfSelf.getVisualProperty(vp);
	}

	/**
	 * {@inheritDoc}
	 */
	public <P, V extends P> void setLockedValue(VisualProperty<? extends P> vp,
			V value) {
		viewEditProxyOfSelf.setLockedValue(vp, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isValueLocked(VisualProperty<?> vp) {
		return viewEditProxyOfSelf.isValueLocked(vp);
	}

	/**
	 * {@inheritDoc}
	 */
	public void clearValueLock(VisualProperty<?> vp) {
		viewEditProxyOfSelf.clearValueLock(vp);
	}

	/**
	 * {@inheritDoc}
	 */
	public CyNetwork getSource() {
		return viewEditProxyOfSelf.getSource();
	}

	/**
	 * {@inheritDoc}
	 */
	public long getSUID() {
		return viewEditProxyOfSelf.getSUID();
	}

	public void addViewChangeListener(ViewChangeListener vcl) {
		viewEditProxyOfSelf.addViewChangeListener(vcl);
	}

	public void removeViewChangeListener(ViewChangeListener vcl) {
		viewEditProxyOfSelf.removeViewChangeListener(vcl);
	}
}