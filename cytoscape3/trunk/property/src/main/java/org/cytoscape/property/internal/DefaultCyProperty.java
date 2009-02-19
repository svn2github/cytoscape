package org.cytoscape.property.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.cytoscape.property.CyProperty;

public class DefaultCyProperty implements CyProperty<Properties> {

	private Properties properties;
	
	public DefaultCyProperty(Properties properties) {
		this.properties = properties;
	}

	public Properties getProperties() {
		return properties;
	}

	public void store(OutputStream os) throws IOException {
		// TODO Auto-generated method stub
		
	}




}
