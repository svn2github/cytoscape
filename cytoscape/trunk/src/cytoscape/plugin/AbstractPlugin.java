/** Copyright (c) 2004 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.plugin;

import java.lang.reflect.Constructor;

import cytoscape.view.CyWindow;
import cytoscape.CytoscapeObj;

/**
 * AbstractPlugin is the class that all Cytoscape plugins must subclass.
 * The plugin should provide a one-argument constructor where the argument
 * is an instance of {@link cytoscape.view.CyWindow CyWindow}.<P>
 *
 * It is encouraged, but not mandatory, for plugins to override the
 * {@link#describe describe} method to state what the plugin does and how it
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
     * Standard constructor with a single {@link CyWindow} argument.
     */
    public AbstractPlugin(CyWindow cyWindow) { }
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


        //System.out.println( "AbstractPlugin loading: "+pluginClass );

        //look for constructor with CyWindow argument
        if (cyWindow != null) {
            Constructor ctor = null;
            try {
                Class[] argClasses = new Class[1];
                argClasses[0] =  CyWindow.class;//cyWindow.getClass();
                ctor = pluginClass.getConstructor(argClasses);
            } catch ( Exception e ) {
              e.printStackTrace();
            }


// (SecurityException se) {
//                 System.err.println("In AbstractPlugin.loadPlugin:");
//                 System.err.println(se.getMessage());
//                 se.printStackTrace();
//                 return false;
//             } catch (NoSuchMethodException nsme) {
//                 //ignore, there are other constructors to look for
//             }

            

            if (ctor != null) {
              try {
                Object[] args = new Object[1];
                args[0] = cyWindow;
                return ctor.newInstance(args) != null;
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


