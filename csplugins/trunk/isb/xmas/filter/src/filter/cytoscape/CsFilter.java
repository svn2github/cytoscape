package filter.cytoscape;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;

import filter.model.*;
import filter.view.*;

public class CsFilter extends AbstractPlugin {

  protected CyWindow window;
  protected CyNetwork network;

  public CsFilter ( CytoscapeWindow window ) {

    this.window = window.getCyWindow();
    this.network = window.getNetwork();
    ( ( CytoscapeMenuBar )window.getCyMenus().getMenuBar() ).addAction( new FilterPlugin( network, this.window ) );
    ( ( CytoscapeMenuBar )window.getCyMenus().getMenuBar() ).addAction( new EditorAction() );
     
     FilterManager.defaultManager().addEditor( new DefaultFilterEditor() );
     FilterManager.defaultManager().addEditor( new FilterTreeEditor() );
     FilterManager.defaultManager().addEditor( new CsNodeTypeFilterEditor( window.getNetwork() ) );
    

  }

  public String describe () {
    return "New Filters";
  }


}
