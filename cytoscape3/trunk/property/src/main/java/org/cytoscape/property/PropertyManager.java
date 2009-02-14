package org.cytoscape.property;

import java.io.OutputStream;

public interface PropertyManager {

	public Object getProperties(String name);

	public void addProperties(Object properties);
	
	// Store to .cytoscape
	public void store();
	
	// for saving in session files
	public void store(OutputStream os);

}
