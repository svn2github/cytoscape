//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.util.PBounds;
import giny.view.*;
import cytoscape.giny.PhoebeNetworkView;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import phoebe.PGraphView;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;
//-------------------------------------------------------------------------
public class FitContentAction extends CytoscapeAction {
      
    public FitContentAction () {
        super();
    }
    
    public void actionPerformed(ActionEvent e) {
      //we have to do it this way because 
      //networkView.getView().fitContent();
        //currently appears to do nothing -AM 12-17-2003
      PGraphView view =(PGraphView) Cytoscape.getCurrentNetworkView();
      
      // AJK: 09/10/05 BEGIN
      //     correct for case in CytoscapeEditor where we start with a blank drawing space, causing a zoom
      //     error because the pre-set bounds may be larger than the minimum enclosing rectangle of the
      //     nodes in the view 
      //     for performance reasons, don't do this correction for large networks, where calculating minumum 
      //     enclosing rectangle can be expensive.
//      view.getCanvas().getCamera().animateViewToCenterBounds( view.getCanvas().getLayer().getFullBounds(), true, 50l );
      CyNetwork net = Cytoscape.getCurrentNetwork();
      if ((net.getNodeCount() > 0) && (net.getNodeCount() < 200))
      {
      	zoomToMinimumEnclosingRectangle(view);
      }
      else
      {
      	view.getCanvas().getCamera().animateViewToCenterBounds( view.getCanvas().getLayer().getFullBounds(), true, 50l );
      }
      // AJK: 09/10/05 END
   }
    
   // AJK: 09/10/05 BEGIN
    public void zoomToMinimumEnclosingRectangle (PGraphView view) {
  
          Iterator nodes_iterator = view.getNodeViewsIterator();
          double bigX;
          double bigY;
          double smallX;
          double smallY;
          double W;
          double H;
          
          NodeView first = ( NodeView )nodes_iterator.next();
          // work with corner, rather than center positions 
          bigX = first.getXPosition() - (first.getWidth() / 2.0);
          smallX = bigX;
          bigY = first.getYPosition() - (first.getHeight() / 2.0);
          smallY = bigY;
          int nbrNodes = 1;
          W = first.getWidth();
          H = first.getHeight();
      
          while ( nodes_iterator.hasNext() ) {
            NodeView nv = ( NodeView )nodes_iterator.next();
            double x = nv.getXPosition() - (nv.getWidth() / 2.0);
            double y = nv.getYPosition() - (nv.getHeight() / 2.0);
            nbrNodes++;

            if ( x > bigX ) {
              bigX = x;
              W = nv.getWidth();
            } else if ( x < smallX ) {
              smallX = x;
            }

            if ( y > bigY ) {
              bigY = y;
              H = nv.getHeight();
            } else if ( y < smallY ) {
              smallY = y;
            }
          }
          

          
          PBounds zoomToBounds;
          if (nbrNodes == 1) {
            zoomToBounds = new PBounds( smallX - 100 , smallY - 100 , ( bigX - smallX + 200 ), ( bigY - smallY + 200 ) );
          } else {
//            zoomToBounds = new PBounds( smallX  , smallY  , ( bigX - smallX + 100 ), ( bigY - smallY + 100 ) );
          	zoomToBounds = new PBounds( smallX  , smallY  , ( bigX - smallX + W), ( bigY - smallY + H) );
        	  
          }
          PTransformActivity activity =  ( ( PhoebeNetworkView )view).getCanvas().getCamera().animateViewToCenterBounds( zoomToBounds, true, 500 );
      }
    
    // AJK: 09/10/05 END
    
}

