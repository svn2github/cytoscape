package giny.model;

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
  public static final int META_RELATIONSHIP_NODES_CREATED_TYPE = 16;
  public static final int META_RELATIONSHIP_EDGES_CREATED_TYPE = 32;
  public static final int META_RELATIONSHIP_NODES_REMOVED_TYPE = 64;
  public static final int META_RELATIONSHIP_EDGES_REMOVED_TYPE = 128;

  public abstract int getType ();
  public abstract boolean isNodesCreatedType ();
  public abstract boolean isEdgesCreatedType ();
  public abstract boolean isNodesRemovedType ();
  public abstract boolean isEdgesRemovedType ();
  public abstract boolean isMetaRelationshipNodesCreatedType ();
  public abstract boolean isMetaRelationshipEdgesCreatedType ();
  public abstract boolean isMetaRelationshipNodesRemovedType ();
  public abstract boolean isMetaRelationshipEdgesRemovedType ();

  public abstract Node[] getCreatedNodes  ();
  public abstract Edge[] getCreatedEdges  ();

  /**
   * @deprecated Use getRemovedNodeIndices() instead; the nodes returned by
   *   this method may have undefined state.
   * @see #getRemovedNodeIndices()
   */
  public abstract Node[] getRemovedNodes  ();

  /**
   * @deprecated Use getRemovedEdgeIndices() instead; the edges returned by
   *   this method may have undefined state.
   * @see #getRemovedEdgeIndices()
   */
  public abstract Edge[] getRemovedEdges  ();

  public abstract Node[][] getMetaRelationshipCreatedNodes  ();
  public abstract Object[][] getMetaRelationshipCreatedEdges  ();
  public abstract Node[][] getMetaRelationshipRemovedNodes  ();
  public abstract Object[][] getMetaRelationshipRemovedEdges  ();

  public abstract int[] getCreatedNodeIndices  ();
  public abstract int[] getCreatedEdgeIndices  ();
  public abstract int[] getRemovedNodeIndices  ();
  public abstract int[] getRemovedEdgeIndices  ();
  public abstract int[][] getMetaRelationshipCreatedNodeIndices  ();
  public abstract int[][] getMetaRelationshipCreatedEdgeIndices  ();
  public abstract int[][] getMetaRelationshipRemovedNodeIndices  ();
  public abstract int[][] getMetaRelationshipRemovedEdgeIndices  ();

} // abstract class RootGraphChangeEvent
