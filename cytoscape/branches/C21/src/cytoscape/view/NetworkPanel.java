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

import cytoscape.util.swing.*;

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

  JTreeTable treeTable;
  DefaultMutableTreeNode root;

  private CytoscapeDesktop cytoscapeDesktop;

  public NetworkPanel ( CytoscapeDesktop desktop ) {
    super();
    this.cytoscapeDesktop = desktop;
    initialize();
  }

  protected void initialize () {
    setLayout( new BorderLayout() );
    setPreferredSize( new Dimension( 181, 700 ) );
    root = new DefaultMutableTreeNode( "Network Root" );
    treeTable = new JTreeTable( new NetworkTreeTableModel( root ) );
    treeTable.getTree().addTreeSelectionListener( this );
    treeTable.getTree().setRootVisible( false );


    treeTable.getColumn( "Network" ).setMaxWidth(100);
    treeTable.getColumn( "Nodes" ).setMaxWidth(40);
    treeTable.getColumn( "Edges" ).setMaxWidth(40);

    //treeTable.setMaximumSize( new Dimension( 150,  ) );
     add( new JScrollPane( treeTable ), BorderLayout.CENTER );
    
  }

  public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
    return pcs;
  }

  //TODO: update to allow for children
  public void addNetwork ( String network_id ) {
    // first see if it exists
    if ( getNetworkNode( network_id ) == null ) {
      System.out.println( "Adding new Network node: "+network_id );
      DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode( network_id );
     
      // TODO: is all this really needed?
      root.add( dmtn );
      treeTable.getTree().collapsePath( new TreePath( new TreeNode[] {root } ) );
      treeTable.getTree().updateUI();
      TreePath path = new TreePath( dmtn.getPath() );
      treeTable.getTree().expandPath( path );
      treeTable.getTree().scrollPathToVisible( path );
      treeTable.doLayout();
    }
  }

  public void focusNetworkNode ( String network_id ) {
    DefaultMutableTreeNode node = getNetworkNode( network_id );
    if ( node != null ) {
      treeTable.getTree().getSelectionModel().setSelectionPath( new TreePath( node.getPath() ) );
      treeTable.getTree().scrollPathToVisible(new TreePath( node.getPath()));
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
        treeTable.getTree().getLastSelectedPathComponent();
    
      if (node == null ) return;
      if (node.getUserObject() == null ) return;
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

  /**
   * Inner class that extends the AbstractTreeTableModel
   */
  class NetworkTreeTableModel extends AbstractTreeTableModel {
    
    String[] columns = { "Network", "Nodes", "Edges" };
    Class[] columns_class = { TreeTableModel.class, Integer.class, Integer.class };

    public NetworkTreeTableModel ( Object root ) {
      super( root );
    }

    public Object getChild (Object parent, int index) {
      Enumeration tree_node_enum = ( ( DefaultMutableTreeNode )getRoot() ).breadthFirstEnumeration();
      while ( tree_node_enum.hasMoreElements() ) {
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode )tree_node_enum.nextElement();
        if ( node == parent  ) {
          return node.getChildAt( index ) ;
        }
      }
    return null;
    }

    public int getChildCount(Object parent) {
        Enumeration tree_node_enum = ( ( DefaultMutableTreeNode )getRoot() ).breadthFirstEnumeration();
      while ( tree_node_enum.hasMoreElements() ) {
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode )tree_node_enum.nextElement();
        if ( node == parent  ) {
          return node.getChildCount() ;
        }
      }
      return 0;
    }

  public int getColumnCount() {
    return columns.length;

  }

  public String getColumnName( int column) {
    return columns[column];
  }

  public Class getColumnClass(int column) {
    return columns_class[column];
  }

  public Object getValueAt(Object node, int column) {
    if ( column == 0 ) 
      return ( ( DefaultMutableTreeNode )node).getUserObject();
    else if ( column == 1 ) 
      return new Integer( Cytoscape.getNetwork( ( String )( ( DefaultMutableTreeNode )node ).getUserObject() ).getNodeCount() );
    else if ( column == 2 )
      return new Integer( Cytoscape.getNetwork( ( String )( ( DefaultMutableTreeNode )node ).getUserObject() ).getEdgeCount() );

    return new Integer(0);

  }
    


  } // NetworkTreeTableModel
  

}
