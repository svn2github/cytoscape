package fing.model;

import giny.model.Edge;
import giny.model.RootGraph;

final class RootGraphEdgesRemovedEvent extends RootGraphChangeEventAdapter
{

  private final Edge[] m_removedEdges;

  // Note that no copy of the array removedEdges is made - the exact
  // array reference is kept.  Methods on this class return this same
  // array reference.  Note that the Edge objects in the input array
  // must contain valid RootGraph indices at the time this constructor is
  // called; further behavior of the Edge objects is not too important
  // because the getRemovedEdges() method has been deprecated in both
  // GraphPerspective and RootGraph listener systems.
  RootGraphEdgesRemovedEvent(RootGraph rootGraph, Edge[] removedEdges)
  {
    super(rootGraph);
    m_removedEdges = removedEdges;
  }

  public final int getType()
  {
    return EDGES_REMOVED_TYPE;
  }

  // If this system of listeners and events is to be used publicly (outside
  // of this package, that is), then we need to make a copy of this array
  // and return that copy.
  public final Edge[] getRemovedEdges()
  {
    return m_removedEdges;
  }

  // This method throws an exception, which is fine, because this system of
  // listeners and events is only used internally by this package.  Nothing
  // in this package calls this method.
  public final int[] getRemovedEdgeIndices()
  {
    throw new UnsupportedOperationException("don't call this method!");
  }

}
