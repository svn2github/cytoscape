//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.awt.geom.Rectangle2D;

import y.base.NodeCursor;
import y.view.Graph2D;
import y.view.Graph2DView;

import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class ZoomSelectedAction extends AbstractAction {
    NetworkView networkView;
    
    public ZoomSelectedAction(NetworkView networkView)  {
        super();
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e) {
        String callerID = "ZoomSelectedAction.actionPerformed";
        networkView.getNetwork().beginActivity(callerID);
        Graph2D graph = networkView.getNetwork().getGraph();
        NodeCursor nc = graph.selectedNodes(); 
        if (nc.ok()) { //selected nodes present? 
            Rectangle2D box = graph.getRealizer(nc.node()).getBoundingBox();
            for (nc.next(); nc.ok(); nc.next()) {
                graph.getRealizer(nc.node()).calcUnionRect(box);
            }
            Graph2DView graphView = networkView.getGraphView();
            graphView.zoomToArea(box.getX(),box.getY(),box.getWidth(),box.getHeight());
            if (graphView.getZoom() > 2.0) graphView.setZoom(2.0);
            networkView.redrawGraph(false, false);
        }
        networkView.getNetwork().endActivity(callerID);
    }
}
