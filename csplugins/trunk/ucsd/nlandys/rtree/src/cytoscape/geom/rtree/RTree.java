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
   * </table>
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
