package fing.model;

import giny.model.Node;
import giny.model.RootGraph;

final class RootGraphNodesRemovedEvent extends RootGraphChangeEventAdapter
{

  private final RootGraph m_rootGraph;
  private final int[] m_removedNodeInx;

  // Note that no copy of the array removedNodeInx is made - the exact
  // array reference is kept.  However, copies are made in the return values
  // of methods of this class.
  RootGraphNodesRemovedEvent(RootGraph rootGraph, int[] removedNodeInx)
  {
    super(rootGraph);
    m_rootGraph = rootGraph;
    m_removedNodeInx = removedNodeInx;
  }

  public final int getType()
  {
    return NODES_REMOVED_TYPE;
  }

  public final Node[] getRemovedNodes()
  {
    final Node[] returnThis = new Node[m_removedNodeInx.length];
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = m_rootGraph.getNode(m_removedNodeInx[i]);
    return returnThis;
  }

  public final int[] getRemovedNodeIndices()
  {
    final int[] returnThis = new int[m_removedNodeInx.length];
    System.arraycopy(m_removedNodeInx, 0, returnThis, 0,
                     m_removedNodeInx.length);
    return returnThis;
  }

}
