
package org.cytoscape.io.read;

import org.cytoscape.model.CyDataTable;
import java.util.List;

/**
 * Extends the {@link CyReader} interface to support the reading of
 * {@link CyAttributes} objects. 
 */
public interface CyDataTableReader extends CyReader {

	/** 
	 * Once the {@link CyReader#read()} method finishes executing, this 
	 * method should return a non-null {@link List} of {@link CyAttributes} objects.
	 * @return A non-null {@link List} of {@link CyAttributes} objects.
	 */
	public List<CyDataTable> getReadDataTables();
}
