package org.cytoscape.view.presentation;

import java.util.Collection;

/**
 * This interface represents a manager service for
 * {@link RenderingEngineFactory} objects. Renderer implementations should add
 * their {@link RenderingEngineFactory} objects to this manager.
 */
public interface RenderingEngineFactoryManager {

	/**
	 * An enumeration containing a list of possible types of RenderingEngineFactory objects. Some RenderingEngineFactory objects may be
	 * used for creating bird's eye view RenderingEngines, or for creating main window RenderingEngines.
	 */
	public enum RenderingEngineFactoryType {
		BIRDS_EYE,
		MAIN_WINDOW,
		VISUAL_STYLE_PREVIEW
	}
	
	/**
	 * Returns the renderer ID of the default renderer. If there is only 1 renderer available, its ID will be returned as the default. If none are available,
	 * returns <code>null</code>.
	 * 
	 * @return The renderer ID of the default renderer, which is the ID of the sole renderer if there is only one available. If
	 * no {@link RenderingEngineFactory} objects are registered, returns <code>null</code>.
	 */
	public String getDefaultRendererID();
	
	/**
	 * Returns modification-safe copy of a list containing the available renderer IDs.
	 * 
	 * @return A list, defensively copied, containing the available renderer IDs.
	 */
	public Collection<String> getAvailableRendererIDs();
	
	/**
	 * Attempts to set the current default renderer ID to the given renderer ID.
	 * 
	 * @param rendererID The ID of the new default renderer
	 * @return A modification-safe copy of the new default renderer ID, or <code>null</code> if no {@link RenderingEngineFactory} was found
	 * with the given renderer ID.
	 */
	public String setDefaultRenderer(String rendererID);
	
	/**
	 * Checks if at least one {@link RenderingEngineFactory} has been registered with the given renderer ID.
	 * 
	 * @return <code>true</code> if at least one {@link RenderingEngineFactory} has been registered with the given renderer ID, false otherwise.
	 */
	public boolean checkRendererIDAvailable(String rendererID);
	
	/**
	 * Adds the given {@link RenderingEngineFactory} to the manager, associating it with the given renderer ID and the given renderer type.
	 * 
	 * @param renderingEngineFactory
	 * @param rendererID The ID of the renderer, which is the renderer name.
	 * @param factoryType The type of the renderer, such as bird's eye view or main, via the {@link RenderingEngineFactoryType} enumeration.
	 */
	public void addRenderingEngineFactory(final RenderingEngineFactory<?> renderingEngineFactory, String rendererID, RenderingEngineFactoryType factoryType);

	/**
	 * If one exists, returns the {@link RenderingEngineFactory} associated with the given renderer ID and renderer type. Otherwise,
	 * returns <code>null</code>.
	 * 
	 * @param rendererID The ID of the renderer, which is the renderer name.
	 * @param factoryType The type of the renderer, such as bird's eye view or main, via the {@link RenderingEngineFactoryType} enumeration.
	 * @return The {@link RenderingEngineFactory} associated with the given renderer ID and renderer type, or null if none exist.
	 */
	public RenderingEngineFactory<?> getRenderingEngineFactory(String rendererID, RenderingEngineFactoryType factoryType);
	
	/**
	 * Removes the given {@link RenderingEngineFactory} from the manager.
	 * 
	 * @param renderingEngineFactory The {@link RenderingEngineFactory} to be removed.
	 */
	public void removeRenderingEngineFactory(final RenderingEngineFactory<?> renderingEngineFactory);
	
	/**
	 * Removes the {@link RenderingEngineFactory} associated with the given renderer ID and of the given renderer type, such as bird's eye view.
	 * 
	 * @param rendererID The ID of the renderer, which is the renderer name.
	 * @param factoryType The type of the renderer, such as bird's eye view or main, via the {@link RenderingEngineFactoryType} enumeration.
	 */
	public void removeRenderingEngineFactory(String rendererID, RenderingEngineFactoryType factoryType);
	
	/**
	 * Removes all {@link RenderingEngineFactory} objects associated with the given rendererID.
	 * 
	 * @param rendererID The ID of the renderer, which is the renderer name.
	 */
	public void removeRenderingEngineFactory(String rendererID);	
}
	
	
	
	
/* TODO:
 * Formalize bird's eye view and main RenderingEngineFactories
 * -separate visual style preview, bird's eye, main window
 * 
 */