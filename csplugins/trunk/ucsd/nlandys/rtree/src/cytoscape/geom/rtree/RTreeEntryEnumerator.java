package cytoscape.geom.rtree;

import cytoscape.util.intr.IntEnumerator;

/**
 * An enumeration over a set of R-tree entries.  The purpose of this
 * class over and beyond IntEnumerator (which it extents) is to efficiently
 * provide minimum bounding rectangle information for each entry returned.
 */
public interface RTreeEntryEnumerator extends IntEnumerator
{

  /**
   * Copies into the supplied array [starting at specified offset] the minimum
   * bounding rectangle of the entry that will be returned in a call to
   * nextInt().  This method has no effect on the enumeration of entries; that
   * is, consecutive repeated calls to this method will each return the
   * same extents.  In other words, to discover the next object key and
   * corresponding extents in this enumeration, first call nextExtents() and
   * then call nextInt().  It only makes sense to call this method when
   * numRemaining() on this enumeration returns a positive value; otherwise,
   * calling this method produces undefined results (maybe even a thrown
   * exception).<p>
   * The information written into the supplied extentsArr parameter consists
   * of the following:
   * <blockquote><table border="1" cellpadding="5" cellspacing="0">
   *   <tr>  <th>array index</th>  <th>information written</th>  </tr>
   *   <tr>  <td>offset</td>       <td>xMin of MBR</td>          </tr>
   *   <tr>  <td>offset+1</td>     <td>yMin of MBR</td>          </tr>
   *   <tr>  <td>offset+2</td>     <td>xMax of MBR</td>          </tr>
   *   <tr>  <td>offset+3</td>     <td>yMax of MBR</td>          </tr>
   * </table></blockquote>
   * @param extentsArr an array to which extent values will be written by this
   *   method.
   * @param offset specifies the beginning index of where to write extent
   *   values into extentsArr; exactly four entries are written starting
   *   at this index (see above table).
   * @exception ArrayIndexOutOfBoundsException if extentsArr cannot be
   *   written to in the index range [offset, offset+3].
   */
  public void nextExtents(float[] extentsArr, int offset);

}
