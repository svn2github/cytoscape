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

import org.cytoscape.viewmodel.VisualPropertyCatalog;
import org.cytoscape.viewmodel.VisualProperty;
import org.cytoscape.viewmodel.Renderer;
import org.cytoscape.viewmodel.CyNetworkView;
import org.cytoscape.viewmodel.View;
import org.cytoscape.viewmodel.DependentVisualPropertyCallback;


import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

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

    public void setBundleContext(BundleContext bundleContext) {
	this.bundleContext = bundleContext;
    }
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


	public void addVisualPropertiesOfRenderer(Renderer renderer){
	    throw new RuntimeException("not applicable");
 	}

	/** Add a top-level VisualProperty. Note: this is most likely _not_ what you want to use */
	public void addVisualProperty(VisualProperty vp){
	    throw new RuntimeException("not applicable");
	}

	public VisualProperty getVisualProperty(String name){
	    for (VisualProperty vp: readAllVisualPropertiesFromOSGI()){
		if (vp.getID().equals(name)){
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
	public Collection<VisualProperty> collectionOfVisualProperties(){
		return collectionOfVisualProperties(null, null);
	}

    public Collection<VisualProperty> collectionOfVisualProperties(VisualProperty.GraphObjectType objectType){
	return collectionOfVisualProperties(null, null, objectType);
	}

    public Collection<VisualProperty> collectionOfVisualProperties(CyNetworkView networkview,
								   VisualProperty.GraphObjectType objectType){
		if (networkview != null){
			return collectionOfVisualProperties(networkview.getCyNodeViews(),
							    networkview.getCyEdgeViews(),
							    objectType);
		} else {
		    return collectionOfVisualProperties(null, null, objectType);
		}
	}
	/**
	 * Returns the collection of all those VisualProperties that are in use for
	 * the given GraphObjects. I.e. these are the VisualProperties, for which
	 * setting a value will actually change the displayed graph.
	 * 
	 * Note: returns the same as collectionOfVisualProperties() if both args are null.
	 */
    public Collection<VisualProperty> collectionOfVisualProperties(Collection<View<CyNode>> nodeviews, Collection<View<CyEdge>> edgeviews, VisualProperty.GraphObjectType objectType){

		Collection<VisualProperty> allVisualProperties = readAllVisualPropertiesFromOSGI();
		if (nodeviews == null && edgeviews == null)
		    return filterForObjectType(allVisualProperties, objectType);
		//System.out.println("making list of VisualProperties in use:");
		Set <VisualProperty> toRemove = new HashSet<VisualProperty>();
		/* temprarily callback-usage is removed (all callbacks ignored) FIXME FIXME
		for (DependentVisualPropertyCallback callback: callbacks.values()){
			toRemove.addAll(callback.changed(nodeviews, edgeviews, allVisualProperties));
			} */
		//System.out.println("removing:"+toRemove.size());
		Set <VisualProperty> result = new HashSet<VisualProperty>(allVisualProperties);
		result.removeAll(toRemove);
		//System.out.println("len of result:"+result.size());
		return filterForObjectType(result, objectType);
	}

    /* return collection of only those that have a matching objectType */
    private Collection<VisualProperty> filterForObjectType(Collection<VisualProperty> vps,
							   VisualProperty.GraphObjectType objectType){
	ArrayList<VisualProperty> result = new ArrayList<VisualProperty>();
	for (VisualProperty vp: vps){
	    if (vp.getObjectType() == objectType){
		result.add(vp);
	    }
	}
	return result;
    }
    private Collection<VisualProperty> readAllVisualPropertiesFromOSGI(){
	Set<VisualProperty> ret = new HashSet<VisualProperty>();
	for (Renderer renderer: getAllRenderersFromOSGI()){
	    ret.addAll(renderer.getVisualProperties());
	}
	return ret;
    }
    private Collection<Renderer> getAllRenderersFromOSGI(){
	Set<Renderer> ret = new HashSet<Renderer>();
	System.out.println("listing values");
	if (bundleContext == null)
	    return ret;
	System.out.println("listing values2");
	try {
	    ServiceReference[] sr = bundleContext.getServiceReferences(Renderer.class.getName(),
								       null);
	    
	    if (sr != null){
		System.out.println("len servicereferences:"+sr.length);
		for (ServiceReference r : sr) {
		    System.out.println("listing values3");
		    Renderer renderer = (Renderer) bundleContext.getService(r);
		    
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
