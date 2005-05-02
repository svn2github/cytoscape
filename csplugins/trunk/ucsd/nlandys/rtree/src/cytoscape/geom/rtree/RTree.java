package cytoscape.geom.rtree;

import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntStack;

/**
 * An in-memory R-tree over real numbers in two dimensions.
 */
public final class RTree
{

  private final static int DEFAULT_MAX_BRANCHES = 7;

  private final double[] m_mbr;
  private final int m_maxBranches;
  private Node m_root;

  /**
   * Instantiates a new R-tree.  A new R-tree has no entries.
   */
  public RTree()
  {
    m_mbr = new double[] {
      Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
      Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY };
    m_maxBranches = DEFAULT_MAX_BRANCHES;
    m_root = new Node(m_maxBranches, true);
  }

  /**
   * Empties this R-tree of all entries.  This method returns in constant
   * time (note however that garbage collection will take place in the
   * background).
   */
  public final void empty() {}

//   /**
//    * Returns the number of entries currently in this R-tree.  This method
//    * returns in constant time.<p>
//    * NOTE: To retrieve an enumeration of all entries in this R-tree, call
//    * queryOverlap() with Double.NEGATIVE_INFINITY minimum values and
//    * Double.POSITIVE_INFINITY maximum values.
//    */
//   public final int size() { return 0; }

  private final static boolean isLeafNode(final Node n)
  {
    return n.data == null;
  }

  /**
   * Inserts a new data entry into this tree; the entry's extents
   * are specified by the input parameters.  "Extents" is a short way
   * of saying "minimum bounding rectangle".  The minimum bounding rectangle
   * of an entry is axis-aligned, meaning that its sides are parallel to the
   * axes of the data space.
   * @param objKey a user-defined unique identifier used to refer to the entry
   *   being inserted in later operations; this identifier must be positive
   *   and cannot be equal to Integer.MAX_VALUE.
   * @param xMin the minimum X coordinate of the entry's extents rectangle.
   * @param yMin the minimum Y coordinate of the entry's extents rectangle.
   * @param xMax the maximum X coordinate of the entry's extents rectangle.
   * @param yMax the maximum Y coordinate of the entry's extents rectangle.
   * @exception IllegalStateException if objKey is already used for an
   *   existing entry in this R-tree.
   * @exception IllegalArgumentException if objKey is negative or equal to
   *   Integer.MAX_VALUE, if xMin is greater than xMax, or if yMin is greater
   *   than yMax.
   */
  public final void insert(final int objKey,
                           final double xMin, final double yMin,
                           final double xMax, final double yMax)
  {
    if (xMin > xMax)
      throw new IllegalArgumentException("xMin > xMax");
    if (yMin > yMax)
      throw new IllegalArgumentException("yMin > yMax");
  }

  private final static Node insert(final Node n, final int objKey,
                                   final double xMin, final double yMin,
                                   final double xMax, final double yMax,
                                   final int maxBranches)
  {
    return null;
  }

  /**
   * Determines whether or not a given entry exists in this R-tree structure,
   * and conditionally retrieves the extents of that entry.  The parameter
   * extentsArr is written into by this method only if it is not null
   * and if objKey exists in this R-tree.  The information written into
   * extentsArr consists of the minimum bounding rectangle (MBR) of objKey:
   * <blockquote><table border="1" cellpadding="5" cellspacing="0">
   *   <tr>  <th>array index</th>  <th>value if objKey exists</th>  </tr>
   *   <tr>  <td>offset</td>       <td>xMin of MBR</td>             </tr>
   *   <tr>  <td>offset+1</td>     <td>yMin of MBR</td>             </tr>
   *   <tr>  <td>offset+2</td>     <td>xMax of MBR</td>             </tr>
   *   <tr>  <td>offset+3</td>     <td>yMax of MBR</td>             </tr>
   * </table></blockquote>
   * The values written into extentsArr are exactly the same ones that
   * were previously passed to insert() using the same objKey.
   * @param objKey a user-defined identifier that was [potentially] used in
   *   a previous insertion.
   * @param extentsArr an array to which extent values will be written by this
   *   method; may be null.
   * @param offset specifies the beginning index of where to write extent
   *   values into extentsArr; exactly four entries are written starting at
   *   this index (see above table); if extentsArr is null then this offset
   *   is ignored.
   * @return true if and only if objKey was previously inserted into this
   *   R-tree and has not since been deleted.
   * @exception ArrayIndexOutOfBoundsException if objKey exists, if
   *   extentsArr is not null, and if extentsArr cannot be written
   *   to in the index range [offset, offset+3].
   */
  public final boolean exists(final int objKey, final double[] extentsArr,
                              final int offset)
  {
    return false;
  }

  /**
   * Deletes the specified data entry from this tree.
   * @param objKey a user-defined identifier that was potentially used in a
   *   previous insertion.
   * @return true if and only if objKey existed in this R-tree prior to this
   *   method invocation.
   */
  public final boolean delete(final int objKey)
  {
    return false;
  }

  /**
   * Returns an enumeration of entries whose extents intersect the
   * specified axis-aligned rectangular area.  By "axis-aligned" I mean that
   * the query rectangle's sides are parallel to the axes of the data
   * space.<p>
   * The parameter extentsArr is written into by this method if it is not null.
   * It provides a way for this method to communicate additional information
   * to the caller of this method.  If not null, extentsArr is populated with
   * information regarding the minimum bounding rectangle (MBR) that contains
   * all returned entries.  The following table describes what is written to
   * extentsArr if it is not null:
   * <blockquote><table border="1" cellpadding="5" cellspacing="0">
   *   <tr>  <th>array index</th>  <th>value if query generates results</th>
   *           <th>value if query does not generate results</th>  </tr>
   *   <tr>  <td>offset</td>       <td>xMin of MBR</td>
   *           <td>Double.POSITIVE_INFINITY</td>                  </tr>
   *   <tr>  <td>offset+1</td>     <td>yMin of MBR</td>
   *           <td>Double.POSITIVE_INFINITY</td>                  </tr>
   *   <tr>  <td>offset+2</td>     <td>xMax of MBR</td>
   *           <td>Double.NEGATIVE_INFINITY</td>                  </tr>
   *   <tr>  <td>offset+3</td>     <td>yMax of MBR</td>
   *           <td>Double.NEGATIVE_INFINITY</td>                  </tr>
   * </table></blockquote><p>
   * IMPORTANT: The returned enumeration becomes invalid as soon as any
   * structure-modifying operation (insert or delete) is performed on this
   * R-tree.  Accessing an invalid enumeration's methods will result in
   * unpredictable and ill-defined behavior in that enumeration, but will
   * have no effect on the integrity of the underlying tree structure.
   * @param xMin the minimum X coordinate of the query rectangle.
   * @param yMin the minimum Y coordinate of the query rectangle.
   * @param xMax the maximum X coordinate of the query rectangle.
   * @param yMax the maximum Y coordinate of the query rectangle.
   * @param extentsArr an array to which
   *   extent values will be written by this method; may be null.
   * @param offset specifies the beginning index of where to write extent
   *   values into extentsArr; exactly four entries are written starting at
   *   this index (see table above); if extentsArr is null then this offset
   *   is ignored.
   * @return a non-null enumeration of all [distinct] R-tree entries
   *   (objKeys) whose extents intersect the specified rectangular query area.
   * @exception IllegalArgumentException if xMin is greater than xMax or if
   *   yMin is greater than yMax.
   * @exception ArrayIndexOutOfBoundsException if extentsArr is not null
   *   and if it cannot be written to in the index range
   *   [offset, offset+3].
   */
  public final IntEnumerator queryOverlap(final double xMin,
                                          final double yMin,
                                          final double xMax,
                                          final double yMax,
                                          final double[] extentsArr,
                                          final int offset)
  {
    if (xMin > xMax)
      throw new IllegalArgumentException("xMin > xMax");
    if (yMin > yMax)
      throw new IllegalArgumentException("yMin > yMax");
    if (extentsArr != null) {
      extentsArr[offset] = Double.POSITIVE_INFINITY;
      extentsArr[offset + 1] = Double.POSITIVE_INFINITY;
      extentsArr[offset + 2] = Double.NEGATIVE_INFINITY;
      extentsArr[offset + 3] = Double.NEGATIVE_INFINITY; }
    final ObjStack nodeStack = new ObjStack();
    final ObjStack stackStack = new ObjStack();
    final int totalCount =
      queryOverlap(m_root, nodeStack, stackStack, xMin, yMin, xMax, yMax,
                   m_mbr[0], m_mbr[1], m_mbr[2], m_mbr[3], extentsArr, offset);
    return new OverlapEnumerator(totalCount, nodeStack, stackStack);
  }

  /*
   * Returns the number of entries under n that overlap specified query
   * rectangle.  Nodes are added to the node stack - internal nodes added
   * recursively contain only overlapping entries, and leaf nodes added
   * should be iterated through to find overlapping entries.
   * (In fact internal nodes added to the node
   * stack are completely contained within specified query rectangle.)
   * An important property is that every node on the returned node stack
   * will recursively contain at least one entry that overlaps the
   * query rectangle, unless n is completely empty.  If n is completely
   * empty, it is expected that its MBR [represented by xMinN, yMinN,
   * xMaxN, and yMaxN] be the infinite inverted rectangle (that is, its
   * min values should all be Double.POSITIVE_INFINITY and its max values
   * should all be Double.NEGATIVE_INFINITY).
   * I'd like to discuss stackStack.  Objects of type IntStack are tossed onto
   * this stack (in other words, stackStack is a stack of IntStack).  For every
   * leaf node on nodeStack, stackStack will contain
   * a corresponding IntStack - if the IntStack is null,
   * then every entry in that leaf node overlaps the query rectangle; if
   * the IntStack is of positive length, then the IntStack contains indices of
   * entries that overlap the query rectangle.
   */
  private final static int queryOverlap(final Node n, final ObjStack nodeStack,
                                        final ObjStack stackStack,
                                        final double xMinQ, final double yMinQ,
                                        final double xMaxQ, final double yMaxQ,
                                        final double xMinN, final double yMinN,
                                        final double xMaxN, final double yMaxN,
                                        final double[] extents, final int off)
  {
    int count = 0;
    if (contains(xMinQ, yMinQ, xMaxQ, yMaxQ, xMinN, yMinN, xMaxN, yMaxN)) {
      // Trivially include node.
      if (isLeafNode(n)) { count += n.entryCount; stackStack.push(null); }
      else { count += n.data.deepCount; }
      nodeStack.push(n);
      if (extents != null) {
        extents[off] = Math.min(extents[off], xMinN);
        extents[off + 1] = Math.min(extents[off + 1], yMinN);
        extents[off + 2] = Math.max(extents[off + 2], xMaxN);
        extents[off + 3] = Math.max(extents[off + 3], yMaxN); } }
    else { // Cannot trivially include node; must recurse.
      if (isLeafNode(n)) {
        final IntStack stack = new IntStack();
        for (int i = 0; i < n.entryCount; i++) {
          if (overlaps(xMinQ, yMinQ, xMaxQ, yMaxQ,
                       n.xMins[i], n.yMins[i], n.xMaxs[i], n.yMaxs[i])) {
            stack.push(i);
            if (extents != null) {
              extents[off] = Math.min(extents[off], n.xMins[i]);
              extents[off + 1] = Math.min(extents[off + 1], n.yMins[i]);
              extents[off + 2] = Math.max(extents[off + 2], n.xMaxs[i]);
              extents[off + 3] = Math.max(extents[off + 3], n.yMaxs[i]); } } }
        if (stack.size() > 0) {
          count = stack.size();
          stackStack.push(stack);
          nodeStack.push(n); } }
      else { // Internal node.
        for (int i = 0; i < n.entryCount; i++) {
          if (overlaps(xMinQ, yMinQ, xMaxQ, yMaxQ,
                       n.xMins[i], n.yMins[i], n.xMaxs[i], n.yMaxs[i])) {
            count += queryOverlap
              (n.data.children[i], nodeStack, stackStack,
               xMinQ, yMinQ, xMaxQ, yMaxQ,
               n.xMins[i], n.yMins[i], n.xMaxs[i], n.yMaxs[i],
               extents, off); } } } }
    return count;
  }

  /*
   * Determines whether or not the first rectangle [specified by the first
   * four parameters] overlaps the second rectangle [specified by the last
   * four parameters].
   */
  private final static boolean overlaps(final double xMin1,
                                        final double yMin1,
                                        final double xMax1,
                                        final double yMax1,
                                        final double xMin2,
                                        final double yMin2,
                                        final double xMax2,
                                        final double yMax2)
  {
    return
      ((Math.max(xMin1, xMin2) <= Math.min(xMax1, xMax2)) &&
       (Math.max(yMin1, yMin2) <= Math.min(yMax1, yMax2)));
  }

  /*
   * Determines whether or not the first rectangle [specified by the first
   * four parameters] fully contains the second rectangle [specified by the
   * last four parameters].  If the second rectangle is the inverted
   * infinite rectangle and the first rectangle is any non-inverted
   * rectangle then this method will return true.
   */
  private final static boolean contains(final double xMin1,
                                        final double yMin1,
                                        final double xMax1,
                                        final double yMax1,
                                        final double xMin2,
                                        final double yMin2,
                                        final double xMax2,
                                        final double yMax2)
  {
    return
      ((xMin1 <= xMin2) && (xMax1 >= xMax2) &&
       (yMin1 <= yMin2) && (yMax1 >= yMax2));
  }

  /**
   * Returns an enumeration of entries whose extents are fully contained
   * within the specified axis-aligned rectangular area.  By "axis-aligned" I
   * mean that the query rectangle's sides are parallel to the axes of the
   * data space.<p>
   * IMPORTANT: The returned enumeration becomes invalid as soon as any
   * structure-modifying operation (insert or delete) is performed on this
   * R-tree.  Accessing an invalid enumeration's methods will result in
   * unpredictable and ill-defined behavior in that enumeration, but will
   * have no effect on the integrity of the underlying tree structure.
   * @param xMin the minimum X coordinate of the query rectangle.
   * @param yMin the minimum Y coordinate of the query rectangle.
   * @param xMax the maximum X coordinate of the query rectangle.
   * @param yMax the maximum Y coordinate of the query rectangle.
   * @return a non-null enumeration of all [distinct] R-tree entries
   *   (objKeys) whose extents are fully contained withing the specified
   *   rectangular area.
   */
  public final IntEnumerator queryEnvelope(final double xMin,
                                           final double yMin,
                                           final double xMax,
                                           final double yMax)
  {
    return null;
  }

  public final IntEnumerator queryContainment(final double xMin,
                                              final double yMin,
                                              final double xMax,
                                              final double yMax)
  {
    return null;
  }

  private final static class Node
  {
    private int entryCount = 0;
    private final double[] xMins;
    private final double[] yMins;
    private final double[] xMaxs;
    private final double[] yMaxs;
    private final int[] objKeys; // null if and only if internal node.
    private final InternalNodeData data;
    private Node(int maxBranches, boolean leafNode) {
      xMins = new double[maxBranches];
      yMins = new double[maxBranches];
      xMaxs = new double[maxBranches];
      yMaxs = new double[maxBranches];
      if (leafNode) { objKeys = new int[maxBranches]; data = null; }
      else { objKeys = null; data = new InternalNodeData(maxBranches); } }
  }

  private final static class InternalNodeData
  {
    private int deepCount;
    private final Node[] children;
    private InternalNodeData(int maxBranches) {
      children = new Node[maxBranches]; }
  }

  private final static class OverlapEnumerator implements IntEnumerator
  {
    private int count;
    private final ObjStack nodeStack;
    private final ObjStack stackStack;
    private Node currentLeafNode;
    private IntStack currentStack;
    private int currentInx;
    private OverlapEnumerator(final int totalCount, final ObjStack nodeStack,
                              final ObjStack stackStack) {
      count = totalCount;
      this.nodeStack = nodeStack;
      this.stackStack = stackStack;
      computeNextLeafNode(); }
    public final int numRemaining() { return count; }
    public final int nextInt() {
      int returnThis = -1;
      if (currentStack == null) {
        returnThis = currentLeafNode.objKeys[currentInx++];
        if (currentInx == currentLeafNode.entryCount) {
          computeNextLeafNode(); } }
      else {
        returnThis = currentLeafNode.objKeys[currentStack.pop()];
        if (currentStack.size() == 0) {
          computeNextLeafNode(); } }
      count--;
      return returnThis; }
    private final void computeNextLeafNode() {
      if (nodeStack.size() == 0) {
        currentLeafNode = null; currentStack = null; return; }
      Node next;
      while (true) {
        next = (Node) nodeStack.pop();
        if (isLeafNode(next)) {
          currentLeafNode = next;
          currentStack = (IntStack) stackStack.pop(); // May be null.
          currentInx = 0; // If currentStack isn't null, this will be ignored.
          return; }
        for (int i = 0; i < next.entryCount; i++) {
          // This 'if' statement could be taken out of 'for' loop for speed.
          if (isLeafNode(next.data.children[i])) stackStack.push(null);
          nodeStack.push(next.data.children[i]); } } }
  }

}
