package csplugins.sbw;

import java.awt.event.*;
import javax.swing.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;


/**
 * The MenuItem for starting SBW services.
 */
public class MenuSBW extends CytoscapeAction {

  SBWPlugin plugin;

  public MenuSBW ( SBWPlugin plugin, ImageIcon icon ) {
    super( "SBW Connect", icon );
    this.plugin = plugin;
    setPreferredMenu( "Tools.SBW" );
  }

  public void actionPerformed ( ActionEvent e ) {
    //    plugin.getConnector().

      SBWConnector connector =  plugin.getConnector();

      if ( connector == null ) {
        System.out.print( "Connector was null" );
        return;
      }

      connector.connect();
  }

  public boolean isInToolBar () {
    return false;
  }

  public boolean isInMenuBar () {
    return true;
  }

}
