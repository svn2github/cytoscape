package cytoscape.giny;

import giny.model.*;
import cytoscape.*;

public abstract class Edge implements giny.model.Edge {

  /**
   * The Index of this Node in its RootGraph
   */
  protected int rootGraphIndex;
  
  /**
   * The RootGraph the we belong to
   */
  protected CytoscapeRootGraph rootGraph;

  /**
   * The Identifier for this Edge
   */
  protected String identifier;

  public RootGraph getRootGraph () {
    return rootGraph;
  }
           
  public int getRootGraphIndex () {
    return rootGraphIndex;
  }

  public boolean isDirected () {
    return rootGraph.isEdgeDirected( rootGraphIndex );
  }

  public giny.model.Node getSource () {
    return rootGraph.getNode( rootGraph.getEdgeSourceIndex( rootGraphIndex ) );
  }
           
  public giny.model.Node getTarget () {
    return rootGraph.getNode( rootGraph.getEdgeTargetIndex( rootGraphIndex ) );
  }
  
 


}
