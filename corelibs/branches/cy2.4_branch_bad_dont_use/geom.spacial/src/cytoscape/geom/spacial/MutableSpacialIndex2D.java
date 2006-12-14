package cytoscape.geom.spacial;

/**
 * A spacial index for objects in two dimensions, with support for
 * insertions and deletions.
 */
public interface MutableSpacialIndex2D extends SpacialIndex2D
{

  /**
   * Empties this structure of all entries.
   */
  public void empty();

  /**
   * Inserts a new data entry into this structure; the entry's extents
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
   *   existing entry in this structure.
   * @exception IllegalArgumentException if objKey is negative,
   *   if xMin is not less than or equal to xMax, or
   *   if yMin is not less than or equal to yMax.
   */
  public void insert(int objKey,
                     float xMin, float yMin, float xMax, float yMax);

  /**
   * Deletes the specified data entry from this structure.
   * @param objKey a user-defined identifier that was potentially used in a
   *   previous insertion.
   * @return true if and only if objKey existed in this structure prior to this
   *   method invocation.
   */
  public boolean delete(int objKey);

}
