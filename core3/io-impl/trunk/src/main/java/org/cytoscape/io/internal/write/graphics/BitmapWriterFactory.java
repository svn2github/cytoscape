package org.cytoscape.io.internal.write.graphics;


import org.cytoscape.io.write.PresentationWriterFactory;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.CyFileFilter;

import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.model.View;

import java.io.OutputStream;

/**
 * Returns a Task that will write
 */
public class BitmapWriterFactory implements PresentationWriterFactory {

	private final CyFileFilter bitmapFilter; 

	private RenderingEngine<?> re;
	private OutputStream outputStream;

	public BitmapWriterFactory(final CyFileFilter bitmapFilter) {
		this.bitmapFilter = bitmapFilter;	
	}

	public CyFileFilter getCyFileFilter() {
		return bitmapFilter; 
	}

	public void setRenderingEngine(final RenderingEngine<?> re) {
		if ( re == null )
			throw new NullPointerException("RenderingEngine is null");

		this.re = re;
	}

	public void setOutputStream(OutputStream os) {
		if ( os == null )
			throw new NullPointerException("Output stream is null");
		outputStream = os;
	}

	public CyWriter getWriterTask() {
		if ( re == null )
			throw new NullPointerException("RenderingEngine is null");
		
		return new BitmapWriter(re, outputStream, bitmapFilter.getExtensions() );
	}
}
