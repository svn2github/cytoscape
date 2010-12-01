package org.cytoscape.view.presentation;

import java.util.Collection;

/**
 * Manager object for RenderingEngineFactories.
 * This will be exported as a service and users can access all rendering engine factories through this interface.
 *
 */
public interface RenderingEngineFactoryManager {
	
	/**
	 * Provides all RenderingEngineFactories.
	 * 
	 * @return all registered factories.
	 */
	Collection<RenderingEngineFactory<?>> getAllRenderingEngineFactories();
	
	
	/**
	 * Get a specific rendering engine factory object.
	 * 
	 * @param factoryID metadata for the factory.  This is a metadata in Spring config file.  This is unique.
	 * 
	 * @return rendering engine factory if available.
	 */
	RenderingEngineFactory<?> getRenderingEngine(final String factoryID);

}
