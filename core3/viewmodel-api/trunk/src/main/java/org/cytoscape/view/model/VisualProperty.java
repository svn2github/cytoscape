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


/**
 * FIXME
 * Think of it as a column in the view.model table.
 *
 * Uses String constants as ObjectTypes, ie. to seperate NodeVisualProperties from EdgeVisualProperties, etc.
 * Ideally, we could use Class<? extends View<?>> or something like that, but unfortunately that is impossible due to type erasure.
 *
 * @param <T> the dataType of the VisualProperty, ie. what kind of objects are the values
 */
public interface VisualProperty<T> {

	/**
	 * Returns what type of objects this VisualProperty stores values for.
	 * canonical values are VisualProperty.NODE, etc.
	 *
	 * @return the string describing the object type
	 */
	public String getObjectType();

	
	/**
	 * The type of object represented by this property.
	 *
	 * @return  Type of object stored in this VP.
	 */
	public Class<T> getType();
	

	/**
	 * The default value of this property.
	 *
	 * @return  DOCUMENT ME!
	 */
	public T getDefault();

	
	/**
	 * A short string used to identify this visual property and suitable for
	 * serializing to XML and other text formats.
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getIdString();

	
	/**
	 * A short string suitable for presentation to humans.  Should not be used
	 * for serialization.
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDisplayName();

	

	/**
	 * Returns a string of the specified value suitable for serializing to XML
	 * other text output.
	 */
	public String toSerializableString(final T value);

	
	/**
	 * Returns an object of type T given a string serialized from the getSerializableString(T value)
	 * method.
	 */
	public T parseSerializableString(final String value);
	
	
	/**
	 * In some cases, 
	 * 
	 * @return
	 */
	public boolean isIgnoreDefault();
	
		
}
