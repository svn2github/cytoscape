package cytoscape.util.intr;

/**
 * This is actually a B+-tree.
 */
public final class IntBTree
{

  // This quantity must be at least 5.
  private final static int MAX_BRANCHES = 5;

  private Node m_root;

  public IntBTree()
  {
    m_root = new Node(MAX_BRANCHES, true);
  }

  public void insert(int x)
  {
  }

  private final static class Node
  {

    private int sliceCount;

    // Exactly one of { values, data } is null, depending on whether or not
    // this is a leaf node.
    private final int[] values;
    private final InternalNodeData data;

    private Node(int maxBranches, boolean leafNode)
    {
      sliceCount = 0;
      if (leafNode) {
        values = new int[maxBranches];
        data = null; }
      else {
        values = null;
        data = new InternalNodeData(maxBranches); }
    }

  }

  private final static class InternalNodeData
  {

    private int deepCount;
    private final int[] splitVals;
    private final Node[] children;

    private InternalNodeData(int maxBranches)
    {
      deepCount = 0;
      splitVals = new int[maxBranches - 1];
      children = new Node[maxBranches];
    }

  }

}
