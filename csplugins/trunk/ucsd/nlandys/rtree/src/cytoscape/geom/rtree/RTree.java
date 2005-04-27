package cytoscape.geom.rtree;

import cytoscape.util.intr.IntEnumerator;

/**
 * An in-memory R-tree over real numbers in two dimensions.
 */
public final class RTree
{

  /**
   * Instantiates a new R-tree.  A new R-tree has no entries.
   */
  public RTree()
  {
  }

  /**
   * Empties this R-tree of all entries.  This method returns in constant
   * time (note however that garbage collection will take place in the
   * background).
   */
  public final void empty() {}

  /**
   * Returns the number of entries currently in this R-tree.  This method
   * returns in constant time.<p>
   * NOTE: To retrieve an enumeration of all entries in this R-tree, call
   * queryIntersection() with Double.MIN_VALUE minimum values and
   * Double.MAX_VALUE maximum values.
   */
  public final int size() { return 0; }

  /**
   * Inserts a new data entry into this tree; the entry's extents
   * are specified by the input parameters.  "Extents" is a short way
   * of saying "minimum bounding rectangle".  The minimum bounding rectangle
   * of an entry is axis-aligned, meaning that its sides are parallel to the
   * axes of the data space.
   * @param objKey a user-defined unique identifier used to refer to the entry
   *   being inserted in later operations; this identifier must be positive
   *   and cannot be equal to Integer.MAX_VALUE.
   * @param minX the minimum X coordinate of the entry's extents rectangle.
   * @param minY the minimum Y coordinate of the entry's extents rectangle.
   * @param maxX the maximum X coordinate of the entry's extents rectangle.
   * @param maxY the maximum Y coordinate of the entry's extents rectangle.
   * @exception IllegalStateException if objKey is already used for an
   *   existing entry in this R-tree.
   * @exception IllegalArgumentException if objKey is negative or equal to
   *   Integer.MAX_VALUE.
   */
  public final void insert(final int objKey,
                           final double minX, final double minY,
                           final double maxX, final double maxY)
  {
  }

  /**
   * Determines whether or not a given key exists in this R-tree structure.<p>
   * NOTE: To retrieve an enumeration of all entries in this R-tree, call
   * queryIntersection() with Double.MIN_VALUE minimum values and
   * Double.MAX_VALUE maximum values.
   * @param objKey a user-defined identifier that was potentially used
   *   in a previous insertion.
   * @return true if and only if objKey was previously inserted into this
   *   R-tree and has not since been deleted.
   */
  public final boolean keyExists(final int objKey)
  {
    return false;
  }

  /**
   * Writes the extents of objKey into the specified array, starting at
   * specified offset.  The following table describes what is written to
   * the extentsArr input parameter by this method:<p>
   * <table border="1" cellpadding="5" cellspacing="0">
   *   <tr>  <th>array index</th>  <th>value</th>  </tr>
   *   <tr>  <td>offset</td>       <td>minX</td>   </tr>
   *   <tr>  <td>offset+1</td>     <td>minY</td>   </tr>
   *   <tr>  <td>offset+2</td>     <td>maxX</td>   </tr>
   *   <tr>  <td>offset+3</td>     <td>maxY</td>   </tr>
   * </table><p>
   * The values written into extentsArr are exactly the same ones that
   * were previously passed to insert() using the same objKey.
   * @param objKey a user-defined identifier that was used in a previous
   *   insertion.
   * @param extentsArr an array, supplied by caller of this method, to which
   *   extent values will be written by this method.
   * @param offset specifies the beginning index of where to write data into
   *   extentsArr; exactly four entries are written starting at this index
   *   (see above table).
   * @exception IllegalStateException if objKey does not exist in this R-tree.
   * @exception NullPointerException if extentsArr is null.
   * @exception ArrayIndexOutOfBoundsException if extentsArr cannot be written
   *   to in the index range [offset, offset+3].
   */
  public final void extents(final int objKey, final double[] extentsArr,
                            final int offset)
  {
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
   * IMPORTANT: The returned enumeration becomes invalid as soon as any
   * structure-modifying operation (insert or delete) is performed on this
   * R-tree.  Accessing an invalid enumeration's methods will result in
   * unpredictable and ill-defined behavior in that enumeration, but will
   * have no effect on the integrity of the underlying tree structure.
   * @param minX the minimum X coordinate of the query rectangle.
   * @param minY the minimum Y coordinate of the query rectangle.
   * @param maxX the maximum X coordinate of the query rectangle.
   * @param maxY the maximum Y coordinate of the query rectangle.
   * @return a non-null enumeration of all [distinct] R-tree entries
   *   (objKeys) whose extents intersect the specified rectangular area.
   */
  public final IntEnumerator queryIntersection(final double minX,
                                               final double minY,
                                               final double maxX,
                                               final double maxY)
  {
    return null;
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
   * @param minX the minimum X coordinate of the query rectangle.
   * @param minY the minimum Y coordinate of the query rectangle.
   * @param maxX the maximum X coordinate of the query rectangle.
   * @param maxY the maximum Y coordinate of the query rectangle.
   * @return a non-null enumeration of all [distinct] R-tree entries
   *   (objKeys) whose extents intersect the specified rectangular area.
   */
  public final IntEnumerator queryEnclosure(final double minX,
                                            final double minY,
                                            final double maxX,
                                            final double maxY)
  {
    return null;
  }

}
