package NetworkBLAST;

import java.util.Map;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * NetworkBLAST plugin provides NCT functionality to Cytoscape.
 */

public class NetworkBLASTPlugin extends CytoscapePlugin
{
  private NetworkBLASTDialog dialog = null;
  
  public NetworkBLASTPlugin()
  {
    this.dialog = new NetworkBLASTDialog(Cytoscape.getDesktop());
    
    JMenu nbMenu = new JMenu("NetworkBLAST");
    
    JMenuItem aboutMenuItem = new JMenuItem(new NetworkBLASTAction(
                                          "About", 0, this.dialog));
					  
    JMenuItem comptMenuItem = new JMenuItem(new NetworkBLASTAction(
                                          "Generate Compatiblity Graph",
					  1, this.dialog));
					  
    JMenuItem pathMenuItem = new JMenuItem(new NetworkBLASTAction(
                                          "Path Search",
					  2, this.dialog));
					  
    JMenuItem compMenuItem = new JMenuItem(new NetworkBLASTAction(
                                          "Complex Search",
					  3, this.dialog));
					  
    JMenuItem scoreMenuItem = new JMenuItem(new NetworkBLASTAction(
                                          "Score Model Settings",
					  4, this.dialog));

					  
    nbMenu.add(aboutMenuItem);
    nbMenu.addSeparator();
    nbMenu.add(comptMenuItem);
    nbMenu.add(pathMenuItem);
    nbMenu.add(compMenuItem);
    nbMenu.addSeparator();
    nbMenu.add(scoreMenuItem);

    Cytoscape.getDesktop().getCyMenus().getMenuBar().
    		getMenu("Plugins").add(nbMenu);
  }
}
