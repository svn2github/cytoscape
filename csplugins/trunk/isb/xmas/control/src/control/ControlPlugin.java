package control;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.plugin.*;

import control.view.*;

import javax.swing.*;

public class ControlPlugin extends CytoscapePlugin {

  protected JFrame frame;


  public ControlPlugin (  ) {
    initialize();
  }

  protected void initialize () {
    ControlAction ca = new ControlAction( );
    Cytoscape.getDesktop().getCyMenus().addCytoscapeAction( ( CytoscapeAction )ca );
  }


}
