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
package org.cytoscape.viewmodel.internal;

import org.cytoscape.viewmodel.CyNetworkView;
import org.cytoscape.viewmodel.DependentVisualPropertyCallback;
import org.cytoscape.viewmodel.Renderer;
import org.cytoscape.viewmodel.View;
import org.cytoscape.viewmodel.VisualProperty;
import org.cytoscape.viewmodel.VisualPropertyCatalog;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * The singleton class that holds all currently defined VisualProperties.
 */
public class VisualPropertyCatalogImpl implements VisualPropertyCatalog {
	private BundleContext bundleContext;

	/**
	 * For setter injection (hmm. whats that?)
	 */
	public VisualPropertyCatalogImpl() {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param bundleContext DOCUMENT ME!
	 */
	public void setBundleContext(final BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public BundleContext getBundleContext() {
		return bundleContext;
	}

	/**
	 * Creates a new CyNetworkFactoryImpl object.
	 *
	 * @param h  DOCUMENT ME!
	 */
	public VisualPropertyCatalogImpl(final BundleContext bundleContext) {
		if (bundleContext == null)
			throw new NullPointerException("bundleContext is null");

		this.bundleContext = bundleContext;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param renderer DOCUMENT ME!
	 */
	public void addVisualPropertiesOfRenderer(final Renderer renderer) {
		throw new RuntimeException("not applicable");
	}

	/** Add a top-level VisualProperty. Note: this is most likely _not_ what you want to use.
	 * @param vp the VisualProperty to add
	 */
	public void addVisualProperty(final VisualProperty<?> vp) {
		throw new RuntimeException("not applicable");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param name DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualProperty<?> getVisualProperty(final String name) {
		for (VisualProperty<?> vp : readAllVisualPropertiesFromOSGI()) {
			if (vp.getID().equals(name)) {
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
	 *  DOCUMENT ME!
	 *
	 * @param objectType DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Collection<VisualProperty<?>> collectionOfVisualProperties(final String objectType) {
		return collectionOfVisualProperties((Collection<View<?>>) null, objectType);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param networkview DOCUMENT ME!
	 * @param objectType DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Collection<VisualProperty<?>> collectionOfVisualProperties(final CyNetworkView networkview,
	                                                                  final String objectType) {
		if (networkview != null) {
			// FIXME: could filter Views based on objectType, right here
			final Collection<View<?>> views = new HashSet<View<?>>(networkview.getCyNodeViews());
			views.addAll(networkview.getCyEdgeViews());

			return collectionOfVisualProperties(views, objectType);
		} else {
			return collectionOfVisualProperties((Collection<View<?>>) null, objectType);
		}
	}

	/**
	 * Returns the collection of all those VisualProperties that are in use for
	 * the given GraphObjects. I.e. these are the VisualProperties, for which
	 * setting a value will actually change the displayed graph.
	 *
	 * Note: returns the same as collectionOfVisualProperties() if both args are null.
	 * @param views DOCUMENT ME!
	 * @param objectType DOCUMENT ME!
	 * @return VisualProperties
	 */
	public Collection<VisualProperty<?>> collectionOfVisualProperties(final Collection<? extends View<?>> views,
	                                                                  final String objectType) {
		final Collection<VisualProperty<?>> allVisualProperties = readAllVisualPropertiesFromOSGI();

		if (views == null)
			return filterForObjectType(allVisualProperties, objectType);

		//System.out.println("making list of VisualProperties in use:");
		final Set<VisualProperty<?>> toRemove = new HashSet<VisualProperty<?>>();

		/* apply DependentVisualPropertyCallbacks */
		for (VisualProperty<?> vp : allVisualProperties) {
			final DependentVisualPropertyCallback callback = vp.dependentVisualPropertyCallback();

			if (callback != null) {
				toRemove.addAll(callback.changed(views, allVisualProperties));
			}
		}

		//System.out.println("removing:"+toRemove.size());
		final Set<VisualProperty<?>> result = new HashSet<VisualProperty<?>>(allVisualProperties);
		result.removeAll(toRemove);

		//System.out.println("len of result:"+result.size());
		return filterForObjectType(result, objectType);
	}

	/* return collection of only those that have a matching objectType */
	private Collection<VisualProperty<?>> filterForObjectType(final Collection<? extends VisualProperty<?>> vps,
	                                                          final String objectType) {
		final ArrayList<VisualProperty<?>> result = new ArrayList<VisualProperty<?>>();

		for (VisualProperty<?> vp : vps) {
			if (vp.getObjectType() == objectType) {
				result.add(vp);
			}
		}

		return result;
	}

	private Collection<VisualProperty<?>> readAllVisualPropertiesFromOSGI() {
		final Set<VisualProperty<?>> ret = new HashSet<VisualProperty<?>>();

		for (Renderer renderer : getAllRenderersFromOSGI()) {
			ret.addAll(renderer.getVisualProperties());
		}

		return ret;
	}

	private Collection<Renderer> getAllRenderersFromOSGI() {
		final Set<Renderer> ret = new HashSet<Renderer>();

		if (bundleContext == null)
			return ret;

		try {
			final ServiceReference[] sr = bundleContext.getServiceReferences(Renderer.class.getName(),
			                                                                 null);

			if (sr != null) {
				for (ServiceReference r : sr) {
					final Renderer renderer = (Renderer) bundleContext.getService(r);

					if (renderer != null)
						ret.add(renderer);
				}
			} else {
				System.out.println("sr is null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}
}
