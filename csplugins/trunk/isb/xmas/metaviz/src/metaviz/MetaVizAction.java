package metaviz;

import java.awt.event.*;
import javax.swing.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;

import giny.model.*;
import giny.view.*;

import java.util.*;

import edu.umd.cs.piccolo.PNode;
import phoebe.*;

public class MetaVizAction extends CytoscapeAction {

  protected CyWindow window;

  public MetaVizAction ( CyWindow window ) {
    super( "Create MetaViz" );
    this.window = window;
    setPreferredMenu( "MetaNodes" );
  }

  public void actionPerformed (ActionEvent e) {
   
    CyNetwork network = window.getNetwork();
    GraphPerspective perspective = network.getGraphPerspective();
    RootGraph root = perspective.getRootGraph();

    List nodes = perspective.nodesList();
    Iterator i = nodes.iterator();
    Node new_node = root.getNode( root.createNode() );

    while ( i.hasNext() ) {
      root.addMetaChild( new_node, ( Node )i.next() );
    }
    List edges = perspective.edgesList();
    i = edges.iterator();
    while( i.hasNext() ) {
      root.addMetaChild( new_node, ( Edge )i.next() );
    }

    perspective.restoreNode( new_node );

    InternalCameraNode node = new InternalCameraNode( new_node.getRootGraphIndex(),  ( PGraphView )window.getView() );
     window.getView().addNodeView( new_node.getRootGraphIndex(), node );

    //node.setBounds( 0, 0, 60, 60 );

  }

}
