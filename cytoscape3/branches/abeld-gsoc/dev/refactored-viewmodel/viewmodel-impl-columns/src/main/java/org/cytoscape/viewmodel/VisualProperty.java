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
package org.cytoscape.viewmodel;


/**
 * FIXME
 * Think of it as a column in the viewmodel table.
 *
 * Uses String constants as ObjectTypes, ie. to seperate NodeVisualProperties from EdgeVisualProperties, etc.
 * Ideally, we could use Class<? extends View<?>> or something like that, but unfortunately that is impossible due to type erasure.
 *
 * @param <T> the dataType of the VisualProperty, ie. what kind of objects are the values
 */
public interface VisualProperty<T> {
	/**
	 * Canonical ObjectType string for CyNode.
	 */
	String NODE = "NODE";
	/**
	 * Canonical ObjectType string for CyEdge.
	 */
	String EDGE = "EDGE";
	/**
	 * Canonical ObjectType string for CyNetwork.
	 */
	String NETWORK = "NETWORK";

	/**
	 * Returns what type of objects this VisualProperty stores values for.
	 * canonical values are VisualProperty.NODE, etc.
	 *
	 * @return the string describing the object type
	 */
	String getObjectType();

	/**
	 * The type of object represented by this property.
	 *
	 * @return  DOCUMENT ME!
	 */
	Class<T> getType();

	/**
	 * The default value of this property.
	 *
	 * @return  DOCUMENT ME!
	 */
	T getDefault();

	/**
	 * Used for hashes identifying this property.
	 *
	 * @return  DOCUMENT ME!
	 */
	String getID();

	/**
	 * For presentation to humans.
	 *
	 * @return  DOCUMENT ME!
	 */
	String getName();

	/**
	 *
	 *
	 * @return callback, or null if there isn't one
	 */
	DependentVisualPropertyCallback dependentVisualPropertyCallback();
}
