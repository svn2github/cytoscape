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
  */
public interface VisualPropertyCatalog {
	/**
	 * register the VisualProperties of the given Renderer
	 *
	 * @param renderer DOCUMENT ME!
	 */
	void addVisualPropertiesOfRenderer(Renderer renderer);

	/**
	 * unregister the VisualProperties of the given Renderer.
	 * Note that these VisualProperties might still be available, if some other Renderer defines them
	 *
	 * @param renderer DOCUMENT ME!
	 */
	void removeVisualPropertiesOfRenderer(Renderer renderer);
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @param name DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	VisualProperty<?> getVisualProperty(String name);

	/**
	 * Returns the collection of all defined VisualProperties. Note that not all
	 * of these will be actually in use. For showing in a UI, use of ... is
	 * recommended ... FIXME
	 *
	 * @return the Collection of all defined VisualProperties
	 */
	Collection<VisualProperty<?>> collectionOfVisualProperties();

	/**
	 *  DOCUMENT ME!
	 *
	 * @param objectType DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	Collection<VisualProperty<?>> collectionOfVisualProperties(String objectType);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param networkview DOCUMENT ME!
	 * @param objectType DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	Collection<VisualProperty<?>> collectionOfVisualProperties(CyNetworkView networkview,
	                                                           String objectType);

	/**
	 * Returns the collection of all those VisualProperties that are in use for
	 * the given GraphObjects. I.e. these are the VisualProperties, for which
	 * setting a value will actually change the displayed graph.
	 *
	 * Note: returns the same as collectionOfVisualProperties() if
	 * arg is null.
	 *
	 * Note: using VisualProperty.GraphObjectType.NETWORK for objectType is not
	 * really useful. For network VPs, use collectionOfVisualProperties(VisualProperty.GraphObjectType
	 * objectType) instead.
	 *
	 * @param views for which the filtering is to be done
	 * @param objectType for which to filter
	 * @return VisualProperties, filtered with the DependentVisualProperty callbacks
	 */
	Collection<VisualProperty<?>> collectionOfVisualProperties(Collection<? extends View<?>> views,
	                                                           String objectType);
}
