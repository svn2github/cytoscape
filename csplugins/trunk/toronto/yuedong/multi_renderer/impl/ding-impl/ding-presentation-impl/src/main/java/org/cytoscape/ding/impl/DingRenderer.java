package org.cytoscape.ding.impl;

import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.ExternalRenderer;
import org.cytoscape.view.presentation.ExternalRendererManager;
import org.cytoscape.view.presentation.RenderingEngineFactory;

public class DingRenderer implements ExternalRenderer {

	private static final String RENDERER_ID = "org.cytoscape.ding";
	
	private VisualLexicon visualLexicon;
	private RenderingEngineFactory<?> mainRenderingEngineFactory;
	private RenderingEngineFactory<?> birdEyeRenderingEngineFactory;
	private CyNetworkViewFactory networkViewFactory;
	
	public DingRenderer(ExternalRendererManager rendererManager,
			VisualLexicon dingVisualLexicon, 
			RenderingEngineFactory<?> dingMainRenderingEngineFactory,
			RenderingEngineFactory<?> dingBirdEyeRenderingEngineFactory,
			CyNetworkViewFactory dingNetworkViewFactory) {
		
		// Add this renderer to the manager
		rendererManager.installRenderer(this);
		
		visualLexicon = dingVisualLexicon;
		mainRenderingEngineFactory = dingMainRenderingEngineFactory;
		birdEyeRenderingEngineFactory = dingBirdEyeRenderingEngineFactory;
		networkViewFactory = dingNetworkViewFactory;
	}
	
	@Override
	public String getRendererID() {
		return RENDERER_ID;
	}

	@Override
	public RenderingEngineFactory<?> getRenderingEngineFactory(
			RenderPurpose renderPurpose) {
		
		switch (renderPurpose) {
			case BIRDS_EYE_VIEW:
				return birdEyeRenderingEngineFactory;
			case DETAIL_VIEW:
				return mainRenderingEngineFactory;
			case VISUAL_STYLE_PREVIEW:
				return mainRenderingEngineFactory;
			default:
				return null;
		}
	}

	@Override
	public CyNetworkViewFactory getNetworkViewFactory(
			RenderPurpose renderPurpose) {
		
		// Ding currently uses the same CyNetworkViewFactory for all network views
		return networkViewFactory;
	}

	@Override
	public VisualLexicon getVisualLexicon(RenderPurpose renderPurpose) {
		
		// Use the same visual lexicon for all rendering purposes
		return visualLexicon;
	}

}
