package control;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.plugin.*;

import control.view.*;

import javax.swing.*;

public class ControlPlugin extends AbstractPlugin {

  protected JFrame frame;
  protected CyWindow window;

  public ControlPlugin ( CyWindow window ) {
    this.window = window;
    initialize();
  }

  protected void initialize () {
    ControlAction ca = new ControlAction( window );
    window.getCyMenus().addCytoscapeAction( ( CytoscapeAction )ca );
  }


}
