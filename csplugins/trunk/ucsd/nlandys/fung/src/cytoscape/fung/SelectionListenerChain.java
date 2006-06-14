package cytoscape.fung;

import cytoscape.util.intr.IntStack;

final class SelectionListenerChain
{

  private final Object a, b;

  private SelectionListenerChain(final Object a, final Object b)
  {
    this.a = a;
    this.b = b;
  }

  final void fireNodesSelected(final IntStack selectedNodes)
  {
    if (a instanceof SelectionListener) {
      ((SelectionListener) a).nodesSelected(selectedNodes.elements()); }
    else {
      ((SelectionListenerChain) a).fireNodesSelected(selectedNodes); }
    if (b instanceof SelectionListener) {
      ((SelectionListener) b).nodesSelected(selectedNodes.elements()); }
    else {
      ((SelectionListenerChain) b).fireNodesSelected(selectedNodes); }
  }

  final void fireNodesUnselected(final IntStack unselectedNodes)
  {
    if (a instanceof SelectionListener) {
      ((SelectionListener) a).nodesUnselected(unselectedNodes.elements()); }
    else {
      ((SelectionListenerChain) a).fireNodesUnselected(unselectedNodes); }
    if (b instanceof SelectionListener) {
      ((SelectionListener) b).nodesUnselected(unselectedNodes.elements()); }
    else {
      ((SelectionListenerChain) b).fireNodesUnselected(unselectedNodes); }
  }

  final void fireEdgesSelected(final IntStack selectedEdges)
  {
    if (a instanceof SelectionListener) {
      ((SelectionListener) a).edgesSelected(selectedEdges.elements()); }
    else {
      ((SelectionListenerChain) a).fireEdgesSelected(selectedEdges); }
    if (b instanceof SelectionListener) {
      ((SelectionListener) b).edgesSelected(selectedEdges.elements()); }
    else {
      ((SelectionListenerChain) b).fireEdgesSelected(selectedEdges); }
  }

  final void fireEdgesUnselected(final IntStack unselectedEdges)
  {
    if (a instanceof SelectionListener) {
      ((SelectionListener) a).edgesUnselected(unselectedEdges.elements()); }
    else {
      ((SelectionListenerChain) a).fireEdgesUnselected(unselectedEdges); }
    if (b instanceof SelectionListener) {
      ((SelectionListener) b).edgesUnselected(unselectedEdges.elements()); }
    else {
      ((SelectionListenerChain) b).fireEdgesUnselected(unselectedEdges); }
  }

  static final Object add(final Object a,
                          final Object b)
  {
    if (a == null) { return b; }
    if (b == null) { return a; }
    return new SelectionListenerChain(a, b);
  }

  static final Object remove(final Object o,
                             final Object oldo)
  {
    if (o == oldo || o == null) { return null; }
    else if (o instanceof SelectionListenerChain) {
      return ((SelectionListenerChain) o).remove(oldo); }
    else return o;
  }

  private final Object remove(final Object oldo)
  {
    if (oldo == a) { return b; }
    if (oldo == b) { return a; }
    final Object a2 = remove(a, oldo);
    final Object b2 = remove(b, oldo);
    if (a2 == a && b2 == b) { return this; }
    return add(a2, b2);
  }

}
