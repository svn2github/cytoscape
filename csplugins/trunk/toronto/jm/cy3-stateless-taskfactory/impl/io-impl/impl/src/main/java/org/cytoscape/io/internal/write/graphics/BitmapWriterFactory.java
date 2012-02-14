package org.cytoscape.io.internal.write.graphics;


import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.write.PresentationWriterContext;
import org.cytoscape.view.presentation.RenderingEngine;

/**
 * Returns a Task that will write
 */
public class BitmapWriterFactory extends AbstractPresentationWriterFactory {

	public BitmapWriterFactory(final CyFileFilter bitmapFilter) {
		super(bitmapFilter);
	}

	public CyWriter createWriterTask(PresentationWriterContext context) {
		RenderingEngine<?> renderingEngine = context.getRenderingEngine();
		if ( renderingEngine == null )
			throw new NullPointerException("RenderingEngine is null");
		
		return new BitmapWriter(renderingEngine, context.getOutputStream(), fileFilter.getExtensions() );
	}
}
