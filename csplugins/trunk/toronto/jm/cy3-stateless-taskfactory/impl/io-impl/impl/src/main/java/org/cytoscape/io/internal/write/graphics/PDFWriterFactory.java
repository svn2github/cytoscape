package org.cytoscape.io.internal.write.graphics;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.write.PresentationWriterContext;
import org.cytoscape.view.presentation.RenderingEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PDFWriterFactory extends AbstractPresentationWriterFactory {

	private static final Logger logger = LoggerFactory.getLogger(PDFWriterFactory.class);

	
	public PDFWriterFactory(CyFileFilter fileFilter) {
		super(fileFilter);
	}

	@Override
	public CyWriter createWriterTask(PresentationWriterContext context) {
		RenderingEngine<?> renderingEngine = context.getRenderingEngine();
		if ( renderingEngine == null )
			throw new NullPointerException("RenderingEngine is null");
		
		return new PDFWriter(renderingEngine, context.getOutputStream());
	}

}
