
package org.cytoscape.io.write;

import org.cytoscape.model.CyDataTable;
import java.util.List;

/**
 * The interface used to write {@link CyDataTable} to files. 
 * Different implementations of this interface can be used
 * to write different kinds of attribute files like XGMML or
 * Cytoscape's native attribute file format.
 */
public interface CyDataTableWriter extends CyWriter {

	/**
	 * Sets the {@link CyDataTable} objects that are to be written.
	 * @param attrs A non-null list of {@link CyDataTable} objects.
	 */
	public void setAttributes(List<CyDataTable> attrs);
}
