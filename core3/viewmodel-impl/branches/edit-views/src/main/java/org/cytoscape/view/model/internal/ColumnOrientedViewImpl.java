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

import org.cytoscape.model.SUIDFactory;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.ViewChangeListener;

import java.util.List;
import java.util.ArrayList;


/**
 * The base interface that defines the methods used to set visual properties
 * for nodes, edges, and networks.
 *
 * Think of it as a row in the viewmodel table.
 * 
 * @param <S> the base (model-level) object for which this is a View. For example, CyNode or CyEdge
 */
public class ColumnOrientedViewImpl<S> implements View<S> {
	private final S source;
	private final long suid;
	private final ColumnOrientedNetworkViewImpl networkView;
	private final List<ViewChangeListener> listeners;

	/**
	 * Creates a new ColumnOrientedViewImpl object.
	 *
	 * @param source  DOCUMENT ME!
	 */
	public ColumnOrientedViewImpl(final S source, final ColumnOrientedNetworkViewImpl networkView) {
		suid = SUIDFactory.getNextSUID();
		this.source = source;
		this.networkView = networkView;
		listeners = new ArrayList<ViewChangeListener>();
	}

	/**
	 * The VisualProperty object identifies which visual property to set and the Object
	 * determines the value.   We should probably consider doing something more type safe like
	 * what we're doing for Attributes.
	 *
	 * @param <T>  DOCUMENT ME!
	 * @param vp  DOCUMENT ME!
	 * @param o  DOCUMENT ME!
	 */
	public <P, V extends P> void setVisualProperty(final VisualProperty<? extends P> vp, final V value) {
		networkView.getColumn(vp).setValue(this, value);
		for ( ViewChangeListener vcl : listeners ) 
			vcl.visualPropertySet(vp,value);
	}

	/**
	 * Getting visual properties in this way incurs lots of casting. We should probably
	 * consider doing something more type safe like what we're doing for Attributes.
	 *
	 * @param <T>  DOCUMENT ME!
	 * @param vp  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public <T> T getVisualProperty(final VisualProperty<T> vp) {
		return networkView.getColumn(vp).getValue(this);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public S getSource() {
		return source;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public long getSUID() {
		return suid;
	}

	/**
	 * Sets ByPass value. This value won't be override by
	 * VisualStyle or such, until it is cleared.
	 *
	 * Note: this should only be used when the user, interactively
	 * sets a bypass either through the gui or through a scripting
	 * api. All other access should be done by defining an
	 * appropriate MappingCalculator.
	 *
	 * @param <T>  DOCUMENT ME!
	 * @param vp  The visualProperty for which to set the value
	 * @param value the value to set
	 */
	public <P, V extends P> void setLockedValue(final VisualProperty<? extends P> vp, final V value){
		networkView.getColumn(vp).setLockedValue(this, value);
	}

	/**
	 * @param vp the VisualProperty
	 * @return true if current VisualProperty value is locked
	 */
	public boolean isValueLocked(final VisualProperty<?> vp){
		return networkView.getColumn(vp).isValueLocked(this);
	}

	/**
	 * Clear value lock for given VisualProperty.
	 * 
	 * @param vp the VisualProperty 
	 */
	public void clearValueLock(final VisualProperty<?> vp){
		networkView.getColumn(vp).clearValueLock(this);
	}

	public void addViewChangeListener(ViewChangeListener vcl) {
		listeners.add(vcl);
	}

	public void removeViewChangeListener(ViewChangeListener vcl) {
		listeners.remove(vcl);
	}
}
