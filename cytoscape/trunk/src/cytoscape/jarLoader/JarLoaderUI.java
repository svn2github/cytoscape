// JarLoaderUI
//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.jarLoader;
//--------------------------------------------------------------------------
import cytoscape.*;

//--------------------------------------------------------------------------
public class JarLoaderUI {
    protected CytoscapeWindow cytoscapeWindow;
    public JarLoaderUI (CytoscapeWindow cytoscapeWindow)
    {
	super(cytoscapeWindow);
	this.cytoscapeWindow = cytoscapeWindow;
	cytoscapeWindow.getOperationsMenu().add
	    (new JarLoaderAction (cytoscapeWindow));
	cytoscapeWindow.getOperationsMenu().add
	    (new JarPluginLoaderAction (cytoscapeWindow));
	cytoscapeWindow.getOperationsMenu().add
	    (new JarPluginDirectoryAction (cytoscapeWindow));
	String[] args = cytoscapeWindow.getConfiguration().getArgs();
	JarLoaderCommandLineParser parser =
	    new JarLoaderCommandLineParser(args,cytoscapeWindow);
    }
} // class JarLoaderUI


