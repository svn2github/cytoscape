package fing.model;

import giny.model.Node;
import giny.model.RootGraph;

final class RootGraphNodesRemovedEvent extends RootGraphChangeEventAdapter
{

  private final Node[] m_removedNodes;
  private final int[] m_removedNodeInx;

  // Note that no copy of the array removedNodes is made - the exact
  // array reference is kept.  However, copies are made in the return values
  // of methods of this class.  Note that the Node objects in the input array
  // must contain valid RootGraph indices at the time this constructor is
  // called; further behavior of the Node objects is not too important
  // becuase the getRemovedNodes() method has been deprecated.
  RootGraphNodesRemovedEvent(RootGraph rootGraph, Node[] removedNodes)
  {
    super(rootGraph);
    m_removedNodes = removedNodes;
    m_removedNodeInx = new int[m_removedNodes.length];
    for (int i = 0; i < m_removedNodeInx.length; i++)
      m_removedNodeInx[i] = m_removedNodes[i].getRootGraphIndex();
  }

  public final int getType()
  {
    return NODES_REMOVED_TYPE;
  }

  // This method has been deprecated in the Giny API.
  public final Node[] getRemovedNodes()
  {
    final Node[] returnThis = new Node[m_removedNodes.length];
    System.arraycopy(m_removedNodes, 0, returnThis, 0, m_removedNodes.length);
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
