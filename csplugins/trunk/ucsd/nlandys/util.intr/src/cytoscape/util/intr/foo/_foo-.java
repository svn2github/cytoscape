public class foo
{

  public final int deleteRange(final int xMin, final int xMax)
  {
    final int returnThis = deleteRange(m_root, xMin, xMax,
                                       Integer.MIN_VALUE, Integer.MAX_VALUE);
    if (m_root.sliceCount <= 1 && !isLeafNode(m_root)) {
      if (m_root.sliceCount == 0) {
        m_root = new Node(m_maxBranches, true); }
      else { // One slice count.
        m_root = m_root.data.children[0]; } }
    return returnThis;
  }

  private final int deleteRange(final Node n,
                                final int xMin, final int xMax,
                                final int minBound, final int maxBound)
  {
    // The input node is modified in such a way that its deep count and
    // slice count are correct once this method returns.  In addition, besides
    // potentially suffering from underflow, everything else under this node
    // will be correct, and not suffering from underflow.
    // NOTE: At the expense of complexity, this recursion could be optimized
    // for performance by not shifting array members more than once.  This
    // implementation does two shifts -- once in this recursive step and once
    // [potentially] by the caller of this method, if underflow is experienced.
    int count = 0;
    if (minBound >= xMin && maxBound <= xMax) { // Trivially delete everything.
      if (!isLeafNode(n)) {
        for (int i = 0; i < n.sliceCount; i++) n.data.children[i] = null;
        count += n.data.deepCount;
        n.data.deepCount = 0; }
      else { count += n.sliceCount; }
      n.sliceCount = 0; }
    else { // Cannot trivially delete everything; must recurse.
      if (isLeafNode(n)) { // Easy.
      }
      else { // Internal node.
        int currentMax = maxBound; int currentMin;
        for (int i = n.sliceCount - 2; i >= -1; i--) {
          currentMin = ((i < 0) ? minBound : n.data.splitVals[i]);
          if (currentMin <= xMax) {
            count += deleteRange(n.data.children[i + 1], xMin, xMax,
                                 currentMin, currentMax);
            if (currentMin < xMin) break; }
          currentMax = currentMin; }}}
    return count;
  }

  public final boolean delete(final int x)
  {
    return delete(m_root, x);
  }

  private final boolean delete(final Node n, final int x)
  {
    if (isLeafNode(n)) {
      final int foundInx = findMatch(x, n.values, n.sliceCount);
      if (foundInx < 0) { return false; }
      else {
        // Here, we fill the hole, knowing that the caller of this method
        // may rearrange the entries again if there is underflow.  While
        // filling the hole is extra work that makes this code inefficient in
        // the specific case of underflow, it does make the code much simpler.
        fillHole(foundInx, n.values, --n.sliceCount);
        return true; } }
    else { // Internal node.
      int deletedPath = -1;
      for (int i = n.sliceCount - 2; i >= -1; i--) {
        int currentMin = ((i < 0) ? Integer.MIN_VALUE : n.data.splitVals[i]);
        if (currentMin <= x) {
          if (delete(n.data.children[i + 1], x)) {
            n.data.deepCount--; deletedPath = i + 1; break; }
          if (currentMin < x) break; } }
      if (deletedPath < 0) return false;
      // We deleted something.
      final Node affectedChild = n.data.children[deletedPath];



      if (affectedChild.sliceCount < minBranches) { // Underflow.
        final Node leftChild = deletedPath > 0 ?
          n.data.children[deletedPath - 1] : null;
        final Node rightChild = deletedPath + 1 < n.sliceCount ?
          n.data.children[deletedPath + 1] : null;
        if (leftChild != null && leftChild.sliceCount > m_minBranches) {
        }
        else if (rightChild != null && rightChild.sliceCount > m_minBranches) {
        }
        else { // Merge with a child sibling.
          if (leftChild != null) { // Merge with left child.
            System.arraycopy(affectedChild);
          }
          else { // Merge with right child.
          }
        }
        return true;
      } // End underflow handling logic.




      else { // No underflow.
        return true;
      }
    }
  }

  /*
   * Copies into leftSibling.  You can discard rightSibling after this.
   * Updates counts and nulls out entries as appropriate.
   */
  private final static void mergeSiblings(final Node leftSibling,
                                          final Node rightSibling,
                                          final int splitValue) {
    if (isLeafNode(leftSibling)) {
      System.arraycopy(rightSibling.values, 0,
                       leftSibling.values, leftSibling.sliceCount,
                       rightSibling.sliceCount);
      leftSibling.sliceCount += rightSibling.sliceCount;
      rightSibling.sliceCount = 0; /* Pedantic. */ }
    else {
      System.arraycopy(rightSibling.data.splitVals, 0,
                       leftSibling.data.splitVals, leftSibling.sliceCount,
                       rightSibling.sliceCount - 1);
      leftSibling.data.splitVals[leftSibling.sliceCount - 1] = splitValue;
      System.arraycopy(rightSibling.data.children, 0,
                       leftSibling.data.children, leftSibling.sliceCount,
                       rightSibling.sliceCount);
      for (int i = 0; i < rightSibling.sliceCount; i++) {
        rightSibling.children[i] = null; /* Pedantic. */ }
      leftSibling.sliceCount += rightSibling.sliceCount;
      rightSibling.sliceCount = 0; // Pedantic.
      leftSibling.data.deepCount += rightSibling.data.deepCount;
      rightSibling.data.deepCount = 0 /* Pedantic. */ }
  }

}
