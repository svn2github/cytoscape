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
 * This is a Cytoscape specific filter that will pass nodes if
 * a selected attribute matches a specific value.
 */
public class SelectAllFilter
  implements Filter  {

  //----------------------------------------//
  // Filter specific properties 
  //----------------------------------------//
  public static String FILTER_ID = "[ No Filter ]";
  public static String FILTER_DESCRIPTION = "Selects all nodes and edges";
  //----------------------------------------//
  // Needed Variables
  //----------------------------------------//
  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);


  //---------------------------------------//
  // Constructor
  //----------------------------------------//

  /**
   * Creates a new SelectAllFilter
   */  
  public SelectAllFilter () { }

  //----------------------------------------//
  // Implements Filter
  //----------------------------------------//

  /**
   * Returns the name for this Filter
   */
  public String toString () {
    return FILTER_ID;
  }

  public String getDescription(){
    return FILTER_DESCRIPTION;
  }

  public String getFilterID () {
    return FILTER_ID;
  }

  public boolean passesFilter ( Object object ) {
    if(object instanceof Node || object instanceof Edge ){
    	return true;
    }else{
      return false;
    }
		
  }

  public Class[] getPassingTypes () {
	return new Class[]{Node.class,Edge.class};
  }

  public boolean equals ( Object other_object ) {
    return super.equals(other_object);
  }

  public Object clone () {
    return new SelectAllFilter ();
  }

  public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
    return pcs;
  }

  public String output () {
    return FILTER_ID; 
  }

  public void input ( String desc ) {
  }

}

