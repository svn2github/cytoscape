package cytoscape.util;

//import shadegrown.DataTypeUtilities;
//import shadegrown.Reidentifiable;
//import shadegrown.PropertyChangeEventSource;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.event.SwingPropertyChangeSupport;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import java.beans.PropertyChangeListener;

public abstract class CytoscapeAction
  extends AbstractAction {

  protected String preferredMenu = null;
  protected String preferredButtonGroup = null;
  protected Integer menuIndex = new Integer( -1 );
  protected boolean acceleratorSet = false;
  protected int keyModifiers;
  protected int keyCode;

  /**
   * @beaninfo (rwb)
   */
  private boolean reidentificationEnabled = true;

  public CytoscapeAction () {
    super();
    initialize();
  }

  public CytoscapeAction ( String name ) {
    super( name );
    initialize();
  }

  public CytoscapeAction ( String name, javax.swing.Icon icon ) {
    super( name, icon );
    initialize();
  }

  // implements AbstractAction
  public abstract void actionPerformed ( ActionEvent e );

  /**
   * Initialization method called by all constructors.  Envelop if you wish.
   */
  protected void initialize () {
    // Do nothing.
  }

  /**
   * The default clone() implementation delegates to the create() method of
   * DataTypeUtilities.getDataTypeFactory( this.getClass() ).  Override if your
   * CytoscapeAction maintains state that must be transmitted to the
   * clone.
   */
  // implements Cloneable
  public Object clone () {
    return this;
  } // clone()

  /**
   * By default all CytoscapeActions wish to be included in CommunityMenuBars,
   * but you may override if you wish.
   * @return true If this Action should be included in a CommunityMenuBar.
   * @see #getPrefferedMenu();
   * @beaninfo (ri)
   */
  public boolean isInMenuBar () {
    return true;
  }

  /**
   * By default no CytoscapeActions wish to be included in CommunityToolBars,
   * but you may override if you wish.
   * @return true If this Action should be included in a CommunityMenuBar.
   * @see #getPrefferedButtonGroup();
   * @beaninfo (ri)
   */
  public boolean isInToolBar () {
    return false;
  }

  public void setPreferredIndex ( int index ) {
    menuIndex = new Integer( index );
  }

  public Integer getPrefferedIndex () {
    return menuIndex;
  }
  
  public void setAcceleratorCombo ( int key_code, int key_mods ) {
    acceleratorSet = true;
    keyCode = key_code;
    keyModifiers = key_mods;
  }
  
  public boolean isAccelerated () {
    return acceleratorSet;
  }
  
  public int getKeyCode () {
    return keyCode;
  }

  public int getKeyModifiers () {
    return keyModifiers;
  }


  /**
   * This method returns a Menu specification string.  Submenus are preceeded
   * by dots in this string, so the result "File.Import" specifies the submenu
   * "Import" of the menu "File".  If the result is null, the menu will be
   * placed in a default location.
   * @return a Menu specification string, or null if this Action should be
   * placed in a default Menu.
   * @see #inMenuBar()
   */
  public String getPreferredMenu () {
    return preferredMenu;
  }

  /**
   * @beaninfo (rwb)
   */
  public void setPreferredMenu ( String new_preferred ) {
    if( ( preferredMenu == new_preferred ) ||
        ( ( preferredMenu != null ) &&
          preferredMenu.equals( new_preferred ) ) ) {
      return;
    }
    String old_preferred = preferredMenu;
    preferredMenu = new_preferred;
    firePropertyChange( "preferredMenu", old_preferred, new_preferred );
  } // setPreferredMenu( String )

  /**
   * This method returns a ButtonGroup specification string.  Subgroups are
   * preceeded by dots in this string, so the result "Edit.Selection Modes"
   * specifies the subgroup "Selection Modes" of the group "Edit".  If the
   * result is null, the button will be placed in a default location.
   * @return a ButtonGroup specification string, or null if the button for this
   * Action should be placed in a default ButtonGroup.
   * @see #inToolBar()
   */
  public String getPreferredButtonGroup () {
    return preferredButtonGroup;
  }

  /**
   * @beaninfo (rwb)
   */
  public void setPreferredButtonGroup ( String new_preferred ) {
    if( preferredButtonGroup.equals( new_preferred ) ) {
      return;
    }
    String old_preferred = preferredButtonGroup;
    preferredButtonGroup = new_preferred;
    firePropertyChange( "preferredButtonGroup", old_preferred, new_preferred );
  } // setPreferredButtonGroup( String )

  /**
   * Delegates to getValue( SHORT_DESCRIPTION ).
   * @see #putValue( String, Object )
   */
  // implements CommunityMember
  public String getIdentifier () {
    Object desc = getValue( SHORT_DESCRIPTION );
    if( desc == null ) {
      return null;
    } else if( desc instanceof String ) {
      return ( String )desc;
    } else {
      return desc.toString();
    }
  } // getIdentifier()

  /**
   * Delegates to putValue( SHORT_DESCRIPTION, Object ).
   * @see #putValue( String, Object )
   * @beaninfo (rwb)
   */
  // implements Reidentifiable
  public void setIdentifier ( String new_identifier ) {
    putValue( SHORT_DESCRIPTION, new_identifier );
  } // setIdentifier( String )

  // implements Reidentifiable
  public boolean isReidentificationEnabled () {
    return reidentificationEnabled;
  } // isReidentificationEnabled()

  /**
   * @beaninfo (rwb)
   */
  protected void setReidentificationEnabled (
    boolean reidentification_enabled
  ) {
    if( reidentificationEnabled == reidentification_enabled ) {
      return;
    }
    reidentificationEnabled = reidentification_enabled;
    firePropertyChange(
      "reidentificationEnabled",
      new Boolean( !reidentificationEnabled ),
      new Boolean( reidentificationEnabled )
    );
  } // isReidentificationEnabled()

  /**
   * Enveloped to fire an "identifier" PropertyChangeEvent when the key is
   * SHORT_DESCRIPTION and the identifer will change as a result of this call.
   * @see #getIdentifier()
   */
  // envelops AbstractAction
  public void putValue ( String key, Object value ) {
    if( key.equals( SHORT_DESCRIPTION ) ) {
      String old_identifier = getIdentifier();
      String new_identifier = ( ( value == null ) ? null : value.toString() );
      if( ( ( new_identifier == null ) && ( old_identifier != null ) ) ||
          ( ( new_identifier != null ) && !new_identifier.equals( old_identifier ) ) ) {
        super.putValue( key, value );
        firePropertyChange( "identifier", old_identifier, new_identifier );
        return;
      }
    }
    super.putValue( key, value );
  } // putValue( String, Object )

 //  // implements CommunityMember
//   public void setCommunity ( Community new_community ) {
//     if( community == new_community ) {
//       return;
//     }
//     Community old_community = community;
//     if( old_community != null ) {
//       leavingCommunityHook();
//     }
//     community = new_community;
//     if( new_community != null ) {
//       enteringCommunityHook();
//     }
//     firePropertyChange( "community", old_community, new_community );
//   } // setCommunity(..)

//   // implements CommunityMember
//   public Community getCommunity () {
//     return community;
//   } // getCommunity()

  /**
   * Envelop this method if you wish to do something just before leaving a
   * Community.  The default implementation does nothing.
   */
  protected void leavingCommunityHook () {
    // Do nothing.
  } // leavingCommunityHook()

  /**
   * Envelop this method if you wish to do something just after entering a
   * Community.  The default implementation does nothing.
   */
  protected void enteringCommunityHook () {
    // Do nothing.
  } // enteringCommunityHook()

  // implements CommunityMember
  public void addPropertyChangeListener (
    String property,
    PropertyChangeListener listener
  ) {
    if( changeSupport == null ) {
      changeSupport = new SwingPropertyChangeSupport( this );
    }
    changeSupport.addPropertyChangeListener( property, listener );
  } // addPropertyChangeListener( String, PropertyChangeListener )

  // implements CommunityMember
  public void removePropertyChangeListener (
    String property,
    PropertyChangeListener listener
  ) {
    if( changeSupport == null ) {
      changeSupport = new SwingPropertyChangeSupport( this );
    }
    changeSupport.removePropertyChangeListener( property, listener );
  } // removePropertyChangeListener( String, PropertyChangeListener )

  // implements CommunityMember
  public PropertyChangeListener[] getPropertyChangeListeners (
    String property
  ) {
    if( changeSupport == null ) {
      changeSupport = new SwingPropertyChangeSupport( this );
    }
    return changeSupport.getPropertyChangeListeners( property );
  } // getPropertyChangeListeners( String )

} // class CytoscapeAction
