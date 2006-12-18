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

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
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
  public static String FILTER_ID = "String Pattern Filter";
  public static String FILTER_DESCRIPTION = "Select nodes or edges based on the value of a text attribute";

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
  public StringPatternFilter ( String classString,
                               String selectedAttribute, 
                               String searchString,
                               String identifier) {
    try{
      NODE_CLASS = Node.class;
      EDGE_CLASS = Edge.class;
    }catch(Exception e){
      e.printStackTrace();
    }
    this.selectedAttribute = selectedAttribute;  
    this.searchString = searchString;
    this.identifier =identifier;
    setClassType(classString); 
  }


 



  public StringPatternFilter ( String desc){
    try{
      NODE_CLASS = Class.forName("giny.model.Node");
      EDGE_CLASS = Class.forName("giny.model.Edge");
    }catch(Exception e){
      e.printStackTrace();
    }
    input(desc);
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
    this.identifier = new_id;
    pcs.firePropertyChange(FILTER_NAME_EVENT,null,new_id);
  }

  /**
   * This is usually the same as the class name
   */
  public String getFilterID () {
    return FILTER_ID;
  }
  public String getDescription(){
    return FILTER_DESCRIPTION;
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
    
    CyAttributes  data = null;
    if(classType.equals(NODE_CLASS)){
      data = Cytoscape.getNodeAttributes();
    }
    else{
      data = Cytoscape.getEdgeAttributes();
    }
			
    String name = ((GraphObject)object).getIdentifier();
    if(name == null){
      return false;
    }
				
    value = data.getStringAttribute( name, selectedAttribute );//.toString();
			
    if (value == null ){
      return false;
    }

    // I think that * and ? are better for now....
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
    return new Class[]{classType};
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
    return new StringPatternFilter ( getClassType(),selectedAttribute, searchString, identifier+"_new" );
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
    buffer.append( getClassType()+"," );
    buffer.append( getSelectedAttribute()+"," );
    buffer.append( getSearchString()+"," );
    buffer.append( toString() );
    return buffer.toString();
  }
  
  public void input(String desc){
    String [] array = desc.split(",");
    if(array[0].equals(NODE)){
      setClassType(NODE);
    }
    else if(array[0].equals(EDGE)){
      setClassType(EDGE);
    }
    else{
      throw new IllegalArgumentException(array[0]+" is not a valid type of class");
    }
    setSelectedAttribute(array[1]);
    setSearchString(array[2]);
    setIdentifier(array[3]);
  }
  
}

