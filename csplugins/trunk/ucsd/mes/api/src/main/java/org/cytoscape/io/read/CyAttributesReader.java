
package org.cytoscape.io.read;

import org.cytoscape.model.attrs.CyAttributes;
import java.util.List;

/**
 * Extends the {@link CyReader} interface to support the reading of
 * {@link CyAttributes} objects. 
 */
public interface CyAttributesReader extends CyReader {

	/** 
	 * Once the {@link CyReader#read()} method finishes executing, this 
	 * method should return a non-null {@link List} of {@link CyAttributes} objects.
	 * @return A non-null {@link List} of {@link CyAttributes} objects.
	 */
	public List<CyAttributes> getReadAttributes();
}
