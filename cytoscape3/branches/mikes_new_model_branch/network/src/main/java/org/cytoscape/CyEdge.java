package org.cytoscape;

public interface CyEdge extends GraphObject {

  /**
   * @return the GraphNode corresponding to the source of this GraphEdge.
   */
  public CyNode getSource ();

  /**
   * @return the GraphNode corresponding to the target of this GraphEdge.
   */
  public CyNode getTarget ();

  /**
   * @return true if this Edge is a directed edge; false otherwise.
   */
  public boolean isDirected ();

  
} // interface Edge
