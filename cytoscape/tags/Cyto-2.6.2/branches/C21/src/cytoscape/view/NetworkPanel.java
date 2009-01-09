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
  NetworkTreeNode root;

  private CytoscapeDesktop cytoscapeDesktop;

  public NetworkPanel ( CytoscapeDesktop desktop ) {
    super();
    this.cytoscapeDesktop = desktop;
    initialize();
  }

  protected void initialize () {
    setLayout( new BorderLayout() );
    setPreferredSize( new Dimension( 181, 700 ) );
    root = new NetworkTreeNode( "Network Root", "root" );
    treeTable = new JTreeTable( new NetworkTreeTableModel( root ) );
    treeTable.getTree().addTreeSelectionListener( this );
    treeTable.getTree().setRootVisible( false );

    ToolTipManager.sharedInstance().registerComponent(treeTable.getTree());
    treeTable.getTree().setCellRenderer( new MyRenderer() );


    treeTable.getColumn( "Network" ).setMaxWidth(100);
    treeTable.getColumn( "Nodes" ).setMaxWidth(40);
    treeTable.getColumn( "Edges" ).setMaxWidth(40);

    //treeTable.setMaximumSize( new Dimension( 150,  ) );
    add( new JScrollPane( treeTable ), BorderLayout.CENTER );
    
  }

  public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
    return pcs;
  }

  public void removeNetwork ( String network_id ) {

    NetworkTreeNode node = getNetworkNode( network_id );
    Enumeration children = node.children();
    NetworkTreeNode child = null;
    while ( children.hasMoreElements() ){
      child = ( NetworkTreeNode )children.nextElement();
      child.removeFromParent();
      root.add( child );
    }
    node.removeFromParent();
    treeTable.getTree().collapsePath( new TreePath( new TreeNode[] {root} ) );
    treeTable.getTree().updateUI();
    //TreePath path = new TreePath( child.getPath() );
    //treeTable.getTree().expandPath( path );
    //treeTable.getTree().scrollPathToVisible( path );
    treeTable.doLayout();
    
  }


  public void addNetwork ( String network_id, String parent_id ) {
    // first see if it exists
    if ( getNetworkNode( network_id ) == null ) {
      System.out.println( "Adding new Network node: "+network_id );
      NetworkTreeNode dmtn = new NetworkTreeNode( Cytoscape.getNetwork( network_id ).getTitle(), network_id );
     
      if ( parent_id != null ) {
        NetworkTreeNode parent = getNetworkNode( parent_id );
        parent.add( dmtn );
      } else {
        root.add( dmtn );
      }
      
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

  public NetworkTreeNode getNetworkNode ( String network_id ) {

    Enumeration tree_node_enum = root.breadthFirstEnumeration();
    while ( tree_node_enum.hasMoreElements() ) {
      NetworkTreeNode node = ( NetworkTreeNode )tree_node_enum.nextElement();
      if ( ( String )node.getNetworkID() == network_id  ) {
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

    NetworkTreeNode node = (NetworkTreeNode)
      treeTable.getTree().getLastSelectedPathComponent();
    
    if (node == null ) return;
    if (node.getUserObject() == null ) return;
    fireFocus( (String)node.getNetworkID() );
                                                     
  }

  public void propertyChange ( PropertyChangeEvent e ) {
   
    if ( e.getPropertyName() == Cytoscape.NETWORK_CREATED ) {
      addNetwork( ( String )e.getNewValue(), ( String )e.getOldValue() );
    } 

    if ( e.getPropertyName() == Cytoscape.NETWORK_DESTROYED ) {
      removeNetwork( ( String )e.getNewValue() );
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
        return  new Integer( Cytoscape.getNetwork( ( ( NetworkTreeNode )node).getNetworkID() ).getNodeCount() );
      else if ( column == 2 )
        return  new Integer( Cytoscape.getNetwork( ( ( NetworkTreeNode )node).getNetworkID() ).getEdgeCount() );

      return new Integer(0);

    }

  } // NetworkTreeTableModel
  
  protected class NetworkTreeNode extends DefaultMutableTreeNode {

    protected String network_uid;

    public NetworkTreeNode ( Object userobj, String id ) {
      super( userobj );
      network_uid = id;
    }

    protected void setNetworkID ( String id ) {
      network_uid = id;
    }

    protected String getNetworkID () {
      return network_uid;
    }
  }

  private class MyRenderer extends DefaultTreeCellRenderer {
        Icon tutorialIcon;

        public MyRenderer() {
            
        }

        public Component getTreeCellRendererComponent(
                            JTree tree,
                            Object value,
                            boolean sel,
                            boolean expanded,
                            boolean leaf,
                            int row,
                            boolean hasFocus) {

            super.getTreeCellRendererComponent(
                            tree, value, sel,
                            expanded, leaf, row,
                            hasFocus);
            
            if ( hasView(value) ) {
              //setIcon(tutorialIcon);
              setBackgroundNonSelectionColor( java.awt.Color.green.brighter() );
              //setBackgroundSelectionColor( java.awt.Color.green.darker() );
            } else {
              setBackgroundNonSelectionColor( java.awt.Color.pink.brighter() );
              //setBackgroundSelectionColor( java.awt.Color.pink.darker() );
               
            }

            return this;
        }


    private boolean hasView ( Object value ) {
      
      NetworkTreeNode node = ( NetworkTreeNode )value;
      setToolTipText( Cytoscape.getNetwork( node.getNetworkID() ).getTitle() );
      return Cytoscape.viewExists(  node.getNetworkID() );
      
    }
  
  }


}
