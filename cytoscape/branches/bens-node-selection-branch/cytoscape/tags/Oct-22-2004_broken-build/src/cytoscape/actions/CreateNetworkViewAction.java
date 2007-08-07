package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.util.*;
import java.awt.event.*;

public class CreateNetworkViewAction extends CytoscapeAction {

  public CreateNetworkViewAction () {
    super( "Create View" );
    setPreferredMenu( "Edit" );
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_V, ActionEvent.ALT_MASK) ;
  }
                               
  public CreateNetworkViewAction ( boolean label ) {
    super();
  }

  public void actionPerformed ( ActionEvent e ) {
    createViewFromCurrentNetwork();
  }

  public static void createViewFromCurrentNetwork () {
    Cytoscape.createNetworkView( Cytoscape.getCurrentNetwork() );
  }
}
