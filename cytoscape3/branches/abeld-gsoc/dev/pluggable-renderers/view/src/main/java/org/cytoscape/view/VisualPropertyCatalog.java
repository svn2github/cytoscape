package org.cytoscape.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.cytoscape.view.renderers.Renderer;

/**
 * The singleton class that holds all currently defined VisualProperties.
 */
public abstract class VisualPropertyCatalog {

	/* Mapping from UID to VisualProperty */
	private static HashMap <String, VisualProperty> visualProperties = new HashMap<String, VisualProperty>();
	
	private static HashMap <String, DependentVisualPropertyCallback>callbacks = new HashMap<String, DependentVisualPropertyCallback>(); 
	
	public static void addVisualPropertiesOfRenderer(Renderer renderer){
		for(VisualProperty vp: renderer.supportedVisualAttributes()){
			addVisualProperty(vp);
		}
	}

	/** Add a top-level VisualProperty. Note: this is most likely _not_ what you want to use */
	public static void addVisualProperty(VisualProperty vp){
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

	public static VisualProperty getVisualProperty(String name){
		return visualProperties.get(name);
	}
	
	/**
	 * Returns the collection of all defined VisualProperties. Note that not all
	 * of these will be actually in use. For showing in a UI, use of ... is
	 * recommended ... FIXME
	 * 
	 * @return the Collection of all defined VisualProperties
	 */
	public static Collection<VisualProperty> collectionOfVisualProperties(){
		return collectionOfVisualProperties(null, null);
	}
	public static Collection<VisualProperty> collectionOfVisualProperties(GraphView networkview){
		List<NodeView> nodeviews = new ArrayList<NodeView>();
		for (Iterator it = networkview.getNodeViewsIterator(); it.hasNext();){
			NodeView nv = (NodeView)it.next();
			nodeviews.add(nv);
		}
		return collectionOfVisualProperties(nodeviews, networkview.getEdgeViewsList());
	}
	/**
	 * Returns the collection of all those VisualProperties that are in use for
	 * the given GraphObjects. I.e. these are the VisualProperties, for which
	 * setting a value will actually change the displayed graph.
	 * 
	 * Note: returns the same as collectionOfVisualProperties() if both args are null.
	 */
	public static Collection<VisualProperty> collectionOfVisualProperties(Collection<NodeView> nodeviews, Collection<EdgeView> edgeviews){
		System.out.println("making list of VisualProperties in use:");
		Collection<VisualProperty> allVisualProperties = visualProperties.values();
		if (nodeviews == null && edgeviews == null)
			return allVisualProperties;

		Set <VisualProperty> toRemove = new HashSet<VisualProperty>();
		for (DependentVisualPropertyCallback callback: callbacks.values()){
			toRemove.addAll(callback.changed(nodeviews, edgeviews, allVisualProperties));
		}
		System.out.println("removing:"+toRemove.size());
		Set <VisualProperty> result = new HashSet<VisualProperty>(allVisualProperties);
		result.removeAll(toRemove);
		System.out.println("len of result:"+result.size());
		return result;
	}

	/**
	 * Returns the collection of all defined edge VisualProperties.
	 */
	public static List<VisualProperty> getEdgeVisualPropertyList(){
		return getEdgeVisualPropertyList(null, null);
	}
	public static Collection<VisualProperty> getEdgeVisualPropertyList(GraphView networkview){
		List<NodeView> nodeviews = new ArrayList<NodeView>();
		for (Iterator it = networkview.getNodeViewsIterator(); it.hasNext();){
			NodeView nv = (NodeView)it.next();
			nodeviews.add(nv);
		}
		return getEdgeVisualPropertyList(nodeviews, networkview.getEdgeViewsList());
	}
	public static Collection<VisualProperty> getEdgeVisualPropertyList(EdgeView edgeview){
		List<EdgeView> edgeviews = new ArrayList<EdgeView>();
		edgeviews.add(edgeview);

		return getEdgeVisualPropertyList(null, edgeviews);
	}
	/**
	 * Returns the collection of all edge VisualProperties that are in use for
	 * the given GraphObjects.
	 * 
	 * Note: returns all defined edge VisualProperties if both args are null.
	 */
	public static List<VisualProperty> getEdgeVisualPropertyList(Collection<NodeView> nodeviews, Collection<EdgeView> edgeviews){
		ArrayList<VisualProperty> result = new ArrayList<VisualProperty>();
		for (VisualProperty vp: collectionOfVisualProperties(nodeviews, edgeviews)){
			if (vp.isNodeProp()){
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
	public static List<VisualProperty> getNodeVisualPropertyList(){
		return getNodeVisualPropertyList(null, null);
	}
	public static Collection<VisualProperty> getNodeVisualPropertyList(GraphView networkview){
		List<NodeView> nodeviews = new ArrayList<NodeView>();
		for (Iterator it = networkview.getNodeViewsIterator(); it.hasNext();){
			NodeView nv = (NodeView)it.next();
			nodeviews.add(nv);
		}
		return getNodeVisualPropertyList(nodeviews, networkview.getEdgeViewsList());
	}
	public static Collection<VisualProperty> getNodeVisualPropertyList(NodeView nv){
		List<NodeView> nodeviews = new ArrayList<NodeView>();
		nodeviews.add(nv);
		return getNodeVisualPropertyList(nodeviews, null);
	}
	/**
	 * Returns the collection of all node VisualProperties that are in use for
	 * the given GraphObjects.
	 * 
	 * Note: returns all defined node VisualProperties if both args are null.
	 */
	public static List<VisualProperty> getNodeVisualPropertyList(Collection<NodeView> nodeviews, Collection<EdgeView> edgeviews){
		ArrayList<VisualProperty> result = new ArrayList<VisualProperty>();
		for (VisualProperty vp: collectionOfVisualProperties(nodeviews, edgeviews)){
			if (vp.isNodeProp()){
				result.add(vp);
			}
		}
		return result;
	}

}
