package giny.model;

import java.util.*;

public interface Node extends GraphObject {
 
  /**
   * If a Node is a meta-parent of any other nodes and edges,
   * then it contains those nodes and edges 
   * in a <code>GraphPerspective</code>.  This method returns such
   * a GraphPerspective.
   * @deprecated Don't use this method because the behavior of the returned
   *   GraphPerspective with respect to changing meta-children of this Node
   *   is ill-defined; use RootGraph.getNodeMetaChildIndicesArray(int) and
   *   RootGraph.getEdgeMetaChildIndicesArray(int) instead.
   * @see RootGraph#getNodeMetaChildIndicesArray(int)
   * @see RootGraph#getEdgeMetaChildIndicesArray(int)
   */
  public GraphPerspective getGraphPerspective ();

  /**
   * This method adds all Nodes and Edges contained in the specified
   * GraphPerspective as children of this Node.
   * The return value of this method is undefined and should not be used.
   * @deprecated Don't use this method because the behavior of the
   *   input GraphPerspective with respect to changing meta-children of
   *   this Node is ill-defined; use RootGraph.addNodeMetaChild(int, int) and
   *   RootGraph.addEdgeMetaChild(int, int) instead.
   * @see RootGraph#addNodeMetaChild(int, int)
   * @see RootGraph#addEdgeMetaChild(int, int)
   */
  public boolean setGraphPerspective ( GraphPerspective gp );

  /**
    * Assign a graph perspective reference to this node.
    */
  public void setNestedNetwork(final GraphPerspective graphPerspective);
		
  /**
    * Return the currently set graph perspective (may be null) associated with this node.
    *
    *  @return a network reference or null.
    */
  public GraphPerspective getNestedNetwork();
} // interface Node
