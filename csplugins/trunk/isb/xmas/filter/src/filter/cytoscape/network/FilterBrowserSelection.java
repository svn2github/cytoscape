package filter.cytoscape.network;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.io.*;

import javax.swing.*;
import javax.swing.border.*;

import ViolinStrings.Strings;

import cytoscape.*;
import cytoscape.browsers.*;
import giny.model.*;
import filter.view.*;
import filter.model.*;
import java.beans.*;
import cytoscape.util.*;
public class FilterBrowserSelection
  extends
    JFrame
  implements
    ActionListener {

  JButton dispayAttributes;
  FilterListPanel filterListPanel;
  final static String invisibilityPropertyName = "nodeAttributeCategories.invisibleToBrowser";

  public FilterBrowserSelection () {
    super( "Filter Browser Selection" );
    initialize();
  }

  protected void initialize () {

    JPanel main_panel = new JPanel();
    
    dispayAttributes = new JButton( "Broswer" );
    dispayAttributes.addActionListener( this );
    filterListPanel = new FilterListPanel(1);


    main_panel.setLayout( new BorderLayout() );
    main_panel.add( filterListPanel, BorderLayout.CENTER );
    main_panel.add( dispayAttributes, BorderLayout.SOUTH );
    setContentPane( main_panel );

    pack();

  }

  public void actionPerformed ( ActionEvent e ) {

    Filter filter = filterListPanel.getSelectedFilter();

    Iterator nodes_i = Cytoscape.getRootGraph().nodesList().iterator();
    Set nodes = new HashSet();
    CyNode node;
  
    while ( nodes_i.hasNext() ) {
      node = ( CyNode )nodes_i.next();
      try {
        if ( filter.passesFilter(node) ) {
          nodes.add( node );
        }
      } catch(StackOverflowError soe){
        return ;
      }
    }
  
    Iterator edges_i = Cytoscape.getRootGraph().edgesList().iterator();
    Set edges = new HashSet();
    CyEdge edge;
  
    while ( edges_i.hasNext() ) {
      edge = ( CyEdge )edges_i.next();
      try {
        if ( filter.passesFilter(edge) ) {
          edges.add( edge );
        }
      } catch(StackOverflowError soe){
        return ;
      }
    }
     Properties configProps = Cytoscape.getCytoscapeObj().getConfiguration().getProperties();
     Vector attributeCategoriesToIgnore = Misc.
       getPropertyValues(configProps, invisibilityPropertyName);
     String webBrowserScript  = configProps.getProperty("webBrowserScript", "noScriptDefined");

    TabbedBrowser nodeBrowser = null;
    TabbedBrowser edgeBrowser = null;

    Node[] selectedNodes = ( Node[] ) nodes.toArray( new Node[0] );
    Edge[] selectedEdges = ( Edge[] ) edges.toArray( new Edge[0] );

     if ( selectedNodes.length > 0) {
      nodeBrowser = new TabbedBrowser (selectedNodes, 
                                       Cytoscape.getNodeNetworkData(),
                                       attributeCategoriesToIgnore, 
                                       webBrowserScript, 
                                       TabbedBrowser.BROWSING_NODES);
    }
        
    if ( selectedEdges.length > 0) {
      edgeBrowser = new TabbedBrowser (selectedEdges, 
                                       Cytoscape.getEdgeNetworkData(),
                                       attributeCategoriesToIgnore, 
                                       webBrowserScript, 
                                       TabbedBrowser.BROWSING_EDGES);
    }

  }

}
