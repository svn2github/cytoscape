package cytoscape;

public interface Edge extends GraphObject {

  /**
   * @return the GraphNode corresponding to the source of this GraphEdge.
   */
  public Node getSource ();

  /**
   * @return the GraphNode corresponding to the target of this GraphEdge.
   */
  public Node getTarget ();

  /**
   * @return true if this Edge is a directed edge; false otherwise.
   */
  public boolean isDirected ();

  
} // interface Edge
