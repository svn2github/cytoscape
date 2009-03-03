package org.cytoscape.io.write;

import java.io.IOException;

import org.cytoscape.io.FileIOFactory;

public interface CyWriterFactory extends FileIOFactory {
	
	public CyWriter getWriter() throws IOException;

}
