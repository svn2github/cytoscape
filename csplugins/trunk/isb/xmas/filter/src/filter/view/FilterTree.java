package filter.view;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.SwingPropertyChangeSupport;
import filter.model.*;
import java.util.*;
import java.beans.*;
import java.awt.*;

/**
 * A Filter Tree holds a tree of Filters.  Filters are usually associated with
 * a Filter node to allow for the stringing together of fitlers.  The Filter 
 * tree pretty much just provides a nice way to view them.
 */
public class FilterTree extends JTree implements Filter {

  protected FilterNode root;
  protected String identifier; 

  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport( this );

  /**
   * Creates an empty FilterTree
   */
  public FilterTree () {
    super();
    setRoot( null );
  }

  public FilterTree ( FilterNode root ) {
    super ();
    setRoot( root );
    // initialize();
  }

  public void initialize () {
    
    setCellRenderer( new  TreeCellRenderer () {

        public Component getTreeCellRendererComponent( JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus ) {
          JLabel label = new JLabel();
          if ( value == null ) 
            label.setText( "null" );
          label.setText( value.toString() );
         
          if(row % 2 != 0){
            label.setBackground(Color.white);
            label.setForeground(Color.black);
          }
          else{
            label.setBackground(Color.gray);
            label.setForeground(Color.black);
          }
          return label;
        }
      } );
  }

  public SwingPropertyChangeSupport getSwingPropertyChangeSupport () {
    return pcs;
  }

  public String getEditorName () {
    return "FilterTree" ;
  }


  public void setRoot ( FilterNode root ) {
    this.root = root;
    setModel( new DefaultTreeModel( root ) );
  }

  public FilterNode getRoot () {
    return root;
  }

  public FilterNode getSelectedNode () {
    TreePath path = getSelectionPath();
    if ( path == null ) {
      return getRoot();
    } 
    FilterNode selection = ( FilterNode )path.getLastPathComponent();
    if ( selection == null ) {
      return getRoot();
    }
    return selection;
  }

  public DefaultTreeModel getTreeModel() {
    return ( DefaultTreeModel )super.getModel();
  }
  

  public FilterNode addFilterNode ( Filter filter ) {
    
    // TODO: child type 
    FilterNode child = new FilterNode( 0, filter );
    FilterNode parent = getSelectedNode();
    getTreeModel().insertNodeInto( child, parent, parent.getChildCount() );
    scrollPathToVisible(new TreePath(child.getPath()));
    return child;
  }

  public FilterNode addFilterNode ( FilterNode child ) {
    FilterNode parent = getSelectedNode();
    getTreeModel().insertNodeInto( child, parent, parent.getChildCount() );
    scrollPathToVisible(new TreePath(child.getPath()));
    return child;

  }


  public FilterNode removeSelectedNode () {

    FilterNode child = getSelectedNode();
    if ( child == getRoot() ) {
      return null;
    }

    FilterNode parent = ( FilterNode )child.getParent();
    if ( parent != null ) {
      getTreeModel().removeNodeFromParent( child );
    }
    return child;
  }

  public FilterNode removeNode ( FilterNode child ) {
    if ( child == getRoot() ) {
      return null;
    }

    FilterNode parent = ( FilterNode )child.getParent();
    if ( parent != null ) {
      getTreeModel().removeNodeFromParent( child );
    }
    return child;
  }



  public String toString () {
    return identifier;
  }

  public void setIdentifier ( String new_id ) {
    identifier = new_id;
  }

  public boolean passesFilter ( Object object ) {
    return root.passesFilter( object );
  }

  /**
   * @return the Passing types of the Root node.
   */
  public Class[] getPassingTypes () {
    return root.getPassingTypes();
  }
  
  public boolean equals ( Object other_object ) {
    if ( other_object instanceof FilterTree ) {
      if ( ( ( FilterTree )other_object).getRoot() == getRoot() ) {
        return true;

      }
    }
    return false;
  }

  public Object clone () {

    FilterNode new_root = ( FilterNode )root.clone();
    Enumeration enum = root.children();
    while ( enum.hasMoreElements() ) {
      FilterNode child = ( FilterNode )enum.nextElement();
      FilterNode new_child = ( FilterNode )child.clone();
      new_root.add( new_child );
      cloneChildren( child, new_child );
    }
    return new FilterTree( new_root );
  }

  private void  cloneChildren ( FilterNode parent, FilterNode new_parent ) {
    Enumeration enum = parent.children();
    while ( enum.hasMoreElements() ) {
      FilterNode child = ( FilterNode )enum.nextElement();
      FilterNode new_child = ( FilterNode )child.clone();
      new_parent.add( new_child );
      cloneChildren( child, new_child );

    }

  }

  public void propertyChange ( PropertyChangeEvent e ) {}


}
