package org.cytoscape.io.internal.write;


import org.cytoscape.io.write.ViewWriter;
import org.cytoscape.io.write.ViewWriterManager;
import org.cytoscape.io.write.ViewWriterFactory;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.RenderingEngine;
import java.io.File;


public class ViewWriterManagerImpl extends AbstractWriterManager<ViewWriterFactory> 
	implements ViewWriterManager {

	public ViewWriterManagerImpl() {
		super(DataCategory.IMAGE);
	}

	public CyWriter getWriter(View<?> view, RenderingEngine re, CyFileFilter filter, File outFile) {
		ViewWriterFactory tf = getMatchingFactory(filter,outFile);
		if ( tf == null )
			throw new NullPointerException("Couldn't find matching factory for filter: " + filter);
		tf.setViewRenderer(view,re);
		return tf.getWriter();
	}
}
