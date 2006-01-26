package cytoscape.fung;

public interface TopologyChangeListener
{

  public void nodeCreated(int node);
  public void nodeRemoved(int node);
  public void edgeCreated(int edge);
  public void edgeRemoved(int edge);

}
