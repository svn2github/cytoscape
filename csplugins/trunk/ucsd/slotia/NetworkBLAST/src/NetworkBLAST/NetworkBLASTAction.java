package NetworkBLAST;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import java.awt.event.ActionEvent;


/**
 * Creates and sets up the NetworkBLAST interface.
 */

public class NetworkBLASTAction extends AbstractAction
{
  public NetworkBLASTAction(String _name, int _tabIndex,
                            NetworkBLASTDialog _dialog)
  {
    super(_name);

    dialog = _dialog;
    tabIndex = _tabIndex;
  }

  public void actionPerformed(ActionEvent _e)
  {
    // Check to make sure we have at least one network loaded.
    // If we don't, exit.
    if (!availableNetworks()) return;
    
    dialog.setup();
    dialog.switchToTab(tabIndex);    
    dialog.setVisible(true);
  }

  /**
   * Checks to make sure there is at least one network loaded in
   * Cytoscape. If there are no available networks, this method will
   * display an error message and return false. Otherwise it returns true.
   */

  private boolean availableNetworks()
  {
    if (Cytoscape.getNetworkSet().size() == 0)
    {
      JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
        "NetworkBLAST requires networks to be loaded.\n\n" +
        "In the File menu choose the Import submenu\n" +
	"and select \"Network...\" to load a network.",
        "NetworkBLAST: No Networks Loaded", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    else
    {
      return true;
    }
  }
  
  private NetworkBLASTDialog dialog;
  private int tabIndex;
}
