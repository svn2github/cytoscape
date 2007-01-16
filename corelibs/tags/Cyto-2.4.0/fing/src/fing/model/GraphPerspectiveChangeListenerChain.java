package fing.model;

import giny.model.GraphPerspectiveChangeEvent;
import giny.model.GraphPerspectiveChangeListener;

// Package visible.
// Analagous to java.awt.AWTEventMulticaster for chaining together
// giny.model.GraphPerspectiveChangeListener objects.  Example usage:
//
// public class Foo implements GraphPerspective
// {
//   private GraphPerspectiveChangeListener lis = null;
//   public void addGraphPerspectiveChangeListener(
//                                          GraphPerspectiveChangeListener l) {
//     lis = GraphPerspectiveChangeListenerChain.add(lis, l); }
//   public void removeGraphPerspectiveChangeListener(
//                                          GraphPerspectiveChangeListener l) {
//     lis = GraphPerspectiveChangeListenerChain.remove(lis, l); }
//   ...
// }

class GraphPerspectiveChangeListenerChain
  implements GraphPerspectiveChangeListener
{

  private final GraphPerspectiveChangeListener a, b;

  private GraphPerspectiveChangeListenerChain(GraphPerspectiveChangeListener a,
                                              GraphPerspectiveChangeListener b)
  {
    this.a = a;
    this.b = b;
  }

  public void graphPerspectiveChanged(GraphPerspectiveChangeEvent evt)
  {
    a.graphPerspectiveChanged(evt);
    b.graphPerspectiveChanged(evt);
  }

  static GraphPerspectiveChangeListener add(GraphPerspectiveChangeListener a,
                                            GraphPerspectiveChangeListener b)
  {
    if (a == null) return b;
    if (b == null) return a;
    return new GraphPerspectiveChangeListenerChain(a, b);
  }

  static GraphPerspectiveChangeListener remove(
    GraphPerspectiveChangeListener l, GraphPerspectiveChangeListener oldl)
  {
    if (l == oldl || l == null) return null;
    else if (l instanceof GraphPerspectiveChangeListenerChain)
      return ((GraphPerspectiveChangeListenerChain) l).remove(oldl);
    else return l;
  }

  private GraphPerspectiveChangeListener remove(
    GraphPerspectiveChangeListener oldl)
  {
    if (oldl == a) return b;
    if (oldl == b) return a;
    GraphPerspectiveChangeListener a2 = remove(a, oldl);
    GraphPerspectiveChangeListener b2 = remove(b, oldl);
    if (a2 == a && b2 == b) return this;
    return add(a2, b2);
  }

}
