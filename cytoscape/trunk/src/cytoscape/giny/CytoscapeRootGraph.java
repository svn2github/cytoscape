package cytoscape.giny;

import giny.model.*;
import cytoscape.*;
import coltginy.*;
import cern.colt.map.*;

public interface CytoscapeRootGraph extends RootGraph {

  public void deleteNode ( giny.model.Node node ) ;

  public void deleteEdge ( giny.model.Edge edge ) ;


  /**
   * A Node can be replaced by another Node so long as the New node is_a CyNode
   */
  public CyNode replaceNode ( int index, CyNode new_node ) ;


 


  public int createNode ( GraphPerspective perspective ) ; // createNode( GraphPerspective )


  
  public int createNode ( CyNetwork network ) ;

  /**
   * Uses Code copied from ColtRootGraph to create a new CyNetwork.
   */
  public CyNetwork createNetwork ( giny.model.Node[] nodes, giny.model.Edge[] edges ) ;

  /**
   * Uses Code copied from ColtRootGraph to create a new Network.
   */
  public CyNetwork createNetwork ( int[] node_indices, int[] edge_indices ) ;

 

}

