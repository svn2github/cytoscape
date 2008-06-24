
package org.cytoscape.io.write;

import org.cytoscape.model.attrs.CyAttributes;
import java.util.List;

/**
 * The interface used to write attribute file types.
 */
public interface CyAttributesWriter extends CyWriter {

	public void setAttributes(List<CyAttributes> attrs);
}
