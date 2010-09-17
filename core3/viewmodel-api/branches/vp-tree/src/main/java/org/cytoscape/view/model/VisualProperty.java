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

import java.util.Collection;


/**
 *
 * Uses String constants as ObjectTypes, ie. to seperate NodeVisualProperties from EdgeVisualProperties, etc.
 * Ideally, we could use Class<? extends View<?>> or something like that, but unfortunately that is impossible due to type erasure.
 *
 * @param <T> the dataType of the VisualProperty, ie. what kind of objects are the values
 */
public interface VisualProperty<T> {
	
	/**
	 * The type of object represented by this property.
	 *
	 * @return  Type of object stored in this VP.
	 */
	Class<T> getType();
	

	/**
	 * The default value of this property.  This value is immutable.
	 *
	 * @return  DOCUMENT ME!
	 */
	T getDefault();

	
	/**
	 * A short string used to identify this visual property and suitable for
	 * serializing to XML and other text formats.
	 *
	 * @return  DOCUMENT ME!
	 */
	String getIdString();

	
	/**
	 * A short string suitable for presentation to humans.  Should not be used
	 * for serialization.
	 *
	 * @return  DOCUMENT ME!
	 */
	String getDisplayName();

	

	/**
	 * Returns a string of the specified value suitable for serializing to XML
	 * other text output.
	 */
	String toSerializableString(final T value);

	
	/**
	 * Returns an object of type T given a string serialized from the getSerializableString(T value)
	 * method.
	 */
	T parseSerializableString(final String value);
	
	
	/**
	 * In some cases, default value from visual style is not suitable, such as x, y, z location of nodes.
	 * If this flag is on, it will be ignored and it will be controlled by mapping only.
	 * 
	 * @return
	 */
	boolean isIgnoreDefault();
	
	
	// New feature: Tree-like structure for visual properties.
	
	/**
	 * Get the parent of this VP node.
	 * The relationship is immutable, i.e., cannot change parent/child relationship.
	 */
	VisualProperty<?> getParent();
	
	/**
	 * 
	 * @return
	 */
	Collection<VisualProperty<?>> getChildren();
}
