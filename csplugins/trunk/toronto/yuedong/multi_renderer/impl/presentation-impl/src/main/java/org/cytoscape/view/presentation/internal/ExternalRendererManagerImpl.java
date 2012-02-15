package org.cytoscape.view.presentation.internal;

import java.util.Collection;
import java.util.Collections;
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

	private Map<Class<? extends ExternalRenderer>, ExternalRenderer> renderers;
	private Map<ExternalRenderer, Map<String, String>> properties;
	
	private ExternalRenderer currentRenderer;
	
	@Override
	public void addRenderer(ExternalRenderer externalRenderer,
			Map<String, String> properties) {
		
		// TODO: Throw exception if given renderer or properties is null
		
		Class<? extends ExternalRenderer> rendererClass = externalRenderer.getClass();
		
		if (renderers.containsKey(rendererClass)) {
			throw new IllegalArgumentException("Already contains an ExternalRenderer with this class.");
		}
		
		renderers.put(rendererClass, externalRenderer);
		
		manageCurrentRenderer();
	}

	@Override
	public void removeRenderer(ExternalRenderer externalRenderer,
			Map<String, String> properties) {
		
		if (!renderers.containsValue(externalRenderer)) {
			throw new IllegalArgumentException("Given renderer is not in the manager.");
		}
		
		Class<? extends ExternalRenderer> rendererClass = externalRenderer.getClass();
		renderers.remove(rendererClass);
		
		manageCurrentRenderer();
	}

	@Override
	public void removeRenderer(Class<? extends ExternalRenderer> rendererType) {
		
		if (!renderers.containsKey(rendererType)) {
			throw new IllegalArgumentException("No renderer in manager matches the given type.");
		}
		
		renderers.remove(rendererType);
		
		manageCurrentRenderer();
	}

	@Override
	public Collection<ExternalRenderer> getAvailableRenderers() {
		return Collections.unmodifiableCollection(renderers.values());
	}

	@Override
	public ExternalRenderer getRenderer(
			Class<? extends ExternalRenderer> rendererType) {
		return renderers.get(rendererType);
	}

	@Override
	public void setCurrentRenderer(
			Class<? extends ExternalRenderer> rendererType) {
		
		if (!renderers.containsKey(rendererType)) {
			throw new IllegalArgumentException("No renderer in manager matches the given type.");
		}
		
		currentRenderer = renderers.get(rendererType);
		
		manageCurrentRenderer();
	}

	@Override
	public ExternalRenderer getCurrentRenderer() {
		return currentRenderer;
	}
	
	/**
	 * If there is only 1 renderer available, set it to be the current renderer
	 */
	private void manageCurrentRenderer() {
		if (renderers.size() == 1) {
			for (ExternalRenderer renderer : renderers.values()) {
				currentRenderer = renderer;
			}
		}
	}
}
