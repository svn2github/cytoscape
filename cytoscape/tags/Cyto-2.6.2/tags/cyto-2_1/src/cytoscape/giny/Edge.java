package cytoscape.giny;

import cytoscape.CyNode;

/**
 * @deprecated
 */
public interface Edge extends giny.model.Edge {

  public CyNode getSourceNode ();

  public CyNode getTargetNode ();

}
