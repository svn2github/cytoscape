package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;
import giny.view.NodeView;

import java.awt.event.ActionEvent;
import java.util.Iterator;

public class BazAction extends CytoscapeAction
{

  public BazAction() { super("Baz Bug"); setPreferredMenu("Layout"); }

  public void actionPerformed(ActionEvent e)
  {
    CyNetworkView graphView = Cytoscape.getCurrentNetworkView();
    Iterator nodeIter = graphView.getNodeViewsIterator();
    while (nodeIter.hasNext()) {
      NodeView node = (NodeView) nodeIter.next();
      node.setXPosition(0.0d);
      node.setYPosition(0.0d); }
  }

}
