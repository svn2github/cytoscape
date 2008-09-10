
package org.cytoscape.io.read;

import org.cytoscape.view.GraphView;
import java.util.List;

/**
 * Extends the {@link CyReader} interface to support the reading of
 * {@link CyNetwork} objects. 
 */
public interface CyNetworkViewReader extends CyReader {

	/** 
	 * Once the {@link CyReader#read()} method finishes executing, this 
	 * method should return a non-null {@link List} of {@link CyNetwork} objects.
	 * @return A non-null {@link List} of {@link CyNetwork} objects.
	 */
	public List<GraphView> getReadNetworkViews();
}


