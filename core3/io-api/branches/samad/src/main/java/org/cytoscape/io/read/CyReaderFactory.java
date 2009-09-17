package org.cytoscape.io.read;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.cytoscape.io.CyFileFilterable;
import org.cytoscape.work.ValuedTask;

public interface CyReaderFactory extends CyFileFilterable {

	public ValuedTask<Map<Class<?>,Object>> getReader(InputStream stream) throws IOException;

}
