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
import java.util.HashMap;


/**
 * The base interface that defines the methods used to set visual properties
 * for nodes, edges, and networks.
 *
 * Think of it as a row in the viewmodel table.
 * 
 * @param <S> the base (model-level) object for which this is a View. For example, CyNode or CyEdge
 */
public class ColumnOrientedViewImpl<S> implements View<S> {
	private static final String VP_IS_NULL = "VisualProperty is null";

	private final S source;
	private final HashMap<VisualProperty<?>, Object> vpValues;

	// note: this surely could be done more efficiently...:
	private final HashMap<VisualProperty<?>, Boolean> bypassLocks;
	private final long suid;
	private final List<ViewChangeListener> listeners;

	/**
	 * Creates a new ColumnOrientedViewImpl object.
	 *
	 * @param source  DOCUMENT ME!
	 */
	public ColumnOrientedViewImpl(final S source) {
		suid = SUIDFactory.getNextSUID();
		this.source = source;
		listeners = new ArrayList<ViewChangeListener>();
		vpValues = new HashMap<VisualProperty<?>, Object>();
		bypassLocks = new HashMap<VisualProperty<?>, Boolean>();
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
		if (vp == null)
			throw new NullPointerException(VP_IS_NULL);

		final Boolean b = bypassLocks.get(vp);

		if ((b == null) || !b.booleanValue())
			vpValues.put(vp, value);
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
		if (vp == null)
			throw new NullPointerException(VP_IS_NULL);

		if (vpValues.containsKey(vp))
			return (T) vpValues.get(vp);
		else

			return vp.getDefault();
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
		if (vp == null)
			throw new NullPointerException(VP_IS_NULL);

		setVisualProperty(vp, value);
		bypassLocks.put(vp, Boolean.TRUE);
	}

	/**
	 * @param vp the VisualProperty
	 * @return true if current VisualProperty value is locked
	 */
	public boolean isValueLocked(final VisualProperty<?> vp){
		if (vp == null)
			throw new NullPointerException(VP_IS_NULL);

		final Boolean value = bypassLocks.get(vp);

		if (value == null) {
			return false;
		} else {
			return value.booleanValue();
		}
	}

	/**
	 * Clear value lock for given VisualProperty.
	 * 
	 * @param vp the VisualProperty 
	 */
	public void clearValueLock(final VisualProperty<?> vp){
		if (vp == null)
			throw new NullPointerException(VP_IS_NULL);

		bypassLocks.put(vp, Boolean.FALSE);
	}

	public void addViewChangeListener(ViewChangeListener vcl) {
		listeners.add(vcl);
	}

	public void removeViewChangeListener(ViewChangeListener vcl) {
		listeners.remove(vcl);
	}
}
