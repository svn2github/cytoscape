package filter.cytoscape;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import filter.model.*;
import javax.swing.border.*;
import java.beans.*;
import javax.swing.event.SwingPropertyChangeSupport;

import cytoscape.*;
import cytoscape.data.*;

import giny.model.*;

import ViolinStrings.Strings;

/**
 * This is a Cytoscape specific filter that will pass edges if
 * a selected attribute matches a specific value.
 */
public class CsEdgeTypeFilter
  implements Filter  {
  
  //----------------------------------------//
  // Filter specific properties 
  //----------------------------------------//
  protected String selectedAttribute;
  protected String searchString;
  
  public static String SEARCH_STRING_EVENT = "SEARCH_STRING_EVENT";
  public static String SELECTED_ATTRIBUTE_EVENT = "SELECTED_ATTRIBUTE_EVENT";
  public static String FILTER_NAME_EVENT = "FILTER_NAME_EVENT";

  public static String FILTER_ID = "CsEdgeTypeFilter";

  //----------------------------------------//
  // Cytoscape specific Variables
  //----------------------------------------//
  protected CyNetwork network;
  protected GraphObjAttributes edgeAttributes;


  //----------------------------------------//
  // Needed Variables
  //----------------------------------------//
  protected String identifier = "default";
  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);
  
  
  //---------------------------------------//
  // Constructor
  //----------------------------------------//

  /**
   * Creates a new CsEdgeTypeFilter
   */  
  public CsEdgeTypeFilter ( CyNetwork network,  
                            String selectedAttribute, 
                            String searchString,
                            String identifier ) {
    this.network = network;
    this.edgeAttributes = network.getEdgeAttributes();
    this.selectedAttribute = selectedAttribute;  
    this.searchString = searchString;
    this.identifier =identifier;
  }
  
  //----------------------------------------//
  // Implements Filter
  //----------------------------------------//

  /**
   * Returns the name for this Filter
   */
  public String toString () {
    return identifier;
  }

  /**
   * sets a new name for this filter
   */
  public void setIdentifier ( String new_id ) {
    FilterManager.defaultManager().renameFilter( identifier, new_id );
    this.identifier = new_id;
  }

  /**
   * This is usually the same as the class name
   */
  public String getFilterID () {
    return FILTER_ID;
  }

  /**
   * An Object Passes this Filter if its "toString" method
   * matches any of the Text from the TextField
   */
  public boolean passesFilter ( Object object ) {

    Object value = null;
    Edge edge;
   
    if ( object instanceof Edge ) {
      edge = ( Edge )object;
      value = edgeAttributes.getValue( selectedAttribute, (String)edgeAttributes.getCanonicalName(edge) );
      System.out.println( "Value returned for edge: "+edge.getIdentifier()+" attribute: "+selectedAttribute+" was: "+value );
    }


    // I am assuming that we should have a string here
    if ( value instanceof String == false )
      return false;

    String[] pattern = searchString.split("\\s");
     for ( int p = 0; p < pattern.length; ++p ) {
       if ( Strings.isLike( ( String )value, pattern[p], 0, true ) ) {
         // this is an OR function
         return true;
       }
     }
     return false;
  }
  
  public Class[] getPassingTypes () {
    return null;
  }
  
  public boolean equals ( Object other_object ) {
    if ( other_object instanceof CsEdgeTypeFilter ) {
      if ( ( ( CsEdgeTypeFilter )other_object).getSearchString().equals( getSearchString() ) ) {
        return true;
      }
    }
    return false;
  }
  
  public Object clone () {
    return new CsEdgeTypeFilter ( network, selectedAttribute, searchString, identifier+"_new" );
  }
  
  public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
    return pcs;
  }

  //----------------------------------------//
  // CsEdgeTypeFilter methods
  //----------------------------------------//

  public void propertyChange ( PropertyChangeEvent e ) {
    if ( e.getPropertyName() == SEARCH_STRING_EVENT ) {
      System.out.println( "Search String Changed to "+( String )e.getNewValue() );
      setSearchString( ( String )e.getNewValue() );
    } else if ( e.getPropertyName() == FILTER_NAME_EVENT ) {
      setIdentifier( ( String )e.getNewValue() );
    } else if ( e.getPropertyName() == SELECTED_ATTRIBUTE_EVENT ) {
      setSelectedAttribute( ( String )e.getNewValue() );
    }  
  }
  
  // SearchString /////////////////////////////////

  public String getSearchString () {
    return searchString;
  }

  public void setSearchString ( String search_string ) {
    this.searchString = search_string;
    fireSearchStringChanged();
  }

  public void fireSearchStringChanged () {
    pcs.firePropertyChange( SEARCH_STRING_EVENT, null, searchString );
  }

  // Selected_Attribute ///////////////////////////

  public String getSelectedAttribute () {
    return selectedAttribute;
  }

  public void setSelectedAttribute ( String new_attr ) {
    this.selectedAttribute = new_attr;
    fireSelectedAttributeModified();
  }

  public void fireSelectedAttributeModified () {
    pcs.firePropertyChange( SELECTED_ATTRIBUTE_EVENT, null, selectedAttribute );
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

