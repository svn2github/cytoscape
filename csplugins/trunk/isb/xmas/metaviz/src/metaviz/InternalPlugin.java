package metaviz;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.plugin.*;

public class InternalPlugin extends AbstractPlugin {

  protected CyWindow window;

  public InternalPlugin ( CyWindow window ) {

    this.window = window;
    window.getCyMenus().getMenuBar().
      addAction( new MetaVizAction( window ) );

  }

  public String describe () {
    return "MetaNode viz";
  }

}
