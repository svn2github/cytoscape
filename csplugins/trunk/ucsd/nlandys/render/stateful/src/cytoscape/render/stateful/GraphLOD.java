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
   * This method answers the question: do we render the graph at full [or
   * low] detail?
   * @param
   */
  public boolean fullDetail(final int visibleNodeCount,
                            final int visibleEdgeCount)
  {
    return visibleNodeCount + visibleEdgeCount < 1200;
  }

}
