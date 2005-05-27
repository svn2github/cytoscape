package cytoscape.geom.rtree;

import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntStack;

/**
 * An in-memory R-tree over real numbers in two dimensions.
 */
public final class RTree
{

  private final static int DEFAULT_MAX_BRANCHES = 7;

  private final double[] m_MBR; // { xMin, yMin, xMax, yMax }.
  private final int m_maxBranches;
  private final int m_minBranches;
  private Node m_root;
  private IntObjHash m_entryMap; // Keys are objKey, values are type Node.

  // These buffers are used during node splitting.
  private final int[] m_objKeyBuff;
  private final Node[] m_childrenBuff;
  private final double[] m_xMinBuff;
  private final double[] m_yMinBuff;
  private final double[] m_xMaxBuff;
  private final double[] m_yMaxBuff;
  private final double[] m_tempBuff1;
  private final double[] m_tempBuff2;

  /**
   * Instantiates a new R-tree.  A new R-tree is empty (it has no entries).
   */
  public RTree()
  {
    m_MBR = new double[] {
      Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
      Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY };
    m_maxBranches = DEFAULT_MAX_BRANCHES;
    m_minBranches = Math.max(2, (int) (((double) (m_maxBranches + 1)) * 0.4d));
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

//   /**
//    * Empties this R-tree of all entries.  This method returns in constant
//    * time (note however that garbage collection will take place in the
//    * background).
//    */
//   public final void empty() {}

//   /**
//    * Returns the number of entries currently in this R-tree.  This method
//    * returns in constant time.<p>
//    * NOTE: To retrieve an enumeration of all entries in this R-tree, call
//    * queryOverlap() with Double.NEGATIVE_INFINITY minimum values and
//    * Double.POSITIVE_INFINITY maximum values.
//    */
//   public final int size() { return 0; }

  /*
   * This gets used a lot.  This test is in the form of a function to make
   * the code more readable (as opposed to being inlined).
   */
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
    if (chosenLeaf.entryCount < m_maxBranches) { // No split is necessary.
      final int newInx = chosenLeaf.entryCount++;
      chosenLeaf.objKeys[newInx] = objKey;
      chosenLeaf.xMins[newInx] = xMin; chosenLeaf.yMins[newInx] = yMin;
      chosenLeaf.xMaxs[newInx] = xMax; chosenLeaf.yMaxs[newInx] = yMax;
      m_entryMap.put(objKey, chosenLeaf);
      adjustTreeNoSplit(chosenLeaf, m_MBR); }
    else { // A split is necessary.
      final Node newLeaf = splitLeafNode
        (chosenLeaf, objKey, xMin, yMin, xMax, yMax, m_maxBranches,
         m_minBranches, m_objKeyBuff,  m_xMinBuff, m_yMinBuff, m_xMaxBuff,
         m_yMaxBuff, m_tempBuff1, m_tempBuff2);
      for (int i = 0; i < chosenLeaf.entryCount; i++)
        m_entryMap.put(chosenLeaf.objKeys[i], chosenLeaf);
      for (int i = 0; i < newLeaf.entryCount; i++)
        m_entryMap.put(newLeaf.objKeys[i], newLeaf);
      final Node rootSplit = adjustTreeWithSplit
        (chosenLeaf, newLeaf, m_maxBranches, m_minBranches, m_MBR,
         m_childrenBuff, m_xMinBuff, m_yMinBuff, m_xMaxBuff, m_yMaxBuff,
         m_tempBuff1, m_tempBuff2);
      if (rootSplit != null) {
        // The MBR at index m_maxBranches - 1 in both rootSplit and m_root
        // will contain the overall MBR of corresponding node.
        // Also, both nodes will have an accurate deep count.
        final Node newRoot = new Node(m_maxBranches, false);
        newRoot.entryCount = 2;
        m_root.parent = newRoot; rootSplit.parent = newRoot;
        newRoot.data.children[0] = m_root;
        newRoot.data.children[1] = rootSplit;
        newRoot.xMins[0] = m_root.xMins[m_maxBranches - 1];
        newRoot.yMins[0] = m_root.yMins[m_maxBranches - 1];
        newRoot.xMaxs[0] = m_root.xMaxs[m_maxBranches - 1];
        newRoot.yMaxs[0] = m_root.yMaxs[m_maxBranches - 1];
        newRoot.xMins[1] = rootSplit.xMins[m_maxBranches - 1];
        newRoot.yMins[1] = rootSplit.yMins[m_maxBranches - 1];
        newRoot.xMaxs[1] = rootSplit.xMaxs[m_maxBranches - 1];
        newRoot.yMaxs[1] = rootSplit.yMaxs[m_maxBranches - 1];
        newRoot.data.deepCount =
          m_root.data.deepCount + rootSplit.data.deepCount;
        m_root = newRoot;
        m_MBR[0] = Math.min(m_root.xMins[0], m_root.xMins[1]);
        m_MBR[1] = Math.min(m_root.yMins[0], m_root.yMins[1]);
        m_MBR[2] = Math.max(m_root.xMaxs[0], m_root.xMaxs[1]);
        m_MBR[3] = Math.max(m_root.yMaxs[0], m_root.yMaxs[1]); } }
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
      // A possible optimization would be to add to each node an area cache
      // for each entry.  That way we wouldn't have to compute this area on
      // each insertion.
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
   * parent is modified.  Everything else in the input node and in the
   * returned node is set as appropriate.  The MBRs at index
   * maxBranches - 1 in both nodes are set to be the new overall MBR of
   * corresponding node.  The node returned is also a leaf node.
   * No claim is made as to the resulting values in the buff arrays.
   */
  private final static Node splitLeafNode(final Node fullLeafNode,
                                          final int newObjKey,
                                          final double newXMin,
                                          final double newYMin,
                                          final double newXMax,
                                          final double newYMax,
                                          final int maxBranches,
                                          final int minBranches,
                                          final int[] objKeyBuff,
                                          final double[] xMinBuff,
                                          final double[] yMinBuff,
                                          final double[] xMaxBuff,
                                          final double[] yMaxBuff,
                                          final double[] tempBuff1,
                                          final double[] tempBuff2)
  {
    // Copy node MBRs and objKeys and new MBR and objKey into arrays.
    for (int i = 0; i < fullLeafNode.entryCount; i++) {
      objKeyBuff[i] = fullLeafNode.objKeys[i];
      xMinBuff[i] = fullLeafNode.xMins[i];
      yMinBuff[i] = fullLeafNode.yMins[i];
      xMaxBuff[i] = fullLeafNode.xMaxs[i];
      yMaxBuff[i] = fullLeafNode.yMaxs[i]; }
    objKeyBuff[fullLeafNode.entryCount] = newObjKey;
    xMinBuff[fullLeafNode.entryCount] = newXMin;
    yMinBuff[fullLeafNode.entryCount] = newYMin;
    xMaxBuff[fullLeafNode.entryCount] = newXMax;
    yMaxBuff[fullLeafNode.entryCount] = newYMax;

    // Pick seeds.  Add seeds to two groups (fullLeafNode and returnThis).
    final int totalEntries = fullLeafNode.entryCount + 1;
    final long seeds = pickSeeds(totalEntries, xMinBuff, yMinBuff,
                                 xMaxBuff, yMaxBuff, tempBuff1);
    // tempBuff1 now contains the areas of the MBRs - we won't use this.
    final int seed1 = (int) (seeds >> 32);
    fullLeafNode.objKeys[0] = objKeyBuff[seed1];
    fullLeafNode.xMins[0] = xMinBuff[seed1];
    fullLeafNode.yMins[0] = yMinBuff[seed1];
    fullLeafNode.xMaxs[0] = xMaxBuff[seed1];
    fullLeafNode.yMaxs[0] = yMaxBuff[seed1];
    fullLeafNode.entryCount = 1;
    final int seed2 = (int) seeds;
    final Node returnThis = new Node(maxBranches, true);
    returnThis.objKeys[0] = objKeyBuff[seed2];
    returnThis.xMins[0] = xMinBuff[seed2];
    returnThis.yMins[0] = yMinBuff[seed2];
    returnThis.xMaxs[0] = xMaxBuff[seed2];
    returnThis.yMaxs[0] = yMaxBuff[seed2];
    returnThis.entryCount = 1;

    // Initialize the overall MBRs at index maxBranches - 1.
    fullLeafNode.xMins[maxBranches - 1] = fullLeafNode.xMins[0];
    fullLeafNode.yMins[maxBranches - 1] = fullLeafNode.yMins[0];
    fullLeafNode.xMaxs[maxBranches - 1] = fullLeafNode.xMaxs[0];
    fullLeafNode.yMaxs[maxBranches - 1] = fullLeafNode.yMaxs[0];
    returnThis.xMins[maxBranches - 1] = returnThis.xMins[0];
    returnThis.yMins[maxBranches - 1] = returnThis.yMins[0];
    returnThis.xMaxs[maxBranches - 1] = returnThis.xMaxs[0];
    returnThis.yMaxs[maxBranches - 1] = returnThis.yMaxs[0];

    // Collapse the arrays where seeds used to be.
    int entriesRemaining = totalEntries - 2;
    for (int i = seed1; i < seed2 - 1; i++) { // seed1 < seed2, guaranteed.
      final int iPlusOne = i + 1;
      objKeyBuff[i] = objKeyBuff[iPlusOne];
      xMinBuff[i] = xMinBuff[iPlusOne];
      yMinBuff[i] = yMinBuff[iPlusOne];
      xMaxBuff[i] = xMaxBuff[iPlusOne];
      yMaxBuff[i] = yMaxBuff[iPlusOne]; }
    for (int i = seed2 - 1; i < entriesRemaining; i++) {
      final int iPlusTwo = i + 2;
      objKeyBuff[i] = objKeyBuff[iPlusTwo];
      xMinBuff[i] = xMinBuff[iPlusTwo];
      yMinBuff[i] = yMinBuff[iPlusTwo];
      xMaxBuff[i] = xMaxBuff[iPlusTwo];
      yMaxBuff[i] = yMaxBuff[iPlusTwo]; }
    
    boolean buff1Valid = false;
    boolean buff2Valid = false;
    while (true) {

      // Test to see if we're all done.
      if (entriesRemaining == 0) break;
      final Node restGroup;
      if (entriesRemaining + fullLeafNode.entryCount == minBranches)
        restGroup = fullLeafNode;
      else if (entriesRemaining + returnThis.entryCount == minBranches)
        restGroup = returnThis;
      else
        restGroup = null;

      if (restGroup != null) { // Assign remaining entries to this group.
        for (int i = 0; i < entriesRemaining; i++) {

          // Add entry to "rest" group.
          final int newInx = restGroup.entryCount++;
          restGroup.objKeys[newInx] = objKeyBuff[i];
          restGroup.xMins[newInx] = xMinBuff[i];
          restGroup.yMins[newInx] = yMinBuff[i];
          restGroup.xMaxs[newInx] = xMaxBuff[i];
          restGroup.yMaxs[newInx] = yMaxBuff[i];

          // Update the overall MBR of "rest" group.
          restGroup.xMins[maxBranches - 1] =
            Math.min(restGroup.xMins[maxBranches - 1], xMinBuff[i]);
          restGroup.yMins[maxBranches - 1] =
            Math.min(restGroup.yMins[maxBranches - 1], yMinBuff[i]);
          restGroup.xMaxs[maxBranches - 1] =
            Math.max(restGroup.xMaxs[maxBranches - 1], xMaxBuff[i]);
          restGroup.yMaxs[maxBranches - 1] =
            Math.max(restGroup.yMaxs[maxBranches - 1], yMaxBuff[i]); }

        break; }

      // We're not done; pick next.
      final int next = pickNext
        (fullLeafNode, returnThis, entriesRemaining, xMinBuff, yMinBuff,
         xMaxBuff, yMaxBuff, tempBuff1, buff1Valid, tempBuff2, buff2Valid);
      final boolean chooseGroup1;
      if (tempBuff1[next] < tempBuff2[next]) chooseGroup1 = true;
      else if (tempBuff1[next] > tempBuff2[next]) chooseGroup1 = false;
      else { // Tie for how much group's covering rectangle will increase.
        // If we had an area cache array field in each node we could prevent
        // these two computations.
        final double group1Area =
          (fullLeafNode.xMaxs[maxBranches - 1] -
           fullLeafNode.xMins[maxBranches - 1]) *
          (fullLeafNode.yMaxs[maxBranches - 1] -
           fullLeafNode.yMins[maxBranches - 1]);
        final double group2Area =
          (returnThis.xMaxs[maxBranches - 1] -
           returnThis.xMins[maxBranches - 1]) *
          (returnThis.yMaxs[maxBranches - 1] -
           returnThis.yMins[maxBranches - 1]);
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
        chosenGroup = fullLeafNode; validTempBuff = tempBuff2;
        buff1Valid = false; buff2Valid = true; }
      else {
        chosenGroup = returnThis; validTempBuff = tempBuff1;
        buff1Valid = true; buff2Valid = false; }

      // Add next to chosen group.
      final int newInx = chosenGroup.entryCount++;
      chosenGroup.objKeys[newInx] = objKeyBuff[next];
      chosenGroup.xMins[newInx] = xMinBuff[next];
      chosenGroup.yMins[newInx] = yMinBuff[next];
      chosenGroup.xMaxs[newInx] = xMaxBuff[next];
      chosenGroup.yMaxs[newInx] = yMaxBuff[next];

      // Update the MBR of chosen group.
      // Note: If we see that the MBR stays the same, we could mark the
      // "invalid" temp buff array as valid to save even more on computations.
      // Because this is a rare occurance (seeds of small area tend to be
      // chosen), I choose not to make this optimization.
      chosenGroup.xMins[maxBranches - 1] =
        Math.min(chosenGroup.xMins[maxBranches - 1], xMinBuff[next]);
      chosenGroup.yMins[maxBranches - 1] =
        Math.min(chosenGroup.yMins[maxBranches - 1], yMinBuff[next]);
      chosenGroup.xMaxs[maxBranches - 1] =
        Math.max(chosenGroup.xMaxs[maxBranches - 1], xMaxBuff[next]);
      chosenGroup.yMaxs[maxBranches - 1] =
        Math.max(chosenGroup.yMaxs[maxBranches - 1], yMaxBuff[next]);

      // Collapse the arrays where next used to be.
      entriesRemaining--;
      for (int i = next; i < entriesRemaining; i++) {
        final int iPlusOne = i + 1;
        objKeyBuff[i] = objKeyBuff[iPlusOne];
        xMinBuff[i] = xMinBuff[iPlusOne];
        yMinBuff[i] = yMinBuff[iPlusOne];
        xMaxBuff[i] = xMaxBuff[iPlusOne];
        yMaxBuff[i] = yMaxBuff[iPlusOne];
        validTempBuff[i] = validTempBuff[iPlusOne]; } } // End while loop.

    return returnThis;
  }

  /*
   * This is the quadratic-cost algorithm described in Guttman's 1984
   * R-tree paper.  The parent pointer of returned node is not set.  The
   * parent pointer in the full node is not modified, and nothing in that
   * parent is modified.  Everything else in the input node and in the
   * returned node is set as appropriate.  The MBRs at index
   * maxBranches - 1 in both nodes are set to be the new overall MBR of
   * corresponding node.  To picture what this function does, imagine
   * adding newChild (with specified MBR) to fullInternalNode.  Note that
   * newChild may be either an internal node or a leaf node.
   * No claim is made as to the resulting values in the buff arrays other
   * than the claim that all entries in childrenBuff will be null when this
   * method returns.
   */
  private final static Node splitInternalNode(final Node fullInternalNode,
                                              final Node newChild,
                                              final double newXMin,
                                              final double newYMin,
                                              final double newXMax,
                                              final double newYMax,
                                              final int maxBranches,
                                              final int minBranches,
                                              final Node[] childrenBuff,
                                              final double[] xMinBuff,
                                              final double[] yMinBuff,
                                              final double[] xMaxBuff,
                                              final double[] yMaxBuff,
                                              final double[] tempBuff1,
                                              final double[] tempBuff2)
  {
    // Copy node MBRs and children and new MBR and child into arrays.
    for (int i = 0; i < fullInternalNode.entryCount; i++) {
      childrenBuff[i] = fullInternalNode.data.children[i];
      xMinBuff[i] = fullInternalNode.xMins[i];
      yMinBuff[i] = fullInternalNode.yMins[i];
      xMaxBuff[i] = fullInternalNode.xMaxs[i];
      yMaxBuff[i] = fullInternalNode.yMaxs[i]; }
    childrenBuff[fullInternalNode.entryCount] = newChild;
    xMinBuff[fullInternalNode.entryCount] = newXMin;
    yMinBuff[fullInternalNode.entryCount] = newYMin;
    xMaxBuff[fullInternalNode.entryCount] = newXMax;
    yMaxBuff[fullInternalNode.entryCount] = newYMax;

    // Pick seeds.  Add seeds to two groups (fullInternalNode and returnThis).
    final int totalEntries = fullInternalNode.entryCount + 1;
    final long seeds = pickSeeds(totalEntries, xMinBuff, yMinBuff,
                                 xMaxBuff, yMaxBuff, tempBuff1);
    // tempBuff1 now contains the areas of the MBRs - we won't use this.
    final int seed1 = (int) (seeds >> 32);
    childrenBuff[seed1].parent = fullInternalNode;
    fullInternalNode.data.children[0] = childrenBuff[seed1];
    fullInternalNode.xMins[0] = xMinBuff[seed1];
    fullInternalNode.yMins[0] = yMinBuff[seed1];
    fullInternalNode.xMaxs[0] = xMaxBuff[seed1];
    fullInternalNode.yMaxs[0] = yMaxBuff[seed1];
    fullInternalNode.entryCount = 1;
    final int seed2 = (int) seeds;
    final Node returnThis = new Node(maxBranches, false);
    childrenBuff[seed2].parent = returnThis;
    returnThis.data.children[0] = childrenBuff[seed2];
    returnThis.xMins[0] = xMinBuff[seed2];
    returnThis.yMins[0] = yMinBuff[seed2];
    returnThis.xMaxs[0] = xMaxBuff[seed2];
    returnThis.yMaxs[0] = yMaxBuff[seed2];
    returnThis.entryCount = 1;

    // Initialize the overall MBRs at index maxBranches - 1.
    fullInternalNode.xMins[maxBranches - 1] = fullInternalNode.xMins[0];
    fullInternalNode.yMins[maxBranches - 1] = fullInternalNode.yMins[0];
    fullInternalNode.xMaxs[maxBranches - 1] = fullInternalNode.xMaxs[0];
    fullInternalNode.yMaxs[maxBranches - 1] = fullInternalNode.yMaxs[0];
    returnThis.xMins[maxBranches - 1] = returnThis.xMins[0];
    returnThis.yMins[maxBranches - 1] = returnThis.yMins[0];
    returnThis.xMaxs[maxBranches - 1] = returnThis.xMaxs[0];
    returnThis.yMaxs[maxBranches - 1] = returnThis.yMaxs[0];

    // Collapse the arrays where seeds used to be.
    int entriesRemaining = totalEntries - 2;
    for (int i = seed1; i < seed2 - 1; i++) { // seed1 < seed2, guarenteed.
      final int iPlusOne = i + 1;
      childrenBuff[i] = childrenBuff[iPlusOne];
      xMinBuff[i] = xMinBuff[iPlusOne];
      yMinBuff[i] = yMinBuff[iPlusOne];
      xMaxBuff[i] = xMaxBuff[iPlusOne];
      yMaxBuff[i] = yMaxBuff[iPlusOne]; }
    for (int i = seed2 - 1; i < entriesRemaining; i++) {
      final int iPlusTwo = i + 2;
      childrenBuff[i] = childrenBuff[iPlusTwo];
      xMinBuff[i] = xMinBuff[iPlusTwo];
      yMinBuff[i] = yMinBuff[iPlusTwo];
      xMaxBuff[i] = xMaxBuff[iPlusTwo];
      yMaxBuff[i] = yMaxBuff[iPlusTwo]; }
    
    boolean buff1Valid = false;
    boolean buff2Valid = false;
    while (true) {

      // Test to see if we're all done.
      if (entriesRemaining == 0) break;
      final Node restGroup;
      if (entriesRemaining + fullInternalNode.entryCount == minBranches)
        restGroup = fullInternalNode;
      else if (entriesRemaining + returnThis.entryCount == minBranches)
        restGroup = returnThis;
      else
        restGroup = null;

      if (restGroup != null) { // Assign remaining entries to this group.
        for (int i = 0; i < entriesRemaining; i++) {

          // Add entry to "rest" group.
          final int newInx = restGroup.entryCount++;
          childrenBuff[i].parent = restGroup;
          restGroup.data.children[newInx] = childrenBuff[i];
          restGroup.xMins[newInx] = xMinBuff[i];
          restGroup.yMins[newInx] = yMinBuff[i];
          restGroup.xMaxs[newInx] = xMaxBuff[i];
          restGroup.yMaxs[newInx] = yMaxBuff[i];

          // Update the overall MBR of "rest" group.
          restGroup.xMins[maxBranches - 1] =
            Math.min(restGroup.xMins[maxBranches - 1], xMinBuff[i]);
          restGroup.yMins[maxBranches - 1] =
            Math.min(restGroup.yMins[maxBranches - 1], yMinBuff[i]);
          restGroup.xMaxs[maxBranches - 1] =
            Math.max(restGroup.xMaxs[maxBranches - 1], xMaxBuff[i]);
          restGroup.yMaxs[maxBranches - 1] =
            Math.max(restGroup.yMaxs[maxBranches - 1], yMaxBuff[i]); }

        break; }

      // We're not done; pick next.
      final int next = pickNext
        (fullInternalNode, returnThis, entriesRemaining, xMinBuff, yMinBuff,
         xMaxBuff, yMaxBuff, tempBuff1, buff1Valid, tempBuff2, buff2Valid);
      final boolean chooseGroup1;
      if (tempBuff1[next] < tempBuff2[next]) chooseGroup1 = true;
      else if (tempBuff1[next] > tempBuff2[next]) chooseGroup1 = false;
      else { // Tie for how much group's covering rectangle will increase.
        // If we had an area cache array field in each node we could prevent
        // these two computations.
        final double group1Area =
          (fullInternalNode.xMaxs[maxBranches - 1] -
           fullInternalNode.xMins[maxBranches - 1]) *
          (fullInternalNode.yMaxs[maxBranches - 1] -
           fullInternalNode.yMins[maxBranches - 1]);
        final double group2Area =
          (returnThis.xMaxs[maxBranches - 1] -
           returnThis.xMins[maxBranches - 1]) *
          (returnThis.yMaxs[maxBranches - 1] -
           returnThis.yMins[maxBranches - 1]);
        if (group1Area < group2Area) chooseGroup1 = true;
        else if (group1Area > group2Area) chooseGroup1 = false;
        else // Tie for group MBR area as well.
          if (fullInternalNode.entryCount < returnThis.entryCount)
            chooseGroup1 = true;
          else
            chooseGroup1 = false; }
      final Node chosenGroup;
      final double[] validTempBuff;
      if (chooseGroup1) {
        chosenGroup = fullInternalNode; validTempBuff = tempBuff2;
        buff1Valid = false; buff2Valid = true; }
      else {
        chosenGroup = returnThis; validTempBuff = tempBuff1;
        buff1Valid = true; buff2Valid = false; }

      // Add next to chosen group.
      final int newInx = chosenGroup.entryCount++;
      childrenBuff[next].parent = chosenGroup;
      chosenGroup.data.children[newInx] = childrenBuff[next];
      chosenGroup.xMins[newInx] = xMinBuff[next];
      chosenGroup.yMins[newInx] = yMinBuff[next];
      chosenGroup.xMaxs[newInx] = xMaxBuff[next];
      chosenGroup.yMaxs[newInx] = yMaxBuff[next];

      // Update the MBR of chosen group.
      // Note: If we see that the MBR stays the same, we could mark the
      // "invalid" temp buff array as valid to save even more on computations.
      // Because this is a rare occurance (seeds of small area tend to be
      // chosen), I choose not to make this optimization.
      chosenGroup.xMins[maxBranches - 1] =
        Math.min(chosenGroup.xMins[maxBranches - 1], xMinBuff[next]);
      chosenGroup.yMins[maxBranches - 1] =
        Math.min(chosenGroup.yMins[maxBranches - 1], yMinBuff[next]);
      chosenGroup.xMaxs[maxBranches - 1] =
        Math.max(chosenGroup.xMaxs[maxBranches - 1], xMaxBuff[next]);
      chosenGroup.yMaxs[maxBranches - 1] =
        Math.max(chosenGroup.yMaxs[maxBranches - 1], yMaxBuff[next]);

      // Collapse the arrays where next used to be.
      entriesRemaining--;
      for (int i = next; i < entriesRemaining; i++) {
        final int iPlusOne = i + 1;
        childrenBuff[i] = childrenBuff[iPlusOne];
        xMinBuff[i] = xMinBuff[iPlusOne];
        yMinBuff[i] = yMinBuff[iPlusOne];
        xMaxBuff[i] = xMaxBuff[iPlusOne];
        yMaxBuff[i] = yMaxBuff[iPlusOne];
        validTempBuff[i] = validTempBuff[iPlusOne]; } } // End while loop.

    fullInternalNode.data.deepCount = 0; // Update deep counts.
    if (isLeafNode(fullInternalNode.data.children[0])) {
      for (int i = 0; i < fullInternalNode.entryCount; i++)
        fullInternalNode.data.deepCount +=
          fullInternalNode.data.children[i].entryCount;
      for (int i = 0; i < returnThis.entryCount; i++)
        returnThis.data.deepCount +=
          returnThis.data.children[i].entryCount; }
    else { // fullInternalNode's children are internal nodes.
      for (int i = 0; i < fullInternalNode.entryCount; i++)
        fullInternalNode.data.deepCount +=
          fullInternalNode.data.children[i].data.deepCount;
      for (int i = 0; i < returnThis.entryCount; i++)
        returnThis.data.deepCount +=
          returnThis.data.children[i].data.deepCount; }

    // Null things out to not hinder future garbage collection.
    for (int i = fullInternalNode.entryCount;
         i < fullInternalNode.data.children.length; i++)
      fullInternalNode.data.children[i] = null;
    // We only null out the buffer because we're neurotically paranoid.
    for (int i = 0; i < childrenBuff.length; i++) childrenBuff[i] = null;

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
                                    final int maxBranches,
                                    final double[] xMins,
                                    final double[] yMins,
                                    final double[] xMaxs,
                                    final double[] yMaxs,
                                    final double[] tempBuff1,
                                    final boolean buff1Valid,
                                    final double[] tempBuff2,
                                    final boolean buff2Valid)
  {
    if (!buff1Valid) {
      // If we had an area cache array field in each node we could prevent
      // this computation.
      final double group1Area =
        (group1.xMaxs[maxBranches - 1] - group1.xMins[maxBranches - 1]) *
        (group1.yMaxs[maxBranches - 1] - group1.yMins[maxBranches - 1]);
      for (int i = 0; i < count; i++) {
        tempBuff1[i] =
          ((Math.max(group1.xMaxs[maxBranches - 1], xMaxs[i]) -
            Math.min(group1.xMins[maxBranches - 1], xMins[i])) *
           (Math.max(group1.yMaxs[maxBranches - 1], yMaxs[i]) -
            Math.min(group1.yMins[maxBranches - 1], yMins[i]))) -
          group1Area; } }
    if (!buff2Valid) {
      // If we had an area cache array field in each node we could prevent
      // this computation.      
      final double group2Area =
        (group2.xMaxs[maxBranches - 1] - group2.xMins[maxBranches - 1]) *
        (group2.yMaxs[maxBranches - 1] - group2.yMins[maxBranches - 1]);
      for (int i = 0; i < count; i++) {
        tempBuff2[i] =
          ((Math.max(group2.xMaxs[maxBranches - 1], xMaxs[i]) -
            Math.min(group2.xMins[maxBranches - 1], xMins[i])) *
           (Math.max(group2.yMaxs[maxBranches - 1], yMaxs[i]) -
            Math.min(group2.yMins[maxBranches - 1], yMins[i]))) -
          group2Area; } }
    double maxDDifference = Double.NEGATIVE_INFINITY;
    int maxInx = -1;
    for (int i = 0; i < count; i++) {
      final double currDDifference = Math.abs(tempBuff1[i] - tempBuff2[i]);
      if (currDDifference > maxDDifference) {
        maxDDifference = currDDifference;
        maxInx = i; } }
    return maxInx;
  }

  /*
   * It is assumed that the entry in the leaf node at index
   * leafNode.entryCount - 1 is the only new entry.  We will use this
   * knowledge to optimize this function.  Deep counts are updated from
   * leaf to root.
   */
  private final static void adjustTreeNoSplit(final Node leafNode,
                                              final double[] globalMBR)
  {
    int currModInx = leafNode.entryCount - 1;
    Node n = leafNode;
    while (true) {
      final Node p = n.parent;

      // "If N is the root, stop."  Adjust the globalMBR.
      if (p == null) {
        if (currModInx >= 0) {
          globalMBR[0] = Math.min(globalMBR[0], n.xMins[currModInx]);
          globalMBR[1] = Math.min(globalMBR[1], n.yMins[currModInx]);
          globalMBR[2] = Math.max(globalMBR[2], n.xMaxs[currModInx]);
          globalMBR[3] = Math.max(globalMBR[3], n.yMaxs[currModInx]); }
        break; }

      // Update the deep count.
      p.data.deepCount++;

      if (currModInx >= 0) {
        final int nInxInP;
        for (int i = 0;; i++)
          if (p.data.children[i] == n) { nInxInP = i; break; }

        // Compute the MBR that tightly encloses all entries in n.
        final double newXMin = Math.min(p.xMins[nInxInP], n.xMins[currModInx]);
        final double newYMin = Math.min(p.yMins[nInxInP], n.yMins[currModInx]);
        final double newXMax = Math.max(p.xMaxs[nInxInP], n.xMaxs[currModInx]);
        final double newYMax = Math.max(p.yMaxs[nInxInP], n.yMaxs[currModInx]);

        // If this MBR of all entries in n does not change, we don't need to
        // update any further MBRs, just deep counts.
        if (newXMin == p.xMins[nInxInP] && newYMin == p.yMins[nInxInP] &&
            newXMax == p.xMaxs[nInxInP] && newYMax == p.yMaxs[nInxInP]) {
          currModInx = -1; }

        else {
          // Set the computed MBR in the parent and move up the tree one step.
          p.xMins[nInxInP] = newXMin; p.yMins[nInxInP] = newYMin;
          p.xMaxs[nInxInP] = newXMax; p.yMaxs[nInxInP] = newYMax;
          currModInx = nInxInP; } }

      n = p; } 
  }

  /*
   * It is required that the MBRs at index m_maxBranches - 1 in both
   * input nodes contain the overall MBR of corresponding node.
   * Returns a node if root was split, otherwise returns null.
   * If a node is returned, then both the old root and the returned node
   * will have an MBR entry at index maxBranches - 1 which will be the
   * overall MBR of that node.  The globalMBR is only updated when null
   * is returned.  Deep counts are updated from leaf to root.
   */
  private final static Node adjustTreeWithSplit(final Node originalLeafNode,
                                                final Node newLeafNode,
                                                final int maxBranches,
                                                final int minBranches,
                                                final double[] globalMBR,
                                                final Node[] childrenBuff,
                                                final double[] xMinBuff,
                                                final double[] yMinBuff,
                                                final double[] xMaxBuff,
                                                final double[] yMaxBuff,
                                                final double[] tempBuff1,
                                                final double[] tempBuff2)
  {
    int currModInx = -1;
    boolean newNodeAdded = false;
    Node n = originalLeafNode;
    Node nn = newLeafNode;
    while (true) {
      final Node p = n.parent;

      // "If N is the root, stop."  Update globalMBR if root not split.
      if (p == null) {
        if (nn == null && currModInx >= 0) {
          globalMBR[0] = Math.min(globalMBR[0], n.xMins[currModInx]);
          globalMBR[1] = Math.min(globalMBR[1], n.yMins[currModInx]);
          globalMBR[2] = Math.max(globalMBR[2], n.xMaxs[currModInx]);
          globalMBR[3] = Math.max(globalMBR[3], n.yMaxs[currModInx]);
          if (newNodeAdded) { // Will only be true when currModInx >= 0.
            final int countMin1 = n.entryCount - 1;
            globalMBR[0] = Math.min(globalMBR[0], n.xMins[countMin1]);
            globalMBR[1] = Math.min(globalMBR[1], n.yMins[countMin1]);
            globalMBR[2] = Math.max(globalMBR[2], n.xMaxs[countMin1]);
            globalMBR[3] = Math.max(globalMBR[3], n.yMaxs[countMin1]); } }
        break; }
      p.data.deepCount++; // Will get rewritten if p is split - that's OK.
      final int nInxInP;
      for (int i = 0;; i++)
        if (p.data.children[i] == n) { nInxInP = i; break; }

      if (nn != null) {
        p.xMins[nInxInP] = n.xMins[maxBranches - 1];
        p.yMins[nInxInP] = n.yMins[maxBranches - 1];
        p.xMaxs[nInxInP] = n.xMaxs[maxBranches - 1];
        p.yMaxs[nInxInP] = n.yMaxs[maxBranches - 1];
        if (p.entryCount < maxBranches) { // No split is necessary.
          final int newInxInP = p.entryCount++;
          nn.parent = p;
          p.data.children[newInxInP] = nn;

          // A split (nn != null) implies total MBR at inx maxBranches - 1.
          // Set the MBR of the new node.
          p.xMins[newInxInP] = nn.xMins[maxBranches - 1];
          p.yMins[newInxInP] = nn.yMins[maxBranches - 1];
          p.xMaxs[newInxInP] = nn.xMaxs[maxBranches - 1];
          p.yMaxs[newInxInP] = nn.yMaxs[maxBranches - 1];

          // The recursive step.
          currModInx = nInxInP;
          newNodeAdded = true;
          nn = null;

        }
        else { // A split is necessary.
          // We require that the MBR at index maxBranches - 1 in nn contain
          // nn's overall MBR at the time this is called.
          nn = splitInternalNode
            (p, nn, nn.xMins[maxBranches - 1], nn.yMins[maxBranches - 1],
             nn.xMaxs[maxBranches - 1], nn.yMaxs[maxBranches - 1],
             maxBranches, minBranches, childrenBuff, xMinBuff, yMinBuff,
             xMaxBuff, yMaxBuff, tempBuff1, tempBuff2);
          
        }
      }
      else if (currModInx >= 0) { // nn == null.
        double newXMin = Math.min(p.xMins[nInxInP], n.xMins[currModInx]);
        double newYMin = Math.min(p.yMins[nInxInP], n.yMins[currModInx]);
        double newXMax = Math.max(p.xMaxs[nInxInP], n.xMaxs[currModInx]);
        double newYMax = Math.max(p.yMaxs[nInxInP], n.yMaxs[currModInx]);
        if (newNodeAdded) {
          newXMin = Math.min(newXMin, n.xMins[n.entryCount - 1]);
          newYMin = Math.min(newYMin, n.yMins[n.entryCount - 1]);
          newXMax = Math.max(newXMax, n.xMaxs[n.entryCount - 1]);
          newYMax = Math.max(newYMax, n.yMaxs[n.entryCount - 1]);
          newNodeAdded = false; }
        if (newXMin == p.xMins[nInxInP] && newYMin == p.yMins[nInxInP] &&
            newXMax == p.xMaxs[nInxInP] && newYMax == p.yMaxs[nInxInP]) {
          currModInx = -1; }
        else {
          p.xMins[nInxInP] = newXMin; p.yMins[nInxInP] = newYMin;
          p.xMaxs[nInxInP] = newXMax; p.yMaxs[nInxInP] = newYMax;
          currModInx = nInxInP; } }
      n = p;

    }
    return nn;
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
                   m_MBR[0], m_MBR[1], m_MBR[2], m_MBR[3], extentsArr, offset);
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
