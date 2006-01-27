package cytoscape.fung;

final class TopologyChangeListenerChain implements TopologyChangeListener
{

  private final TopologyChangeListener a, b;

  private TopologyChangeListenerChain(final TopologyChangeListener a,
                                      final TopologyChangeListener b)
  {
    this.a = a;
    this.b = b;
  }

  public final void nodeCreated(final int node)
  {
    a.nodeCreated(node);
    b.nodeCreated(node);
  }

  public final void nodeRemoved(final int node)
  {
    a.nodeRemoved(node);
    b.nodeRemoved(node);
  }

  public final void edgeCreated(final int edge)
  {
    a.edgeCreated(edge);
    b.edgeCreated(edge);
  }

  public final void edgeRemoved(final int edge)
  {
    a.edgeRemoved(edge);
    b.edgeRemoved(edge);
  }

  static final TopologyChangeListener add(final TopologyChangeListener a,
                                          final TopologyChangeListener b)
  {
    if (a == null) { return b; }
    if (b == null) { return a; }
    return new TopologyChangeListenerChain(a, b);
  }

  static final TopologyChangeListener remove(final TopologyChangeListener l,
                                             final TopologyChangeListener oldl)
  {
    if (l == oldl || l == null) { return null; }
    else if (l instanceof TopologyChangeListenerChain) {
      return ((TopologyChangeListenerChain) l).remove(oldl); }
    else return l;
  }

  private final TopologyChangeListener remove(
                                             final TopologyChangeListener oldl)
  {
    if (oldl == a) { return b; }
    if (oldl == b) { return a; }
    final TopologyChangeListener a2 = remove(a, oldl);
    final TopologyChangeListener b2 = remove(b, oldl);
    if (a2 == a && b2 == b) { return this; }
    return add(a2, b2);
  }

}
