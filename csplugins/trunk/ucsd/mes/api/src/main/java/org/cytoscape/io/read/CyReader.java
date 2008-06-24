
package org.cytoscape.io.read;

import java.net.URI;

public interface CyReader {
	public void read();
	public void setInput(URI u);
}
