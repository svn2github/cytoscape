package org.cytoscape.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;

/**
 * The singleton class that holds all currently defined VisualProperties.
 */
public abstract class VisualPropertyCatalog {
	private static HashMap <String, VisualProperty> visualProperties = new HashMap<String, VisualProperty>();
	
	public static void addVisualProperty(VisualProperty vp){
		String name = vp.getName();
		if (visualProperties.containsKey(name)){
			// check that they are the same -- but how?
			// FIXME FIXME FIXME
		} else {
			visualProperties.put(name, vp);
		}
	}
	
	public static VisualProperty getVisualProperty(String name){
		return visualProperties.get(name);
	}
	
	public static Collection<VisualProperty> collectionOfVisualProperties(){
		return visualProperties.values();
	}
	public static List<VisualProperty> getEdgeVisualPropertyList(){
		ArrayList<VisualProperty> result = new ArrayList<VisualProperty>();
		for (VisualProperty vp: collectionOfVisualProperties()){
			if (vp.isNodeProp()){
				continue;
			} else {
				result.add(vp);
			}
		}
		return result;
	}

	public static List<VisualProperty> getNodeVisualPropertyList(){
		ArrayList<VisualProperty> result = new ArrayList<VisualProperty>();
		for (VisualProperty vp: collectionOfVisualProperties()){
			if (vp.isNodeProp()){
				result.add(vp);
			}
		}
		return result;
	}

}
