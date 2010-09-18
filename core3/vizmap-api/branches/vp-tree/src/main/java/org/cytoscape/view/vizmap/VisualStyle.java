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
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;


/**
 * A VisualStyle is a collection of VisualMappingFunctions and default values
 * that defines how a set of attributes modify visual properties of a View object.
 *
 */
public interface VisualStyle {
	
	/**
	 * Returns name of this visual style. This should NOT be used as the ID of this
	 * Visual Style. Just for GUI components and may not be unique.
	 * 
	 * Title of Visual Style is a mutable field.
	 *
	 * @return title of this visual style
	 */
	String getTitle();

	
	/**
	 * Set new title of this VS.
	 * Will be used by rename function.
	 *
	 * @param title New title.
	 */
	void setTitle(final String title);
	

	/**
	 * Add a new mapping for this Visual Style.
	 *
	 * @param mapping new mapping.
	 */
	void addVisualMappingFunction(final VisualMappingFunction<?, ?> mapping);
	
	
	/**
	 *  Remove a mapping for Visual Property.
	 *  One visual property can be associated with only one mapping function, 
	 *  so this always removes correct mapping.
	 *
	 * @param vp mapping associated with this vp will be removed.
	 *
	 */
	void removeVisualMappingFunction(final VisualProperty<?> vp);

	
	/**
	 *  get current mapping for the Visual Property vp.
	 *
	 * @param <V> Type of visual property.
	 * @param vp visual property associated with the target mapping.
	 *
	 * @return mapping function for vp
	 * 
	 */
	<V> VisualMappingFunction<?, V> getVisualMappingFunction(final VisualProperty<V> vp);

	
	/**
	 *  Returns all available mappings.
	 *
	 * @return  All visual mappings for this style.
	 */
	Collection<VisualMappingFunction<?, ?>> getAllVisualMappingFunctions();

	
	/**
	 *  Returns default value for the Visual Property vp.
	 *  This is style's default value.  Not same as VP's default.
	 *
	 * @param <V> Type of object associated with vp
	 * @param vp target visual property
	 *
	 * @return  Style's default value for vp
	 */
	<V> V getDefaultValue(final VisualProperty<V> vp);

	
	/**
	 *  Setter for the default value of vp.
	 *
	 * @param <V> Type of object associated with vp
	 * @param vp target visual property
	 * @param value Value to be set as default.  This can be child type of V.  For example, 
	 * 				if V is Number, S can be Double, Integer, etc.
	 */
	<V, S extends V> void setDefaultValue(final VisualProperty<V> vp, S value);

	
	/**
	 * Apply this visual style to the view.
	 * Currently this is only for network view.
	 *
	 * @param v Visual Style will be applied to this network view.
	 */
	void apply(final CyNetworkView networkViewModel);
	
	
	/**
	 * A Visual Style is always associated with a lexicon tree provided 
	 * by a rendering engine.  This method returns its associated lexicon.
	 * 
	 * @return VisualLexicon provided by a rendering engine.
	 */
	VisualLexicon getVisualLexicon();
}
