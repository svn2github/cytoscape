package fing.model;

import giny.model.Edge;
import giny.model.RootGraph;

final class RootGraphEdgesCreatedEvent extends RootGraphChangeEventAdapter
{

  private final RootGraph m_rootGraph;
  private final int[] m_createdEdgeInx;

  // Note that no copy of the array createdEdgeInx is made - the exact
  // array reference is kept.  However, copies are made in the return values
  // of methods of this class.
  RootGraphEdgesCreatedEvent(RootGraph rootGraph, int[] createdEdgeInx)
  {
    super(rootGraph);
    m_rootGraph = rootGraph;
    m_createdEdgeInx = createdEdgeInx;
  }

  public final int getType()
  {
    return EDGES_CREATED_TYPE;
  }

  public final Edge[] getCreatedEdges()
  {
    final Edge[] returnThis = new Edge[m_createdEdgeInx.length];
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = m_rootGraph.getEdge(m_createdEdgeInx[i]);
    return returnThis;
  }

  public final int[] getCreatedEdgeIndices()
  {
    final int[] returnThis = new int[m_createdEdgeInx.length];
    System.arraycopy(m_createdEdgeInx, 0, returnThis, 0,
                     m_createdEdgeInx.length);
    return returnThis;
  }

}
