package cytoscape.render.immed;

/**
 * Specifies edge anchor points to use when rendering edges in full detail
 * mode.
 */
public interface EdgeAnchors
{

  /**
   * Returns the number of edge anchors.
   */
  public int numAnchors();

  /**
   * Writes an edge anchor point into the array provided, at offset
   * specified.  The information written into the supplied anchorArr parameter
   * consists of the following:
   * <blockquote><table border="1" cellpadding="5" cellspacing="0">
   *   <tr>  <th>array index</th>  <th>information written</th>     </tr>
   *   <tr>  <td>offset</td>       <td>X coordinate of anchor</td>  </tr>
   *   <tr>  <td>offset+1</td>     <td>Y coordinate of anchor</td>  </tr>
   * </table></blockquote>
   * @exception IndexOutOfBoundsException if anchorIndex is not in the
   *   range [0, numAnchors()-1].
   */
  public void getAnchor(int anchorIndex, float[] anchorArr, int offset);

}
