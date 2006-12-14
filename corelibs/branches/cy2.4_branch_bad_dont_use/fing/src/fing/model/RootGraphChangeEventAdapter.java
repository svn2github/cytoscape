package fing.model;

import giny.model.Edge;
import giny.model.RootGraphChangeEvent;
import giny.model.Node;
import giny.model.RootGraph;

abstract class RootGraphChangeEventAdapter extends RootGraphChangeEvent
{

  RootGraphChangeEventAdapter(RootGraph rootGraph)
  {
    super(rootGraph);
  }

  // This is the only abstract method on this class; whatever the type of
  // event, make sure to override the appropriate getXXX() methods - those
  // methods all return null in this implementation.
  public abstract int getType ();

  public final boolean isNodesCreatedType () {
    return (getType() & NODES_CREATED_TYPE) != 0; }
  public final boolean isEdgesCreatedType () {
    return (getType() & EDGES_CREATED_TYPE) != 0; }
  public final boolean isNodesRemovedType () {
    return (getType() & NODES_REMOVED_TYPE) != 0; }
  public final boolean isEdgesRemovedType () {
    return (getType() & EDGES_REMOVED_TYPE) != 0; }
  public final boolean isMetaRelationshipNodesCreatedType () {
    return (getType() & META_RELATIONSHIP_NODES_CREATED_TYPE) != 0; }
  public final boolean isMetaRelationshipEdgesCreatedType () {
    return (getType() & META_RELATIONSHIP_EDGES_CREATED_TYPE) != 0; }
  public final boolean isMetaRelationshipNodesRemovedType () {
    return (getType() & META_RELATIONSHIP_NODES_REMOVED_TYPE) != 0; }
  public final boolean isMetaRelationshipEdgesRemovedType () {
    return (getType() & META_RELATIONSHIP_EDGES_REMOVED_TYPE) != 0; }

  public Node[] getCreatedNodes() { return null; }
  public Edge[] getCreatedEdges() { return null; }
  public Node[] getRemovedNodes() { return null; }
  public Edge[] getRemovedEdges() { return null; }
  public Node[][] getMetaRelationshipCreatedNodes() { return null; }
  public Object[][] getMetaRelationshipCreatedEdges() { return null; }
  public Node[][] getMetaRelationshipRemovedNodes() { return null; }
  public Object[][] getMetaRelationshipRemovedEdges() { return null; }

  public int[] getCreatedNodeIndices() { return null; }
  public int[] getCreatedEdgeIndices() { return null; }
  public int[] getRemovedNodeIndices() { return null; }
  public int[] getRemovedEdgeIndices() { return null; }
  public int[][] getMetaRelationshipCreatedNodeIndices() { return null; }
  public int[][] getMetaRelationshipCreatedEdgeIndices() { return null; }
  public int[][] getMetaRelationshipRemovedNodeIndices() { return null; }
  public int[][] getMetaRelationshipRemovedEdgeIndices() { return null; }

}
