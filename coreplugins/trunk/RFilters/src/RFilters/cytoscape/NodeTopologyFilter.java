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

import cytoscape.CyNetwork;
import giny.model.*;

import ViolinStrings.Strings;

/**
 * This is a Cytoscape specific filter that will pass nodes if
 * a selected attribute matches a specific value.
 */
public class NodeTopologyFilter
  implements Filter  {

  //----------------------------------------//
  // Filter specific properties 
  //----------------------------------------//
  protected int filter;
  protected Integer count;
  protected Integer distance;
  protected HashSet seenNodes;
  protected GraphPerspective myPerspective;
  public static String FILTER_NAME_EVENT = "FILTER_NAME_EVENT";
  public static String FILTER_ID = "Node Topology Filter";
  public static String FILTER_DESCRIPTION = "Select nodes based on the attributes of surrounding nodes";
  public static String FILTER_EVENT = "FILTER_EVENT";	
  public static String FILTER_BOX_EVENT = "FILTER_BOX";	
  public static String COUNT_EVENT = "COUNT";
  public static String DISTANCE_EVENT = "DISTANCE";
  //----------------------------------------//
  // Needed Variables
  //----------------------------------------//
  protected String identifier = "default";
  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);


  //---------------------------------------//
  // Constructor
  //----------------------------------------//

  /**
   * Creates a new NodeTopologyFilter
   */  
  public NodeTopologyFilter (Integer count,
			     Integer distance,
			     int filter,
			     String identifier) {
    this.count = count;
    this.distance = distance;
    this.filter = filter;
    this.identifier =identifier;
  }

  /**
   * Creates a new NodeTopologyFilter
   */  
  public NodeTopologyFilter (	String desc ){
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

  public String getDescription(){
    return FILTER_DESCRIPTION;
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

  /**
   * An Object Passes this Filter if its "toString" method
   * matches any of the Text from the TextField
   */
  public boolean passesFilter ( Object object ) {
    if(object instanceof Node){
      seenNodes = new HashSet();
      seenNodes.add(object);
      myPerspective = Cytoscape.getCurrentNetwork();
      int totalSum = countNeighbors((Node)object,0);
      Filter filter = FilterManager.defaultManager().getFilter(this.filter);
      if(filter.passesFilter((Node)object)){
	  totalSum -= 1;
      }
      return totalSum >= count.intValue();
    }else{
      return false;
    }
		
  }

    private int countNeighbors(Node currentNode,int currentDistance){
	Filter filter = FilterManager.defaultManager().getFilter(this.filter);
	int sum = 0;
	if (filter == null) {
	    return sum;
	}
	if(sum >= count.intValue()){
	    return sum;
	}
	if(currentDistance == distance.intValue()){
	    if(filter.passesFilter(currentNode)){
		return 1;
	    }
	    else{
		return 0;
	    }
	}
	else{
	    java.util.List neighbors = myPerspective.neighborsList(currentNode);
	    Iterator nodeIt = neighbors.iterator();
	    while(nodeIt.hasNext() && sum < count.intValue()){
		Node nextNode = (Node)nodeIt.next();
		if(!seenNodes.contains(nextNode)){
		    seenNodes.add(nextNode);
		    sum += countNeighbors(nextNode,currentDistance+1);
		}
		if(sum > count.intValue()){
		    return sum;
		}
	    }
	    if(filter.passesFilter(currentNode)){
		return sum+1;
	    }
	    else{
		return sum;
	    }
	}
    }
    public Class[] getPassingTypes () {
	return new Class[]{Node.class};
  }

  public boolean equals ( Object other_object ) {
    return super.equals(other_object);
  }

  public Object clone () {
    return new NodeTopologyFilter ( count,distance,filter,identifier+"_new" );
  }

  public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
    return pcs;
  }

  //----------------------------------------//
  // NodeTopologyFilter methods
  //----------------------------------------//

  public void setFilter(int filter){
    int oldvalue = this.filter;
    this.filter = filter;
    pcs.firePropertyChange(FILTER_BOX_EVENT,oldvalue,filter);
  }
  public int getFilter(){
    return filter;
  }

  public void setCount(Integer count){
    this.count = count;
    pcs.firePropertyChange(COUNT_EVENT,null,count);
  }

  public Integer getCount(){
    return count;
  }

  public void setDistance(Integer distance){
    this.distance = distance;
    pcs.firePropertyChange(DISTANCE_EVENT,null,distance);
  }

  public Integer getDistance(){
    return distance;
  }




  //----------------------------------------//
  // IO
  //----------------------------------------//

  public String output () {
    StringBuffer buffer = new StringBuffer();
    buffer.append( getCount()+"," );
    buffer.append( getDistance()+"," );
    buffer.append( getFilter()+"," );
    buffer.append( toString() );
    return buffer.toString();
  }

  public void input ( String desc ) {
    String [] array = desc.split(",");
    setCount(new Integer(array[0]));
    setDistance(new Integer(array[1]));
    setFilter((new Integer(array[2])).intValue());
    setIdentifier(array[3]);
  }

}

