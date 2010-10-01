package org.cytoscape.io.write;

import java.io.File;
import java.io.OutputStream;

import org.cytoscape.io.FileIOFactory;

/**
 *
 */
public interface CyWriterFactory extends FileIOFactory {
	void setOutputStream(OutputStream os);
	CyWriter getWriter();
}
