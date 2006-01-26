package cytoscape.fung;

final class TopologyChangeListenerChain implements TopologyChangeListener
{

  private final TopologyChangeListener a, b;

  private TopologyChangeListenerChain(TopologyChangeListener a,
                                      TopologyChangeListener b)
  {
    this.a = a;
    this.b = b;
  }

  public void nodeCreated(int node)
  {
    a.nodeCreated(node);
    b.nodeCreated(node);
  }

  public void nodeRemoved(int node)
  {
    a.nodeRemoved(node);
    b.nodeRemoved(node);
  }

  public void edgeCreated(int edge)
  {
    a.edgeCreated(edge);
    b.edgeCreated(edge);
  }

  public void edgeRemoved(int edge)
  {
    a.edgeRemoved(edge);
    b.edgeRemoved(edge);
  }

  static TopologyChangeListener add(TopologyChangeListener a,
                                    TopologyChangeListener b)
  {
    if (a == null) { return b; }
    if (b == null) { return a; }
    return new TopologyChangeListenerChain(a, b);
  }

  static TopologyChangeListener remove(TopologyChangeListener l,
                                       TopologyChangeListener oldl)
  {
    if (l == oldl || l == null) { return null; }
    else if (l instanceof TopologyChangeListenerChain) {
      return ((TopologyChangeListenerChain) l).remove(oldl); }
    else return l;
  }

  private TopologyChangeListener remove(TopologyChangeListener oldl)
  {
    if (oldl == a) { return b; }
    if (oldl == b) { return a; }
    TopologyChangeListener a2 = remove(a, oldl);
    TopologyChangeListener b2 = remove(b, oldl);
    if (a2 == a && b2 == b) { return this; }
    return add(a2, b2);
  }

}
