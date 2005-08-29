package cytoscape.render.stateful;

public class GraphLOD
{

  /**
   * Defines the visible node count threshold at which node label text is
   * rendered as primitive shapes; text is renderes as shapes only if [node
   * labels are rendered altogether and] the
   * number of nodes visible is less than the number returned by this method.
   * By default this method returns 30.
   */
  public int textAsShapeThreshold() {
    return 30; }

  /**
   * Defines the visible node count threshold at which node labels are
   * rendered; node labels are only rendered if the number of nodes visible
   * is less than the number returned by this method and if we're rendering in
   * full detail.  By default this method returns 80.
   */
  public int nodeLabelThreshold() {
    return 80; }

  /**
   * Defines the threshold of low detail rendering versus full detail
   * rendering; the integer returned relates to the sum of visible nodes
   * and visible edges; if this sum is less than the value returned by this
   * method, full detail is used, otherwise low detail is used.
   * By default this method returns 1200.
   */
  public int fullDetailThreshold() {
    return 1200; }


  /**
   * This method determines whether or not to render the graph at full detail.
   * By default this method returns true if and only if the sum of visible
   * nodes and visible edges is less than 1200.<p>
   * The following table describes the difference between full and low
   * rendering detail, in terms of what methods on an instance of
   * cytoscape.render.immed.GraphGraphics get called:
   * <blockquote><table border="1" cellpadding="5" cellspacing="0">
   *   <tr>  <td></td>
   *         <th>full detail</th>
   *         <th>low detail</th>                                          </tr>
   *   <tr>  <th>nodes</th>
   *         <td>drawNodeFull()</td>
   *         <td>drawNodeLow()</td>                                       </tr>
   *   <tr>  <th>edges</th>
   *         <td>drawEdgeFull()</td>
   *         <td>drawEdgeLow()</td>                                       </tr>
   *   <tr>  <th>node labels</th>
   *         <td>drawTextFull()</td>
   *         <td>not rendered</td>                                        </tr>
   * </table></blockquote>
   *
   * @param visibleNodeCount the number of nodes that are about to be rendered.
   * @param visibleEdgeCount the number of edges that are about to be rendered.
   * @return true for full detail, false for low detail.
   */
  public boolean detail(final int visibleNodeCount,
                        final int visibleEdgeCount)
  {
    return visibleNodeCount + visibleEdgeCount < 1200;
  }

}
