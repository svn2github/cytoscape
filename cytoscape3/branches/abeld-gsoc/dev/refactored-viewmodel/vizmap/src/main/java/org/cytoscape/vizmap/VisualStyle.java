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
package org.cytoscape.vizmap;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualProperty;


/**
 * This is simply a collection of MappingCalculators that define
 * how a set of attributes modify the visual properties of a
 * View object.
 */
public interface VisualStyle {
	/**
	 *  DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 */
	void setMappingCalculator(MappingCalculator c);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param t DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	MappingCalculator getMappingCalculator(VisualProperty<?> t);

	/**
	 *  Removes the mapping for the given VisualProperty
	 *
	 * @param t DOCUMENT ME!
	 *
	 * @return  the removed MappingCalculator (null if one was not defined for the VisualProperty)
	 */
	MappingCalculator removeMappingCalculator(VisualProperty<?> t);
	/**
	 *  DOCUMENT ME!
	 *
	 * @param <T> DOCUMENT ME!
	 * @param prop DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	<T> T getDefault(VisualProperty<T> prop);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param <T> DOCUMENT ME!
	 * @param vp DOCUMENT ME!
	 * @param value DOCUMENT ME!
	 */
	<T> void setDefault(VisualProperty<T> vp, T value);

	// ??
	/**
	 *  DOCUMENT ME!
	 *
	 * @param v DOCUMENT ME!
	 */
	void apply(CyNetworkView v);
}
