package cytoscape.util.intr;

/**
 * This is actually a B*-tree.
 */
public final class IntBTree
{

  // This quantity must be at least 3.
  private final static int MAX_BRANCHES = 3;

  private final int m_min_capacity;
  private Node m_root;

  public IntBTree()
  {
    m_min_capacity = Math.max(2, (int) (MAX_BRANCHES / 2));
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
   * Deletes at most one entry of the integer x.  To delete all
   * entries of the integer x, use deleteRange(x, 1).
   * @param x the integer to try to delete (just one entry).
   * @return true if and only if an entry was deleted (at most one entry is
   *   deleted by this method).
   */
  public boolean delete(int x)
  {
    return false;
  }

  /**
   * Deletes all entries in the range [xStart, xStart + spanSize)
   * from this structure.
   * @param xStart specifies the beginning of the range of integers to
   *   delete from this structure.
   * @param spanSize specifies the range width of integers to delete; all
   *   integers greater than or equal to xStart and less than xStart + spanSize
   *   will be deleted; spanSize cannot be negative
   *   (if spanSize is zero no action is taken).
   * @return the number of entries that were deleted from this structure.
   * @exception IllegalArgumentException if spanSize is negative.
   */
  public int deleteRange(int xStart, int spanSize)
  {
    return 0;
  }

  /**
   * @param x the integer whose count to query.
   * @return the number of entries x currently in this structure.
   */
  public int count(int x)
  {
    return 0;
  }

  /**
   * Returns an enumeration of all entries in the range
   * [xStart, xStart + spanSize) currently in this structure; the entries
   * within the enumeration are returned in non-descending order.<p>
   * IMPORTANT: The returned enumeration becomes invalid as soon as any
   * structure-modifying operation (insert or delete) is performed on this
   * tree.  Accessing an invalid enumeration's methods will result in
   * unpredictable and ill-defined behavior in the enumeration, but will
   * have no effect on the integrity of this tree structure.
   * @param xStart specifies the beginning of the range of integers to
   *   search.
   * @param spanSize specifies the range width of integers to search;
   *   all integers (duplicates included) greater than or equal to xStart
   *   and less than xStart + spanSize will be returned; spanSize cannot be
   *   negative (if spanSize is zero no action is taken).
   * @return an enumeration of all entries matching this search query.
   * @exception IllegalArgumentException if spanSize is negative.
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
