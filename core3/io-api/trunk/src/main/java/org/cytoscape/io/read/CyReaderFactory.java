package org.cytoscape.io.read;

import java.io.IOException;
import java.net.URI;

import org.cytoscape.io.FileIOFactory;

public interface CyReaderFactory extends FileIOFactory {
	
	public CyReader getReader(URI uri) throws IOException;

}
