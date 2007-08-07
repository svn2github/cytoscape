package cytoscape.giny;

import giny.model.*;
import cytoscape.*;

public abstract class Node implements giny.model.Node {

  
  /**
   * The CytoscapeRootGraph the we belong to
   */
  protected CytoscapeRootGraph rootGraph;

   /**
   * The Index of this Node in its RootGraph
   */
  protected int rootGraphIndex;
  

  /**
   * The Identifier for this Node
   */
  protected String identifier;

  /**
   * The Network that consists of our MetaChildren
   */
  protected CyNetwork network;

  
 
  public RootGraph getRootGraph () {
    return rootGraph;
  }

  public GraphPerspective getGraphPerspective () {
    if ( network != null ) {
      network.restoreNodes( rootGraph.getNodeMetaChildIndicesArray( rootGraphIndex ) );
      network.restoreEdges( rootGraph.getEdgeMetaChildIndicesArray( rootGraphIndex ) );
    } else {
      network =  ( CyNetwork )rootGraph.createNetwork( rootGraph.getNodeMetaChildIndicesArray( rootGraphIndex ), rootGraph.getEdgeMetaChildIndicesArray( rootGraphIndex ) );
    }
    return ( GraphPerspective )network;
  }

   public int getRootGraphIndex () {
    return rootGraphIndex;
  }
     

  public boolean setGraphPerspective ( GraphPerspective gp ) {
    this.network = ( CyNetwork )gp;
    int[] nodes =  network.getNodeIndicesArray();
    int[] edges =  network.getEdgeIndicesArray();
    for ( int i = 0; i < nodes.length; ++i ) {
      rootGraph.addNodeMetaChild( rootGraphIndex, nodes[i] );
    }

    for ( int i = 0; i < edges.length; ++i ) {
      rootGraph.addEdgeMetaChild( rootGraphIndex, edges[i] );
    }
    
    return true;
  }



}
