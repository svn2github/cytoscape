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
	protected Filter filter;
	protected Integer count;
	protected Integer distance;
	protected HashSet seenNodes;
	protected CyWindow cyWindow;
	protected GraphPerspective myPerspective;
	public static String FILTER_NAME_EVENT = "FILTER_NAME_EVENT";
	public static String FILTER_ID = "NodeTopologyFilter";
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
	public NodeTopologyFilter (CyWindow cyWindow, 
			Integer count,
			Integer distance,
			Filter filter,
			String identifier) {
		this.cyWindow = cyWindow;
		this.count = count;
		this.distance = distance;
		this.filter = filter;
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
		if(object instanceof Node){
			seenNodes = new HashSet();
			myPerspective = cyWindow.getView().getGraphPerspective();
			int totalSum = countNeighbors((Node)object,0);
			return totalSum >= count.intValue();
		}else{
			return false;
		}
		
	}

	private int countNeighbors(Node currentNode,int currentDistance){
		if(currentDistance == distance.intValue()){
			if(filter.passesFilter(currentNode)){
				return 1;
			}
			else{
				return 0;
			}
		}
		else{
			int sum = 0;
			java.util.List neighbors = myPerspective.neighborsList(currentNode);
			Iterator nodeIt = neighbors.iterator();
			while(nodeIt.hasNext() && sum < count.intValue()){
				Node nextNode = (Node)nodeIt.next();
				if(!seenNodes.contains(nextNode)){
					seenNodes.add(nextNode);
					sum += countNeighbors(nextNode,currentDistance+1);
				}
			}
			if(sum >= count.intValue()){
				return sum;
			}
			else if(filter.passesFilter(currentNode)){
				return sum+1;
			}
			else{
				return sum;
			}
		}
	}
	public Class[] getPassingTypes () {
		return null;
	}

	public boolean equals ( Object other_object ) {
		return super.equals(other_object);
	}

	public Object clone () {
		return new NodeTopologyFilter ( cyWindow,count,distance,filter,identifier+"_new" );
	}

	public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
		return pcs;
	}

	//----------------------------------------//
	// NodeTopologyFilter methods
	//----------------------------------------//

	public void propertyChange ( PropertyChangeEvent e ) {
		if ( e.getPropertyName() == FILTER_NAME_EVENT ) {
			setIdentifier( ( String )e.getNewValue() );
		} else if (e.getPropertyName() == FILTER_BOX_EVENT)  {
			setFilter((Filter)e.getNewValue());
		} else if (e.getPropertyName() == COUNT_EVENT){
			setCount((Integer)e.getNewValue());
		} else if (e.getPropertyName() == DISTANCE_EVENT){
			setDistance((Integer)e.getNewValue());
		}
	}



	public void setFilter(Filter filter){
		this.filter = filter;
		pcs.firePropertyChange(FILTER_BOX_EVENT,null,filter);
	}
	public Filter getFilter(){
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
		return null;
	}

	public Filter input ( String desc ) {
		return null;
	}

}

