package cytoscape.giny;

import giny.model.Node;
import giny.model.RootGraph;
import fing.model.*;
import cytoscape.CyNode;

final class CyNodeDepot implements FingNodeDepot
{

  private final static int INITIAL_CAPACITY = 11; // Must be non-negative.

  private Node[] m_nodeStack;
  private int m_size;

  CyNodeDepot ()
  {
    m_nodeStack = new Node[INITIAL_CAPACITY];
    m_size = 0;
  }

  // Gimme a node, darnit!
  // Don't forget to initialize the node's member variables!
  public Node getNode(RootGraph root, int index, String id)
  {
    //System.out.println( "NODE DEPOT: create node: index: "+index );

    final CyNode returnThis;
    if (m_size == 0) returnThis = new CyNode(root, index);
    else returnThis = (CyNode) m_nodeStack[--m_size];
    //returnThis.m_rootGraph = root;
    //returnThis.m_rootGraphIndex = index;
    //returnThis.m_identifier = id;
    return returnThis;
  }

  // Deinitialize the object's members yourself if you need or want to.
  public void recycleNode(Node node)
  {
    if (node == null) return;
    try { m_nodeStack[m_size] = node; m_size++; }
    catch (ArrayIndexOutOfBoundsException e) {
      final int newArrSize = (int)
        Math.min((long) Integer.MAX_VALUE,
                 ((long) m_nodeStack.length) * 2l + 1l);
      if (newArrSize == m_nodeStack.length)
        throw new IllegalStateException
          ("unable to allocate large enough array");
      Node[] newArr = new Node[newArrSize];
      System.arraycopy(m_nodeStack, 0, newArr, 0, m_nodeStack.length);
      m_nodeStack = newArr;
      m_nodeStack[m_size++] = node; }
  }

}
