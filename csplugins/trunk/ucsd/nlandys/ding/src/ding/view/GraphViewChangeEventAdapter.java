package ding.view;

import giny.model.Edge;
import giny.model.Node;
import giny.view.GraphView;

import giny.view.GraphViewChangeEvent;

abstract class GraphViewChangeEventAdapter extends GraphViewChangeEvent
{

  GraphViewChangeEventAdapter(GraphView source)
  {
    super(source);
  }

  public abstract int getType();

  public final boolean isNodesRestoredType() {
    return (getType() & NODES_RESTORED_TYPE) != 0; }
  public final boolean isEdgesRestoredType() {
    return (getType() & EDGES_RESTORED_TYPE) != 0; }
  public final boolean isNodesHiddenType() {
    return (getType() & NODES_HIDDEN_TYPE) != 0; }
  public final boolean isEdgesHiddenType() {
    return (getType() & EDGES_HIDDEN_TYPE) != 0; }
  public final boolean isNodesSelectedType() {
    return (getType() & NODES_SELECTED_TYPE) != 0; }
  public final boolean isNodesUnselectedType() {
    return (getType() & NODES_UNSELECTED_TYPE) != 0; }
  public final boolean isEdgesSelectedType() {
    return (getType() & EDGES_SELECTED_TYPE) != 0; }
  public final boolean isEdgesUnselectedType() {
    return (getType() & EDGES_UNSELECTED_TYPE) != 0; }

  public Node[] getRestoredNodes() { return null; }
  public Edge[] getRestoredEdges() { return null; }
  public Node[] getHiddenNodes() { return null; }
  public Edge[] getHiddenEdges() { return null; }
  public Node[] getSelectedNodes() { return null; }
  public Node[] getUnselectedNodes() { return null; }
  public Edge[] getSelectedEdges() { return null; }
  public Edge[] getUnselectedEdges() { return null; }

  public int[] getRestoredNodeIndices() { return null; }
  public int[] getRestoredEdgeIndices() { return null; }
  public int[] getHiddenNodeIndices() { return null; }
  public int[] getHiddenEdgeIndices() { return null; }
  public int[] getSelectedNodeIndices() { return null; }
  public int[] getUnselectedNodeIndices() { return null; }
  public int[] getSelectedEdgeIndices() { return null; }
  public int[] getUnselectedEdgeIndices() { return null; }

}
