package cytoscape.fung;

import cytoscape.util.intr.IntEnumerator;

public interface SelectionListener
{

  public void nodesSelected(IntEnumerator selectedNodes);
  public void nodesUnselected(IntEnumerator unselectedNodes);
  public void edgesSelected(IntEnumerator selectedEdges);
  public void edgesUnselected(IntEnumerator unselectedEdges);

}
