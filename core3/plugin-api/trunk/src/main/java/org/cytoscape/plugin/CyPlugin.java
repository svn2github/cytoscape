package org.cytoscape.plugin;


/**
 * The primary plugin interface for Cytoscape that all
 * plugins must extend. Plugin developer will have access 
 * to most of Cytoscape 3.X services, but 
 * still program in the way as in Cytoscape 2.X.
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
		if ( adapter == null )
			throw new NullPointerException("null adapter");
		this.adapter = adapter;
	}
}
