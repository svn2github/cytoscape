package cytoscape.view;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;

import cytoscape.view.CyMenus;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CyNodeView;
import cytoscape.view.CyEdgeView;

import cytoscape.giny.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.beans.*;

public class NetworkPanel 
  extends
    JPanel
  implements
    PropertyChangeListener,
    TreeSelectionListener {

  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport( this );

  JTree tree;
  DefaultMutableTreeNode root;

  private CytoscapeDesktop cytoscapeDesktop;

  public NetworkPanel ( CytoscapeDesktop desktop ) {
    super();
    this.cytoscapeDesktop = desktop;
    initialize();
  }

  protected void initialize () {
    root = new DefaultMutableTreeNode( "Network Root" );
    tree = new JTree( root );
    tree.addTreeSelectionListener( this );
    add( tree );
    
  }

  public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
    return pcs;
  }


  public void addNetwork ( String network_id ) {
    // first see if it exists
    if ( getNetworkNode( network_id ) == null ) {
      DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode( network_id );
      ( ( DefaultTreeModel )tree.getModel() ).insertNodeInto( dmtn, root,
                                                              root.getChildCount());
      
      tree.scrollPathToVisible(new TreePath(dmtn.getPath()));
    }
  }

  public void focusNetworkNode ( String network_id ) {
    DefaultMutableTreeNode node = getNetworkNode( network_id );
    if ( node != null ) {
      tree.getSelectionModel().setSelectionPath( new TreePath( node.getPath() ) );
      tree.scrollPathToVisible(new TreePath( node.getPath()));
    }
  }

  public DefaultMutableTreeNode getNetworkNode ( String network_id ) {

    Enumeration tree_node_enum = root.breadthFirstEnumeration();
    while ( tree_node_enum.hasMoreElements() ) {
      DefaultMutableTreeNode node = ( DefaultMutableTreeNode )tree_node_enum.nextElement();
      if ( ( String )node.getUserObject() == network_id  ) {
        return node;
      }
    }
    return null;
  }

  public void fireFocus ( String network_id ) {
    pcs.firePropertyChange( new PropertyChangeEvent( this,
                                                     CytoscapeDesktop.NETWORK_VIEW_FOCUS,
                                                     null,
                                                     network_id ) );
  }

  public void valueChanged ( TreeSelectionEvent e ) {

      DefaultMutableTreeNode node = (DefaultMutableTreeNode)
        tree.getLastSelectedPathComponent();
    
      if (node == null) return;
    
      fireFocus( (String)node.getUserObject() );
                                                     
  }

  public void propertyChange ( PropertyChangeEvent e ) {
   
    if ( e.getPropertyName() == Cytoscape.NETWORK_CREATED ) {
      addNetwork( ( String )e.getNewValue() );
    } 

    else if ( e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUSED ) {
      focusNetworkNode( ( String )e.getNewValue() );
    }
    
  }


}
