package fing.model;

import giny.model.RootGraphChangeEvent;
import giny.model.RootGraphChangeListener;

// Package visible.
// Analagous to java.awt.AWTEventMulticaster for chaining together
// giny.model.RootGraphChangeListener objects.  Example usage:
//
// public class Bar implements RootGraph
// {
//   private RootGraphChangeListener lis = null;
//   void addRootGraphChangeListener(RootGraphChangeListener l) {
//     lis = RootGraphChangeListenerChain.add(lis, l); }
//   void removeRootGraphChangeListener(RootGraphChangeListener l) {
//     lis = RootGraphChangeListenerChain.remove(lis, l); }
//   ...
// }

class RootGraphChangeListenerChain implements RootGraphChangeListener
{

  private final RootGraphChangeListener a, b;

  private RootGraphChangeListenerChain(RootGraphChangeListener a,
                                       RootGraphChangeListener b)
  {
    this.a = a;
    this.b = b;
  }

  public void rootGraphChanged(RootGraphChangeEvent evt)
  {
    a.rootGraphChanged(evt);
    b.rootGraphChanged(evt);
  }

  static RootGraphChangeListener add(RootGraphChangeListener a,
                                     RootGraphChangeListener b)
  {
    if (a == null) return b;
    if (b == null) return a;
    return new RootGraphChangeListenerChain(a, b);
  }

  static RootGraphChangeListener remove(RootGraphChangeListener l,
                                        RootGraphChangeListener oldl)
  {
    if (l == oldl || l == null) return null;
    else if (l instanceof RootGraphChangeListenerChain)
      return ((RootGraphChangeListenerChain) l).remove(oldl);
    else return l;
  }

  private RootGraphChangeListener remove(RootGraphChangeListener oldl)
  {
    if (oldl == a) return b;
    if (oldl == b) return a;
    RootGraphChangeListener a2 = remove(a, oldl);
    RootGraphChangeListener b2 = remove(b, oldl);
    if (a2 == a && b2 == b) return this;
    return add(a2, b2);
  }

}
