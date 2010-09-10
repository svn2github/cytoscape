package org.cytoscape.io.internal.write.graphics;


import org.cytoscape.io.write.ViewWriterFactory;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.CyFileFilter;

import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.model.View;

import java.io.File;

/**
 * Returns a Task that will write
 */
public class BitmapWriterFactory implements ViewWriterFactory {

	private final CyFileFilter bitmapFilter; 

	private View<?> view;
	private RenderingEngine re;
	private File outputFile;

	public BitmapWriterFactory(CyFileFilter bitmapFilter) {
		this.bitmapFilter = bitmapFilter;	
	}

	public CyFileFilter getCyFileFilter() {
		return bitmapFilter; 
	}

	public void setViewRenderer(View<?> view, RenderingEngine re) {
		if ( view == null )
			throw new NullPointerException("View is null");
		if ( re == null )
			throw new NullPointerException("RenderingEngine is null");

		this.view = view;
		this.re = re;
	}

	public void setOutputFile(File f) {
		if ( f == null )
			throw new NullPointerException("OutputFile is null");
		outputFile = f;
	}

	public CyWriter getWriter() {
		return new BitmapWriter(view, re, outputFile, bitmapFilter.getExtensions() );
	}
}
