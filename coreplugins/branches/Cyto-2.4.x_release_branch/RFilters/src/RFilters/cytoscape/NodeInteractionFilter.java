package filter.cytoscape;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.util.List;
import filter.model.*;
import javax.swing.border.*;
import java.beans.*;
import javax.swing.event.SwingPropertyChangeSupport;

import cytoscape.*;
import cytoscape.data.*;

import giny.model.*;
import cytoscape.CyNetwork;


/**
 * This filter will pass nodes based on the edges that 
 * they have.
 */
public class NodeInteractionFilter 
  implements Filter {

  //----------------------------------------//
  // Filter specific properties 
  //----------------------------------------//
  protected int filter;
  protected String target;

  public static String FILTER_NAME_EVENT = "FILTER_NAME_EVENT";
  public static String FILTER_BOX_EVENT = "FILTER_BOX_EVENT";
  public static String TARGET_BOX_EVENT = "TARGET_BOX_EVENT";
  public static String FILTER_ID = "NodeInteractionFilter";
  public static String FILTER_DESCRIPTION = "Select nodes based on adjacent edges";
  public static String SOURCE = "source";
  public static String TARGET = "target";
  public static String EITHER = "source or target";
  //----------------------------------------//
  // Cytoscape specific Variables
  //----------------------------------------//
  
  //----------------------------------------//
  // Needed Variables
  //----------------------------------------//
  protected String identifier = "NodeInteractionFilter";
  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);
  
  
  //---------------------------------------//
  // Constructor
  //----------------------------------------//

  /**
   * Creates a new InteractionFilter
   */  
  public NodeInteractionFilter ( int filter, 
			     String target,
			     String identifier ) {
    this.filter = filter;
    this.target = target;  
    this.identifier =identifier;
  }
  
  public NodeInteractionFilter(String desc){
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
   * An object passes this Filter if it is the source/target
   * node for an edge that has a matching property for
   * the given Edge atttribute.
   */
  public boolean passesFilter ( Object object ) {
    Filter filter = FilterManager.defaultManager().getFilter(this.filter);
    if(filter == null){
      return false;
    }
    if(!(object instanceof Node)){
      return false;
    }
    Node node = (Node)object;
    //get the list of all relevant edges
    List adjacentEdges;
    GraphPerspective myPerspective = Cytoscape.getCurrentNetwork();
    if(target == SOURCE){
      adjacentEdges = myPerspective.getAdjacentEdgesList(node, true, false, true);	
    }else if(target == TARGET){
      adjacentEdges = myPerspective.getAdjacentEdgesList(node,true,true,false);
    }else{
      adjacentEdges = adjacentEdges = myPerspective.getAdjacentEdgesList(node,true,true,true);
    }

    Iterator edgeIt = adjacentEdges.iterator();
    while(edgeIt.hasNext()){
      if(filter.passesFilter(edgeIt.next())){
	return true;
      }
    }
    return false;
  }

  public Class[] getPassingTypes () {
    return new Class[]{Node.class};
  }
  
  public boolean equals ( Object other_object ) {
    return super.equals(other_object);
  }
  
  public Object clone () {
    return new NodeInteractionFilter ( filter,target,identifier+"_new" );
  }
  
  public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
    return pcs;
  }

  //----------------------------------------//
  // InteractionFilter methods
  //----------------------------------------//



  public String getTarget () {
    return target;
  }

  public void setTarget ( String target ) {
    this.target = target;
    pcs.firePropertyChange( TARGET_BOX_EVENT, null, target );
  }

  public void setFilter(int filter){
    int oldvalue = this.filter;
    this.filter = filter;
    pcs.firePropertyChange(FILTER_BOX_EVENT,oldvalue,filter);
  }
  public int getFilter(){
    return filter;
  }


  //----------------------------------------//
  // IO
  //----------------------------------------//

  public String output () {
    StringBuffer buffer = new StringBuffer();
    buffer.append( getFilter()+"," );
    buffer.append( getTarget()+"," );
    buffer.append( toString() );
    return buffer.toString();
  }
  
  public void input ( String desc ) {
    String [] array = desc.split(",");
    setFilter((new Integer(array[0])).intValue());
    if(array[1].equals(TARGET)){
      setTarget(TARGET);
    }
    else if(array[1].equals(SOURCE)){
      setTarget(SOURCE);
    }
    else if(array[1].equals(EITHER)){
      setTarget(EITHER);
    }
    else{
      throw new IllegalArgumentException(array[1]+" is not a valid interaction type");
    }
    setIdentifier(array[2]);
  }

}
