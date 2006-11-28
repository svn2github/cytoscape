package fing.model;

import giny.model.Edge;
import giny.model.RootGraph;

final class EdgeDepository implements FingEdgeDepot
{

  private final static int INITIAL_CAPACITY = 11; // Must be non-negative.

  private Edge[] m_edgeStack;
  private int m_size;

  EdgeDepository()
  {
    m_edgeStack = new Edge[INITIAL_CAPACITY];
    m_size = 0;
  }

  // Gimme an edge, darnit!
  // Don't forget to initialize the edge's member variables!
  public Edge getEdge(RootGraph root, int index, String id)
  {
    final FEdge returnThis;
    if (m_size == 0) returnThis = new FEdge();
    else returnThis = (FEdge) m_edgeStack[--m_size];
    returnThis.m_rootGraph = root;
    returnThis.m_rootGraphIndex = index;
    returnThis.m_identifier = id;
    return returnThis;
  }

  // Deinitialize the object's members yourself if you need or want to.
  public void recycleEdge(Edge edge)
  {
    if (edge == null) return;
    try { m_edgeStack[m_size] = edge; m_size++; }
    catch (ArrayIndexOutOfBoundsException e) {
      final int newArrSize = (int)
        Math.min((long) Integer.MAX_VALUE,
                 ((long) m_edgeStack.length) * 2l + 1l);
      if (newArrSize == m_edgeStack.length)
        throw new IllegalStateException
          ("unable to allocate large enough array");
      Edge[] newArr = new Edge[newArrSize];
      System.arraycopy(m_edgeStack, 0, newArr, 0, m_edgeStack.length);
      m_edgeStack = newArr;
      m_edgeStack[m_size++] = edge; }
  }

}
