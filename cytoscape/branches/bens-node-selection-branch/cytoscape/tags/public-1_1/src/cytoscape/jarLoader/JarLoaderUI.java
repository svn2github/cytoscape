// JarLoaderUI
//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.jarLoader;
//--------------------------------------------------------------------------
import cytoscape.*;
import javax.swing.*;

//--------------------------------------------------------------------------
public class JarLoaderUI {
    protected CytoscapeWindow cytoscapeWindow;
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
    public JarLoaderUI (CytoscapeWindow cytoscapeWindow, JMenu theMenu)
    {
	this.cytoscapeWindow = cytoscapeWindow;
	theMenu.add
	    (new JarPluginLoaderAction (cytoscapeWindow));
	theMenu.add
	    (new JarPluginDirectoryAction (cytoscapeWindow));
	String[] args = cytoscapeWindow.getConfiguration().getArgs();
	JarLoaderCommandLineParser parser =
	    new JarLoaderCommandLineParser(args,cytoscapeWindow);
    }
} // class JarLoaderUI


