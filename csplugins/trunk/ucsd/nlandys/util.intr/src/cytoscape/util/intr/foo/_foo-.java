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
      if (deletedPath < 0) { return false; }
      final Node affectedChild = n.data.children[deletedPath];
      if (affectedChild.sliceCount < m_minBranches) { // Underflow handling.
        final Node leftChild =
          deletedPath > 0 ? n.data.children[deletedPath - 1] : null;
        final Node rightChild =
          deletedPath + 1 < n.sliceCount ?
          n.data.children[deletedPath + 1] : null;
        if (leftChild != null && leftChild.sliceCount > m_minBranches) {
          n.data.splitVals[deletedPath - 1] = distributeFromLeft
            (leftChild, affectedChild, n.data.splitVals[deletedPath - 1]); }
        else if (rightChild != null && rightChild.sliceCount > m_minBranches) {
          n.data.splitVals[deletedPath] = distributeFromRight
            (rightChild, affectedChild, n.data.splitVals[deletedPath]);
        else { // Merge with a child sibling.
          final int holeInx;
          if (leftChild != null) // Merge with left child.
            mergeSiblings(leftChild, affectedChild,
                          n.data.splitVals[holeInx = deletedPath - 1]);
          else // Merge with right child.
            mergeSiblings(affectedChild, rightChild,
                          n.data.splitVals[holeInx = deletedPath]);
          fillHole(n.data.children, holeInx + 1, --n.sliceCount);
          fillHole(n.data.splitVals, holeInx, n.sliceCount - 1); } }
      return true;
    }
  }

  /*
   * Returns a new splitVal.  Updates counts and nulls out entries as
   * appropriate.
   */
  private final int distributeFromLeft(final Node leftSibling,
                                       final Node thisSibling,
                                       final int oldSplitVal)
  {
    final int distributeNum = (1 + leftSibling.sliceCount - m_minBranches) / 2;
    if (isLeafNode(leftSibling)) {
      for (int i = thisSibling.sliceCount, o = i + distributeNum; i > 0;)
        thisSibling.values[--o] = thisSibling.values[--i];
      System.arraycopy
        (leftSibling.values, leftSibling.sliceCount - distributeNum,
         thisSibling.values, 0, distributeNum);
      leftSibling.sliceCount -= distributeNum;
      thisSibling.sliceCount += distributeNum;
      return thisSibling.values[0]; }
    else {
      for (int i = thisSibling.sliceCount, o = i + distributeNum; i > 0;)
        thisSibling.data.children[--o] = thisSibling.data.children[--i];
      System.arraycopy
        (leftSibling.data.children, leftSibling.sliceCount - distributeNum,
         thisSibling.data.children, 0, distributeNum);
      for (int i = thisSibling.sliceCount - 1, o = i + distributeNum; i > 0;)
        thisSibling.data.splitVals[--o] = thisSibling.data.splitVals[--i];
      thisSibling.data.splitVals[distributeNum - 1] = oldSplitVal;
      System.arraycopy
        (leftSibling.data.splitVals, leftSibling.sliceCount - distributeNum,
         thisSibling.data.splitVals, 0, distributeNum - 1);
      final int returnThis =
        leftSibling.data.splitVals[leftSibling.sliceCount - distributeNum - 1];
      int deepCountDiff = 0;
      for (int i = leftSibling.sliceCount - distributeNum;
           i < leftSibling.sliceCount; i++) {
        deepCountDiff += leftSibling.data.children[i].data.deepCount;
        leftSibling.data.children[i] = null; }
      leftSibling.sliceCount -= distributeNum;
      thisSibling.sliceCount += distributeNum;
      leftSibling.data.deepCount -= deepCountDiff;
      thisSibling.data.deepCount += deepCountDiff;
      return returnThis; }
  }

  /*
   * Returns a new splitVal.  Updates counts and nulls out entries as
   * appropriate.
   */
  private final int distributeFromRight(final Node rightSibling,
                                        final Node thisSibling,
                                        final int oldSplitVal)
  {
    final int distributeNum =
      (1 + rightSibling.sliceCount - m_minBranches) / 2;
    if (isLeafNode(rightSibling)) {
      System.arraycopy(rightSibling.values, 0,
                       thisSibling.values, thisSibling.sliceCount,
                       distributeNum);
      for (int i = 0, o = distributeNum; o < rightSibling.sliceCount;)
        rightSibling.values[i++] = rightSibling.values[o++];
      rightSibling.sliceCount -= distributeNum;
      thisSibling.sliceCount += distributeNum;
      return rightSibling.values[0]; }
    else {
      final int returnThis = rightSibling.data.splitVals[distributeNum - 1];
      int deepCountDiff = 0;
      for (int i = 0, o = thisSibling.sliceCount; i < distributeNum;) {
        deepCountDiff += rightSibling.data.children[i].data.deepCount;
        thisSibling.data.children[o++] = rightSibling.data.children[i++]; }
      for (int i = distributeNum, o = 0; i < rightSibling.sliceCount;)
        rightSibling.data.children[o++] = rightSibling.data.children[i++];
      for (int i = rightSibling.sliceCount - distributeNum;
           i < rightSibling.sliceCount; i++)
        rightSibling.data.children[i] = null;
      thisSibling.data.splitVals[thisSibling.sliceCount - 1] = oldSplitVal;
      System.arraycopy(rightSibling.data.splitVals, 0,
                       thisSibling.data.splitVals, thisSibling.sliceCount,
                       distributeNum - 1);
      for (int i = distributeNum, o = 0; i < rightSibling.sliceCount - 1;)
        rightSibling.data.splitVals[o++] = rightSibling.data.splitVals[i++];
      rightSibling.sliceCount -= distributeNum;
      thisSibling.sliceCount += distributeNum;
      rightSibling.data.deepCount -= deepCountDiff;
      thisSibling.data.deepCount += deepCountDiff;
      return returnThis; }
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
