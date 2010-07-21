package org.cytoscape.plugin;


public abstract class CyPlugin {

	protected CyPluginAdapter adapter;

	// so no one calls this constructor
	private CyPlugin() {
		throw new NullPointerException("no adapter provided!");
	}

	public CyPlugin(final CyPluginAdapter adapter) {
		this.adapter = adapter;
	}
}
