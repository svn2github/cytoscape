package csplugins.layout;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyNetworkView;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

public class SouravPlugin extends CytoscapePlugin
{

  public SouravPlugin()
  {
    JMenuItem sourav = new JMenuItem(new AbstractAction("Sourav's worries") {
        public void actionPerformed(ActionEvent e) {
        } });
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Layout").add
      (sourav);
  }

}
