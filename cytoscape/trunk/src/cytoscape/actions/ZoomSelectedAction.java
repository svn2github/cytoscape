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

import giny.view.*;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import java.util.List;
import java.util.Iterator;
import phoebe.PGraphView;

import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class ZoomSelectedAction extends AbstractAction {
    NetworkView networkView;
    
    public ZoomSelectedAction(NetworkView networkView)  {
        super();
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e) {
      // Y-Files check
      if ( networkView.getCytoscapeObj().getConfiguration().isYFiles() ) {
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
      } else {
        // GINY
        GraphView view = networkView.getView();
        List selected_nodes = view.getSelectedNodes();

        if ( selected_nodes.size() == 0 ) 
          return;

        Iterator selected_nodes_iterator = selected_nodes.iterator();
        double bigX;
        double bigY;
        double smallX;
        double smallY;
        double W;
        double H;
        bigX = ( ( NodeView )selected_nodes_iterator.next() ).getXPosition();
        smallX = bigX;
        bigY = ( ( NodeView )selected_nodes_iterator.next() ).getYPosition();
        smallY = bigY;
    
        while ( selected_nodes_iterator.hasNext() ) {
          NodeView nv = ( NodeView )selected_nodes_iterator.next();
          double x = nv.getXPosition();
          double y = nv.getYPosition();

          if ( x > bigX ) {
            bigX = x;
          } else if ( x < smallX ) {
            smallX = x;
          }

          if ( y > bigY ) {
            bigY = y;
          } else if ( y < smallY ) {
            smallY = y;
          }
        }
        
        PBounds zoomToBounds = new PBounds( smallX, smallY, ( bigX - smallX + 50 ), ( bigY - smallY + 50 ) );
        PTransformActivity activity =  ( ( PGraphView )view).getCanvas().getCamera().animateViewToCenterBounds( zoomToBounds, true, 500 );

      }

    }
}
