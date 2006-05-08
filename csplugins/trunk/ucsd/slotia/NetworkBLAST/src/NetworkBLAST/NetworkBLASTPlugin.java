package NetworkBLAST;

import java.util.Map;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;

import giny.model.GraphPerspective;

import javax.swing.JMenu;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.WindowConstants;
import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

/**
 * NetworkBLAST plugin provides NCT functionality to Cytoscape.
 * This plugin creates the menu item "NetworkBLAST" in the Plugins
 * menu. NetworkBLASTPlugin delegates the response to the menu item's
 * selection to the NetworkBLASTAction class.
 */

public class NetworkBLASTPlugin extends CytoscapePlugin
{
  private NetworkBLASTDialog m_dialog = null;
  
  public NetworkBLASTPlugin()
  {
    m_dialog = new NetworkBLASTDialog(Cytoscape.getDesktop(), true);
    initializeMenuItem();
  }

  /**
   * Creates the "NetworkBLAST" menu item in the Plugins menu
   * with NetworkBLASTAction as the menu item's action;
   */
   
  private void initializeMenuItem()
  {
    JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
    			        .getMenu("Plugins");

    JMenu nbMenu = new JMenu("NetworkBLAST");
    
    JMenuItem aboutMenuItem = new JMenuItem(new NetworkBLASTAction(
                                          "About", 0, m_dialog));
    JMenuItem comptMenuItem = new JMenuItem(new NetworkBLASTAction(
                                          "Generate Compatiblity Graph",
					  1, m_dialog));
    JMenuItem pathMenuItem = new JMenuItem(new NetworkBLASTAction(
                                          "Path Search",
					  2, m_dialog));
    JMenuItem compMenuItem = new JMenuItem(new NetworkBLASTAction(
                                          "Complex Search",
					  3, m_dialog));
    JMenuItem scoreMenuItem = new JMenuItem(new NetworkBLASTAction(
                                          "Score Model Settings",
					  4, m_dialog));
    nbMenu.add(aboutMenuItem);
    nbMenu.addSeparator();
    nbMenu.add(comptMenuItem);
    nbMenu.add(pathMenuItem);
    nbMenu.add(compMenuItem);
    nbMenu.addSeparator();
    nbMenu.add(scoreMenuItem);
    
    pluginMenu.add(nbMenu);
  }
}
