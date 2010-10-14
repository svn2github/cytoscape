package org.cytoscape.view.presentation;

public interface RenderingEngineManager {
	void addRenderingEngine(final RenderingEngine<?> engine);
	
	void removeRenderingEngine(final RenderingEngine<?> engine);
}
