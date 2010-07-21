package org.cytoscape.plugin;


/**
 * The primary plugin interface for Cytoscape that all
 * plugins must extend. 
 */
public abstract class CyPlugin {

	protected CyPluginAdapter adapter;

	// so no one calls this constructor
	private CyPlugin() {
		throw new NullPointerException("no adapter provided!");
	}

	/**
	 * The constructor that all plugins must call using "super(adapter);" where
	 * the "adapter" is a {@link CyPluginAdapter} reference provided as an
	 * argument to the constructor. Cytoscape's plugin loader will execute
	 * the constructor and provide the proper CyPluginAdapter reference.
	 * <br/>
	 * <code>
	 * public class MyPlugin extends CyPlugin {
	 *    public MyPlugin(CyPluginAdapter adapter) {
	 *       super(adapter);
	 *       // plugin code here
	 *    }
	 * }
	 * </code>
	 */
	public CyPlugin(final CyPluginAdapter adapter) {
		this.adapter = adapter;
	}
}
