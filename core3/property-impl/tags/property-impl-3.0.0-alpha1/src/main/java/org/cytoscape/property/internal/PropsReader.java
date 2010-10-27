package org.cytoscape.property.internal;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

import org.cytoscape.property.CyProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropsReader implements CyProperty<Properties> {

	private static final Logger logger = LoggerFactory.getLogger(PropsReader.class);

	private Properties props;

	/**
	 * Creates a new BookmarkReader object.
	 */
	public PropsReader(String resourceLocation) {
	
		InputStream is = null; 

		try {
			if ( resourceLocation == null )
				throw new NullPointerException("resourceLocation is null");

			is = this.getClass().getClassLoader().getResourceAsStream(resourceLocation);

			props = new Properties();
			props.load(is);

		} catch (Exception e) {
			logger.warn("Could not read properties file - using empty intance.", e);
			props = new Properties();
		} finally {
			if (is != null) {
				try { is.close(); } catch (IOException ioe) {}
				is = null;
			}
		}
	}

	public Properties getProperties() {
		return props;
	}
}
