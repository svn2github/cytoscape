package cytoscape;

import giny.model.*;
import cytoscape.giny.Edge;
import cytoscape.giny.CytoscapeRootGraph;

public class CyEdge extends Edge {

  public CyEdge ( int root_graph_index, RootGraph root_graph ) {
    this.rootGraphIndex = root_graph_index;
    this.rootGraph = ( CytoscapeRootGraph )root_graph;    
  }

  public String getIdentifier () {
    if ( identifier == null ) {
      return ( new Integer( rootGraphIndex )).toString();
    } 
    return identifier;
  }

  public boolean setIdentifier ( String new_id ) {
    identifier = new_id;
    return true;
  }
  
  public String toString () {
    return getIdentifier();
  }

  public int getUniqueIdentifier () {
    return rootGraphIndex;
  }

  public boolean isDirected () {
    return rootGraph.isEdgeDirected( rootGraphIndex );
  }

  public CyNode getSourceNode () {
    return ( CyNode )rootGraph.getNode( rootGraph.getEdgeSourceIndex( rootGraphIndex ) );
  }
           
  public CyNode getTargetNode () {
    return ( CyNode )rootGraph.getNode( rootGraph.getEdgeTargetIndex( rootGraphIndex ) );
  }
  
 

}
