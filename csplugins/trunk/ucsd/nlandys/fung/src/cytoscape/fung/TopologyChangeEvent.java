package cytoscape.fung;

public abstract class TopologyChangeEvent
{

  public static final int NODE_ADDED = 1;
  public static final int NODE_REMOVED = 2;
  public static final int EDGE_ADDED = 3;
  public static final int EDGE_REMOVED = 4;

  public abstract int getTopologyChangeType();

  public abstract int getAddedNode();
  public abstract int getRemovedNode();
  public abstract int getAddedEdge();
  public abstract int getRemovedEdge();

}
