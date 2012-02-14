package org.cytoscape.view.presentation.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.cytoscape.view.presentation.ExternalRenderer;
import org.cytoscape.view.presentation.ExternalRendererManager;

/**
 * Implementation for {@link ExternalRendererManager} interface.
 * 
 * @author yuedong
 */
public class ExternalRendererManagerImpl implements ExternalRendererManager {

	private Map<String, ExternalRenderer> installedRenderers;
	
	private String defaultRendererID;
	
	public ExternalRendererManagerImpl() {
		installedRenderers = new HashMap<String, ExternalRenderer>();
	}
	
	@Override
	public void installRenderer(ExternalRenderer externalRenderer) {
		
		if (externalRenderer == null) {
			throw new NullPointerException("Cannot install a null renderer.");
		}
		
		installedRenderers.put(externalRenderer.getRendererID(), externalRenderer);
		
		// If we only have 1 renderer, set it as the default
		manageDefaultRenderer();
	}

	@Override
	public void uninstallRenderer(ExternalRenderer externalRenderer) {
		
		if (!installedRenderers.containsValue(externalRenderer)) {
			throw new IllegalArgumentException("Provided renderer " + externalRenderer + " is not installed.");
		}
		
		// Let the renderer know
		externalRenderer.dispose();
	
		installedRenderers.remove(externalRenderer.getRendererID());
		
		// If we only have 1 renderer, set it as the default
		manageDefaultRenderer();
	}

	@Override
	public void uninstallRenderer(String rendererID) {
		ExternalRenderer removed = installedRenderers.get(rendererID);
		
		if (removed == null) {
			throw new IllegalArgumentException("No renderer with given ID <" + rendererID + "> is installed.");
		}
		
		// Let the renderer know
		removed.dispose();
	
		manageDefaultRenderer();
	}

	@Override
	public Collection<String> getInstalledRenderers() {
		return installedRenderers.keySet();
	}

	@Override
	public int getInstalledRendererCount() {
		return installedRenderers.size();
	}

	@Override
	public ExternalRenderer getRenderer(String rendererID) {
		return installedRenderers.get(rendererID);
	}

	@Override
	public String setDefaultRendererID(String rendererID) {
		if (!installedRenderers.containsKey(rendererID)) {
			return null;
		}
		
		defaultRendererID = new String(rendererID);
		return new String(defaultRendererID);
	}

	@Override
	public String getDefaultRendererID() {
		if (installedRenderers.size() == 0) {
			return null;
		}
		
		return new String(defaultRendererID);
		
		// TODO: Check if necessary to return defensively copied Strings this way
	}
	
	/**
	 * Checks if there is only 1 renderer currently installed, and if so, sets it to be the default renderer
	 */
	private void manageDefaultRenderer() {
		if (installedRenderers.size() == 1) {
			for (String id : installedRenderers.keySet()) {
				defaultRendererID = new String(id);
			}
		}
	}
}
