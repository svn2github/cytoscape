package org.cytoscape.io.read;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.cytoscape.io.FileIOFactory;

public interface CyReaderFactory extends FileIOFactory {

	public CyReader getReader(URI uri) throws IOException;

	public CyReader getReader(InputStream stream) throws IOException;

}
