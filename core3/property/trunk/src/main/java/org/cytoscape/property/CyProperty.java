package org.cytoscape.property;

import java.io.IOException;
import java.io.OutputStream;

public interface CyProperty<P> {

	public void store(OutputStream os) throws IOException;

	public P getProperties();

}
