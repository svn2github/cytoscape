package filter.model;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import filter.model.*;
import javax.swing.border.*;
import java.beans.*;
import javax.swing.event.SwingPropertyChangeSupport;
import ViolinStrings.Strings;

public class DefaultFilter 
  implements Filter  {

  protected String searchString;
  
  protected String identifier = "default";
  
  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);

  public static String SEARCH_STRING_EVENT = "SEARCH_STRING_EVENT";
  public static String FILTER_NAME_EVENT = "FILTER_NAME_EVENT";
  public static String FILTER_ID = "DefaultFilter";

  public DefaultFilter () {
    // creates a Filter 
  }

  public DefaultFilter ( String search_string, String identifier ) {
      this.searchString = search_string;
      this.identifier =identifier;
  }
  
  public DefaultFilter ( String search_string ) {
    this.searchString = search_string;
    this.identifier = "default";
  }

  //----------------------------------------//
  // Implements Filter

  public String toString () {
    return identifier;
  }

  public String getFilterID() {
    return FILTER_ID;
  }
  

  public void setIdentifier ( String new_id ) {
    FilterManager.defaultManager().renameFilter( identifier, new_id );
    this.identifier = new_id;
  }

  public String getEditorName () {
    return "Default";
  }

  /**
   * An Object Passes this Filter if its "toString" method
   * matches any of the Text from the TextField
   */
  public boolean passesFilter ( Object object ) {
    String object_string = object.toString();
    String[] pattern = searchString.split("\\s");
    for ( int p = 0; p < pattern.length; ++p ) {
      if ( Strings.isLike( object_string, pattern[p], 0, true ) ) {
        // since this is an "or" filter, as soon as we pass
        return true;
      }
    }
    return false;
  }
  
  public Class[] getPassingTypes () {
    return null;
  }
  
  public boolean equals ( Object other_object ) {
    if ( other_object instanceof DefaultFilter ) {
      if ( ( ( DefaultFilter )other_object).getSearchString().equals( getSearchString() ) ) {
        return true;
      }
    }
    return false;
  }
  
  public Object clone () {
    return new DefaultFilter ( searchString );
  }
  
  public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
    return pcs;
  }

  //----------------------------------------//
  // DefaultFilter methods
  //----------------------------------------//

  public void propertyChange ( PropertyChangeEvent e ) {
    if ( e.getPropertyName() == SEARCH_STRING_EVENT ) {
      //System.out.println( "Search String Changed to "+( String )e.getNewValue() );
      setSearchString( ( String )e.getNewValue() );
    } else if ( e.getPropertyName() == FILTER_NAME_EVENT ) {
      setIdentifier( ( String )e.getNewValue() );
    }
  }

  public String getSearchString () {
    return searchString;
  }

  public void setSearchString ( String search_string ) {
    this.searchString = search_string;
  }

  public void fireSearchStringChanged () {
    pcs.firePropertyChange( Filter.FILTER_MODIFIED, null, searchString );
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






