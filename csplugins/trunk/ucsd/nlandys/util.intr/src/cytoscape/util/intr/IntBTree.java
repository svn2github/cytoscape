package cytoscape.util.intr;

/**
 * This is actually a B*-tree.
 */
public final class IntBTree
{

  // This quantity must be at least 3.
  private final static int MAX_BRANCHES = 3;

  private final int m_min_capacity;
  private final int[] m_buff; // Poor man's algorithm; optimize later.
  private Node m_root;

  public IntBTree()
  {
    m_min_capacity = (int) Math.ceil(((double) MAX_BRANCHES) / 2.0d);
    m_buff = new int[MAX_BRANCHES + 1];
    m_buff[0] = Integer.MIN_VALUE; // This ought to never be changed.
    m_root = new Node(MAX_BRANCHES, true);
  }

  /**
   * Inserts a new entry into this structure.  Duplicate entries are allowed.
   * @param x the new entry to insert.
   */
  public void insert(int x)
  {
    Node newNode = insert(m_root, x);
    if (newNode != null) {
      
    }
  }

  // Return a Node being the newly created node if a split was performed.
  // The first value of the Node is to be the new split index.
  private Node insert(Node n, int x)
  {
    if (isLeafNode(n)) {
      if (n.sliceCount < n.values.length) { // There's room for a value.
        boolean found = false;
        for (int i = 0; i < n.sliceCount; i++) {
          if (x < n.values[i]) {
            for (int j = n.sliceCount; j > i; j--) {
              n.values[j] = n.values[j - 1]; }
            n.values[i] = x;
            found = true;
            break; } }
        if (!found) {
          n.values[n.sliceCount] = x; }
        n.sliceCount++;
        return null; }
      else { // No room for another value in this leaf node; perform split.
        return null;
      }
    }
    return null;
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
   *   integers greater than or equal to xStart but less than xStart + spanSize
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
   * Returns the number of entries of the integer x in this tree.
   * This method is superfluous because we can use searchRange(x, 1) to
   * get the same information; I'm implementing this method as a warm-up
   * to the more difficult methods.
   * @param x the integer whose count to query.
   * @return the number of entries x currently in this structure.
   */
  public int count(int x)
  {
    return count(m_root, x);
  }

  private int count(Node n, int x)
  {
    if (isLeafNode(n)) {
      int count = 0;
      for (int i = 0; i < n.sliceCount; i++) { // For the sake of simple
        if (n.values[i] == x) count++; }       // code, don't abort on over.
      return count; }
    else {
      // Poor man's algorithm; optimize later.
      System.arraycopy(n.data.splitVals, 0, m_buff, 1, n.sliceCount - 1);
      m_buff[n.sliceCount] = Integer.MAX_VALUE;
      int count = 0;
      for (int i = 0; i < n.sliceCount; i++)
      {
        if (x >= m_buff[i] && x <= m_buff[i + 1]) {
          if (m_buff[i] == m_buff[i + 1]) count += n.data.deepCount;
          else count += count(n.data.children[i], x); }
      }
      return count;
    }
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
   *   but less than xStart + spanSize will be returned; spanSize cannot be
   *   negative (if spanSize is zero no action is taken).
   * @return an enumeration of all entries matching this search query.
   * @exception IllegalArgumentException if spanSize is negative.
   */
  public IntEnumerator searchRange(int xStart, int spanSize)
  {
    return null;
  }

  private boolean isLeafNode(Node n)
  {
    return n.data == null;
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
