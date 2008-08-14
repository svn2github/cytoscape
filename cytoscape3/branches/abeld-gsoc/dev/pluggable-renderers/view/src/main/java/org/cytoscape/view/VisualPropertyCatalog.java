package org.cytoscape.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cytoscape.view.renderers.Renderer;

/**
 * The singleton class that holds all currently defined VisualProperties.
 */
public abstract class VisualPropertyCatalog {

	/* Mapping from UID to VisualProperty */
	private static HashMap <String, VisualProperty> visualProperties = new HashMap<String, VisualProperty>();
	
	/*
	 * When adding/removing Renderers, we will have to add/remove the
	 * VisualProperties they define. Because multiple Renderers can define the
	 * same VisualProperty, we will have to be carefull to only remove a
	 * VisualProperty if no Renderer in use defines it. To keep track of this,
	 * the following map will store a mapping from the UID of the Visual
	 * Properties to the Renderers that define the given VisualProperty.
	 */
	private static HashMap <String, Set<Renderer> > vpToRenderersMap = new HashMap <String, Set<Renderer> >();
	
	public static void addVisualPropertiesOfRenderer(Renderer renderer){
		for(VisualProperty vp: renderer.supportedVisualAttributes()){
			addVisualProperty(vp, renderer);
		}
	}
	public static void removeVisualPropertiesOfRenderer(Renderer renderer){
		for(VisualProperty vp: renderer.supportedVisualAttributes()){
			removeVisualProperty(vp, renderer);
		}
	}
	
	private static void addVisualProperty(VisualProperty vp, Renderer renderer){
		String name = vp.getName();
		if (visualProperties.containsKey(name)){
			// check that they are the same -- but how?
			// FIXME FIXME FIXME
			System.out.println("FIXME FIXME FIXME FIXME");
			// assuming that they are the same, for now:
			Set<Renderer> renderers = vpToRenderersMap.get(name);
			if (renderers == null){
				System.out.println("can't happen! -- this is an exsisting VisualProperty thus it has to be in vpToRenderersMap!");
			} else {
				renderers.add(renderer);
			}
		} else { // New VisualProperty, store it:
			visualProperties.put(name, vp);
			Set<Renderer> renderers = vpToRenderersMap.get(name);
			if (renderers == null){
				renderers = new HashSet<Renderer>();
				renderers.add(renderer);
				vpToRenderersMap.put(name, renderers);
			} else {
				System.out.println("can't happen! -- this is a new VisualProperty thus it can't be in vpToRenderersMap!");
			}
		}
	}

	private static void removeVisualProperty(VisualProperty vp, Renderer renderer){
		String name = vp.getName();
		if (visualProperties.containsKey(name)){
			Set<Renderer> renderers = vpToRenderersMap.get(name);
			if (renderers == null){
				System.out.println("can't happen! -- this is an exsisting VisualProperty thus it has to be in vpToRenderersMap!");
			} else {
				renderers.remove(renderer);
				if (renderers.isEmpty()){
					// this was the last renderer that defined this VisualProperty, thus we have to remove the VisualProperty altogether:
					vpToRenderersMap.remove(name);
					visualProperties.remove(name);
				}
			}
		} else {
			System.out.println("can't happen! -- can't remove non-exsisting VisualProperty!");
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
