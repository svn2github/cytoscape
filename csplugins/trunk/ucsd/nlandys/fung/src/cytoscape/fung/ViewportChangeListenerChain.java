package cytoscape.fung;

final class ViewportChangeListenerChain implements ViewportChangeListener
{

  private final ViewportChangeListener a, b;

  private ViewportChangeListenerChain(ViewportChangeListener a,
                                      ViewportChangeListener b)
  {
    this.a = a;
    this.b = b;
  }

  public final void viewportChanged(final int w, final int h,
                                    final double newXCenter,
                                    final double newYCenter,
                                    final double newScaleFactor)
  {
    a.viewportChanged(w, h, newXCenter, newYCenter, newScaleFactor);
    b.viewportChanged(w, h, newXCenter, newYCenter, newScaleFactor);
  }

  static final ViewportChangeListener add(final ViewportChangeListener a,
                                          final ViewportChangeListener b)
  {
    if (a == null) { return b; }
    if (b == null) { return a; }
    return new ViewportChangeListenerChain(a, b);
  }

  static final ViewportChangeListener remove(final ViewportChangeListener l,
                                             final ViewportChangeListener oldl)
  {
    if (l == oldl || l == null) { return null; }
    else if (l instanceof ViewportChangeListenerChain) {
      return ((ViewportChangeListenerChain) l).remove(oldl); }
    else return l;
  }

  private final ViewportChangeListener remove(
                                             final ViewportChangeListener oldl)
  {
    if (oldl == a) { return b; }
    if (oldl == b) { return a; }
    final ViewportChangeListener a2 = remove(a, oldl);
    final ViewportChangeListener b2 = remove(b, oldl);
    if (a2 == a && b2 == b) { return this; }
    return add(a2, b2);
  }

}
