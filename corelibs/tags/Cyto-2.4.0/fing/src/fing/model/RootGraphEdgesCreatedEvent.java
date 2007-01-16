package fing.model;

import giny.model.Edge;
import giny.model.RootGraph;

// This class is not currently being used.  Thus its constructor is private.
final class RootGraphEdgesCreatedEvent extends RootGraphChangeEventAdapter
{

  private final int[] m_createdEdgeInx;

  // Note that no copy of the array createdEdgeInx is made - the exact
  // array reference is kept.  Methods on this class return this same
  // array reference.
  private RootGraphEdgesCreatedEvent(RootGraph rootGraph, int[] createdEdgeInx)
  {
    super(rootGraph);
    m_createdEdgeInx = createdEdgeInx;
  }

  public final int getType()
  {
    return EDGES_CREATED_TYPE;
  }

  // This method throws an exception, which is fine, because this system of
  // listeners and events is only used internally by this package.  Nothing
  // in this package calls this method.
  public final Edge[] getCreatedEdges()
  {
    throw new UnsupportedOperationException("don't call this method!");
  }

  public final int[] getCreatedEdgeIndices()
  {
    return m_createdEdgeInx;
  }

}
