package cytoscape.util.intr;

/**
 * This is actually a B+-tree.
 */
public final class IntBTree
{

  // This quantity must be at least 3.
  private final static int MAX_BRANCHES = 3;

//   private final int m_min_capacity;
  private final int[] m_buff; // Poor man's algorithms; optimize later.
  private Node m_root;

  public IntBTree()
  {
//     m_min_capacity = (int) Math.ceil(((double) MAX_BRANCHES) / 2.0d);
    m_buff = new int[MAX_BRANCHES + 1];
    m_root = new Node(MAX_BRANCHES, true);
  }

  /**
   * Inserts a new entry into this structure.  Duplicate entries are allowed.
   * @param x the new entry to insert.
   */
  public void insert(int x)
  {
    Node newNode = insert(m_root, x);
    if (newNode != null) { // The root has been split into two.
      int newSplitVal;
      int newDeepCount;
      if (isLeafNode(newNode)) {
        newSplitVal = newNode.values[0];
        newDeepCount = m_root.sliceCount + newNode.sliceCount; }
      else {
        newSplitVal = newNode.data.splitVals[newNode.sliceCount - 1];
        newDeepCount = m_root.data.deepCount + newNode.data.deepCount; }
      Node newRoot = new Node(MAX_BRANCHES, false);
      newRoot.sliceCount = 2;
      newRoot.data.deepCount = newDeepCount;
      newRoot.data.splitVals[0] = newSplitVal;
      newRoot.data.children[0] = m_root;
      newRoot.data.children[1] = newNode;
      m_root = newRoot; }
  }

  // Returns a node being the newly created node if a split was performed;
  // the node returned is the right sibling of node n.  If the returned node
  // is a leaf node then the first value of the node is to be the new split
  // index; if return value is internal node, then the split index to be used
  // is returnValue.data.splitVals[returnValue.sliceCount - 1].
  private Node insert(Node n, int x)
  {
    if (isLeafNode(n))
    {
      if (n.sliceCount < n.values.length) { // There's room for a value.
        boolean found = false;
        for (int i = 0; i < n.sliceCount; i++) {
          if (x <= n.values[i]) {
            for (int j = n.sliceCount; j > i; j--) {
              n.values[j] = n.values[j - 1]; }
            n.values[i] = x;
            found = true;
            break; } }
        if (!found) n.values[n.sliceCount] = x;
        n.sliceCount++;
        return null; }
      else { // No room for another value in this leaf node; perform split.
        // Perform poor man's correct but inefficient algorithm using m_buff.
        boolean found = false;
        for (int i = 0; i < n.sliceCount; i++) {
          if (x <= n.values[i]) {
            for (int j = n.sliceCount; j > i; j--) m_buff[j] = n.values[j - 1];
            m_buff[i] = x;
            found = true;
            break; }
          else { m_buff[i] = n.values[i]; } }
        if (!found) m_buff[n.sliceCount] = x;
        Node newNode = new Node(MAX_BRANCHES, true);
        int combinedCount = n.sliceCount + 1;
        n.sliceCount = combinedCount >> 1; // Divide by two.
        System.arraycopy(m_buff, 0, n.values, 0, n.sliceCount);
        newNode.sliceCount = combinedCount - n.sliceCount;
        System.arraycopy(m_buff, n.sliceCount,
                         newNode.values, 0, newNode.sliceCount);
        return newNode; }
    }
    else
    { // Not a leaf node.
      int foundPath = -1;
      for (int i = 0; i < n.sliceCount - 1; i++) {
        if (x <= n.data.splitVals[i]) {
          foundPath = i;
          break; } }
      if (foundPath < 0) foundPath = n.sliceCount - 1;
      Node oldChild = n.data.children[foundPath];
      Node newChild = insert(oldChild, x);
      if (newChild == null) {
        n.data.deepCount++;
        return null; }
      else
      { // A split was performed at one level deeper.
        int newSplit;
        if (isLeafNode(newChild)) newSplit = newChild.values[0];
        else newSplit = newChild.data.splitVals[newChild.sliceCount - 1];
        if (n.sliceCount < n.data.children.length) { // There's room here.
          for (int j = n.sliceCount; j > foundPath + 1; j--) {
            n.data.children[j] = n.data.children[j - 1];
            n.data.splitVals[j - 1] = n.data.splitVals[j - 2]; }
          n.sliceCount++;
          n.data.deepCount++;
          n.data.children[foundPath + 1] = newChild;
          n.data.splitVals[foundPath] = newSplit;
          return null; }
        else { // No room in this internal node; perform split.
          // Being poor but correct, we're going to use poor man's m_buff.
          System.arraycopy(n.data.splitVals, 0, m_buff, 0, foundPath);
          m_buff[foundPath] = newSplit;
          System.arraycopy(n.data.splitVals, foundPath, m_buff, foundPath + 1,
                           n.sliceCount - (foundPath + 1));
          Node[] nodeBuff = new Node[n.sliceCount + 1];
          System.arraycopy(n.data.children, 0, nodeBuff, 0, foundPath + 1);
          nodeBuff[foundPath + 1] = newChild;
          System.arraycopy(n.data.children, foundPath + 1,
                           nodeBuff, foundPath + 2,
                           n.sliceCount - (foundPath + 1));
          int combinedSplitCount = n.sliceCount;
          int middleInx = combinedSplitCount >> 1; // Divide by two.
          Node returnThis = new Node(MAX_BRANCHES, false);
          System.arraycopy(m_buff, 0,
                           n.data.splitVals, 0, middleInx); // Superfluous?
          System.arraycopy(nodeBuff, 0, n.data.children, 0,
                           middleInx + 1); // Superfluous?
          n.sliceCount = middleInx + 1;
          System.arraycopy(m_buff, middleInx + 1,
                           returnThis.data.splitVals, 0,
                           combinedSplitCount - (middleInx + 1));
          // Todo: Remove dangling pointers so garbage collect.
          // Todo: Put more data into right (new) sibling.
          // Todo: Update deep counts.
          System.arraycopy(nodeBuff, middleInx + 1,
                           returnThis.data.children, 0,
                           combinedSplitCount - middleInx);
          returnThis.sliceCount = combinedSplitCount - middleInx;
          returnThis.data.splitVals[combinedSplitCount - (middleInx + 1)] =
            m_buff[middleInx];
          return returnThis; }
      }
    }
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
      m_buff[0] = Integer.MIN_VALUE;
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

  public void debugPrint()
  {
    java.util.Vector v = new java.util.Vector();
    v.add(m_root);
    while (true) {
      v = debugPrint_level(v);
      if (v.size() == 0) break; }
  }

  private java.util.Vector debugPrint_level(java.util.Vector v)
  {
    java.util.Vector returnThis = new java.util.Vector();
    while (v.size() > 0) {
      Node n = (Node) v.remove(0);
      if (!isLeafNode(n)) {
        for (int i = 0; i < n.sliceCount; i++) {
          returnThis.add(n.data.children[i]); } }
      debugPrint_node(n); }
    System.out.println();
    return returnThis;
  }

  private void debugPrint_node(Node n)
  {
    if (isLeafNode(n)) {
      System.out.print(" [");
      for (int i = 0; i < n.sliceCount - 1; i++) {
        System.out.print(n.values[i] + " "); }
      if (n.sliceCount > 0) System.out.print(n.values[n.sliceCount - 1]);
      System.out.print("]"); }
    else {
      System.out.print(" <.");
      for (int i = 0; i < n.sliceCount - 1; i++) {
        System.out.print(n.data.splitVals[i] + "."); }
      System.out.print(">"); }
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

  public static void main(String[] args)
  {
    IntBTree tree = new IntBTree();
    for (int i = 0; i < args.length; i++) {
      int entry = Integer.parseInt(args[i]);
      tree.insert(entry); }
    tree.debugPrint();
  }

}
