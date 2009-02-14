package org.cytoscape.property;

import java.io.IOException;

public interface PropertyReader<P> {

	public void read() throws IOException;

	public P getProperties();
}
