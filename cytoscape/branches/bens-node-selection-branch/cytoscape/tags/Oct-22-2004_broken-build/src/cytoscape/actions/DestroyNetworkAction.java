package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.util.*;
import java.awt.event.*;

public class DestroyNetworkAction extends CytoscapeAction {

  public DestroyNetworkAction () {
    super( "Destroy Network" );
    setPreferredMenu( "Edit" );
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_W, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK) ;
  }
                               
  public DestroyNetworkAction ( boolean label ) {
    super();
  }

  public void actionPerformed ( ActionEvent e ) {
    destroyCurrentNetwork();
  }

  public static void destroyCurrentNetwork () {
    Cytoscape.destroyNetwork( Cytoscape.getCurrentNetwork() );
  }
}
