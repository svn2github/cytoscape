package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;
import giny.view.Bend;
import giny.view.EdgeView;

import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

public class TehAction extends CytoscapeAction
{

  public TehAction() { super("Teh Bug"); setPreferredMenu("Layout"); }

  public void actionPerformed(ActionEvent e)
  {
    CyNetworkView graphView = Cytoscape.getCurrentNetworkView();
    Iterator edgeIter = graphView.getEdgeViewsIterator();
    while (edgeIter.hasNext()) {
      EdgeView edge = (EdgeView) edgeIter.next();
      Bend bend = edge.getBend();
      List handles = bend.getHandles();
      for (int i = 0; i < handles.size(); i++) {
        Point2D point = (Point2D) handles.get(i);
        bend.moveHandle(i, new Point2D.Double(point.getX() + 40.0d,
                                              point.getY() + 40.0d)); } }
  }

}
