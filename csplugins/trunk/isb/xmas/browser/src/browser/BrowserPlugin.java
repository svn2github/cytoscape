package browser;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.plugin.*;


import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.beans.*;
import java.io.*;
import java.util.List;
import javax.swing.*;




public class BrowserPlugin extends CytoscapePlugin {

  public BrowserPlugin () {

    initialize();
  }

  protected void initialize () {


    DataTable table_nodes = new DataTable(Cytoscape.getNodeAttributes(), DataTable.NODES );
    DataTable table_edges = new DataTable(Cytoscape.getEdgeAttributes(), DataTable.EDGES );

  }

}