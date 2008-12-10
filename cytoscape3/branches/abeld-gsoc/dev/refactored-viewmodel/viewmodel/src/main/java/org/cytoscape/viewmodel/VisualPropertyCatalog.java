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

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

import java.util.Collection;

public interface VisualPropertyCatalog {

    public void addVisualPropertiesOfRenderer(Renderer renderer);

    public VisualProperty getVisualProperty(String name);

    /**
     * Returns the collection of all defined VisualProperties. Note that not all
     * of these will be actually in use. For showing in a UI, use of ... is
     * recommended ... FIXME
     * 
     * @return the Collection of all defined VisualProperties
     */
    public Collection<VisualProperty> collectionOfVisualProperties();

    public Collection<VisualProperty> collectionOfVisualProperties(VisualProperty.GraphObjectType objectType);

    public Collection<VisualProperty> collectionOfVisualProperties(CyNetworkView networkview,
							 VisualProperty.GraphObjectType objectType);

    /**
     * Returns the collection of all those VisualProperties that are in use for
     * the given GraphObjects. I.e. these are the VisualProperties, for which
     * setting a value will actually change the displayed graph.
     * 
     * Note: returns the same as collectionOfVisualProperties() if
     * both args are null.  Note: using
     * VisualProperty.GraphObjectType.NETWORK for objectType is not
     * really usefull. For network VPs, use
     * collectionOfVisualProperties(VisualProperty.GraphObjectType
     * objectType) instead.
     */
    public Collection<VisualProperty> collectionOfVisualProperties(Collection<View<CyNode>> nodeviews,
								   Collection<View<CyEdge>> edgeviews,
								   VisualProperty.GraphObjectType objectType);

}