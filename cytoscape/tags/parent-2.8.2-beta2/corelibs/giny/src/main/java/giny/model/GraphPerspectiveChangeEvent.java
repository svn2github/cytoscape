package giny.model;

import java.util.EventObject;

/**
 * The event source must be the GraphPerspective that changed.
 */
public abstract class GraphPerspectiveChangeEvent
  extends EventObject {

  public static final int NODES_RESTORED_TYPE = 1;
  public static final int EDGES_RESTORED_TYPE = 2;
  public static final int NODES_HIDDEN_TYPE = 4;
  public static final int EDGES_HIDDEN_TYPE = 8;
  public static final int NODES_SELECTED_TYPE = 16;
  public static final int NODES_UNSELECTED_TYPE = 32;
  public static final int EDGES_SELECTED_TYPE = 64;
  public static final int EDGES_UNSELECTED_TYPE = 128;

  /**
   * The source parameter should be either a RootGraph or a GraphPerspective;
   * see specification of getSource().
   * @see #getSource()
   */
  public GraphPerspectiveChangeEvent ( Object source ) {
    super( source );
  }

  /**
   * The Object returned is one of two types - either a GraphPerspective or
   * a RootGraph.  The return value is a RootGraph if this event is a
   * NODES_HIDDEN_TYPE or EDGES_HIDDEN_TYPE which resulted from Nodes or
   * Edges being removed from the underlying RootGraph of a GraphPerspective;
   * the return value is a GraphPerspective in all other cases.
   */
  public final Object getSource() {
    return super.getSource(); }

  public abstract int getType ();
  public abstract boolean isNodesRestoredType ();
  public abstract boolean isEdgesRestoredType ();
  public abstract boolean isNodesHiddenType ();
  public abstract boolean isEdgesHiddenType ();

  /**
   * @deprecated Nowhere in GraphPerspective is there functionality that
   *   specifies node selection logic.
   */
  public abstract boolean isNodesSelectedType ();

  /**
   * @deprecated Nowhere in GraphPerspective is there functionality that
   *   specifies node un-selection logic.
   */
  public abstract boolean isNodesUnselectedType ();

  /**
   * @deprecated Nowhere in GraphPerspective is there functionality that
   *   specifies edge selection logic.
   */
  public abstract boolean isEdgesSelectedType ();

  /**
   * @deprecated Nowhere in GraphPerspective is there functionality that
   *   specifies edge un-selection logic.
   */
  public abstract boolean isEdgesUnselectedType ();

  public abstract Node[] getRestoredNodes ();
  public abstract Edge[] getRestoredEdges ();

  /**
   * @deprecated Use getHiddenNodeIndices() instead; the nodes returned by
   *   this method may have undefined state.
   * @see #getHiddenNodeIndices()
   */
  public abstract Node[] getHiddenNodes ();

  /**
   * @deprecated Use getHiddenEdgeIndices() instead; the edges returned by
   *   this method may have undefined state.
   * @see #getHiddenEdgeIndices()
   */
  public abstract Edge[] getHiddenEdges ();

  /**
   * @deprecated Nowhere in GraphPerspective is there functionality that
   *   specifies node selection logic.
   */
  public abstract Node[] getSelectedNodes ();

  /**
   * @deprecated Nowhere in GraphPerspective is there functionality that
   *   specifies node un-selection logic.
   */
  public abstract Node[] getUnselectedNodes ();

  /**
   * @deprecated Nowhere in GraphPerspective is there functionality that
   *   specifies edge selection logic.
   */
  public abstract Edge[] getSelectedEdges ();

  /**
   * @deprecated Nowhere in GraphPerspective is there functionality that
   *   specifies edge un-selection logic.
   */
  public abstract Edge[] getUnselectedEdges ();

  /** The integer values in the returned array are RootGraph indices. */
  public abstract int[] getRestoredNodeIndices ();

  /** The integer values in the returned array are RootGraph indices. */
  public abstract int[] getRestoredEdgeIndices ();

  /** The integer values in the returned array are RootGraph indices. */
  public abstract int[] getHiddenNodeIndices ();

  /** The integer values in the returned array are RootGraph indices. */
  public abstract int[] getHiddenEdgeIndices ();

  /**
   * @deprecated Nowhere in GraphPerspective is there functionality that
   *   specifies node selection logic.
   */
  public abstract int[] getSelectedNodeIndices ();

  /**
   * @deprecated Nowhere in GraphPerspective is there functionality that
   *   specifies node un-selection logic.
   */
  public abstract int[] getUnselectedNodeIndices ();

  /**
   * @deprecated Nowhere in GraphPerspective is there functionality that
   *   specifies edge selection logic.
   */
  public abstract int[] getSelectedEdgeIndices ();

  /**
   * @deprecated Nowhere in GraphPerspective is there functionality that
   *   specifies edge un-selection logic.
   */
  public abstract int[] getUnselectedEdgeIndices ();

} // abstract class GraphPerspectiveChangeEvent
