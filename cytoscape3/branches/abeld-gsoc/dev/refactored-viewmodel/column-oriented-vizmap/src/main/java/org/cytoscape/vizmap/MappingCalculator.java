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

import java.util.List;

import org.cytoscape.model.GraphObject;

import org.cytoscape.viewmodel.View;
import org.cytoscape.viewmodel.ViewColumn;
import org.cytoscape.viewmodel.VisualProperty;


/**
 * This class defines how an attribute gets mapped to a visual property.
 * It takes two values: an attribute and a visual property and provides
 * the mapping function from converting the attribute to the visual
 * property.
 *
 * Or should the mapping calculator map from Attr to Class<?>?
 * @param <T> DOCUMENT ME!
 */
public interface MappingCalculator {
	/**
	 * The attribute to be mapped.
	 *
	 * @param name  DOCUMENT ME!
	 */
	void setMappingAttributeName(String name);

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	String getMappingAttributeName();

	/**
	 * The visual property the attribute gets mapped to.
	 *
	 * @param vp  DOCUMENT ME!
	 */
	void setVisualProperty(VisualProperty<?> vp);

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	VisualProperty<?> getVisualProperty();

	/**
	 *  Since current MappingCalculators map from Attributes to
	 *  VisualProperties, have to restrict View<?> to those
	 *  generic types that have CyAttributes; currently this is
	 *  GraphObject.
	 *
	 * @param <V> DOCUMENT ME!
	 * @param column DOCUMENT ME!
	 * @param views DOCUMENT ME!
	 */
	<T, V extends GraphObject> void apply(ViewColumn<T> column, List<? extends View<V>> views);
}
