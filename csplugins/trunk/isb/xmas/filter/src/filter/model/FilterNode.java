package filter.model;

import javax.swing.tree.*;
import javax.swing.event.*;
import java.util.*;
import java.beans.*;


/**
 * The Root FilterNode has a default of having its children be CHILDREN_ARE_AND.  The
 * default for all other nodes is for nodes with a single child to be CHILDREN_ARE_OR,
 * and nodes with multiple children to be CHILDREN_ARE_AND.  This can all be changed.
 *
 * When an Object is first passed to this node
 */
public class FilterNode extends DefaultMutableTreeNode implements Filter {


  public static int CHILDREN_ARE_AND = 0;
  public static int CHILDREN_ARE_OR = 1;
  public static int CHILDREN_ARE_XOR = 2;

  private int childrenType = CHILDREN_ARE_AND; // default, I guess
  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this );

  public FilterNode ( int children_type, Filter filter ) {
    super( filter );
    this.childrenType = children_type;
  }

  public  SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
    return pcs;
  }

  public void propertyChange ( PropertyChangeEvent event ) {
  }

  public String getEditorName () {
    return "Node";
  }

  public int getChildrenType () {
    return childrenType;
  }

  public void setChildrenType ( int type ) {
    childrenType = type;
  }


  public void add ( FilterNode child ) {
    super.add( child );
  }
  public String toString () {
    return getFilter().toString();
  }
  
  public String getFilterID () {
    return getFilter().toString();
  }
  

  public Object clone () {
    return super.clone();
  }

  public Class[] getPassingTypes () { 
    return getFilter().getPassingTypes();
  }

  public boolean equals ( Object other_object ) {
    if ( other_object instanceof FilterNode && ( ( FilterNode )other_object).getChildrenType() == getChildrenType()   && ( ( FilterNode )other_object).getFilter() == getFilter() ) {
      return true;
    }
    return false;
  }

  /**
   * This returns the Filter that is  tied to this FilterNode
   */
  public Filter getFilter () {
    return ( Filter )getUserObject();
  }

  public Filter setFilter ( Filter new_filter ) {
    Filter old_filter = getFilter();
    setUserObject( new_filter );
    return old_filter;
  }


  public boolean passesFilterNode ( Object o ) {
    return passesFilter( o );
  }

  public boolean passesFilter ( Object o ) {
    // determine if the object passes the Filter associated with _this_ node.
    boolean my_result = getFilter().passesFilter( o );
    if ( childrenType == CHILDREN_ARE_AND ) {
      if ( !my_result ) {
        // if we don't pass this filter, then there is no reason to go to the 
        // next filter.
        return false;
      } else if ( my_result && getChildCount() == 0 ) {
        return true;
      }
      // our result was true so return what our children found. And we have children
      return passesChildren( o );
    } else if ( childrenType == CHILDREN_ARE_OR ) {
      if ( my_result ) {
        // we passed, doesn't matter about our children
        return true;
      } 
      // we were false, now return the result of our children
      return passesChildren( o );
    } else if ( childrenType == CHILDREN_ARE_XOR ) {
      boolean children_pass = passesChildren( o );
      if ( my_result && !children_pass ) {
        // we are true, children false or empty, we pass
        return true;
      } else if ( my_result && children_pass ) {
        // we both can't be true
        return false;
      } else if ( !my_result && children_pass ) {
        // just the children pass
        return true;
      } else {
        // both us and our chilren were false
        return false;
      }
    } 
    return false;
  }

  /**
   * This method will test all of its children accoriding to the 
   * children type that was set.  If there are no children it will return
   * false.
   */
  public boolean passesChildren ( Object o ) {
    boolean child_passed;

    if ( getChildCount() == 0 ) {
      // there are no Children for this node
      return false;
    }

    Enumeration children = children();
    
    if ( childrenType == CHILDREN_ARE_AND ) {
      child_passed = true;
      while ( child_passed && children.hasMoreElements() ) {
        FilterNode node = ( FilterNode )children.nextElement();
        child_passed = node.passesFilterNode( o );
      }
      return child_passed;
    } else if ( childrenType == CHILDREN_ARE_XOR ) {
      child_passed = false;
      while ( children.hasMoreElements() ) {
        FilterNode node = ( FilterNode )children.nextElement();
        boolean passed_node =  node.passesFilterNode( o );
        if ( child_passed && passed_node ) {
          // this means that two children passed, which violates XOR
          return false;
        }
        child_passed = passed_node;
      }
      return child_passed;
    } else if ( childrenType == CHILDREN_ARE_OR ) {
      child_passed = false;
      while ( !child_passed && children.hasMoreElements() ) {
        FilterNode node = ( FilterNode )children.nextElement();
        if ( node.passesFilterNode( o ) ) {
          // as soon as just one criteria is met, we are done.
          return true;
        }
      }
      return child_passed;
    }
    return false;
  }
  
  //----------------------------------------//
  // IO
  //----------------------------------------//

  public String output () {
    return null;
  }
  
  public Filter input ( String desc ) {
    return null;
  }


}
