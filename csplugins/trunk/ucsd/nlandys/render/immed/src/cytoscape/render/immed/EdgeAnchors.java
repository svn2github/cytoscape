package cytoscape.render.immed;

public interface EdgeAnchors
{

  /**
   * Returns an integer N such that nextAnchor() can be successfully
   * called no more and no less than N times.
   */
  public int numRemaining();

  /**
   * Writes the next edge anchor point into the array provided, at offset
   * specified.  The information written into the supplied anchorArr parameter
   * consists of the following:
   * <blockquote><table border="1" cellpadding="5" cellspacing="0">
   *   <tr>  <th>array index</th>  <th>information written</th>     </tr>
   *   <tr>  <td>offset</td>       <td>X coordinate of anchor</td>  </tr>
   *   <tr>  <td>offset+1</td>     <td>Y coordinate of anchor</td>  </tr>
   * </table></blockquote>
   */
  public void nextAnchor(float[] anchorArr, int offset);

}
