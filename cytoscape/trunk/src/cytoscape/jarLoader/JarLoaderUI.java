// JarLoaderUI
//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.jarLoader;
//--------------------------------------------------------------------------
//import cytoscape.*;

//--------------------------------------------------------------------------
public class JarLoaderUI extends AbstractPlugin {
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
    public String describe() {
	return new String("Basic plugin for loading other plugins " +
			  "from .jar files.");
    }
} // class JarLoaderUI


