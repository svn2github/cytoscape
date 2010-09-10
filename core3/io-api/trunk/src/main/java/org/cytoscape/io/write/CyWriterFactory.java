package org.cytoscape.io.write;

import java.io.File;

import org.cytoscape.io.FileIOFactory;

/**
 *
 */
public interface CyWriterFactory extends FileIOFactory {
	void setOutputFile(File f);
	CyWriter getWriter();
}
