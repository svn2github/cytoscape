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
import cytoscape.data.GraphObjAttributes;
import cytoscape.view.CyWindow;
import cytoscape.CyNetwork;
import giny.model.*;

import ViolinStrings.Strings;

/**
 * This is a Cytoscape specific filter that will pass nodes if
 * a selected attribute matches a specific value.
 */
public class StringPatternFilter
  implements Filter  {
  
  //----------------------------------------//
  // Filter specific properties 
  //----------------------------------------//
  protected String selectedAttribute;
  protected String searchString;
  protected Class classType;
  protected Class NODE_CLASS;
  protected Class EDGE_CLASS;
		
  public static String NODE="Node";
  public static String EDGE="Edge";
  public static String SEARCH_STRING_EVENT = "SEARCH_STRING_EVENT";
  public static String SELECTED_ATTRIBUTE_EVENT = "SELECTED_ATTRIBUTE_EVENT";
  public static String FILTER_NAME_EVENT = "FILTER_NAME_EVENT";
  public static String CLASS_TYPE_EVENT = "CLASS_TYPE";
  public static String FILTER_ID = "StringPatternFilter";

  //----------------------------------------//
  // Cytoscape specific Variables
  //----------------------------------------//
  protected CyWindow cyWindow;

  //----------------------------------------//
  // Needed Variables
  //----------------------------------------//
  protected String identifier = "default";
  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);
  
  
  //---------------------------------------//
  // Constructor
  //----------------------------------------//

  /**
   * Creates a new StringPatternFilter
   */  
  public StringPatternFilter ( CyWindow cyWindow,
                               String classString,
                               String selectedAttribute, 
                               String searchString,
                               String identifier ) {
    this.cyWindow = cyWindow;
    //this.classType = classType;
    try{
      NODE_CLASS = Class.forName("giny.model.Node");
      EDGE_CLASS = Class.forName("giny.model.Edge");
    }catch(Exception e){
      e.printStackTrace();
    }
    this.selectedAttribute = selectedAttribute;  
    this.searchString = searchString;
    this.identifier =identifier;
    setClassType(classString); 
  }
  
  public StringPatternFilter ( String classString,
                               String selectedAttribute, 
                               String searchString,
                               String identifier ) {
    this.cyWindow = Cytoscape.getDesktop();
    //this.classType = classType;
    try{
      NODE_CLASS = Class.forName("giny.model.Node");
      EDGE_CLASS = Class.forName("giny.model.Edge");
    }catch(Exception e){
      e.printStackTrace();
    }
    this.selectedAttribute = selectedAttribute;  
    this.searchString = searchString;
    this.identifier =identifier;
    setClassType(classString); 
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
    String value = "";
    if (!classType.isInstance(object)) {
      return false;
    }
    GraphObjAttributes objectAttributes = null;
    if(classType.equals(NODE_CLASS)){
      objectAttributes = Cytoscape.getNodeNetworkData();
    }
    else{
      objectAttributes = Cytoscape.getEdgeNetworkData();
    }
			
    String name = objectAttributes.getCanonicalName(object);
    if(name == null){
      return false;
    }
				
    Object valueObj = objectAttributes.getValue( selectedAttribute, name );//.toString();
			
    if (valueObj == null ){
      return false;
    }

    value = valueObj.toString();

    // I think that * and ? are better for now....
    String[] pattern = searchString.split("\\s");
    for ( int p = 0; p < pattern.length; ++p ) {
      if ( Strings.isLike( ( String )value, pattern[p], 0, true ) ) {
        // this is an OR function
        return true;
      }
    }
    return false;
    // try{
    //           return value.matches(searchString);
    // 				}catch(Exception e){
    // 								return false;
    // 				}
		
  }

  public Class[] getPassingTypes () {
    return null;
  }
  
  public boolean equals ( Object other_object ) {
    if ( other_object instanceof StringPatternFilter ) {
      if ( ( ( StringPatternFilter )other_object).getSearchString().equals( getSearchString() ) ) {
        return true;
      }
    }
    return false;
  }
  
  public Object clone () {
    return new StringPatternFilter ( cyWindow, getClassType(),selectedAttribute, searchString, identifier+"_new" );
  }
  
  public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
    return pcs;
  }

  //----------------------------------------//
  // StringPatternFilter methods
  //----------------------------------------//

  public void propertyChange ( PropertyChangeEvent e ) {
    if ( e.getPropertyName() == SEARCH_STRING_EVENT ) {
      //System.out.println( "Search String Changed to "+( String )e.getNewValue() );
      setSearchString( ( String )e.getNewValue() );
    } else if ( e.getPropertyName() == FILTER_NAME_EVENT ) {
      setIdentifier( ( String )e.getNewValue() );
    } else if ( e.getPropertyName() == SELECTED_ATTRIBUTE_EVENT ) {
      setSelectedAttribute( ( String )e.getNewValue() );
    } else if (e.getPropertyName() == CLASS_TYPE_EVENT)  {
      setClassType((String)e.getNewValue());
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

  public void setClassType(String classString){
    
    if(classString == NODE || classString.equals( "Node" ) ){
      classType = NODE_CLASS;
    }
    else{
      classType = EDGE_CLASS;
    }
    pcs.firePropertyChange(CLASS_TYPE_EVENT,null,classType);
  }

  public String getClassType(){
    if(classType == NODE_CLASS){
      return NODE;
    }
    else{
      return EDGE;
    }
  }
		
  
  


  //----------------------------------------//
  // IO
  //----------------------------------------//

  public String output () {
    StringBuffer buffer = new StringBuffer();
    buffer.append( "filter.cytoscape.StringPatternFilter,");
    buffer.append( getClassType()+"," );
    buffer.append( getSelectedAttribute()+"," );
    buffer.append( getSearchString()+"," );
    buffer.append( toString() );
    return buffer.toString();
  }
  
  public Filter input ( String desc ) {
    String[] array = desc.split( "," );
    if ( array[0].equals( "filter.cytoscape.StringPatternFilter" ) ) {
      System.out.println( "Found Filter" );
      Filter new_filter = new StringPatternFilter( array[1], array[2], array[3], array[4] );
      return new_filter;
    }
    return null;
  }

}

