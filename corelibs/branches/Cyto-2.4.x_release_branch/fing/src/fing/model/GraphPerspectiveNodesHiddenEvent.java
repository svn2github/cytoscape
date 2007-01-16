package fing.model;

import giny.model.GraphPerspective;
import giny.model.Node;

final class GraphPerspectiveNodesHiddenEvent
  extends GraphPerspectiveChangeEventAdapter
{

  private final Node[] m_hiddenNodes;
  private final int[] m_hiddenNodeInx;

  // Note that no copy of the array hiddenNodes is made - the exact
  // array reference is kept.  However, copies are made in the return values
  // of methods of this class.  Note that the Node objects in the input array
  // must contain valid RootGraph indices at the time this constructor is
  // called; further behavior of the Node objects is not too important
  // because the getHiddenNodes() method has been deprecated.
  GraphPerspectiveNodesHiddenEvent(Object source,
                                   Node[] hiddenNodes)
  {
    super(source);
    m_hiddenNodes = hiddenNodes;
    m_hiddenNodeInx = new int[m_hiddenNodes.length];
    for (int i = 0; i < m_hiddenNodeInx.length; i++)
      m_hiddenNodeInx[i] = m_hiddenNodes[i].getRootGraphIndex();
  }

  public final int getType()
  {
    return NODES_HIDDEN_TYPE;
  }

  // This method has been deprecated in the Giny API.
  public final Node[] getHiddenNodes()
  {
    final Node[] returnThis = new Node[m_hiddenNodes.length];
    System.arraycopy(m_hiddenNodes, 0, returnThis, 0, m_hiddenNodes.length);
    return returnThis;
  }

  public final int[] getHiddenNodeIndices()
  {
    final int[] returnThis = new int[m_hiddenNodeInx.length];
    System.arraycopy(m_hiddenNodeInx, 0, returnThis, 0,
                     m_hiddenNodeInx.length);
    return returnThis;
  }

}
