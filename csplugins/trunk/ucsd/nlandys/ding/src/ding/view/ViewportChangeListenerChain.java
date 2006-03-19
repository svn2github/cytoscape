package ding.view;

class ViewportChangeListenerChain implements ViewportChangeListener
{

  private final ViewportChangeListener a, b;

  private ViewportChangeListenerChain(ViewportChangeListener a,
                                      ViewportChangeListener b)
  {
    this.a = a;
    this.b = b;
  }

  public void viewportChanged(double newXCenter, double newYCenter,
                              double newScaleFactor)
  {
    a.viewportChanged(newXCenter, newYCenter, newScaleFactor);
    b.viewportChanged(newXCenter, newYCenter, newScaleFactor);
  }

  static ViewportChangeListener add(ViewportChangeListener a,
                                    ViewportChangeListener b)
  {
    if (a == null) return b;
    if (b == null) return a;
    return new ViewportChangeListenerChain(a, b);
  }

  static ViewportChangeListener remove(ViewportChangeListener l,
                                       ViewportChangeListener oldl)
  {
    if (l == oldl || l == null) return null;
    else if (l instanceof ViewportChangeListenerChain)
      return ((ViewportChangeListenerChain) l).remove(oldl);
    else return l;
  }

  private ViewportChangeListener remove(ViewportChangeListener oldl)
  {
    if (oldl == a) return b;
    if (oldl == b) return a;
    ViewportChangeListener a2 = remove(a, oldl);
    ViewportChangeListener b2 = remove(b, oldl);
    if (a2 == a && b2 == b) return this;
    return add(a2, b2);
  }

}
