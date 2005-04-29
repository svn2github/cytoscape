package cytoscape.geom.rtree;

import cytoscape.util.intr.IntEnumerator;

/**
 * An in-memory R-tree over real numbers in two dimensions.
 */
public final class RTree
{

  private final static int DEFAULT_MAX_BRANCHES = 7;

  private final int m_maxBranches;
  private Node m_root;

  /**
   * Instantiates a new R-tree.  A new R-tree has no entries.
   */
  public RTree()
  {
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
   *   Integer.MAX_VALUE.
   */
  public final void insert(final int objKey,
                           final double xMin, final double yMin,
                           final double xMax, final double yMax)
  {
  }

//   /**
//    * Determines whether or not a given entry exists in this R-tree structure.<p>
//    * NOTE: To retrieve an enumeration of all entries in this R-tree, call
//    * queryOverlap() with Double.NEGATIVE_INFINITY minimum values and
//    * Double.POSITIVE_INFINITY maximum values.
//    * @param objKey a user-defined identifier that was potentially used
//    *   in a previous insertion.
//    * @return true if and only if objKey was previously inserted into this
//    *   R-tree and has not since been deleted.
//    */
//   public final boolean exists(final int objKey)
//   {
//     return false;
//   }

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
    return null;
  }

  /*
   * Returns the number of entries under n that overlap specified query
   * rectangle.  Nodes are added to the stack - internal nodes added
   * recursively contain only overlapping entries, and leaf nodes added are
   * overlapping.  (You can quickly deduce that internal nodes added to the
   * stack are completely contained withing specified query rectangle.)
   */
  private final static int queryOverlap(final Node n, final NodeStack stack,
                                        final double xMin, final double yMin,
                                        final double xMax, final double yMax)
  {
    return -1;
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

  private final static class NodeStack
  {
    private Node[] stack;
    private int currentSize = 0;
    private NodeStack() { stack = new Node[3]; }
    private final void push(final Node value) {
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
