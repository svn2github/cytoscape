package fing.model;

import giny.model.Edge;
import giny.model.RootGraph;

final class RootGraphEdgesRemovedEvent extends RootGraphChangeEventAdapter
{

  private final Edge[] m_removedEdges;
  private final int[] m_removedEdgeInx;

  // Note that no copy of the array removedEdges is made - the exact
  // array reference is kept.  However, copies are made in the return values
  // of methods of this class.  Note that the Edge objects in the input array
  // must contain valid RootGraph indices at the time this constructor is
  // called; further behavior of the Edge objects is not too important
  // because the getRemovedEdges() method has been deprecated.
  RootGraphEdgesRemovedEvent(RootGraph rootGraph, Edge[] removedEdges)
  {
    super(rootGraph);
    m_removedEdges = removedEdges;
    m_removedEdgeInx = new int[m_removedEdges.length];
    for (int i = 0; i < m_removedEdgeInx.length; i++)
      m_removedEdgeInx[i] = m_removedEdges[i].getRootGraphIndex();
  }

  public final int getType()
  {
    return EDGES_REMOVED_TYPE;
  }

  public final Edge[] getRemovedEdges()
  {
    final Edge[] returnThis = new Edge[m_removedEdges.length];
    System.arraycopy(m_removedEdges, 0, returnThis, 0, m_removedEdges.length);
    return returnThis;
  }

  public final int[] getRemovedEdgeIndices()
  {
    final int[] returnThis = new int[m_removedEdgeInx.length];
    System.arraycopy(m_removedEdgeInx, 0, returnThis, 0,
                     m_removedEdgeInx.length);
    return returnThis;
  }

}
