package diff;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;

public class DiffPlugin extends CytoscapePlugin {


  public DiffPlugin () {
    initialize();
  }

  protected void initialize () {
    DiffAction diff_action = new DiffAction();
    Cytoscape.getDesktop().getCyMenus().addCytoscapeAction( diff_action );
  }
}
