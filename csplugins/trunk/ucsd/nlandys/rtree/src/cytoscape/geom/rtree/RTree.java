package cytoscape.geom.rtree;

import cytoscape.util.intr.IntEnumerator;

/**
 * An R-tree over real numbers in two dimensions.
 * Only intersection and containment queries over an orthogonal
 * (axis-aligned) range are suppored.  This class only knows about
 * [minimum bounding] rectangles; to compute exact intersections of query
 * rectangles with lines and polygons, for example, you can build a higher
 * level module using this class as the underlying engine.
 */
public class RTree
{

  public void empty() {}

  /**
   * Inserts a new data entry to this tree; the entry's minimum bounding
   * rectangle is specified by the input parameters.
   */
  public int insert(double minX, double minY,
                    double maxX, double maxY)
  {
    return -1;
  }

  /**
   * Removes the specified data entry from this tree.
   */
  public boolean remove(int identifier)
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
