//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import giny.util.SpringEmbeddedLayouter;
import giny.util.GraphPartition;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.SwingWorker;
import cytoscape.layout.*;

import cytoscape.*;
import cytoscape.view.*;
import java.util.*;



public class SpringEmbeddedLayoutAction extends CytoscapeAction {
    
  public SpringEmbeddedLayoutAction () {
    super("Apply Spring Embedded Layout");
    setPreferredMenu( "Layout" );
  }
    
  public void actionPerformed ( ActionEvent e ) {
    //SpringEmbeddedLayouter lay = new SpringEmbeddedLayouter( Cytoscape.getCurrentNetworkView() );
    // lay.doLayout();

   
    


    final SwingWorker worker = new SwingWorker(){
        public Object construct(){
          LayoutAlgorithm layout = new ISOMLayout( Cytoscape.getCurrentNetworkView() );
          Cytoscape.getCurrentNetworkView().applyLayout( layout );
          return null;
        }
      };
    worker.start();

    
    CyNetwork network = Cytoscape.getCurrentNetwork();
    CyNetworkView network_view = Cytoscape.getCurrentNetworkView();
    
    List partions = GraphPartition.partition( network );
    Iterator i = partions.iterator();
    float r, g, b;
    while ( i.hasNext() ) {
      int[] array = ( int[] )i.next();
      r = ( float )Math.random();
      g = ( float )Math.random();
      b = ( float )Math.random();
      for ( int j = 0; j < array.length; ++j ) {
        //network_view.getNodeView( network.getEdgeTargetIndex( array[j] ) ).select();
        //network_view.getNodeView( network.getEdgeSourceIndex( array[j] ) ).select();
        
        // network_view.getNodeView( array[j] ).select();
        
        network_view.getNodeView( array[j] ).setUnselectedPaint( new java.awt.Color( r, g, b ) );
        network_view.getNodeView( array[j] ).setUnselectedPaint( new java.awt.Color( r, g, b ) );
      }
    }
        
        

  }
}

