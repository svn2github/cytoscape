package org.cytoscape.app.internal;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.cytoscape.app.CyAppAdapter;

public class AppLoaderTaskContext {

	private CyAppAdapter adapter;
	 
	// App Jar file URLs
	public static final Set<URL> urls = new HashSet<URL>();

	public CyAppAdapter getCyAppAdapter() {
		return adapter;
	}

	public Set<URL> getUrls() {
		return urls;
	}
}
