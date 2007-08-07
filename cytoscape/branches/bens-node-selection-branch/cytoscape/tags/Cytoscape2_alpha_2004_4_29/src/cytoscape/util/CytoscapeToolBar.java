package cytoscape.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JToolBar;
import javax.swing.JButton;

import java.util.*;

public class CytoscapeToolBar
  extends JToolBar {

  protected Map actionButtonMap = null;
  protected Set actionMembersSet;

  /**
   * Default constructor delegates to the superclass void constructor and then
   * calls {@link #initializeCytoscapeToolBar()}.
   */
  public CytoscapeToolBar () {
    super();
    initializeCytoscapeToolBar();
  }

  /**
   * Envelop if you wish.  Presently does nothing.
   */
  protected void initializeCytoscapeToolBar () {
    // Do nothing.
  } 



 
  /**
   * If the given Action has an absent or false inToolBar property, return;
   * otherwise delegate to addAction( String, Action ) with the value of its
   * preferredButtonGroup property, or null if it does not have that property.
   */
  public boolean addAction ( Action action ) {
    String button_group_name = null;
    if( action instanceof CytoscapeAction ) {
      if( ( ( CytoscapeAction )action ).isInToolBar() ) {
        button_group_name =
          ( ( CytoscapeAction )action ).getPreferredButtonGroup();
      } else {
        return false;
      }
    }
    return addAction( button_group_name, action );
  } // addAction( action )

  /**
   * Note that this presently ignores the button group name.
   */
  public boolean addAction ( String button_group_name, Action action ) {
    // At present we allow an Action to be in this tool bar only once.
    JButton button = null;
    if( actionButtonMap != null ) {
      button =
      ( JButton )actionButtonMap.get( action );
    }
    if( button != null ) {
      return false;
    }
    button = createJButton( action );
    // TODO: Do something with the preferred button group.
    //add( button );
    
    add( action );
    
    if( actionButtonMap == null ) {
      actionButtonMap = createActionButtonMap();
    }
    actionButtonMap.put( action, button );
    return true;
  } // addAction( button_group_name, action )

  /**
   * If the given Action has an absent or false inToolBar property, return;
   * otherwise if there's a button for the action, remove it.
   */
  public boolean removeAction ( Action action ) {
    if( actionButtonMap == null ) {
      return false;
    }
    JButton button = 
      ( JButton )actionButtonMap.remove( action );
    if( button == null ) {
      return false;
    }
    remove( button );
    return true;
  } // removeAction( action )

  /**
   * CytoscapeToolBars are unique -- this equals() method returns true
   * iff the other object == this.
   */
  public boolean equals ( Object other_object ) {
    return ( this == other_object );
  } // equals( Object )

 
  /**
   * Factory method for instantiating the buttons in the toolbar.
   */
  protected JButton createJButton ( Action action ) {
    return new JButton( action );
  }

  /**
   * Factory method for instantiating the action->button map.
   */
  protected Map createActionButtonMap () {
    return new HashMap();
  }

} // class CytoscapeToolBar
