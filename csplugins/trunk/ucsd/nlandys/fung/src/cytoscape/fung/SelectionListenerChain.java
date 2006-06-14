package cytoscape.fung;

import cytoscape.util.intr.IntStack;

final class SelectionListenerChain implements SelectionListener
{

  private final SelectionListener a, b;

  private SelectionListenerChain(final SelectionListener a,
                                 final SelectionListener b)
  {
    this.a = a;
    this.b = b;
  }

  public final void nodeSelected(final int node)
  {
    a.nodeSelected(node);
    b.nodeSelected(node);
  }

  public final void nodeUnselected(final int node)
  {
    a.nodeUnselected(node);
    b.nodeUnselected(node);
  }

  public final void edgeSelected(final int edge)
  {
    a.edgeSelected(edge);
    b.edgeSelected(edge);
  }

  public final void edgeUnselected(final int edge)
  {
    a.edgeUnselected(edge);
    b.edgeUnselected(edge);
  }

  static final SelectionListener add(final SelectionListener a,
                                     final SelectionListener b)
  {
    if (a == null) { return b; }
    if (b == null) { return a; }
    return new SelectionListenerChain(a, b);
  }

  static final SelectionListener remove(final SelectionListener l,
                                        final SelectionListener oldl)
  {
    if (l == oldl || l == null) { return null; }
    else if (l instanceof SelectionListenerChain) {
      return ((SelectionListenerChain) l).remove(oldl); }
    else return l;
  }

  private final SelectionListener remove(final SelectionListener oldl)
  {
    if (oldl == a) { return b; }
    if (oldl == b) { return a; }
    final SelectionListener a2 = remove(a, oldl);
    final SelectionListener b2 = remove(b, oldl);
    if (a2 == a && b2 == b) { return this; }
    return add(a2, b2);
  }

}
