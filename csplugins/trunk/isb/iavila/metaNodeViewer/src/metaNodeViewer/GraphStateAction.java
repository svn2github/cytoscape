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
    int[] p_nodes = perspective.getNodeIndicesArray();
    for ( int i = 0; i < p_nodes.length; ++i ) {
      System.out.println( "\tPerspective Index: "+p_nodes[i] );//+" Root Index: "+perspective.getRootGraphNodeIndex( p_nodes[i] ) );
    }
    System.out.println( "\t---------edges-------");
    int[] p_edges = perspective.getEdgeIndicesArray();
    for ( int i = 0; i < p_edges.length; ++i ) {
      int s = perspective.getEdgeSourceIndex( p_edges[i] );
      int t = perspective.getEdgeTargetIndex( p_edges[i] );
      System.out.println( "\t Perspecitve Edge Index: "+p_edges[i] );//+" Root Index: "+perspective.getRootGraphEdgeIndex( p_edges[i] ) );
      System.out.println( "\t\tSource Perspecitve Index: "+s+" Root Index: "+perspective.getRootGraphNodeIndex( s ) );
      System.out.println( "\t\tTarget Perspecitve Index: "+t+" Root Index: "+perspective.getRootGraphNodeIndex( t ) );
    }
  }
}
