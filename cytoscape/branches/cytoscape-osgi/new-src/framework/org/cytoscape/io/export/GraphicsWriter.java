
package org.cytoscape.io.export;

import java.awt.Component;

public interface GraphicsWriter {

	public void write(OutputStream os, Component comp);
}

