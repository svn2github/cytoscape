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
    dialog.switchToTab(tabIndex);    
    dialog.setVisible(true);
  }
  
  private NetworkBLASTDialog dialog;
  private int tabIndex;
}
