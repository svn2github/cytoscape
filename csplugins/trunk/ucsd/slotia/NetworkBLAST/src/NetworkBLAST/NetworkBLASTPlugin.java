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
  public NetworkBLASTPlugin()
  {
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
    JMenuItem pluginItem = new JMenuItem(new NetworkBLASTAction());
    pluginMenu.add(pluginItem);
  }
}
