package org.cytoscape.view;

import java.util.HashMap;

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
}
