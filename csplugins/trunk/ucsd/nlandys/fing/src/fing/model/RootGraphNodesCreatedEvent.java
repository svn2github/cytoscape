package fing.model;

import giny.model.Node;
import giny.model.RootGraph;

final class RootGraphNodesCreatedEvent extends RootGraphChangeEventAdapter
{

  private final RootGraph m_rootGraph;
  private final int[] m_createdNodeInx;

  // Note that no copy of the array createdNodeInx is made - the exact
  // array reference is kept.  However, copies are made in the return values
  // of methods of this class.
  RootGraphNodesCreatedEvent(RootGraph rootGraph, int[] createdNodeInx)
  {
    super(rootGraph);
    m_rootGraph = rootGraph;
    m_createdNodeInx = createdNodeInx;
  }

  public final int getType()
  {
    return NODES_CREATED_TYPE;
  }

  public final Node[] getCreatedNodes()
  {
    final Node[] returnThis = new Node[m_createdNodeInx.length];
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = m_rootGraph.getNode(m_createdNodeInx[i]);
    return returnThis;
  }

  public final int[] getCreatedNodeIndices()
  {
    final int[] returnThis = new int[m_createdNodeInx.length];
    System.arraycopy(m_createdNodeInx, 0, returnThis, 0,
                     m_createdNodeInx.length);
    return returnThis;
  }

}
