package fing.model;

import giny.model.GraphPerspective;
import giny.model.Node;

final class GraphPerspectiveNodesRestoredEvent
  extends GraphPerspectiveChangeEventAdapter
{

  private final GraphPerspective m_persp;
  private final int[] m_restoredNodeInx;

  // Note that no copy of the array restoredNodeInx is made - the exact
  // array reference is kept.  However, copies are made in the return values
  // of methods of this class.
  GraphPerspectiveNodesRestoredEvent(GraphPerspective persp,
                                     int[] restoredNodeInx)
  {
    super(persp);
    m_persp = persp;
    m_restoredNodeInx = restoredNodeInx;
  }

  public final int getType()
  {
    return NODES_RESTORED_TYPE;
  }

  public final Node[] getRestoredNodes()
  {
    final Node[] returnThis = new Node[m_restoredNodeInx.length];
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = m_persp.getRootGraph().getNode(m_restoredNodeInx[i]);
    return returnThis;
  }

  public final int[] getRestoredNodeIndices()
  {
    final int[] returnThis = new int[m_restoredNodeInx.length];
    System.arraycopy(m_restoredNodeInx, 0, returnThis, 0,
                     m_restoredNodeInx.length);
    return returnThis;
  }

}
