package fing.model;

import giny.model.RootGraph;

/**
 * This class defines static methods that provide new instances
 * of giny.model.RootGraph objects.
 */
public final class FingRootGraphFactory
{

  // "No constructor".
  private FingRootGraphFactory() { }

  /**
   * Returns a new instance of giny.model.RootGraph.  Obviously, a new
   * RootGraph instance contains no nodes or edges.
   */
  public final static RootGraph instantiateRootGraph()
  {
    return new FRootGraph();
  }

}
