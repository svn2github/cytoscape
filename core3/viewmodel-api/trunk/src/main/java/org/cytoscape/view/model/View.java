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
package org.cytoscape.view.model;

import org.cytoscape.model.Identifiable;


/**
 * The base interface that defines the methods used to set visual properties
 * for nodes, edges, and networks.
 *
 * Think of it as a row in the viewmodel table.
 *
 * @param <S> the base (model-level) object for which this is a View. For example, CyNode or CyEdge
 */
public interface View<S> extends Identifiable {
	
	/**
	 * @param <T> Data type of the visual property.  This can be subclasses of type T.
	 * @param <V> Value of the visual property.  This can be subclasses of T. 
	 * @param vp the VisualProperty
	 * @param value actual value stored in this visual property.
	 */
	<T, V extends T> void setVisualProperty(VisualProperty<? extends T> vp, V value);

	/**
	 * @param <T> DOCUMENT ME!
	 * @param vp the VisualProperty
	 * @return DOCUMENT ME!
	 */
	<T> T getVisualProperty(VisualProperty<T> vp);

	/**
	 * @param <T> DOCUMENT ME!
	 * @param vp the VisualProperty
	 * @param value DOCUMENT ME!
	 */
	<T, V extends T> void setLockedValue(VisualProperty<? extends T> vp, V value);

	/**
	 * @param vp the VisualProperty
	 * @return true if current VisualProperty value is locked
	 */
	boolean isValueLocked(VisualProperty<?> vp);

	/**
	 * Clear value lock for given VisualProperty.
	 *
	 * @param vp the VisualProperty
	 */
	void clearValueLock(VisualProperty<?> vp);

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	S getSource();
}
