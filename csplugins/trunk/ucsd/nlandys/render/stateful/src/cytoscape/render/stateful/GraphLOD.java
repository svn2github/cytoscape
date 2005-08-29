package cytoscape.render.stateful;

/**
 * An instance of this class defines the level of detail that goes into
 * a single rendering of a graph.  This class is meant to be subclassed; its
 * methods are meant to be overridden; nonetheless, sane defaults are
 * used in the default method implementations.
 */
public class GraphLOD
{

  /**
   * Determines whether or not to render the graph at full detail.
   * By default this method returns true if and only if the sum of visible
   * nodes and visible edges is less than 1200.<p>
   * The following table describes the difference between full and low
   * rendering detail in terms of what methods on an instance of
   * GraphGraphics get called:
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
   * @param visibleNodeCount the number of nodes that are about to be rendered.
   * @param visibleEdgeCount the number of edges that are about to be rendered.
   * @return true for full detail, false for low detail.
   */
  public boolean detail(final int visibleNodeCount,
                        final int visibleEdgeCount)
  {
    return visibleNodeCount + visibleEdgeCount < 1200;
  }

  /**
   * Determines whether or not the render node borders.  By default this
   * method returns true if and only if the sum of visible nodes and visible
   * edges is less than 500.<p>
   * Note that it is only possible to draw node borders at the full detail
   * level.  If low detail is chosen, the output of this method is ignored.
   * @param visibleNodeCount the number of nodes that are about to be rendered.
   * @param visibleEdgeCount the number of edges that are about to be rendered.
   * @return true if and only if node borders are to be rendered.
   * @see #detail(int, int)
   */
  public boolean nodeBorders(final int visibleNodeCount,
                             final int visibleEdgeCount)
  {
    return visibleNodeCount + visibleEdgeCount < 500;
  }

  /**
   * Determines whether or not to render edge arrows.  By default this
   * method returns true if and only if the sum of visible nodes and visible
   * edges is less than 500.<p>
   * Note that it is only possible to draw edge arrows at the full detail
   * level.  If low detail is chosen, the output of this method is ignored.
   * @param visibleNodeCount the number of nodes that are about to be rendered.
   * @param visibleEdgeCount the number of edges that are about to be rendered.
   * @return true if and only if edge arrows are to be rendered.
   * @see #detail(int, int)
   */
  public boolean edgeArrows(final int visibleNodeCount,
                            final int visibleEdgeCount)
  {
    return visibleNodeCount + visibleEdgeCount < 500;
  }

  /**
   * Determines whether or not to honor dashed edges.  By default this
   * method always returns true.  If false is returned, edges which
   * are claiming themselves as dahsed will be rendered as solid.<p>
   * Note that it is only possible to draw dashed edges at the full detail
   * level.  If low detail is chosen, the output of this method is ignored.
   * Note also that are computationally very expensive; the default
   * implementation of this method makes a poor performance-minded choice if
   * a lot of edges happen to be dashed.
   * @param visibleNodeCount the number of nodes that are about to be rendered.
   * @param visibleEdgeCount the number of edges that are about to be rendered.
   * @return true if and only if dashed edges are to be honored.
   * @see #detail(int, int)
   */
  public boolean dashedEdges(final int visibleNodeCount,
                             final int visibleEdgeCount)
  {
    return true;
  }

  /**
   * Determines whether or not to honor edge anchors.  By default this
   * method always returns true.  If false is returned, edges which are
   * claiming to have edge anchors will be rendered as simple straight
   * edges.<p>
   * Note that it is only possible to draw poly-edges at the full detail
   * level.  If low detail is chosen, the output of this method is ignored.
   * @param visibleNodeCount the number of nodes that are about to be rendered.
   * @param visibleEdgeCount the number of edges that are about to be rendered.
   * @return true if and only if edge anchors are to be honored.
   * @see #detail(int, int)
   */
  public boolean edgeAnchors(final int visibleNodeCount,
                             final int visibleEdgeCount)
  {
    return true;
  }

  /**
   * Determines whether or not to render node labels.  By default this method
   * returns true if and only if the number of visible nodes is less than
   * 80.<p>
   * Node labels are only rendered at the full detail level.  If low detail is
   * chosen, the output of this method is ignored.
   * @param visibleNodeCount the number of nodes that are about to be rendered.
   * @param visibleEdgeCount the number of edges that are about to be rendered.
   * @return true if and only if node labels are to be rendered.
   * @see #detail(int, int)
   */
  public boolean nodeLabels(final int visibleNodeCount,
                            final int visibleEdgeCount)
  {
    return visibleNodeCount < 80;
  }

  /**
   * Determines whether or not to draw text as shape when rendering node
   * labels.  By default this method always returns false.<p>
   * This method affects the boolean parameter drawTextAsShape in the method
   * call GraphGraphics.drawTextFull().  If node labels are not rendered
   * altogether, the output of this method is ignored.
   * @param visibleNodeCount the number of nodes that are about to be rendered.
   * @param visibleEdgeCount the number of edges that are about to be rendered.
   * @return true if and only if rendered node label text should be drawn as
   *   primitive shapes.
   * @see #nodeLabels(int, int)
   */
  public boolean textAsShape(final int visibleNodeCount,
                             final int visibleEdgeCount)
  {
    return false;
  }

}
