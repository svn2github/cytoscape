package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.util.*;
import java.awt.event.*;

public class DestroyNetworkViewAction extends CytoscapeAction {

  public DestroyNetworkViewAction () {
    super( "Destroy View" );
    setPreferredMenu( "Edit" );
  }
                               
  public DestroyNetworkViewAction ( boolean label ) {
    super();
  }

  public void actionPerformed ( ActionEvent e ) {
    destroyViewFromCurrentNetwork();
  }

  public static void destroyViewFromCurrentNetwork () {
    Cytoscape.destroyNetworkView( Cytoscape.getCurrentNetwork() );
  }
}
