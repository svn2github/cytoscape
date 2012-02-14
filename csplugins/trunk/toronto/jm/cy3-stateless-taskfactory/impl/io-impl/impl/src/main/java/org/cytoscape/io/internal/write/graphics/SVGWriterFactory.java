package org.cytoscape.io.internal.write.graphics;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.write.PresentationWriterContext;
import org.cytoscape.view.presentation.RenderingEngine;

public class SVGWriterFactory extends AbstractPresentationWriterFactory {

	public SVGWriterFactory(final CyFileFilter fileFilter) {
		super(fileFilter);
	}

	@Override
	public CyWriter createWriterTask(PresentationWriterContext context) {
		RenderingEngine<?> renderingEngine = context.getRenderingEngine();
		if (renderingEngine == null)
			throw new NullPointerException("RenderingEngine is null");

		return new SVGWriter(renderingEngine, context.getOutputStream());
	}

}
