package fing.model;

import giny.model.Edge;
import giny.model.RootGraph;

final class RootGraphEdgesRemovedEvent extends RootGraphChangeEventAdapter
{

  private final RootGraph m_rootGraph;
  private final int[] m_removedEdgeInx;

  // Note that no copy of the array removedEdgeInx is made - the exact
  // array reference is kept.  However, copies are made in the return values
  // of methods of this class.
  RootGraphEdgesRemovedEvent(RootGraph rootGraph, int[] removedEdgeInx)
  {
    super(rootGraph);
    m_rootGraph = rootGraph;
    m_removedEdgeInx = removedEdgeInx;
  }

  public final int getType()
  {
    return EDGES_REMOVED_TYPE;
  }

  public final Edge[] getRemovedEdges()
  {
    final Edge[] returnThis = new Edge[m_removedEdgeInx.length];
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = m_rootGraph.getEdge(m_removedEdgeInx[i]);
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
