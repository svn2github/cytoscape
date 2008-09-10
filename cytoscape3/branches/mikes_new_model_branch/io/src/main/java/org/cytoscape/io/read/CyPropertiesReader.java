

package org.cytoscape.io.read;

import java.util.List;
import java.util.Properties;

/**
 * Extends the {@link CyReader} interface to support the reading of
 * {@link Properties} objects. 
 */
public interface CyPropertiesReader extends CyReader {

	/** 
	 * Once the {@link CyReader#read()} method finishes executing, this 
	 * method should return a non-null {@link List} of {@link Properties} objects.
	 * @return A non-null {@link List} of {@link Properties} objects.
	 */
	public List<Properties> getReadProperties();
}


