package foo;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyNetworkView;

import giny.model.Node;
import giny.view.Bend;
import giny.view.EdgeView;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

public class MolenaarPlugin extends CytoscapePlugin
{

  public MolenaarPlugin()
  {
    JMenuItem molenaar = new JMenuItem
      (new AbstractAction("Molenaar is everywhere")
      {
        public void actionPerformed(ActionEvent e)
        {
          final CyNetworkView gView = Cytoscape.getCurrentNetworkView();
          final Iterator eViews = gView.getEdgeViewsIterator();
          while (eViews.hasNext()) {
            EdgeView eView = (EdgeView) eViews.next();
            eView.clearBends();
            Bend bend = eView.getBend();
            Node source = eView.getEdge().getSource();
            Node target = eView.getEdge().getTarget();
            double sX = gView.getNodeView(source).getXPosition();
            double sY = gView.getNodeView(source).getYPosition();
            double tX = gView.getNodeView(target).getXPosition();
            double tY = gView.getNodeView(target).getYPosition();
            double aX = 0.5 * (tX + sX);
            double aY = 0.5 * (tY + sY);
            bend.addHandle(new java.awt.geom.Point2D.Double(aX, aY)); }
          gView.updateView();
        }
      });
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Layout").add
      (molenaar);
  }

}
