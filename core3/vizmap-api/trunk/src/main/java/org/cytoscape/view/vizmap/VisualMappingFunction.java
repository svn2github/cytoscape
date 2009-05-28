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

import java.util.List;

import org.cytoscape.model.GraphObject;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.ViewColumn;
import org.cytoscape.view.model.VisualProperty;


/**
 * This class defines how an attribute gets mapped to a visual property.<br />
 * 
 * It takes two values:
 * <ul>
 *  <li>Attribute value: node name(Strings), expression values(Numbers), ...</li>
 *  <li>Visual Property: node size(Numbers), edge color(Color), node shape(NodeShape), ...</li>
 * </ul>
 * 
 * This provides the mapping function from converting the attribute to the visual
 * property.  Essentially, this is a map using <K> as the key and <V> as the value.
 *
 * The direction of mapping is ALWAYS:<br />
 * 
 * K(Attribute) ---> V(Visual Property)
 * 
 * K will be used in implementations.
 * 
 * @param <K> Attribute value object type.  This is the key of this mapping (Can be any objects)
 * @param <V> Visual property value type. (can be any type)
 * 
 */
public interface VisualMappingFunction<K, V> {
	
	/**
	 *  Returns attribute name used in this mapping.
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getMappingAttributeName();
	
	/**
	 * 
	 * @return
	 */
	public Class<K> getMappingAttributeType();

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualProperty<V> getVisualProperty();

	/**
	 *  Since current MappingCalculators map from Attributes to
	 *  VisualProperties, have to restrict View<?> to those
	 *  generic types that have CyAttributes; currently this is
	 *  GraphObject.
	 *
	 * @param <G> Graph object type.
	 * 
	 * @param column DOCUMENT ME!
	 * @param views DOCUMENT ME!
	 */
	<G extends GraphObject> void apply(ViewColumn<V> column, List<? extends View<G>> views);
}
