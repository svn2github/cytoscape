package integration;

import cytoscape.*;
import integration.view.IntegrationWindow;
import javax.swing.*;

public class IntegrationPlugin extends AbstractPlugin {

  public IntegrationPlugin ( CytoscapeWindow cw ) {
    IntegrationWindow iw = new IntegrationWindow();
    JFrame f = new JFrame( "Test" );
    f.getContentPane().add( iw );
    f.pack();
    f.setVisible( true );
  }

  public String describe ( ) {
    return ( "This be a Plugin" );
  }

}
