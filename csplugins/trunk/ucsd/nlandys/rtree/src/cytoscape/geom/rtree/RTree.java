package cytoscape.geom.rtree;

import cytoscape.util.intr.IntEnumerator;

/**
 * An in-memory R-tree over real numbers in two dimensions.
 * Only intersection and enclosure queries over an orthogonal
 * (axis-aligned) range are suppored.  This class only knows about
 * [minimum bounding] rectangles; to compute exact intersections of query
 * rectangles with lines and polygons, for example, you can build a higher
 * level module using this class as the underlying engine.
 */
public final class RTree
{

  public final void empty() {}

  /**
   * Inserts a new data entry into this tree; the entry's extents are
   * specified by the input parameters.
   * @param objKey a user-defined identifier used to refer to the entry
   *   being inserted in later operations.
   * @param minX the minimum X coordinate of the minimum bounding rectangle
   *   of the entry being inserted.
   * @param minY the minimum Y coordinate of the minimum bounding rectangle
   *   of the entry being inserted.
   * @param maxX the maximum X coordinate of the minimum bounding rectangle
   *   of the entry being inserted.
   * @param maxY the maximum Y coordinate of the minimum bounding rectangel
   *   of the entry being inserted.
   * @exception IllegalStateException if objKey is already used for an
   *   existing entry in this R-tree.
   * @exception IllegalArgumentException if objKey is negative or equal to
   *   Integer.MAX_VALUE.
   */
  public final void insert(int objKey,
                           double minX, double minY,
                           double maxX, double maxY)
  {
  }

  /**
   * Writes the extents of objKey into the specified array, starting at
   * specified offset.  The following table describes what is written to
   * the extentsArr input parameter:<p>
   * <table border="1" cellpadding="5" cellspacing="0">
   *   <tr><th>array index</th><th>value</th></tr>
   *   <tr><td>offset</td><td>minX</td></tr>
   *   <tr><td>offset+1</td><td>minY</td></tr>
   *   <tr><td>offset+2</td><td>maxX</td></tr>
   *   <tr><td>offset+3</td><td>maxY</td></tr>
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
  public final void getExtents(int objKey, double[] extentsArr, int offset)
  {
  }

  /**
   * Removes the specified data entry from this tree.
   * @return true if and only if objKey existed in this R-tree prior to this
   *   method invocation.
   */
  public boolean remove(int objKey)
  {
    return false;
  }

  /**
   * Returns all data entries which intersect the specified area.
   */
  public IntEnumerator intersected(double minX, double minY,
                                   double maxX, double maxY)
  {
    return null;
  }

  /**
   * Returns all data entries which are fully enclosed by the specified
   * rectangle.
   */
  public IntEnumerator enclosed(double minX, double minY,
                                double maxX, double maxY)
  {
    return null;
  }
  
}
