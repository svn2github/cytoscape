package org.cytoscape;

import java.util.EventObject;

/**
 * The event source must be the RootGraph that changed.
 */
public abstract class RootGraphChangeEvent
  extends EventObject {

  public RootGraphChangeEvent ( RootGraph source ) {
    super( source );
  }

  public static final int NODES_CREATED_TYPE = 1;
  public static final int EDGES_CREATED_TYPE = 2;
  public static final int NODES_REMOVED_TYPE = 4;
  public static final int EDGES_REMOVED_TYPE = 8;

  public abstract int getType ();
  public abstract boolean isNodesCreatedType ();
  public abstract boolean isEdgesCreatedType ();
  public abstract boolean isNodesRemovedType ();
  public abstract boolean isEdgesRemovedType ();

  public abstract CyNode[] getCreatedNodes  ();
  public abstract CyEdge[] getCreatedEdges  ();

  /**
   *   this method may have undefined state.
   * @see #getRemovedNodeIndices()
   */
  public abstract CyNode[] getRemovedNodes  ();

  /**
   *   this method may have undefined state.
   * @see #getRemovedEdgeIndices()
   */
  public abstract CyEdge[] getRemovedEdges  ();

  public abstract int[] getCreatedNodeIndices  ();
  public abstract int[] getCreatedEdgeIndices  ();
  public abstract int[] getRemovedNodeIndices  ();
  public abstract int[] getRemovedEdgeIndices  ();

} // abstract class RootGraphChangeEvent
