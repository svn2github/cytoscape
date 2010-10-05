
package org.cytoscape.property;

import java.util.Properties;

/**
 * A simple implementation of CyProperty&lt;Properties&gt; suitable for 
 * general purpose use. 
 */
public final class DefaultCyProperty implements CyProperty<Properties> {

	private final Properties properties;

	/**
	 * @param properties The non-null Properties object this CyProperty object
	 * should encapsulate.  Throws NullPointerException if Properties is null.
	 */
	public DefaultCyProperty(final Properties properties) {
		if ( properties == null )
			throw new NullPointerException("properties object is null");
		this.properties = properties;
	}

	/**
	 * {@inheritDoc} 
	 */
	public Properties getProperties() {
		return properties;
	}
}
