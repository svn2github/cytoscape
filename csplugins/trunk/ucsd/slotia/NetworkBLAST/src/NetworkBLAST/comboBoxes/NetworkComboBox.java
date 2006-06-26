package NetworkBLAST.comboBoxes;

import javax.swing.JComboBox;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;

public class NetworkComboBox extends JComboBox
{
  public void setup()
  {
    this.removeAllItems();

    Object [] networks = Cytoscape.getNetworkSet().toArray();

    for (Object network : networks)
    {
      CyNetwork cyNetwork = (CyNetwork) network;
      this.addItem(new NetworkComboBoxItem(cyNetwork.getTitle(), cyNetwork));
    }  
  }

  public CyNetwork getSelectedNetwork()
  {
    if (this.getSelectedItem() == null) return null;

    return ((NetworkComboBoxItem) this.getSelectedItem()).getNetwork();
  }

  private class NetworkComboBoxItem
  {
    public NetworkComboBoxItem(String _networkName, CyNetwork _network)
    {
      this.networkName = _networkName;
      this.network = _network;
    }

    public String toString()
    {
      return networkName;
    }

    public CyNetwork getNetwork()
    {
      return network;
    }

    private String networkName;
    private CyNetwork network;
  }
}
