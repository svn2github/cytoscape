package org.cytoscape.graph.layout;

/**
 * This class encapsulates the logic needed to perform a layout on a graph.
 * One of the goals of this class is to limit the set
 * of dependent classes and packages to an absolute minimum.  This class
 * should be so concise that it would be a simple effort to
 * provide layout algorithms as web services using this layout framework.
 */
public abstract class LayoutAlgorithm implements Runnable
{

  /**
   * The graph that this algorithm will lay out.
   * By agreement, methods on <code>graph</code> will be called only from
   * the thread that invokes <code>run()</code>.
   **/
  protected final LayoutGraph graph;

  protected LayoutAlgorithm(LayoutGraph graph)
  {
    if (graph == null) throw new NullPointerException("graph is null");
    this.graph = graph;
  }

  /**
   * <code>run()</code> is called externally, not by subclasses.
   * Subclasses must implement this method - layout logic should be
   * executed in this method, by the same thread that calls this method.
   * The layout logic should act on
   * the <code>LayoutGraph</code> stored in <code>this.graph</code>.
   * <code>run()</code> shall only be called once for a given instance.
   * If an asynchronous call to <code>destroy()</code> is made while
   * <code>run()</code> is executing, <code>run()</code> should make an effort
   * to abort its operations and exit as soon as possible.
   */
  public abstract void run();

  /**
   * <code>destroy()</code> is called externally, not by subclasses.
   * Subclasses must implement this method.  <code>destroy()</code> should
   * not block; it should return quickly.  If [an asynchronous] thread is
   * executing <code>run()</code> when <code>destroy()</code> is called,
   * a signal should be sent to the thread that is executing <code>run()</code>
   * to abort and exit <code>run()</code>.  If <code>run()</code> has not
   * been called at the time that <code>destroy()</code> is invoked,
   * a later call to <code>run()</code> should not actually
   * &quot;run&quot; anything.  If <code>run()</code> has already been run
   * and has exited by the time <code>destroy()</code> is called,
   * <code>destroy()</code> should do nothing.
   * There is no guarantee that <code>destroy()</code> will be called on
   * and instance of this class.
   */
  public abstract void destroy();

}
