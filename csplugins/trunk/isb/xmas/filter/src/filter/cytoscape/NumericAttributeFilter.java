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
import cytoscape.view.CyWindow;
import cytoscape.data.GraphObjAttributes;
import giny.model.*;
import cytoscape.CyNetwork;
import ViolinStrings.Strings;

/**
 * This is a Cytoscape specific filter that will pass nodes if
 * a selected attribute matches a specific value.
 */
public class NumericAttributeFilter
  implements Filter  {
  
  //----------------------------------------//
  // Filter specific properties 
  //----------------------------------------//
  protected String selectedAttribute;
  protected Number searchNumber;
		protected String comparison;
		protected Class classType;
		protected Class NODE_CLASS;
		protected Class EDGE_CLASS;
		public static String EQUAL = "=";
		public static String LESS = "<";
		public static String GREATER = ">";
		public static String NODE = "Node";
		public static String EDGE = "Edge";
		
  public static String SEARCH_NUMBER_EVENT = "SEARCH_STRING_EVENT";
  public static String SELECTED_ATTRIBUTE_EVENT = "SELECTED_ATTRIBUTE_EVENT";
  public static String FILTER_NAME_EVENT = "FILTER_NAME_EVENT";
		public static String CLASS_TYPE_EVENT = "CLASS_TYPE";
  public static String FILTER_ID = "NumericAttributeFilter";
		public static String COMPARISON_EVENT = "COMPARISON_EVENT";
		
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
   * Creates a new NumericAttributeFilter
   */  
  public NumericAttributeFilter ( CyWindow cyWindow,
										String comparison,
										String classString,
                            String selectedAttribute, 
                            Number searchNumber,
                            String identifier ) {
    this.cyWindow = cyWindow;
				this.comparison = comparison;
				try{
								NODE_CLASS = Class.forName("giny.model.Node");
								EDGE_CLASS = Class.forName("giny.model.Edge");
				}catch(Exception e){
								e.printStackTrace();
				}
    this.selectedAttribute = selectedAttribute;  
    this.searchNumber = searchNumber;
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
				if ( !classType.isInstance(object)) {
      return false;
				}
				GraphObjAttributes objectAttributes = null;
				if(classType.equals(NODE_CLASS)){
								objectAttributes = cyWindow.getNetwork().getNodeAttributes();
				}
				else{
								objectAttributes = cyWindow.getNetwork().getEdgeAttributes();
				}
				String name = objectAttributes.getCanonicalName(object);
				if(name == null){
								return false;
				}
				Number value = (Number)objectAttributes.getValue( selectedAttribute,name );
				if( value == null){
								return false;
				}
				if(comparison == EQUAL){
								return searchNumber.doubleValue() == value.doubleValue();
				}
				else if(comparison == LESS){
								return searchNumber.doubleValue() > value.doubleValue();
				}
				else if(comparison == GREATER){
								return searchNumber.doubleValue() < value.doubleValue();
				}
				else{
          //System.err.println("Comparison not identified");
          return false;
				}
				
				
		}

  public Class[] getPassingTypes () {
    return null;
  }
  
  public boolean equals ( Object other_object ) {
    /*if ( other_object instanceof NumericAttributeFilter ) {
      if ( ( ( NumericAttributeFilter )other_object).getSearchNumber().equals( getSearchNumber() ) ) {
        return true;
      }
    }
    return false;*/
						return super.equals(other_object);
  }
  
  public Object clone () {
    return new NumericAttributeFilter ( cyWindow, comparison,getClassType(),selectedAttribute, searchNumber, identifier+"_new" );
  }
  
  public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
    return pcs;
  }

  //----------------------------------------//
  // NumericAttributeFilter methods
  //----------------------------------------//

  public void propertyChange ( PropertyChangeEvent e ) {
    if ( e.getPropertyName() == SEARCH_NUMBER_EVENT ) {
      setSearchNumber( ( Number )e.getNewValue() );
    } else if ( e.getPropertyName() == FILTER_NAME_EVENT ) {
      setIdentifier( ( String )e.getNewValue() );
    } else if ( e.getPropertyName() == SELECTED_ATTRIBUTE_EVENT ) {
      setSelectedAttribute( ( String )e.getNewValue() );
    } else if (e.getPropertyName() == CLASS_TYPE_EVENT)  {
						setClassType((String)e.getNewValue());
				} else if (e.getPropertyName() == COMPARISON_EVENT){
								setComparison((String)e.getNewValue());
				}
  }
  
  // SearchString /////////////////////////////////

  public Number getSearchNumber () {
    return searchNumber;
  }

  public void setSearchNumber ( Number searchNumber ) {
    this.searchNumber = searchNumber;
    fireSearchNumberChanged();
  }

  public void fireSearchNumberChanged () {
    pcs.firePropertyChange( SEARCH_NUMBER_EVENT, null, searchNumber );
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
						if(classString == NODE){
										this.classType = NODE_CLASS;
						}else{
										this.classType = EDGE_CLASS;
						}
						
						pcs.firePropertyChange(CLASS_TYPE_EVENT,null,classString);
		}

		public String getClassType(){
						if(classType == NODE_CLASS){
										return NODE;
						}else{
										return EDGE;
						}
		}

		public void setComparison(String comparison){
						this.comparison = comparison;
						pcs.firePropertyChange(COMPARISON_EVENT,null,comparison);	
		}

		public String getComparison(){
						return comparison;
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

