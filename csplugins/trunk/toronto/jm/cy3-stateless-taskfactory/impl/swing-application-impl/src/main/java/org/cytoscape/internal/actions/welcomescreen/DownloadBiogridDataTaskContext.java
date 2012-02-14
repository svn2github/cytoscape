package org.cytoscape.internal.actions.welcomescreen;

import java.net.URL;
import java.util.Map;

public class DownloadBiogridDataTaskContext {

	private Map<String, URL> sourceMap;

	public void setSourceMap(Map<String, URL> sourceMap) {
		this.sourceMap = sourceMap;
	}

	public Map<String, URL> getSourceMap() {
		return sourceMap;
	}
}
