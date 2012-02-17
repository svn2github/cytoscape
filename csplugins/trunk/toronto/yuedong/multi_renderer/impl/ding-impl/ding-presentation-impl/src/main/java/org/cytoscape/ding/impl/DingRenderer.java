package org.cytoscape.ding.impl;

import java.util.HashMap;

import org.cytoscape.application.swing.presentation.BirdsEyeViewRenderingEngineFactory;
import org.cytoscape.application.swing.presentation.MainViewRenderingEngineFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.ExternalRenderer;
import org.cytoscape.view.presentation.ExternalRendererManager;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.vizmap.gui.presentation.VisualStylePreviewRenderingEngineFactory;

public class DingRenderer implements ExternalRenderer {

	private static final String READBLE_NAME = "Ding";
	
	private VisualLexicon visualLexicon;
	private RenderingEngineFactory<?> mainRenderingEngineFactory;
	private RenderingEngineFactory<?> birdEyeRenderingEngineFactory;
	private CyNetworkViewFactory networkViewFactory;
	
	public DingRenderer(ExternalRendererManager externalRendererManager,
			VisualLexicon visualLexicon,
			RenderingEngineFactory<?> mainRenderingEngineFactory,
			RenderingEngineFactory<?> birdEyeRenderingEngineFactory,
			CyNetworkViewFactory networkViewFactory) {
		
		externalRendererManager.addRenderer(this, new HashMap<String, String>());
		
		this.visualLexicon = visualLexicon;
		this.mainRenderingEngineFactory = mainRenderingEngineFactory;
		this.birdEyeRenderingEngineFactory = birdEyeRenderingEngineFactory;
		this.networkViewFactory = networkViewFactory;
	}
	
	@Override
	public String getRendererName() {
		
		return READBLE_NAME;
	}
	
	@Override
	public RenderingEngineFactory<?> getRenderingEngineFactory(
			final Class<? extends RenderingEngineFactory<?>> type) {
	
		if (type == MainViewRenderingEngineFactory.class) {
			return mainRenderingEngineFactory;
		} else if (type == BirdsEyeViewRenderingEngineFactory.class) {
			return birdEyeRenderingEngineFactory;
		} else if (type == VisualStylePreviewRenderingEngineFactory.class) {
			
			// Ding uses the default rendering engine factory for visual style previews
			return mainRenderingEngineFactory;
		}
		
		return null;
	}

	@Override
	public CyNetworkViewFactory getNetworkViewFactory() {
		
		return networkViewFactory;
	}
	@Override
	public VisualLexicon getVisualLexicon() {
		
		return visualLexicon;
	}
}
