package cytoscape.layout;

import cytoscape.view.CyNetworkView;

/**
 * A Layout can be applied to a CyNetworkView
 */
public interface Layout {

  /**
   * This could have unexpected consequences
   */
  public void setNetworkView ( CyNetworkView new_view );

  public CyNetworkView getNetworkView();

  public void applyLayout ();

}
