package org.cytoscape.io.write;

import java.io.IOException;
import java.io.OutputStream;

import java.util.Map;
import org.cytoscape.io.CyFileFilterable;
import org.cytoscape.work.Task;

public interface CyWriterFactory extends CyFileFilterable {

	public Task getWriter(Map<Class<?>,Object> contents, OutputStream output) throws IOException;

}
