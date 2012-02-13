package org.cytoscape.view.presentation;

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
	 * Adds the given {@link RenderingEngineFactory} to the manager, associating it with the given renderer ID and the given renderer type.
	 * 
	 * @param renderingEngineFactory
	 * @param rendererID The ID of the renderer, which is the renderer name.
	 * @param rendererType The type of the renderer, such as bird's eye view or main, via the {@link RenderingEngineFactoryType} enumeration.
	 */
	public void addRenderingEngineFactory(RenderingEngineFactory<?> renderingEngineFactory, String rendererID, RenderingEngineFactoryType rendererType);

	/**
	 * If one exists, returns the {@link RenderingEngineFactory} associated with the given renderer ID and renderer type. Otherwise,
	 * returns null.
	 * 
	 * @param rendererID The ID of the renderer, which is the renderer name.
	 * @param rendererType The type of the renderer, such as bird's eye view or main, via the {@link RenderingEngineFactoryType} enumeration.
	 * @return The {@link RenderingEngineFactory} associated with the given renderer ID and renderer type, or null if none exist.
	 */
	public RenderingEngineFactory<?> getRenderingEngineFactory(String rendererID, RenderingEngineFactoryType rendererType);
	
	/**
	 * Removes the given {@link RenderingEngineFactory} from the manager.
	 * 
	 * @param renderingEngineFactory The {@link RenderingEngineFactory} to be removed.
	 */
	public void removeRenderingEngineFactory(RenderingEngineFactory<?> renderingEngineFactory);
	
	/**
	 * Removes the {@link RenderingEngineFactory} associated with the given renderer ID and of the given renderer type, such as bird's eye view.
	 * 
	 * @param rendererID The ID of the renderer, which is the renderer name.
	 * @param rendererType The type of the renderer, such as bird's eye view or main, via the {@link RenderingEngineFactoryType} enumeration.
	 */
	public void removeRenderingEngineFactory(String rendererID, RenderingEngineFactoryType rendererType);
	
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