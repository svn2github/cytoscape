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

import cytoscape.CytoscapeWindow;
//-------------------------------------------------------------------------
public class ZoomSelectedAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public ZoomSelectedAction(CytoscapeWindow cytoscapeWindow)  {
        super();
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed(ActionEvent e) {
        Graph2D graph = cytoscapeWindow.getGraph();
        NodeCursor nc = graph.selectedNodes(); 
        if (nc.ok ()) { //selected nodes present? 
            Rectangle2D box = graph.getRealizer(nc.node()).getBoundingBox();
            for (nc.next(); nc.ok(); nc.next()) {
                graph.getRealizer(nc.node()).calcUnionRect(box);
            }
            Graph2DView graphView = cytoscapeWindow.getGraphView();
            graphView.zoomToArea(box.getX(),box.getY(),box.getWidth(),box.getHeight());
            if (graphView.getZoom() > 2.0) graphView.setZoom(2.0);
            cytoscapeWindow.redrawGraph(false, false);
        }
    }
}
