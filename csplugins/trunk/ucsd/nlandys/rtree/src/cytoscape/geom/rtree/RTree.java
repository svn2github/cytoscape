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
  private final int m_minBranches;
  private Node m_root;
  private IntObjHash m_entryMap; // Keys are objKey, values are type Node.

  // These four buffer variables are used during node splitting.
  private final int[] m_objKeyBuff;
  private final Node[] m_childrenBuff;
  private final double[] m_xMinBuff;
  private final double[] m_yMinBuff;
  private final double[] m_xMaxBuff;
  private final double[] m_yMaxBuff;
  private final double[] m_tempBuff1;
  private final double[] m_tempBuff2;

  /**
   * Instantiates a new R-tree.  A new R-tree has no entries.
   */
  public RTree()
  {
    m_mbr = new double[] {
      Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
      Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY };
    m_maxBranches = DEFAULT_MAX_BRANCHES;
    m_minBranches = (m_maxBranches + 1) / 2;
    m_root = new Node(m_maxBranches, true);
    m_objKeyBuff = new int[m_maxBranches + 1];
    m_childrenBuff = new Node[m_maxBranches + 1];
    m_xMinBuff = new double[m_maxBranches + 1];
    m_yMinBuff = new double[m_maxBranches + 1];
    m_xMaxBuff = new double[m_maxBranches + 1];
    m_yMaxBuff = new double[m_maxBranches + 1];
    m_tempBuff1 = new double[m_maxBranches + 1];
    m_tempBuff2 = new double[m_maxBranches + 1];
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
   *   being inserted in later operations; this identifier must be
   *   non-negative.
   * @param xMin the minimum X coordinate of the entry's extents rectangle.
   * @param yMin the minimum Y coordinate of the entry's extents rectangle.
   * @param xMax the maximum X coordinate of the entry's extents rectangle.
   * @param yMax the maximum Y coordinate of the entry's extents rectangle.
   * @exception IllegalStateException if objKey is already used for an
   *   existing entry in this R-tree.
   * @exception IllegalArgumentException if objKey is negative,
   *   if xMin is greater than xMax, or if yMin is greater than yMax.
   */
  public final void insert(final int objKey,
                           final double xMin, final double yMin,
                           final double xMax, final double yMax)
  {
    if (objKey < 0) throw new IllegalArgumentException("objKey is negative");
    if (xMin > xMax)
      throw new IllegalArgumentException("xMin > xMax");
    if (yMin > yMax)
      throw new IllegalArgumentException("yMin > yMax");
    if (m_entryMap.get(objKey) != null) // The hashtable caches lookups so
      throw new IllegalStateException   // subsequent put() is almost free.
        ("objkey " + objKey + " is already in this tree");
    final Node chosenLeaf = chooseLeaf(m_root, xMin, yMin, xMax, yMax);
    // Do stuff.
  }

  /*
   * Returns a leaf node.  The returned leaf node is chosen by this
   * algorithm as the most suitable leaf node [under specified root] in
   * which to place specified new entry.
   */
  private final static Node chooseLeaf(final Node root,
                                       final double xMin, final double yMin,
                                       final double xMax, final double yMax)
  {
    Node n = root;
    while (!isLeafNode(n))
      n = n.data.children[chooseSubtree(n, xMin, yMin, xMax, yMax)];
    return n;
  }

  /*
   * Returns the index of entry in n whose rectangular boundary
   * needs least enlargment to swallow the input rectangle.  Ties are resolved
   * by choosing the entry with the rectangle of smallest area.
   */
  private final static int chooseSubtree(final Node n,
                                         final double xMin, final double yMin,
                                         final double xMax, final double yMax)
  {
    double bestAreaDelta = Double.POSITIVE_INFINITY;
    double bestArea = Double.POSITIVE_INFINITY;
    int bestInx = -1;
    for (int i = 0; i < n.entryCount; i++) {
      final double currArea =
        (n.xMaxs[i] - n.xMins[i]) * (n.yMaxs[i] - n.yMins[i]);
      final double newArea =
        (Math.max(n.xMaxs[i], xMax) - Math.min(n.xMins[i], xMin)) *
        (Math.max(n.yMaxs[i], yMax) - Math.min(n.yMins[i], yMin));
      final double currAreaDelta = newArea - currArea;
      if ((currAreaDelta < bestAreaDelta) ||
          (currAreaDelta == bestAreaDelta && currArea < bestArea)) {
        bestAreaDelta = currAreaDelta;
        bestArea = currArea;
        bestInx = i; } }
    return bestInx;
  }

  /*
   * This is the quadratic-cost algorithm described in Guttman's 1984
   * R-tree paper.  The parent pointer of returned node is not set.  The
   * parent pointer in the full node is not modified, and nothing in that
   * parent is modified.  Everything else is modified.  The MBRs at index
   * m_maxBranches - 1 in both nodes are set to be the new overall MBR of
   * corresponding node.
   */
  private final Node splitLeafNode(final Node fullLeafNode,
                                   final int newObjKey,
                                   final double newXMin,
                                   final double newYMin,
                                   final double newXMax,
                                   final double newYMax)
  {
    final int maxBranchesMinusOne = m_maxBranches - 1;

    // Copy node MBRs and objKeys and new MBR and objKey into arrays.
    for (int i = 0; i < fullLeafNode.entryCount; i++) {
      m_objKeyBuff[i] = fullLeafNode.objKeys[i];
      m_xMinBuff[i] = fullLeafNode.xMins[i];
      m_yMinBuff[i] = fullLeafNode.yMins[i];
      m_xMaxBuff[i] = fullLeafNode.xMaxs[i];
      m_yMaxBuff[i] = fullLeafNode.yMaxs[i]; }
    m_objKeyBuff[fullLeafNode.entryCount] = newObjKey;
    m_xMinBuff[fullLeafNode.entryCount] = newXMin;
    m_yMinBuff[fullLeafNode.entryCount] = newYMin;
    m_xMaxBuff[fullLeafNode.entryCount] = newXMax;
    m_yMaxBuff[fullLeafNode.entryCount] = newYMax;

    // Pick seeds.  Add seeds to two groups (fullLeafNode and returnThis).
    final int totalEntries = fullLeafNode.entryCount + 1;
    final long seeds = pickSeeds(totalEntries, m_xMinBuff, m_yMinBuff,
                                 m_xMaxBuff, m_yMaxBuff, m_tempBuff1);
    final int seed1 = (int) (seeds >> 32);
    fullLeafNode.objKeys[0] = m_objKeyBuff[seed1];
    fullLeafNode.xMins[0] = m_xMinBuff[seed1];
    fullLeafNode.yMins[0] = m_yMinBuff[seed1];
    fullLeafNode.xMaxs[0] = m_xMaxBuff[seed1];
    fullLeafNode.yMaxs[0] = m_yMaxBuff[seed1];
    fullLeafNode.entryCount = 1;
    final int seed2 = (int) seeds;
    final Node returnThis = new Node(m_maxBranches, true);
    returnThis.objKeys[0] = m_objKeyBuff[seed2];
    returnThis.xMins[0] = m_xMinBuff[seed2];
    returnThis.yMins[0] = m_yMinBuff[seed2];
    returnThis.xMaxs[0] = m_xMaxBuff[seed2];
    returnThis.yMaxs[0] = m_yMaxBuff[seed2];
    returnThis.entryCount = 1;

    // Initialize the MBRs at index m_maxBranches - 1.
    fullLeafNode.xMins[maxBranchesMinusOne] = fullLeafNode.xMins[0];
    fullLeafNode.yMins[maxBranchesMinusOne] = fullLeafNode.yMins[0];
    fullLeafNode.xMaxs[maxBranchesMinusOne] = fullLeafNode.xMaxs[0];
    fullLeafNode.yMaxs[maxBranchesMinusOne] = fullLeafNode.yMaxs[0];
    returnThis.xMins[maxBranchesMinusOne] = returnThis.xMins[0];
    returnThis.yMins[maxBranchesMinusOne] = returnThis.yMins[0];
    returnThis.xMaxs[maxBranchesMinusOne] = returnThis.xMaxs[0];
    returnThis.yMaxs[maxBranchesMinusOne] = returnThis.yMaxs[0];

    // Collapse the arrays where seeds used to be.
    int entriesRemaining = totalEntries - 2;
    for (int i = seed1; i < seed2 - 1; i++) { // seed1 < seed2, guarenteed.
      final int iPlusOne = i + 1;
      m_objKeyBuff[i] = m_objKeyBuff[iPlusOne];
      m_xMinBuff[i] = m_xMinBuff[iPlusOne];
      m_yMinBuff[i] = m_yMinBuff[iPlusOne];
      m_xMaxBuff[i] = m_xMaxBuff[iPlusOne];
      m_yMaxBuff[i] = m_yMaxBuff[iPlusOne]; }
    for (int i = seed2 - 1; i < entriesRemaining; i++) {
      final int iPlusTwo = i + 2;
      m_objKeyBuff[i] = m_objKeyBuff[iPlusTwo];
      m_xMinBuff[i] = m_xMinBuff[iPlusTwo];
      m_yMinBuff[i] = m_yMinBuff[iPlusTwo];
      m_xMaxBuff[i] = m_xMaxBuff[iPlusTwo];
      m_yMaxBuff[i] = m_yMaxBuff[iPlusTwo]; }
    
    boolean buff1Valid = false;
    boolean buff2Valid = false;
    while (true) {

      // Test to see if we're all done.
      if (entriesRemaining == 0) break;
      final Node restGroup;
      if (entriesRemaining + fullLeafNode.entryCount == m_minBranches)
        restGroup = fullLeafNode;
      else if (entriesRemaining + returnThis.entryCount == m_minBranches)
        restGroup = returnThis;
      else
        restGroup = null;

      if (restGroup != null) { // Assign remaining entries to this group.
        for (int i = 0; i < entriesRemaining; i++) {

          // Add entry to "rest" group.
          final int newInx = restGroup.entryCount++;
          restGroup.objKeys[newInx] = m_objKeyBuff[i];
          restGroup.xMins[newInx] = m_xMinBuff[i];
          restGroup.yMins[newInx] = m_yMinBuff[i];
          restGroup.xMaxs[newInx] = m_xMaxBuff[i];
          restGroup.yMaxs[newInx] = m_yMaxBuff[i];

          // Update the MBR of "rest" group.
          restGroup.xMins[maxBranchesMinusOne] =
            Math.min(restGroup.xMins[maxBranchesMinusOne], m_xMinBuff[i]);
          restGroup.yMins[maxBranchesMinusOne] =
            Math.min(restGroup.yMins[maxBranchesMinusOne], m_yMinBuff[i]);
          restGroup.xMaxs[maxBranchesMinusOne] =
            Math.max(restGroup.xMaxs[maxBranchesMinusOne], m_xMaxBuff[i]);
          restGroup.yMaxs[maxBranchesMinusOne] =
            Math.max(restGroup.yMaxs[maxBranchesMinusOne], m_yMaxBuff[i]); }

        break; }

      // We're not done; pick next.
      final int next = pickNext
        (fullLeafNode, returnThis, entriesRemaining,
         m_xMinBuff, m_yMinBuff, m_xMaxBuff, m_yMaxBuff,
         m_tempBuff1, buff1Valid, m_tempBuff2, buff2Valid);
      final boolean chooseGroup1;
      if (m_tempBuff1[next] < m_tempBuff2[next]) chooseGroup1 = true;
      else if (m_tempBuff1[next] > m_tempBuff2[next]) chooseGroup1= false;
      else { // Tie for how much group's covering rectangle will increase.
        final double group1Area =
          (fullLeafNode.xMaxs[maxBranchesMinusOne] -
           fullLeafNode.xMins[maxBranchesMinusOne]) *
          (fullLeafNode.yMaxs[maxBranchesMinusOne] -
           fullLeafNode.yMins[maxBranchesMinusOne]);
        final double group2Area =
          (returnThis.xMaxs[maxBranchesMinusOne] -
           returnThis.xMins[maxBranchesMinusOne]) *
          (returnThis.yMaxs[maxBranchesMinusOne] -
           returnThis.yMins[maxBranchesMinusOne]);
        if (group1Area < group2Area) chooseGroup1 = true;
        else if (group1Area > group2Area) chooseGroup1 = false;
        else // Tie for group MBR area as well.
          if (fullLeafNode.entryCount < returnThis.entryCount)
            chooseGroup1 = true;
          else
            chooseGroup1 = false; }
      final Node chosenGroup;
      final double[] validTempBuff;
      if (chooseGroup1) {
        chosenGroup = fullLeafNode; validTempBuff = m_tempBuff2;
        buff1Valid = false; buff2Valid = true; }
      else {
        chosenGroup = returnThis; validTempBuff = m_tempBuff1;
        buff1Valid = true; buff2Valid = false; }

      // Add next to chosen group.
      final int newInx = chosenGroup.entryCount++;
      chosenGroup.objKeys[newInx] = m_objKeyBuff[next];
      chosenGroup.xMins[newInx] = m_xMinBuff[next];
      chosenGroup.yMins[newInx] = m_yMinBuff[next];
      chosenGroup.xMaxs[newInx] = m_xMaxBuff[next];
      chosenGroup.yMaxs[newInx] = m_yMaxBuff[next];

      // Update the MBR of chosen group.
      // Note: If we see that the MBR stays the same, we could mark the
      // "invalid" temp buff array as valid to save even more on computations.
      chosenGroup.xMins[maxBranchesMinusOne] =
        Math.min(chosenGroup.xMins[maxBranchesMinusOne], m_xMinBuff[next]);
      chosenGroup.yMins[maxBranchesMinusOne] =
        Math.min(chosenGroup.yMins[maxBranchesMinusOne], m_yMinBuff[next]);
      chosenGroup.xMaxs[maxBranchesMinusOne] =
        Math.max(chosenGroup.xMaxs[maxBranchesMinusOne], m_xMaxBuff[next]);
      chosenGroup.yMaxs[maxBranchesMinusOne] =
        Math.max(chosenGroup.yMaxs[maxBranchesMinusOne], m_yMaxBuff[next]);

      // Collapse the arrays where next used to be.
      entriesRemaining--;
      for (int i = next; i < entriesRemaining; i++) {
        final int iPlusOne = i + 1;
        m_objKeyBuff[i] = m_objKeyBuff[iPlusOne];
        m_xMinBuff[i] = m_xMinBuff[iPlusOne];
        m_yMinBuff[i] = m_yMinBuff[iPlusOne];
        m_xMaxBuff[i] = m_xMaxBuff[iPlusOne];
        m_yMaxBuff[i] = m_yMaxBuff[iPlusOne];
        validTempBuff[i] = validTempBuff[iPlusOne]; } }

    return returnThis;
  }

  /*
   * This is the quadratic-cost algorithm described by Guttman.
   * The first seed's index is returned as the 32 most significant bits
   * of returned quantity.  The second seed's index is returned as the 32
   * least significant bits of returned quantity.  The first seed's index
   * is closer to zero than the second seed's index.  None of the input
   * arrays are modified except for tempBuff.  tempBuff is populated with
   * the areas of the MBRs.
   */
  private final static long pickSeeds(final int count,
                                      final double[] xMins,
                                      final double[] yMins,
                                      final double[] xMaxs,
                                      final double[] yMaxs,
                                      final double[] tempBuff)
  {
    for (int i = 0; i < count; i++)
      tempBuff[i] = (xMaxs[i] - xMins[i]) * (yMaxs[i] - yMins[i]); // Area.
    double maximumD = Double.NEGATIVE_INFINITY;
    int maximumInx1 = -1;
    int maximumInx2 = -1;
    for (int i = 0; i < count; i++)
      for (int j = i + 1; j < count; j++) {
        final double areaJ =
          (Math.max(xMaxs[i], xMaxs[j]) - Math.min(xMins[i], xMins[j])) *
          (Math.max(yMaxs[i], yMaxs[j]) - Math.min(yMins[i], yMins[j]));
        final double d = areaJ - tempBuff[i] - tempBuff[j];
        if (d > maximumD) {
          maximumD = d;
          maximumInx1 = i;
          maximumInx2 = j; } }
    return (((long) maximumInx1) << 32) | ((long) maximumInx2);
  }

  /*
   * Returns the index (in xMins, etc.) of next entry to add to a group.
   * The arrays tempBuff1 and tempBuff2 are used to store the [positive]
   * area increase required in respective groups to swallow corresponding
   * MBR at same index.  If buff1Valid is true then tempBuff1 already
   * contains this information and it need not be computed by this
   * method.  Analagous is true for buff2Valid and tempBuff2.  The nodes
   * group1 and group2 are only used by this method to read information
   * of current MBR of corresponding group - the MBR is stored at index
   * m_maxBranches - 1.  None of the input variables are modified except
   * for tempBuff1 and tempBuff2.
   */
  private final static int pickNext(final Node group1,
                                    final Node group2,
                                    final int count,
                                    final double[] xMins,
                                    final double[] yMins,
                                    final double[] xMaxs,
                                    final double[] yMaxs,
                                    final double[] tempBuff1,
                                    final boolean buff1Valid,
                                    final double[] tempBuff2,
                                    final boolean buff2Valid)
  {
    final int maxBranches = group1.xMins.length;
    if (!buff1Valid) {
      final double group1Area =
        (group1.xMaxs[maxBranches - 1] - group1.xMins[maxBranches - 1]) *
        (group1.yMaxs[maxBranches - 1] - group1.yMins[maxBranches - 1]);
      for (int i = 0; i < count; i++) {
        tempBuff1[i] = // Area of group1 swallowing ith rectangle.
          (Math.max(group1.xMaxs[maxBranches - 1], xMaxs[i]) -
           Math.min(group1.xMins[maxBranches - 1], xMins[i])) *
          (Math.max(group1.yMaxs[maxBranches - 1], yMaxs[i]) -
           Math.min(group1.yMins[maxBranches - 1], yMins[i]));
        tempBuff1[i] -= group1Area; } }
    if (!buff2Valid) {
      final double group2Area =
        (group2.xMaxs[maxBranches - 1] - group2.xMins[maxBranches - 1]) *
        (group2.yMaxs[maxBranches - 1] - group2.yMins[maxBranches - 1]);
      for (int i = 0; i < count; i++) {
        tempBuff2[i] = // Area of group2 swallowing ith rectangle.
          (Math.max(group2.xMaxs[maxBranches - 1], xMaxs[i]) -
           Math.min(group2.xMins[maxBranches - 1], xMins[i])) *
          (Math.max(group2.yMaxs[maxBranches - 1], yMaxs[i]) -
           Math.min(group2.yMins[maxBranches - 1], yMins[i]));
        tempBuff2[i] -= group2Area; } }
    double maxDDifference = Double.NEGATIVE_INFINITY;
    int maxInx = -1;
    for (int i = 0; i < count; i++) {
      final double currDDifference = Math.abs(tempBuff1[i] - tempBuff2[i]);
      if (currDDifference > maxDDifference) {
        maxDDifference = currDDifference;
        maxInx = i; } }
    return maxInx;
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
    if (objKey < 0) return false;
    final Object o = m_entryMap.get(objKey);
    if (o == null) return false;
    if (extentsArr != null) {
      final Node n = (Node) o;
      int i = -1;
      while (n.objKeys[++i] != objKey);
      extentsArr[offset] = n.xMins[i];
      extentsArr[offset + 1] = n.yMins[i];
      extentsArr[offset + 2] = n.xMaxs[i];
      extentsArr[offset + 3] = n.yMaxs[i]; }
    return true;
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
    private Node parent;
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
    private int deepCount = 0;
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
