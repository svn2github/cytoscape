
package org.cytoscape.io.read;

import org.cytoscape.model.attrs.CyAttributes;
import java.util.List;

public interface CyAttributesReader extends CyReader {

	public List<CyAttributes> getReadAttributes();
}
