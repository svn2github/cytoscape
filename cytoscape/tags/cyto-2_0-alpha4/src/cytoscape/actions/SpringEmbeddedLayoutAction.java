//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;


import giny.util.GraphPartition;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.SwingWorker;
import cytoscape.util.IndeterminateProgressBar;
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
   
    
    final SwingWorker worker = new SwingWorker(){
        public Object construct(){
          LayoutAlgorithm layout = new SpringEmbeddedLayouter( Cytoscape.getCurrentNetworkView() );
          //LayoutAlgorithm layout = new ISOMLayout( Cytoscape.getCurrentNetworkView() );
          Cytoscape.getCurrentNetworkView().applyLayout( layout );
          return null;
        }
      };
    worker.start();

  }
}

