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
  }

}
