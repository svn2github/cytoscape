package cytoscape.giny;

import giny.model.*;
import cytoscape.*;
import coltginy.*;
import cern.colt.map.*;
import java.util.Collection;

public interface CytoscapeRootGraph extends RootGraph {
  
  //public int createNode ( CyNetwork network ) ;

  /**
   * Uses Code copied from ColtRootGraph to create a new CyNetwork.
   */
  public CyNetwork createNetwork ( giny.model.Node[] nodes, giny.model.Edge[] edges ) ;

  public CyNetwork createNetwork ( Collection nodes, Collection edges ) ;

  /**
   * Uses Code copied from ColtRootGraph to create a new Network.
   */
  public CyNetwork createNetwork ( int[] node_indices, int[] edge_indices ) ;

 
  public cytoscape.CyNode getNode ( String identifier );

  public cytoscape.CyEdge getEdge ( String identifier );

  public void setNodeIdentifier ( String identifier, int index );

  public void setEdgeIdentifier ( String identifier, int index );

}
