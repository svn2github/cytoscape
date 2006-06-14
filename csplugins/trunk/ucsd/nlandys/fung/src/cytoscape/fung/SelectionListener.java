package cytoscape.fung;

import cytoscape.util.intr.IntEnumerator;

public interface SelectionListener
{

  public void nodeSelected(int selectedNode);
  public void nodeUnselected(int unselectedNode);
  public void edgeSelected(int selectedEdge);
  public void edgeUnselected(int unselectedEdge);

}
