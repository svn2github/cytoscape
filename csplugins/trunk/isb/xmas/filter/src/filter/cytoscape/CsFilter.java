package filter.cytoscape;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;

public class CsFilter extends AbstractPlugin {

  protected CyWindow window;
  protected CyNetwork network;

  public CsFilter ( CytoscapeWindow window ) {

    this.window = window.getCyWindow();
    this.network = window.getNetwork();
    ( ( CytoscapeMenuBar )window.getCyMenus().getMenuBar() ).addAction( new FilterPlugin() );

    // this class should also add its FilterCustomizers

  }

  public String describe () {
    return "New Filters";
  }


}
