package org.cytoscape.io.read;

import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.Task;

/**
 * An extension of the Task interface that returns an array of
 * {@link org.cytoscape.view.model.CyNetworkView} objects as well as optional
 * {@link org.cytoscape.view.vizmap.VisualStyle} objects that are read as part
 * of the Task. Instances of this interface are created by
 * InputStreamTaskFactory objects registered as OSGi services, which are in turn
 * processed by associated reader manager objects that distinguish
 * InputStreamTaskFactories based on the DataCategory associated with the
 * {@link org.cytoscape.io.CyFileFilter}.
 */
public interface CyNetworkViewReader extends Task {

    /**
     * Return an array of {@link org.cytoscape.view.model.CyNetworkView} objects
     * 
     * <p>
     * This array may contain {@link org.cytoscape.view.model.NullCyNetworkView}.
     * Create actual view or not is controlled by a parameter for reader implementation.
     * </p>
     * 
     * @return An array of {@link org.cytoscape.view.model.CyNetworkView}
     *         objects.
     */
    CyNetworkView[] getNetworkViews();

    /**
     * Return an array of {@link org.cytoscape.view.vizmap.VisualStyle} objects.
     * 
     * @return An array of {@link org.cytoscape.view.vizmap.VisualStyle}
     *         objects. The list may be empty if no VisualStyle is defined by
     *         the input being read.
     */
    VisualStyle[] getVisualStyles();
}
