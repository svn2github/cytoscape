package org.cytoscape.plugin;


/**
 * The primary plugin interface for Cytoscape that all
 * plugins must extend. 
 */
public abstract class CyPlugin {

	/**
	 * Reference to access Cytoscape functionality -- various managers and 
	 * factories that are normally available as OSGi services.
	 */
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
	 * 
	 * <blockquote><pre> 
	 * public class MyPlugin extends CyPlugin {
	 *    public MyPlugin(CyPluginAdapter adapter) {
	 *       super(adapter);
	 *       // plugin code here
	 *    }
	 * }
	 * </pre></blockquote>
	 */
	public CyPlugin(final CyPluginAdapter adapter) {
		this.adapter = adapter;
	}
}
