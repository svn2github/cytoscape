package org.cytoscape.io.read;

import java.io.InputStream;

import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.Task;

/**
 * An extension of the Task interface that returns an array of 
 * {@link CyNetworkView} objects as well as optional 
 * {@link VisualStyle} objects that are read as part of the Task.
 * Instances of this interface are created by InputStreamTaskFactory
 * objects registered as OSGi services, which are in turn processed
 * by associated reader manager objects that distinguish 
 * InputStreamTaskFactories based on the DataCategory associated with
 * the CyFileFilter.
 */
public interface CyNetworkViewReader extends Task {

	/**
	 * @return An array of CyNetworkView objects.
	 */
	CyNetworkView[] getNetworkViews();

	/**
	 * @return An array of VisualStyle objects. The list may be
	 * empty if no VisualStyle is defined by the input being
	 * read.
	 */
	VisualStyle[] getVisualStyles();
}
