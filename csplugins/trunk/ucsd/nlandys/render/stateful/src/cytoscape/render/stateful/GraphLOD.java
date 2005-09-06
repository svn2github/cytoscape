package cytoscape.render.stateful;

/**
 * An instance of this class defines the level of detail that goes into
 * a single rendering of a graph.  This class is meant to be subclassed; its
 * methods are meant to be overridden; nonetheless, sane defaults are
 * used in the default method implementations.<p>
 * To understand the significance of each method's return value, it makes
 * sense to become familiar with the API cytoscape.render.immed.GraphGraphics.
 */
public class GraphLOD
{

  /**
   * Determines whether or not to render all edges in a graph.  By default
   * this method returns false, which leads the rendering engine to render
   * only those edges that touch at least one visible node.  Note that
   * rendering all edges leads to a dramatic performance decrease when
   * rendering large graphs.
   * @param renderNodeCount the number of nodes that are about to be rendered.
   * @param totalNodeCount the total number of nodes in the graph that is
   *   being rendered.
   * @param totalEdgeCount the total number of edges in the graph that is
   *   being rendered.
   */
  public boolean renderAllEdges(final int renderNodeCount,
                                final int totalNodeCount,
                                final int totalEdgeCount)
  {
    return false;
  }

  /**
   * Determines whether or not to render a graph at full detail.
   * By default this method returns true if and only if the sum of rendered
   * nodes and rendered edges is less than 1200.<p>
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
   * </table></blockquote><p>
   * This method gets called after renderAllEdges() by the rendering
   * engine.  The renderEdgeCount parameter will be the total edge count in
   * the graph if renderAllEdges() returned true.
   * @param renderNodeCount the number of nodes that are about to be rendered.
   * @param renderEdgeCount the number of edges that are about to be rendered.
   * @return true for full detail, false for low detail.
   * @see #renderAllEdges(int, int)
   */
  public boolean detail(final int renderNodeCount, final int renderEdgeCount)
  {
    return renderNodeCount + renderEdgeCount < 1200;
  }

  /**
   * Determines whether or not to render node borders.  By default this
   * method returns true if and only if the sum of rendered nodes and rendered
   * edges is less than 500.<p>
   * It is only possible to draw node borders at the full detail
   * level.  If low detail is chosen, the output of this method is ignored.<p>
   * This method gets called after renderAllEdges() by the rendering
   * engine.  The renderEdgeCount parameter will be the total edge count in
   * the graph if renderAllEdges() returned true.
   * @param renderNodeCount the number of nodes that are about to be rendered.
   * @param renderEdgeCount the number of edges that are about to be rendered.
   * @return true if and only if node borders are to be rendered.
   * @see #detail(int, int)
   * @see #renderAllEdges(int, int)
   */
  public boolean nodeBorders(final int renderNodeCount,
                             final int renderEdgeCount)
  {
    return renderNodeCount + renderEdgeCount < 500;
  }

  /**
   * Determines whether or not to render node labels.  By default this method
   * returns true if and only if the number of rendered nodes is less than
   * 80.<p>
   * Node labels are only rendered at the full detail level.  If low detail is
   * chosen, the output of this method is ignored.<p>
   * This method gets called after renderAllEdges() by the rendering
   * engine.  The renderEdgeCount parameter will be the total edge count in
   * the graph if renderAllEdges() returned true.
   * @param renderNodeCount the number of nodes that are about to be rendered.
   * @param renderEdgeCount the number of edges that are about to be rendered.
   * @return true if and only if node labels are to be rendered.
   * @see #detail(int, int)
   * @see #renderAllEdges(int, int)
   */
  public boolean nodeLabels(final int renderNodeCount,
                            final int renderEdgeCount)
  {
    return renderNodeCount < 80;
  }

  /**
   * Determines whether or not to draw text as shape when rendering node
   * labels.  By default this method always returns false.<p>
   * This method affects the boolean parameter drawTextAsShape in the method
   * call GraphGraphics.drawTextFull().  If node labels are not rendered
   * altogether, the output of this method is ignored.<p>
   * This method gets called after renderAllEdges() by the rendering
   * engine.  The renderEdgeCount parameter will be the total edge count in
   * the graph if renderAllEdges() returned true.
   * @param renderNodeCount the number of nodes that are about to be rendered.
   * @param renderEdgeCount the number of edges that are about to be rendered.
   * @return true if and only if rendered node label text should be drawn as
   *   primitive shapes.
   * @see #nodeLabels(int, int)
   * @see #renderAllEdges(int, int)
   */
  public boolean textAsShape(final int renderNodeCount,
                             final int renderEdgeCount)
  {
    return false;
  }

  /**
   * Determines whether or not to render edge arrows.  By default this
   * method returns true if and only if the sum of rendered nodes and rendered
   * edges is less than 500.<p>
   * It is only possible to draw edge arrows at the full detail
   * level.  If low detail is chosen, the output of this method is ignored.<p>
   * This method gets called after renderAllEdges() by the rendering
   * engine.  The renderEdgeCount parameter will be the total edge count in
   * the graph if renderAllEdges() returned true.
   * @param renderNodeCount the number of nodes that are about to be rendered.
   * @param renderEdgeCount the number of edges that are about to be rendered.
   * @return true if and only if edge arrows are to be rendered.
   * @see #detail(int, int)
   * @see #renderAllEdges(int, int)
   */
  public boolean edgeArrows(final int renderNodeCount,
                            final int renderEdgeCount)
  {
    return renderNodeCount + renderEdgeCount < 500;
  }

  /**
   * Determines whether or not to honor dashed edges.  By default this
   * method always returns true.  If false is returned, edges that
   * claim to be dashed will be rendered as solid.<p>
   * It is only possible to draw dashed edges at the full detail
   * level.  If low detail is chosen, the output of this method is ignored.
   * Note that drawing dashed edges is computationally expensive;
   * the default implementation of this method does not make a very
   * performance-minded decision if a lot of edges happen to be dashed.<p>
   * This method gets called after renderAllEdges() by the rendering
   * engine.  The renderEdgeCount parameter will be the total edge count in
   * the graph if renderAllEdges() returned true.
   * @param renderNodeCount the number of nodes that are about to be rendered.
   * @param renderEdgeCount the number of edges that are about to be rendered.
   * @return true if and only if dashed edges are to be honored.
   * @see #detail(int, int)
   * @see #renderAllEdges(int, int)
   */
  public boolean dashedEdges(final int renderNodeCount,
                             final int renderEdgeCount)
  {
    return true;
  }

  /**
   * Determines whether or not to honor edge anchors.  By default this
   * method always returns true.  If false is returned, edges that
   * claim to have edge anchors will be rendered as simple straight
   * edges.<p>
   * It is only possible to draw poly-edges at the full detail
   * level.  If low detail is chosen, the output of this method is ignored.<p>
   * This method gets called after renderAllEdges() by the rendering
   * engine.  The renderEdgeCount parameter will be the total edge count in
   * the graph if renderAllEdges() returned true.
   * @param renderNodeCount the number of nodes that are about to be rendered.
   * @param renderEdgeCount the number of edges that are about to be rendered.
   * @return true if and only if edge anchors are to be honored.
   * @see #detail(int, int)
   * @see #renderAllEdges(int, int)
   */
  public boolean edgeAnchors(final int renderNodeCount,
                             final int renderEdgeCount)
  {
    return true;
  }

}
