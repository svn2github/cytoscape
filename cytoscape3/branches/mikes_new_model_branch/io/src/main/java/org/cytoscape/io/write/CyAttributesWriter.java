
package org.cytoscape.io.write;

import org.cytoscape.attributes.CyAttributes;
import java.util.List;

/**
 * The interface used to write {@link CyAttributes} to files. 
 * Different implementations of this interface can be used
 * to write different kinds of attribute files like XGMML or
 * Cytoscape's native attribute file format.
 */
public interface CyAttributesWriter extends CyWriter {

	/**
	 * Sets the {@link CyAttributes} objects that are to be written.
	 * @param attrs A non-null list of {@link CyAttributes} objects.
	 */
	public void setAttributes(List<CyAttributes> attrs);
}
