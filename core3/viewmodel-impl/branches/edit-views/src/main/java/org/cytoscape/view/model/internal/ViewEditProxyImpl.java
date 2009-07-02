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

import org.cytoscape.view.model.View;
import org.cytoscape.view.model.ViewChangeListener;
import org.cytoscape.view.model.ViewEditProxy;
import org.cytoscape.view.model.VisualProperty;

public class ViewEditProxyImpl<S> implements ViewEditProxy<S> {
	private final View<S> underlying;

	public ViewEditProxyImpl(View<S> underlying) {
		this.underlying = underlying;
	}

	public void mergeEdits() {
		// FIXME
	}

	// View methods:
	/**
	 * Returns an EditProxy bound to this instance
	 * 
	 * This method can't be called getEditProxy(), since a CyNetworkView is a
	 * View as well, and the two getEditProxy() methods would clash. Thus have
	 * to put return type in method name.
	 * 
	 * @return EditProxy bound to this instance
	 */
	public ViewEditProxy<S> getViewEditProxy() {
		return this;
	}

	/**
	 * @param <T>
	 *            Data type of the visual property. This can be subclasses of
	 *            type T.
	 * @param <V>
	 *            Value of the visual property. This can be subclasses of T.
	 * @param vp
	 *            the VisualProperty
	 * @param value
	 *            actual value stored in this visual property.
	 */
	public <T, V extends T> void setVisualProperty(
			VisualProperty<? extends T> vp, V value) {

	}

	/**
	 * @param <T>
	 *            DOCUMENT ME!
	 * @param vp
	 *            the VisualProperty
	 * @return DOCUMENT ME!
	 */
	public <T> T getVisualProperty(VisualProperty<T> vp) {

	}

	/**
	 * @param <T>
	 *            DOCUMENT ME!
	 * @param vp
	 *            the VisualProperty
	 * @param value
	 *            DOCUMENT ME!
	 */
	public <T, V extends T> void setLockedValue(VisualProperty<? extends T> vp,
			V value) {

	}

	/**
	 * @param vp
	 *            the VisualProperty
	 * @return true if current VisualProperty value is locked
	 */
	public boolean isValueLocked(VisualProperty<?> vp) {

	}

	/**
	 * Clear value lock for given VisualProperty.
	 * 
	 * @param vp
	 *            the VisualProperty
	 */
	public void clearValueLock(VisualProperty<?> vp) {

	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public S getSource() {
		return underlying.getSource(); // this can't have changed
	}

	public long getSUID() {
		return underlying.getSUID(); // this can't have changed
	}
	
	/**
	 * Adds the specified listener to this View.
	 */
	public void addViewChangeListener(ViewChangeListener vcl) {

	}

	/**
	 * Removes the specified listener from this View.
	 */
	public void removeViewChangeListener(ViewChangeListener vcl) {

	}

}
