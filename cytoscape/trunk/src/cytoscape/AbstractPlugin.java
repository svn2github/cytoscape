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
 * and there must be a {@link #describe} method returning a
 * String description of the plugin.
 */
public abstract class AbstractPlugin {
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


