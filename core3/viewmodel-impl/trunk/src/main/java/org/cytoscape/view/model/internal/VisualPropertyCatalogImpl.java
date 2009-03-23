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
package org.cytoscape.view.model.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.DependentVisualPropertyCallback;
import org.cytoscape.view.model.Renderer;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.VisualPropertyCatalog;

/**
 * The singleton class that holds all currently defined VisualProperties.
 */
public class VisualPropertyCatalogImpl implements VisualPropertyCatalog {

	// Map of visual properties. This object will be updated dynamically by
	// listeners.
	//
	// In order to be able to remove VisualProperties of a Renderer only if they
	// are not used by some other renderer, we need to store, for each VP, which
	// Renderers define the given VP. Thus need to use a VP->(list of Renderers) map.
	private final Map<VisualProperty<?>, List<Renderer>> visualPropertySet;

	/**
	 * Constructor. Just initializes collections for currently available
	 * renderers and VPs
	 */
	public VisualPropertyCatalogImpl() {
		visualPropertySet = new HashMap<VisualProperty<?>, List<Renderer>>();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param serializableName
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public VisualProperty<?> getVisualProperty(final String serializableName) {
		for (VisualProperty<?> vp : visualPropertySet.keySet()) {
			if (vp.getSerializableName().equals(serializableName)) {
				return vp;
			}
		}

		return null; // no matching VisualProperty found
	}

	/**
	 * Returns the collection of all defined VisualProperties. Note that not all
	 * of these will be actually in use. For showing in a UI, use of ... is
	 * recommended ... FIXME
	 * 
	 * @return the Collection of all defined VisualProperties
	 */
	public Collection<VisualProperty<?>> collectionOfVisualProperties() {
		return collectionOfVisualProperties((Collection<View<?>>) null, null);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param objectType
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Collection<VisualProperty<?>> collectionOfVisualProperties(
			final String objectType) {
		return collectionOfVisualProperties((Collection<View<?>>) null,
				objectType);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param networkview
	 *            DOCUMENT ME!
	 * @param objectType
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Collection<VisualProperty<?>> collectionOfVisualProperties(
			final CyNetworkView networkview, final String objectType) {
		if (networkview != null) {
			// FIXME: could filter Views based on objectType, right here
			final Collection<View<?>> views = new HashSet<View<?>>(networkview.getNodeViews());
			views.addAll(networkview.getEdgeViews());

			return collectionOfVisualProperties(views, objectType);
		} else {
			return collectionOfVisualProperties((Collection<View<?>>) null,
					objectType);
		}
	}

	/**
	 * Returns the collection of all those VisualProperties that are in use for
	 * the given GraphObjects. I.e. these are the VisualProperties, for which
	 * setting a value will actually change the displayed graph.
	 * 
	 * Note: returns the same as collectionOfVisualProperties() if both args are
	 * null.
	 * 
	 * @param views
	 *            DOCUMENT ME!
	 * @param objectType
	 *            DOCUMENT ME!
	 * @return VisualProperties
	 */
	public Collection<VisualProperty<?>> collectionOfVisualProperties(
			final Collection<? extends View<?>> views, final String objectType) {

		if (views == null)
			return filterForObjectType(visualPropertySet.keySet(), objectType);

		// System.out.println("making list of VisualProperties in use:");
		final Set<VisualProperty<?>> toRemove = new HashSet<VisualProperty<?>>();

		/* apply DependentVisualPropertyCallbacks */
		for (VisualProperty<?> vp : visualPropertySet.keySet()) {
			final DependentVisualPropertyCallback callback = vp
					.dependentVisualPropertyCallback();

			if (callback != null) {
				toRemove.addAll(callback.changed(views, visualPropertySet.keySet()));
			}
		}

		// System.out.println("removing:"+toRemove.size());
		final Set<VisualProperty<?>> result = new HashSet<VisualProperty<?>>(
				visualPropertySet.keySet());
		result.removeAll(toRemove);

		// System.out.println("len of result:"+result.size());
		return filterForObjectType(result, objectType);
	}

	/* return collection of only those that have a matching objectType */
	private Collection<VisualProperty<?>> filterForObjectType(
			final Collection<? extends VisualProperty<?>> vps,
			final String objectType) {
		final ArrayList<VisualProperty<?>> result = new ArrayList<VisualProperty<?>>();

		for (VisualProperty<?> vp : vps) {
			if (vp.getObjectType() == objectType) {
				result.add(vp);
			}
		}

		return result;
	}


	@SuppressWarnings("unchecked")
	public void addRenderer(Renderer renderer, Map props) {
		for (VisualProperty<?>vp: renderer.getVisualProperties()){
			if (this.visualPropertySet.containsKey(vp)){
				List<Renderer> renderers = this.visualPropertySet.get(vp);
				renderers.add(renderer);	
			} else {
				List<Renderer> renderers = new ArrayList<Renderer>();
				renderers.add(renderer);
				this.visualPropertySet.put(vp, renderers);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void removeRenderer(Renderer renderer, Map props) {
		for (VisualProperty<?>vp: renderer.getVisualProperties()){
			List<Renderer> renderers = this.visualPropertySet.get(vp);
			if (renderers.size() == 1){
				// this is the last renderer that defined this VP, remove the VP
				this.visualPropertySet.remove(vp);
			} else {
				// others also defined this VP, only remove renderer from the list:
				renderers.remove(renderer);
			}
		}
	}
}
