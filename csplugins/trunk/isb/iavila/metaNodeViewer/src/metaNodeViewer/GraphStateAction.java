package metaNodeViewer;

import java.awt.event.*;
import javax.swing.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
import giny.model.*;

public class GraphStateAction extends CytoscapeAction {

  CyWindow window;

  public GraphStateAction ( CyWindow window) {
    super( "See State" );
    this.window = window;
    setPreferredMenu( "Tools" );
  }

  public void actionPerformed (ActionEvent e) {
    CyNetwork network = window.getNetwork();
    RootGraph root = network.getRootGraph();
    GraphPerspective perspective = network.getGraphPerspective();

    System.out.println( "++++++++++++++++GraphPerspective++++++++++++++++") ;
    
    System.out.println( "\t---------Nodes-------");
    int[] p_nodes = perspective.getNodeIndicesArray(); // These are RootGraph indices
    for ( int i = 0; i < p_nodes.length; ++i ) {
      System.out.println( "\tRoot Index: "+p_nodes[i]+" Perspective Index: "+perspective.getNodeIndex(p_nodes[i]));
    }
    System.out.println( "\t---------edges-------");
    int[] p_edges = perspective.getEdgeIndicesArray(); // These are RootGraph indices
    for ( int i = 0; i < p_edges.length; ++i ) {
      int s = perspective.getEdgeSourceIndex( p_edges[i] );
      int t = perspective.getEdgeTargetIndex( p_edges[i] );
      System.out.println( "\t Root Index: "+p_edges[i]+" Perspective Index: "+perspective.getEdgeIndex(p_edges[i]));
      System.out.println( "\t\tSource Perspecitve Index: "+s+" Root Index: "+perspective.getRootGraphNodeIndex( s ) );
      System.out.println( "\t\tTarget Perspecitve Index: "+t+" Root Index: "+perspective.getRootGraphNodeIndex( t ) );
    }
  }
}
