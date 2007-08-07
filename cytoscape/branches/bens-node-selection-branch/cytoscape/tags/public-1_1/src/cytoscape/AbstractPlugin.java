//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape;

/**
 * AbstractPlugin is the class that all plugins must subclass;
 * the interface is simple - the constructor must take a single
 * {@link cytoscape.CytoscapeWindow CytoscapeWindow} argument,
 * and there must be a {@link #describe describe} method
 * returning a String description of the plugin.
 */
public abstract class AbstractPlugin {
    /**
     * this method's presence is superfluous;
     * it is only here so that you don't have to
     * call super(cytoscapeWindow) in your ctor.
     */
    public AbstractPlugin() { }
    /**
     * required constructor for plugins takes a single
     * {@link cytoscape.CytoscapeWindow CytoscapeWindow} argument.
     */
    public AbstractPlugin(CytoscapeWindow cytoscapeWindow) { }
    /**
     * method returning a String description of the plugin.
     */
    public String describe() { return new String("No description."); }
}


