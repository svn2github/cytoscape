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

import ViolinStrings.Strings;

/**
 * This is a Cytoscape specific filter that will pass nodes if
 * a selected attribute matches a specific value.
 */
public class CsNodeTypeFilter
  implements Filter  {
  
  //----------------------------------------//
  // Filter specific properties 
  //----------------------------------------//
  protected String selectedAttribute;
  protected String searchString;
  
  public static String SEARCH_STRING_EVENT = "SEARCH_STRING_EVENT";
  public static String SELECTED_ATTRIBUTE_EVENT = "SELECTED_ATTRIBUTE_EVENT";
  public static String FILTER_NAME_EVENT = "FILTER_NAME_EVENT";
 

  //----------------------------------------//
  // Cytoscape specific Variables
  //----------------------------------------//
  protected CyNetwork network;
  protected GraphObjAttributes nodeAttributes;


  //----------------------------------------//
  // Needed Variables
  //----------------------------------------//
  protected String identifier = "default";
  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);
  
  
  //---------------------------------------//
  // Constructor
  //----------------------------------------//

  /**
   * Creates a new CsNodeTypeFilter
   */  
  public CsNodeTypeFilter ( CyNetwork network,  
                            String selectedAttribute, 
                            String searchString,
                            String identifier ) {
    this.network = network;
    this.nodeAttributes = network.getNodeAttributes();
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
  public String getEditorName () {
    return "Node Attribute";
  }

  /**
   * An Object Passes this Filter if its "toString" method
   * matches any of the Text from the TextField
   */
  public boolean passesFilter ( Object object ) {
    // test to make sure this object has an entry in the node map
    if ( !nodeAttributes.getObjectMap().containsKey( object )  ) {
      System.out.println( "Object: "+object+" was not found in the nodeAttributes object map." );
      return false;
    }

    // therefore we must be in the map, do a test to determine if 
    // our search string does indeed match the selectedAttribute for this
    // object.  this will be an "isLike" test to allow for wildcards.
    
    // get the value for the selectedAttribute for the given object
    Object value = nodeAttributes.getValue( selectedAttribute, ( String )object );
    
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
    if ( other_object instanceof CsNodeTypeFilter ) {
      if ( ( ( CsNodeTypeFilter )other_object).getSearchString().equals( getSearchString() ) ) {
        return true;
      }
    }
    return false;
  }
  
  public Object clone () {
    return new CsNodeTypeFilter ( network, selectedAttribute, searchString, identifier+"_new" );
  }
  
  public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
    return pcs;
  }

  //----------------------------------------//
  // CsNodeTypeFilter methods
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

