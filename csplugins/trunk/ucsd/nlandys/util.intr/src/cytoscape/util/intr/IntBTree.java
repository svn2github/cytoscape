package cytoscape.util.intr;

/**
 * A B<sup>+</sup>-tree that stores integers.
 */
public final class IntBTree
{

  // This quantity must be at least 3.
  // The author prefers that this quantity be odd because that way nodes
  // are split evenly when they get full.
  // 45 or so seems to be the optimal value for large trees.
  public final static int DEFAULT_MAX_BRANCHES = 45;

  private final int m_maxBranches;
  private Node m_root;

  /**
   * Creates a new tree structure with the default maximum branching factor.
   */
  public IntBTree()
  {
    m_maxBranches = DEFAULT_MAX_BRANCHES;
    m_root = new Node(m_maxBranches, true);
  }

  /**
   * Creates a new tree structure with the specified maximum branching
   * factor.  Overriding the default maximum branching factor is only
   * useful for testing purposes; there are no performance gains to be had.
   * @param maxBranches the maximum branching factor of this tree.
   * @exception IllegalArgumentException if maxBranches is less than three.
   */
  public IntBTree(final int maxBranches)
  {
    if (maxBranches < 3) throw new IllegalArgumentException
                           ("maxBranches is less than three");
    m_maxBranches = maxBranches;
    m_root = new Node(m_maxBranches, true);
  }

  /**
   * Empties this structure of all elements.  This method returns in constant
   * time (note however that garbage collection will take place in the
   * background).
   */
  public final void empty()
  {
    m_root = new Node(m_maxBranches, true);
  }

  /**
   * Returns the number of elements currently in this structure.  Duplicate
   * entries are counted however many times they are present.  This method
   * returns in constant time.
   */
  public final int size()
  {
    return isLeafNode(m_root) ? m_root.sliceCount : m_root.data.deepCount;
  }

  /*
   * Perhaps this should be inlined later for performance.
   */
  private static final boolean isLeafNode(final Node n)
  {
    return n.data == null;
  }

  /**
   * Inserts a new entry into this tree structure; duplicate entries may be
   * entered.  This method has a time complexity of O(log(N)) where N is the
   * number of entries currently stored in this tree structure.
   * @param x the new entry to insert.
   */
  public final void insert(final int x)
  {
    final Node newSibling = insert(m_root, x);
    if (newSibling != null) { // The root has been split into two.
      final int newSplitVal;
      final int newDeepCount;
      if (isLeafNode(newSibling)) {
        newSplitVal = newSibling.values[0];
        newDeepCount = m_root.sliceCount + newSibling.sliceCount; }
      else {
        newSplitVal = m_root.data.splitVals[m_root.sliceCount - 1];
        newDeepCount = m_root.data.deepCount + newSibling.data.deepCount; }
      final Node newRoot = new Node(m_maxBranches, false);
      newRoot.sliceCount = 2;
      newRoot.data.deepCount = newDeepCount;
      newRoot.data.splitVals[0] = newSplitVal;
      newRoot.data.children[0] = m_root;
      newRoot.data.children[1] = newSibling;
      m_root = newRoot; }
  }

  /*
   * Returns a node being the newly created node if a split was performed;
   * the node returned is the right sibling of node n.  If the returned node
   * is a leaf node then the first value of the node is to be the new split
   * index; if return value is internal node, then the split index to be used
   * is n.data.splitVals[n.sliceCount - 1].  (This is something that this
   * method sets; it's this method saying "use this index in the higher
   * levels".)
   */
  private final Node insert(final Node n, final int x)
  {
    if (isLeafNode(n)) {
      if (n.sliceCount < m_maxBranches) { // There's room for a value.
        int i = -1; while (++i < n.sliceCount) if (x <= n.values[i]) break;
        for (int j = n.sliceCount; j > i;) n.values[j] = n.values[--j];
        n.values[i] = x; n.sliceCount++;
        return null; }
      else { // No room for another value in this leaf node; perform split.
        final Node newLeafSibling = new Node(m_maxBranches, true);
        final int combinedCount = m_maxBranches + 1;
        n.sliceCount = combinedCount >> 1; // Divide by two.
        newLeafSibling.sliceCount = combinedCount - n.sliceCount;
        split(x, n.values, newLeafSibling.values, newLeafSibling.sliceCount);
        return newLeafSibling; } }
    else { // Not a leaf node.
      int foundPath = 0;
      for (int i = n.sliceCount - 2; i >= 0; i--) {
        if (x >= n.data.splitVals[i]) { foundPath = i + 1; break; } }
      final Node oldChild = n.data.children[foundPath];
      final Node newChild = insert(oldChild, x);
      if (newChild == null) {
        n.data.deepCount++;
        return null; }
      else { // A split was performed at one level deeper.
        final int newSplit;
        if (isLeafNode(newChild)) newSplit = newChild.values[0];
        else newSplit = oldChild.data.splitVals[oldChild.sliceCount - 1];
        if (n.sliceCount < m_maxBranches) { // There's room here.
          for (int j = n.sliceCount - 1; j > foundPath;) {
            n.data.children[j + 1] = n.data.children[j];
            n.data.splitVals[j] = n.data.splitVals[--j]; }
          n.sliceCount++; n.data.deepCount++;
          n.data.children[foundPath + 1] = newChild;
          n.data.splitVals[foundPath] = newSplit;
          return null; }
        else { // No room in this internal node; perform split.
          final Node newInternalSibling = new Node(m_maxBranches, false);
          final int combinedCount = m_maxBranches + 1;
          n.sliceCount = combinedCount >> 1; // Divide by two.
          newInternalSibling.sliceCount = combinedCount - n.sliceCount;
          split(newChild, foundPath, n.data.children,
                newInternalSibling.data.children,
                newInternalSibling.sliceCount);
          split(newSplit, n.data.splitVals, newInternalSibling.data.splitVals,
                newInternalSibling.sliceCount - 1);
          n.data.deepCount = 0; // Update the deep count in both nodes.
          if (isLeafNode(newChild)) {
            for (int i = 0; i < n.sliceCount; i++)
              n.data.deepCount += n.data.children[i].sliceCount;
            for (int i = 0; i < newInternalSibling.sliceCount; i++)
              newInternalSibling.data.deepCount +=
                newInternalSibling.data.children[i].sliceCount; }
          else {
            for (int i = 0; i < n.sliceCount; i++)
              n.data.deepCount += n.data.children[i].data.deepCount;
            for (int i = 0; i < newInternalSibling.sliceCount; i++)
              newInternalSibling.data.deepCount +=
                newInternalSibling.data.children[i].data.deepCount; }
          return newInternalSibling; } } }
  }

  /*
   * It's tedious to rigorously define what this method does.  I give an
   * example:
   *
   *
   *   INPUTS
   *   ======
   *
   *   newVal: 5
   *
   *             +---+---+---+---+---+---+---+
   *   origBuff: | 0 | 2 | 3 | 6 | 6 | 8 | 9 |
   *             +---+---+---+---+---+---+---+
   *
   *                 +---+---+---+---+---+---+---+
   *   overflowBuff: | / | / | / | / | / | / | / |
   *                 +---+---+---+---+---+---+---+
   *
   *   overflowCount: 4
   *
   *
   *   OUTPUTS
   *   =======
   *
   *             +---+---+---+---+---+---+---+
   *   origBuff: | 0 | 2 | 3 | 5 | / | / | / |
   *             +---+---+---+---+---+---+---+
   *
   *                 +---+---+---+---+---+---+---+
   *   overflowBuff: | 6 | 6 | 8 | 9 | / | / | / |
   *                 +---+---+---+---+---+---+---+
   */
  private final void split(final int newVal, final int[] origBuff,
                           final int[] overflowBuff, final int overflowCount)
  {
    int[] currentArr = overflowBuff;
    int currentInx = overflowCount;
    boolean found = false;
    for (int i = origBuff.length - 1; i >= 0; i--) {
      if ((!found) && (newVal >= origBuff[i])) {
        currentArr[--currentInx] = newVal; found = true;
        if (currentArr == origBuff) break; i++; }
      else { currentArr[--currentInx] = origBuff[i]; }
      if (currentInx == 0) {
        if (found) break; currentArr = origBuff;
        currentInx = origBuff.length - overflowCount + 1; } }
    if (!found) currentArr[0] = newVal;
  }

  /*
   * It's tedious to rigorously define what this method does.  I give an
   * example:
   *
   *
   *   INPUTS
   *   ======
   *
   *   newNode: Z
   *
   *   newInx: 5
   *
   *              +---+---+---+---+---+---+---+
   *   origNodes: | Q | I | E | A | Y | N | W |
   *              +---+---+---+---+---+---+---+
   *
   *                  +---+---+---+---+---+---+---+
   *   overflowNodes: | / | / | / | / | / | / | / |
   *                  +---+---+---+---+---+---+---+
   *
   *   overflowCount: 4
   *
   *
   *   OUTPUTS
   *   =======
   *
   *             +---+---+---+---+---+---+---+
   *   origBuff: | Q | I | E | A | / | / | / |
   *             +---+---+---+---+---+---+---+
   *
   *                 +---+---+---+---+---+---+---+
   *   overflowBuff: | Y | N | Z | W | / | / | / |
   *                 +---+---+---+---+---+---+---+
   *
   *   In addition, the "unused" entries in origBuff are nulled out (remove
   *   pointers to enable garbage collection).
   *
   *   Note tht newInx means to put the new node after the existing node
   *   at index newInx in the original array.  Placing the new node before
   *   every other node would entail specifying newInx as -1, which is not
   *   allowed.
   */
  private final void split(Node newNode, final int newInx,
                           final Node[] origNodes, final Node[] overflowNodes,
                           final int overflowCount)
  {
    Node[] currentNodes = overflowNodes;
    int currentInx = overflowCount;
    for (int i = origNodes.length - 1; i >= 0; i--) {
      if ((newNode != null) && (i == newInx)) {
        currentNodes[--currentInx] = newNode; newNode = null;
        if (currentNodes == origNodes) break; i++; }
      else { currentNodes[--currentInx] = origNodes[i]; }
      if (currentInx == 0) {
        if (newNode == null) break; currentNodes = origNodes;
        currentInx = origNodes.length - overflowCount + 1; } }
    for (int i = origNodes.length - overflowCount + 1;
         i < origNodes.length; i++)
      origNodes[i] = null; // Remove dangling pointers for garbage collection.
  }

//   /**
//    * Deletes at most one entry of the integer x.  To delete all
//    * entries of the integer x, use ____.
//    * @param x the integer to try to delete (just one entry).
//    * @return true if and only if an entry was deleted (at most one entry is
//    *   deleted by this method).
//    */
//   public boolean delete(int x)
//   {
//     return false;
//   }

  /**
   * Returns the number of entries of the integer x in this tree.
   * This method has a time complexity of O(log(N)) where N is the total
   * number of entries currently in this tree structure.<p>
   * This method is superfluous because we can use searchRange(x, x) to
   * get the same information, paying the same hit in time complexity.
   * @param x the integer whose count to query.
   * @return the number of entries x currently in this structure.
   * @deprecated Use searchRange(x, x) in place of this method; the author
   *   may decide to remove this method at some point.
   */
  public final int count(final int x)
  {
    return count(m_root, x, Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  /*
   * It's important that with every invocation of this method, we have
   * minBound <= x <= maxBound.
   */
  private final int count(final Node n, final int x,
                          final int minBound, final int maxBound)
  {
    int count = 0;
    if (minBound == maxBound) { // Trivially include node.
      count += (isLeafNode(n) ? n.sliceCount : n.data.deepCount); }
    else { // Cannot trivially include node; must recurse.
      if (isLeafNode(n)) {
        for (int i = 0; i < n.sliceCount; i++)
          if (x <= n.values[i]) {
            if (x == n.values[i]) count++; else break; } }
      else { // Internal node.
        int currentMax = maxBound; int currentMin;
        for (int i = n.sliceCount - 2; i >= -1; i--) {
          currentMin = ((i < 0) ? minBound : n.data.splitVals[i]);
          if (currentMin <= x) {
            count += count(n.data.children[i + 1], x, currentMin, currentMax);
            if (currentMin < x) break; }
          currentMax = currentMin; } } }
    return count;
  }

  /**
   * Returns an enumeration of all entries in the range [xMin, xMax] currently
   * in this tree, duplicates included; the elements of the enumeration are
   * returned in non-descending order.  This method takes O(log(N)) time to
   * compute a return value, where N is the number of entries currently in
   * this tree structure.  The returned enumeration reports the number of
   * elements remaining in constant time.  The returned enumeration can
   * be completely traversed in O(K) time, where K is the number of elements
   * in the returned enumeration; this is always true, regardless of how
   * small the set of returned enumerated elements is.  Note, however, that
   * there is no guarantee that each successive element of the enumeration
   * is returned in constant time; instead, the time complexity of getting
   * each successive element is constant on average, and is O(log(K)).<p>
   * IMPORTANT: The returned enumeration becomes invalid as soon as any
   * structure-modifying operation (insert or delete) is performed on this
   * tree.  Accessing an invalid enumeration's methods will result in
   * unpredictable and ill-defined behavior in that enumeration, but will
   * have no effect on the integrity of underlying tree structure.<p>
   * IMPLEMENTATION NOTE: To find out how many entries are in this tree,
   * one should use the size() method.  Doing so using this method will
   * cost O(log(N)) time, where the size() method returns in constant time.
   * The reason why this method takes longer is because we pre-compute the
   * first element of the enumeration in order to reduce the number of 'if'
   * statements in the code.
   * @param xMin the lower [inclusive] bound of the range to search.
   * @param xMax the upper [inclusive] bound of the range to search.
   * @return an non-descending enumeration of all entries matching this search
   *   query, duplicates included.
   * @exception IllegalArgumentException if xMin is greater than xMax.
   */
  public final IntEnumerator searchRange(final int xMin, final int xMax)
  {
    if (xMin > xMax) throw new IllegalArgumentException
                       ("xMin is greater than xMax");
    final NodeStack nodeStack = new NodeStack();
    final int totalCount = searchRange
      (m_root, nodeStack, xMin, xMax, Integer.MIN_VALUE, Integer.MAX_VALUE);
    return new IntEnumerator() {
        private int count = totalCount;
        private int wholeLeafNodes = 0; // Whole leaf nodes on stack.
        private int currentNodeInx = 0;
        private Node currentLeafNode = computeNextLeafNode();
        public final int numRemaining() { return count; }
        public final int nextInt() {
          int returnThis = 0x80000000; // To keep compiler from complaining.
          if (wholeLeafNodes > 0)
            returnThis = currentLeafNode.values[currentNodeInx];
          else
            for (; currentNodeInx < currentLeafNode.sliceCount;
                 currentNodeInx++)
              if (currentLeafNode.values[currentNodeInx] >= xMin) {
                returnThis = currentLeafNode.values[currentNodeInx]; break; }
          if (++currentNodeInx == currentLeafNode.sliceCount) {
            if (wholeLeafNodes > 0) wholeLeafNodes--;
            currentLeafNode = computeNextLeafNode(); currentNodeInx = 0; }
          count--;
          return returnThis; }
        private final Node computeNextLeafNode() {
          if (nodeStack.currentSize == 0) return null;
          Node returnThis;
          while (true) {
            returnThis = nodeStack.pop();
            if (isLeafNode(returnThis)) return returnThis;
            for (int i = returnThis.sliceCount; i > 0;)
              nodeStack.push(returnThis.data.children[--i]);
            if (isLeafNode(returnThis.data.children[0]))
              wholeLeafNodes += returnThis.sliceCount; } } };
  }

  /*
   * Returns the count.  The node stack is added to, never read from.
   * The elements added to the node stack -- leaf nodes should be iterated
   * through and appropriate values examined; internal nodes represent
   * regions of the tree which can be included, as whole, as part of the
   * range query.  Every node on the returned stack will have at least one
   * leaf entry counting towards the enumeration in the range query (this
   * statement is important for leaf nodes).  [xMin, xMax] must intersect
   * [minBound, maxBound] on each call to this method.
   */
  private final int searchRange(final Node n, final NodeStack nodeStack,
                                final int xMin, final int xMax,
                                final int minBound, final int maxBound)
  {
    int count = 0;
    if (minBound >= xMin && maxBound <= xMax) { // Trivially include node.
      count += (isLeafNode(n) ? n.sliceCount : n.data.deepCount);
      nodeStack.push(n); }
    else { // Cannot trivially include node; must recurse.
      if (isLeafNode(n)) {
        int i = 0;
        for (; i < n.sliceCount; i++) if (xMin <= n.values[i]) break;
        for (int j = i; j < n.sliceCount; j++) {
          if (n.values[j] <= xMax) count++;
          else break; }
        if (count > 0) nodeStack.push(n); }
      else { // Internal node.
        int currentMax = maxBound; int currentMin;
        for (int i = n.sliceCount - 2; i >= -1; i--) {
          currentMin = ((i < 0) ? minBound : n.data.splitVals[i]);
          if (Math.max(currentMin, xMin) <= Math.min(currentMax, xMax))
            count += searchRange(n.data.children[i + 1], nodeStack, xMin, xMax,
                                 currentMin, currentMax);
          currentMax = currentMin; } } }
    return count;
  }

//   public void debugPrint()
//   {
//     java.util.Vector v = new java.util.Vector();
//     v.add(m_root);
//     while (true) {
//       v = debugPrint_level(v);
//       if (v.size() == 0) break; }
//     System.out.print("total count: ");
//     if (isLeafNode(m_root))
//       System.out.println(m_root.sliceCount);
//     else
//       System.out.println(m_root.data.deepCount);
//   }

//   private java.util.Vector debugPrint_level(java.util.Vector v)
//   {
//     java.util.Vector returnThis = new java.util.Vector();
//     while (v.size() > 0) {
//       Node n = (Node) v.remove(0);
//       if (!isLeafNode(n)) {
//         for (int i = 0; i < n.sliceCount; i++) {
//           returnThis.add(n.data.children[i]); } }
//       debugPrint_node(n); }
//     System.out.println();
//     return returnThis;
//   }

//   private void debugPrint_node(Node n)
//   {
//     if (isLeafNode(n)) {
//       System.out.print(" [");
//       for (int i = 0; i < n.sliceCount - 1; i++) {
//         System.out.print(n.values[i] + " "); }
//       if (n.sliceCount > 0) System.out.print(n.values[n.sliceCount - 1]);
//       System.out.print("]"); }
//     else {
//       System.out.print(" <.");
//       for (int i = 0; i < n.sliceCount - 1; i++) {
//         System.out.print(n.data.splitVals[i] + "."); }
//       System.out.print(">"); }
//   }

  private final static class Node
  {
    private int sliceCount = 0;
    private final int[] values; // null if and only if internal node.
    private final InternalNodeData data;
    private Node(int maxBranches, boolean leafNode) {
      if (leafNode) { values = new int[maxBranches]; data = null; }
      else { values = null; data = new InternalNodeData(maxBranches); } }
  }

  private final static class InternalNodeData
  {
    private int deepCount;
    private final int[] splitVals;
    private final Node[] children;
    private InternalNodeData(int maxBranches) {
      splitVals = new int[maxBranches - 1];
      children = new Node[maxBranches]; }
  }

  private final static class NodeStack
  {
    private Node[] stack;
    private int currentSize = 0;
    private NodeStack() { stack = new Node[3]; }
    private final void push(Node value) {
      try { stack[currentSize++] = value; }
      catch (ArrayIndexOutOfBoundsException e) {
        currentSize--;
        final int newStackSize = (int)
          Math.min((long) Integer.MAX_VALUE, ((long) stack.length) * 2l + 1l);
        if (newStackSize == stack.length)
          throw new IllegalStateException
            ("cannot allocate large enough array");
        final Node[] newStack = new Node[newStackSize];;
        System.arraycopy(stack, 0, newStack, 0, stack.length);
        stack = newStack;
        stack[currentSize++] = value; } }
    private final Node pop() {
      try { return stack[--currentSize]; }
      catch (ArrayIndexOutOfBoundsException e) {
        currentSize++;
        throw e; } }
  }

}
