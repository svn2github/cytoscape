package NetworkBLAST.comboBoxes;

import javax.swing.JComboBox;
import java.util.Arrays;
import java.util.Set;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;

public class NetworkComboBox extends JComboBox
{
  public void setup()
  {
    this.removeAllItems();

    Set networks = Cytoscape.getNetworkSet();
    
    NetworkComboBoxItem[] items = new NetworkComboBoxItem[networks.size()];
    int i = 0;
    
    for (Object networkObj : networks)
    {
      CyNetwork network = (CyNetwork) networkObj;
      items[i++] = new NetworkComboBoxItem(network.getTitle(), network);
    }

    Arrays.sort(items);

    for (i = 0; i < items.length; i++)
      this.addItem(items[i]);
  }

  public CyNetwork getSelectedNetwork()
  {
    if (this.getSelectedItem() == null) return null;

    return ((NetworkComboBoxItem) this.getSelectedItem()).getNetwork();
  }

  private class NetworkComboBoxItem implements Comparable<NetworkComboBoxItem>
  {
    public NetworkComboBoxItem(String _networkName, CyNetwork _network)
    {
      this.networkName = _networkName;
      this.network = _network;
    }

    public String toString()
      { return networkName; }

    public CyNetwork getNetwork()
      { return network; }

    public int compareTo(NetworkComboBoxItem _other)
      { return this.toString().compareTo(_other.toString()); }

    private String networkName;
    private CyNetwork network;
  }
}
