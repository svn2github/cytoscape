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



/**
 * The singleton class that holds all currently defined VisualProperties.
 */
public class VisualPropertyCatalogImpl implements VisualPropertyCatalog {

	/* Mapping from UID to VisualProperty */
	private HashMap <String, VisualProperty> visualProperties = new HashMap<String, VisualProperty>();
	
	private HashMap <String, DependentVisualPropertyCallback>callbacks = new HashMap<String, DependentVisualPropertyCallback>(); 
	
	public void addVisualPropertiesOfRenderer(Renderer renderer){
		for(VisualProperty vp: renderer.getVisualProperties()){
			addVisualProperty(vp);
		}
	}

	/** Add a top-level VisualProperty. Note: this is most likely _not_ what you want to use */
	public void addVisualProperty(VisualProperty vp){
		String name = vp.getName();
		if (visualProperties.containsKey(name)){
			System.out.println("Error: VisualProperty already exsists!");
		} else {
			DependentVisualPropertyCallback callback = vp.dependentVisualPropertyCallback();
			if (callback != null)
				callbacks.put(vp.getName(), callback);

			visualProperties.put(name, vp);
		}
	}

	public VisualProperty getVisualProperty(String name){
		return visualProperties.get(name);
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
	public Collection<VisualProperty> collectionOfVisualProperties(CyNetworkView networkview){
		if (networkview != null){
			return collectionOfVisualProperties(networkview.getCyNodeViews(),
							    networkview.getCyEdgeViews());
		} else {
			return collectionOfVisualProperties(null, null);
		}
	}
	/**
	 * Returns the collection of all those VisualProperties that are in use for
	 * the given GraphObjects. I.e. these are the VisualProperties, for which
	 * setting a value will actually change the displayed graph.
	 * 
	 * Note: returns the same as collectionOfVisualProperties() if both args are null.
	 */
	public Collection<VisualProperty> collectionOfVisualProperties(Collection<View<CyNode>> nodeviews, Collection<View<CyEdge>> edgeviews){

		Collection<VisualProperty> allVisualProperties = visualProperties.values();
		if (nodeviews == null && edgeviews == null)
			return allVisualProperties;
		//System.out.println("making list of VisualProperties in use:");
		Set <VisualProperty> toRemove = new HashSet<VisualProperty>();
		for (DependentVisualPropertyCallback callback: callbacks.values()){
			toRemove.addAll(callback.changed(nodeviews, edgeviews, allVisualProperties));
		}
		//System.out.println("removing:"+toRemove.size());
		Set <VisualProperty> result = new HashSet<VisualProperty>(allVisualProperties);
		result.removeAll(toRemove);
		//System.out.println("len of result:"+result.size());
		return result;
	}

	/**
	 * Returns the collection of all defined edge VisualProperties.
	 */
	public Collection<VisualProperty> getEdgeVisualProperties(){
		return getEdgeVisualProperties(null, null);
	}
	public Collection<VisualProperty> getEdgeVisualProperties(CyNetworkView networkview){
		return getEdgeVisualProperties(networkview.getCyNodeViews(),
						 networkview.getCyEdgeViews());
	}
	public Collection<VisualProperty> getEdgeVisualProperties(View<CyEdge> edgeview){
		Collection<View<CyEdge>> edgeviews = new ArrayList<View<CyEdge>>();
		edgeviews.add(edgeview);

		return getEdgeVisualProperties(null, edgeviews);
	}
	/**
	 * Returns the collection of all edge VisualProperties that are in use for
	 * the given GraphObjects.
	 * 
	 * Note: returns all defined edge VisualProperties if both args are null.
	 */
	public Collection<VisualProperty> getEdgeVisualProperties(Collection<View<CyNode>> nodeviews, Collection<View<CyEdge>> edgeviews){
		ArrayList<VisualProperty> result = new ArrayList<VisualProperty>();
		for (VisualProperty vp: collectionOfVisualProperties(nodeviews, edgeviews)){
			if (vp.getObjectType() == VisualProperty.GraphObjectType.EDGE){
				continue;
			} else {
				result.add(vp);
			}
		}
		return result;
	}

	/**
	 * Returns the collection of all defined node VisualProperties.
	 */
	public Collection<VisualProperty> getNodeVisualProperties(){
		return getNodeVisualProperties(null, null);
	}
	public Collection<VisualProperty> getNodeVisualProperties(CyNetworkView networkview){
		return getNodeVisualProperties(networkview.getCyNodeViews(), networkview.getCyEdgeViews());
	}
	public Collection<VisualProperty> getNodeVisualProperties(View<CyNode> nv){
		Collection<View<CyNode>> nodeviews = new ArrayList<View<CyNode>>();
		nodeviews.add(nv);
		return getNodeVisualProperties(nodeviews, null);
	}
	/**
	 * Returns the collection of all node VisualProperties that are in use for
	 * the given GraphObjects.
	 * 
	 * Note: returns all defined node VisualProperties if both args are null.
	 */
	public Collection<VisualProperty> getNodeVisualProperties(Collection<View<CyNode>> nodeviews, Collection<View<CyEdge>> edgeviews){
		ArrayList<VisualProperty> result = new ArrayList<VisualProperty>();
		for (VisualProperty vp: collectionOfVisualProperties(nodeviews, edgeviews)){
			if (vp.getObjectType() == VisualProperty.GraphObjectType.NODE){
				result.add(vp);
			}
		}
		return result;
	}

    /**
     * Returns the collection of all defined node VisualProperties.
     */
    public Collection<VisualProperty> getNetworkVisualProperties(){
	ArrayList<VisualProperty> result = new ArrayList<VisualProperty>();
	for (VisualProperty vp: visualProperties.values()){
	    if (vp.getObjectType() == VisualProperty.GraphObjectType.NETWORK){
		result.add(vp);
	    }
	}
	return result;
    }
}