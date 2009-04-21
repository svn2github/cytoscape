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
package org.cytoscape.view.vizmap;

import java.util.Collection;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualProperty;


/**
 * This is simply a collection of MappingCalculators that define how a set of
 * attributes modify the visual properties of a View object.
 *
 */
public interface VisualStyle {
	/**
	 * Returns name of this visual style. This should NOT be used as ID of this
	 * Visual Style. Just for GUI components.
	 *
	 * @return title of this visual style
	 */
	public String getTitle();

	/**
	 * Set new title of this VS.
	 * Will be used by rename function.
	 *
	 * @param title
	 *            New title.
	 */
	public void setTitle(String title);

	/**
	 * Add a new mapping for this Visual Style.
	 *
	 * Note: renamed from "set" to "add" for consistency.
	 *
	 * @param mapping
	 *            DOCUMENT ME!
	 */
	public void addVisualMappingFunction(VisualMappingFunction<?, ?> mapping);

	/**
	 *  get current mapping for the Visual Property vp.
	 *
	 * @param <V> DOCUMENT ME!
	 * @param vp DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public <V> VisualMappingFunction<?, V> getVisualMappingFunction(VisualProperty<V> vp);

	/**
	 *  Returns all available mappings.
	 *
	 * @return  All visual mappings for this style.
	 */
	public Collection<VisualMappingFunction<?, ?>> getAllVisualMappingFunctions();

	/**
	 *  Remove a mapping for Visual Property.
	 *
	 * @param <V> DOCUMENT ME!
	 * @param vp DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public <V> VisualMappingFunction<?, V> removeVisualMappingFunction(VisualProperty<V> vp);

	/**
	 *  Returns default value for the Visual Property vp.
	 *
	 * @param <V> DOCUMENT ME!
	 * @param vp DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public <V> V getDefaultValue(VisualProperty<V> vp);

	/**
	 *  Setter for the default value of vp.
	 *
	 * @param <V> DOCUMENT ME!
	 * @param vp DOCUMENT ME!
	 * @param value DOCUMENT ME!
	 */
	public <V> void setDefaultValue(VisualProperty<V> vp, V value);

	/**
	 * Apply this visual style to the view.
	 *
	 * @param v
	 *            DOCUMENT ME!
	 */
	void apply(CyNetworkView v);
}
