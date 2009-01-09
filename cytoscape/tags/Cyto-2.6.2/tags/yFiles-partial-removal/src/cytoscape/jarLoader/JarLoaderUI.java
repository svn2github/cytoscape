// JarLoaderUI
//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.jarLoader;
//--------------------------------------------------------------------------
import javax.swing.*;

import cytoscape.view.CyWindow;
//--------------------------------------------------------------------------
public class JarLoaderUI {
    protected CyWindow cyWindow;
    /**
     * <p>The JarLoaderUI constructor adds two items to the
     * File Loading menu, or whatever menu gets passed
     * as the secont argument to the constructor.  The
     * first of the two items loads plugins from a single
     * .jar file; the second loads all plugins from all
     * .jar files in a directory.  These items are
     * instances of JarPluginLoaderActiona and
     * JarPluginDirectoryAction, respectively.</p>
     *
     * <p>After adding the menu items, the JarLoaderUI
     * constructor calls the JarLoaderCommandLineParser
     * to instantiate any plugins that may have been
     * specified at the command line.</p>
     */
    public JarLoaderUI (CyWindow cyWindow, JMenu theMenu)
    {
	this.cyWindow = cyWindow;
	theMenu.add
	    (new JarPluginLoaderAction (cyWindow));
	theMenu.add
	    (new JarPluginDirectoryAction (cyWindow));
	String[] args = cyWindow.getCytoscapeObj().getConfiguration().getArgs();
	JarLoaderCommandLineParser parser =
	    new JarLoaderCommandLineParser(args,cyWindow);
    }
} // class JarLoaderUI


