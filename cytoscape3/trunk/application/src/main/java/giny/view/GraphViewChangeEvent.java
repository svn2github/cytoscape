package giny.view;

import java.util.EventObject;
import org.cytoscape.*;

/**
 * The event source must be the GraphPerspective that changed.
 */
public abstract class GraphViewChangeEvent
  extends EventObject {

  public static final int NODES_RESTORED_TYPE = 1;
  public static final int EDGES_RESTORED_TYPE = 2;
  public static final int NODES_HIDDEN_TYPE = 4;
  public static final int EDGES_HIDDEN_TYPE = 8;
  public static final int NODES_SELECTED_TYPE = 16;
  public static final int NODES_UNSELECTED_TYPE = 32;
  public static final int EDGES_SELECTED_TYPE = 64;
  public static final int EDGES_UNSELECTED_TYPE = 128;

  public GraphViewChangeEvent ( GraphView source ) {
    super( source );
  }

  public abstract int getType ();
  public abstract boolean isNodesRestoredType ();
  public abstract boolean isEdgesRestoredType ();
  public abstract boolean isNodesHiddenType ();
  public abstract boolean isEdgesHiddenType ();
  public abstract boolean isNodesSelectedType ();
  public abstract boolean isNodesUnselectedType ();
  public abstract boolean isEdgesSelectedType ();
  public abstract boolean isEdgesUnselectedType ();

  public abstract Node[] getRestoredNodes ();
  public abstract Edge[] getRestoredEdges ();
  public abstract Node[] getHiddenNodes ();
  public abstract Edge[] getHiddenEdges ();
  public abstract Node[] getSelectedNodes ();
  public abstract Node[] getUnselectedNodes ();
  public abstract Edge[] getSelectedEdges ();
  public abstract Edge[] getUnselectedEdges ();

  public abstract int[] getRestoredNodeIndices ();
  public abstract int[] getRestoredEdgeIndices ();
  public abstract int[] getHiddenNodeIndices ();
  public abstract int[] getHiddenEdgeIndices ();
  public abstract int[] getSelectedNodeIndices ();
  public abstract int[] getUnselectedNodeIndices ();
  public abstract int[] getSelectedEdgeIndices ();
  public abstract int[] getUnselectedEdgeIndices ();

} // abstract class GraphPerspectiveChangeEvent
