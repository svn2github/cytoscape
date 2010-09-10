package org.cytoscape.io.write;


import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.model.View;

/**
 * Returns a Task that will write
 */
public interface ViewWriterFactory extends CyWriterFactory {

	void setViewRenderer(View<?> view, RenderingEngine re);
}
