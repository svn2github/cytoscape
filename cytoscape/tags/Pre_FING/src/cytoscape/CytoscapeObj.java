package cytoscape;

import java.io.File;

import cytoscape.data.servers.BioDataServer;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.CalculatorCatalogFactory;
import cytoscape.visual.CalculatorIO;
import cytoscape.plugin.*;

import java.beans.*;

public class CytoscapeObj {
 
  public CytoscapeObj () {
  }

  /**
   * Returns the (possibly null) bioDataServer.
   *
   * @see BioDataServer
   */
  public BioDataServer getBioDataServer() {
    return Cytoscape.getBioDataServer();
  }

  public CytoscapeConfig getConfiguration () {
    return new CytoscapeConfig();
  }


  public int getViewThreshold () {
    return 500;
  }

}

