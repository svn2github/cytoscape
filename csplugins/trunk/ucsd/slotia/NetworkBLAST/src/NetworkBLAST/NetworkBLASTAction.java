package NetworkBLAST;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;

import java.awt.event.ActionEvent;


/**
 * Creates and sets up the NetworkBLAST interface.
 */

public class NetworkBLASTAction extends AbstractAction
{
  private NetworkBLASTDialog m_dialog;
  private int m_tabIndex;
  
  public NetworkBLASTAction(String _name, int _tabIndex,
                            NetworkBLASTDialog _dialog)
  {
    super(_name);
    m_dialog = _dialog;
    m_tabIndex = _tabIndex;
  }

  public void actionPerformed(ActionEvent _e)
  {
    if (!this.checkForNetworks()) return;
    
    this.updateComboBoxes();
    m_dialog.switchToTab(m_tabIndex);
    m_dialog.show();
  }

  /**
   * Checks to make sure there is at least one network loaded in
   * Cytoscape. If there are no available networks, this method will
   * display an error message and return false. Otherwise it returns true.
   */

  private boolean checkForNetworks()
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

    return true;
  }

  /**
   * This method updates the combo boxes in the dialog so that
   * each combo box has a list of all the loaded networks in Cytoscape.
   * It uses the NetworkItem private class defined below for each
   * item in the combo boxes.
   */

  private void updateComboBoxes()
  {
    JComboBox jComboBoxes[] =
    {
      m_dialog.getComp_graphComboBox(),
      m_dialog.getCompt_graph1ComboBox(),
      m_dialog.getCompt_graph2ComboBox(),
      m_dialog.getCompt_homgraphComboBox(),
      m_dialog.getPath_graphComboBox()
    };

    for (int j = 0; j < jComboBoxes.length; j++)
    {
      jComboBoxes[j].removeAllItems();
    }

    Object networks[] = Cytoscape.getNetworkSet().toArray();
    for (int i = 0; i < networks.length; i++)
    {
      CyNetwork network = (CyNetwork) networks[i];
      NetworkItem item = new NetworkItem(network.getTitle(), network);
      
      for (int j = 0; j < jComboBoxes.length; j++)
      {
        jComboBoxes[j].addItem(item);
      }
    }
  }

  /**
   * Used for items in the combo boxes in the NetworkBLASTDialog.
   */
  private class NetworkItem
  {
    private String m_name;
    private CyNetwork m_network;

    public NetworkItem(String _name, CyNetwork _network)
    {
      m_name = _name;
      m_network = _network;
    }

    public String getName()
    {
      return m_name;
    }

    public CyNetwork getNetwork()
    {
      return m_network;
    }
	    
    public String toString()
    {
      return getName();
    }
  }
}
