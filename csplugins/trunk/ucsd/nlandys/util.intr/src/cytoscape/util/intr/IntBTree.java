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

  /**
   * Inserts a new entry into this structure.  Duplicate entries are allowed.
   * @param x the new entry to insert.
   */
  public void insert(int x)
  {
  }

  /**
   * Deletes exactly one instance of the integer x.  To delete all
   * instances of the integer x, use deleteRange(x, 1).
   * @return true if and only if an entry was deleted (at most one entry is
   *   deleted by this method).
   */
  public boolean delete(int x)
  {
    return false;
  }

  /**
   * Deletes all instances of entries in the range [xStart, xStart + spanSize)
   * from this structure.
   * @param spanSize specifies how many consecutive integers, starting
   *   at xStart, to delete; spanSize cannot be negative (if spanSize is zero
   *   no action is taken).
   * @return the number of entries that were deleted from this structure.
   * @exception IllegalArgumentException if spanSize is negative.
   */
  public int deleteRange(int xStart, int spanSize)
  {
    return 0;
  }

  /**
   * @return the number of entries x currently in this structure.
   */
  public int count(int x)
  {
    return 0;
  }

  /**
   * Returns an enumeration of all entries in the range
   * [xStart, xStart + spanSize) currently in this structure; the entries
   * within the enumeration are returned in non-descending order.
   */
  public IntEnumerator searchRange(int xStart, int spanSize)
  {
    return null;
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
