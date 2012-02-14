package org.cytoscape.io.write;

import org.cytoscape.view.presentation.RenderingEngine;

public class PresentationWriterContextImpl extends CyWriterContextImpl
		implements PresentationWriterContext {

	private RenderingEngine<?> renderingEngine;

	@Override
	public void setRenderingEngine(RenderingEngine<?> re) {
		renderingEngine = re;
	}

	@Override
	public RenderingEngine<?> getRenderingEngine() {
		return renderingEngine;
	}

}
