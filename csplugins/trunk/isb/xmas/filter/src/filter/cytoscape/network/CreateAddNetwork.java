package filter.cytoscape.network;

import java.awt.event.*;
import javax.swing.*;

import filter.*;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;


public class CreateAddNetwork extends CytoscapeAction {

  protected NetworkManagement nm;

  public CreateAddNetwork ( ImageIcon icon ) {
    super( "Network +/-", icon );
    setPreferredMenu( "Filters" );
  }

  public void actionPerformed ( ActionEvent e ) {

    getNetworkManagement().setVisible( true );

  }

  public NetworkManagement getNetworkManagement () {
    if ( nm == null ) {
      nm = new NetworkManagement();
    }
    return nm;
  }
  


}
