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
    }
    return count;
  }

}
