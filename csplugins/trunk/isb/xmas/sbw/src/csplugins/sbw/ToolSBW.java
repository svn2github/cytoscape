package csplugins.sbw;

import java.awt.event.*;
import javax.swing.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;

/**
 * The ToolBar item for starting SBW services.
 */
public class ToolSBW extends CytoscapeAction {
  
  SBWPlugin plugin;

  public ToolSBW ( SBWPlugin plugin, ImageIcon icon ) {
    super( "", icon );
    this.plugin = plugin;
    setPreferredMenu( "Tools.SBW" );
  }

  public void actionPerformed ( ActionEvent e ) {
    plugin.getConnector().connect();
  }

  public boolean isInToolBar () {
    return true;
  }

  public boolean isInMenuBar () {
    return false;
  }
}
