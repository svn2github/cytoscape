package org.cytoscape.io.internal.write.graphics;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.write.PresentationWriterContext;
import org.cytoscape.view.presentation.RenderingEngine;


public class PSWriterFactory extends AbstractPresentationWriterFactory {
	
	public PSWriterFactory(final CyFileFilter fileFilter) {
		super(fileFilter);
 	}

	@Override
	public CyWriter createWriterTask(PresentationWriterContext context) {
		RenderingEngine<?> renderingEngine = context.getRenderingEngine();
		if ( renderingEngine == null )
			throw new NullPointerException("RenderingEngine is null");
		
		return new PSWriter(renderingEngine, context.getOutputStream());
	}

}
