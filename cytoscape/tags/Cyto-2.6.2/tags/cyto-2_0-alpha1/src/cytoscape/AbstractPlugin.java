//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape;

import java.lang.reflect.Constructor;

import cytoscape.view.CyWindow;

/**
 * AbstractPlugin is the class that all Cytoscape plugins must subclass.
 * The plugin should provide a one-argument constructor where the argument
 * is an instance of @link{cytoscape.view.CyWindow CyWindow}.
 *
 * Currently, plugins that provide a constructor taking a
 * @link{cytoscape.CytoscapeWindow CytoscapeWindow} argument instead of a
 * CyWindow are also supported, but this feature is expected to be removed
 * at a later date.
 *
 * It is encouraged, but not mandatory, for plugins to override the
 * @link{#describe describe} method to state what the plugin does and how it
 * should be used.
 */
public abstract class AbstractPlugin {
    /**
     * this method's presence is superfluous;
     * it is only here so that you don't have to
     * call super(cytoscapeWindow) in your ctor.
     */
    public AbstractPlugin() { }
    /**
     * Standard constructor with a single {@link cytoscape.view.CyWindow CyWindow}
     * argument.
     */
    public AbstractPlugin(CyWindow cyWindow) { }
    /**
     * required constructor for plugins takes a single
     * {@link cytoscape.CytoscapeWindow CytoscapeWindow} argument.
     */
    public AbstractPlugin(CytoscapeWindow cytoscapeWindow) { }
    /**
     * method returning a String description of the plugin.
     */
    public String describe() { return new String("No description."); }
    
    /**
     * Attempts to instantiate a plugin of the class defined by the
     * first argument. The other arguments to this method are used as
     * possible arguments for the plugin constructor. This method searches
     * for a constructor of a known type in the plugin class and then
     * attempts to use that constructor to create an instance of the plugin.
     *
     * Currently the only plugin constructor recognized has one argument
     * which is a CytoscapeWindow.
     *
     * @return true if the plugin was successfulyl constructed, false otherwise
     */
    public static boolean loadPlugin(Class pluginClass, CytoscapeObj cytoscapeObj,
                                     CyWindow cyWindow) {
        if (pluginClass == null) {return false;}
        
        //look for constructor with CyWindow argument
        if (cyWindow != null) {
            Constructor ctor = null;
            try {
                Class[] argClasses = new Class[1];
                argClasses[0] =  cyWindow.getClass();
                ctor = pluginClass.getConstructor(argClasses);
            } catch (SecurityException se) {
                System.err.println("In AbstractPlugin.loadPlugin:");
                System.err.println(se.getMessage());
                se.printStackTrace();
                return false;
            } catch (NoSuchMethodException nsme) {
                //ignore, there are other constructors to look for
            }
            if (ctor != null) {
                try {
                    Object[] args = new Object[1];
                    args[0] = cyWindow;
                    Object plugin = ctor.newInstance(args);
                    return true;
                } catch (Exception e) {
                    System.err.println("In AbstractPlugin.loadPlugin:");
                    System.err.println("Exception while constructing plugin instance:");
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                    return false;
                }
            }
        }
        
        //look for constructor with CytoscapeWindow argument
        if (cyWindow != null && cyWindow.getCytoscapeWindow() != null) {
            Constructor ctor = null;
            try {
                Class[] argClasses = new Class[1];
                argClasses[0] =  cyWindow.getCytoscapeWindow().getClass();
                ctor = pluginClass.getConstructor(argClasses);
            } catch (SecurityException se) {
                System.err.println("In AbstractPlugin.loadPlugin:");
                System.err.println(se.getMessage());
                se.printStackTrace();
                return false;
            } catch (NoSuchMethodException nsme) {
                //ignore, there are other constructors to look for
            }
            if (ctor != null) {
                try {
                    Object[] args = new Object[1];
                    args[0] = cyWindow.getCytoscapeWindow();
                    Object plugin = ctor.newInstance(args);
                    return true;
                } catch (Exception e) {
                    System.err.println("In AbstractPlugin.loadPlugin:");
                    System.err.println("Exception while constructing plugin instance:");
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }
}


