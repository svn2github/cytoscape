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
import cytoscape.view.CyWindow;
import giny.model.*;

import ViolinStrings.Strings;

/**
 * This filter will pass nodes based on the edges that 
 * they have.
 */
public class InteractionFilter 
  implements Filter {

  //----------------------------------------//
  // Filter specific properties 
  //----------------------------------------//
  protected Filter filter;
  protected String target;

  public static String FILTER_NAME_EVENT = "FILTER_NAME_EVENT";
		public static String FILTER_BOX_EVENT = "FILTER_BOX_EVENT";
		public static String TARGET_BOX_EVENT = "TARGET_BOX_EVENT";
  public static String FILTER_ID = "InteractionFilter";
		public static String SOURCE = "source";
		public static String TARGET = "target";
  //----------------------------------------//
  // Cytoscape specific Variables
  //----------------------------------------//
  protected CyWindow cyWindow;


  //----------------------------------------//
  // Needed Variables
  //----------------------------------------//
  protected String identifier = "InteractionFilter";
  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);
  
  
  //---------------------------------------//
  // Constructor
  //----------------------------------------//

  /**
   * Creates a new InteractionFilter
   */  
  public InteractionFilter ( CyWindow cyWindow,  
                                   Filter filter, 
                                   String target,
                                   String identifier ) {
    this.cyWindow = cyWindow;
    this.filter = filter;
    this.target = target;  
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
   * An object passes this Filter if it is the source/target
   * node for an edge that has a matching property for
   * the given Edge atttribute.
   */
  public boolean passesFilter ( Object object ) {
						if(!(object instanceof Node)){
										return false;
						}
						Node node = (Node)object;
						//get the list of all relevant edges
						List adjacentEdges;
						GraphPerspective myPerspective = cyWindow.getView().getGraphPerspective();
						if(target == SOURCE){
									adjacentEdges = myPerspective.getAdjacentEdgesList(node, true, false, true);	
						}
						else{
									adjacentEdges = myPerspective.getAdjacentEdgesList(node,true,true,false);
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
    return null;
  }
  
  public boolean equals ( Object other_object ) {
						return super.equals(other_object);
		}
  
  public Object clone () {
    return new InteractionFilter ( cyWindow,filter,target,identifier+"_new" );
  }
  
  public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
    return pcs;
  }

  //----------------------------------------//
  // InteractionFilter methods
  //----------------------------------------//

  public void propertyChange ( PropertyChangeEvent e ) {
    if ( e.getPropertyName() == FILTER_NAME_EVENT ) {
      setIdentifier( ( String )e.getNewValue() );
    } else if ( e.getPropertyName() == FILTER_BOX_EVENT ) {
      setFilter( ( Filter )e.getNewValue() );
    } else if (  e.getPropertyName() == TARGET_BOX_EVENT ) {
      setTarget( ( String )e.getNewValue() );
    }
  }
  
  // SearchString /////////////////////////////////

  public String getTarget () {
    return target;
  }

  public void setTarget ( String target ) {
    this.target = target;
    pcs.firePropertyChange( TARGET_BOX_EVENT, null, target );
		}

		public void setFilter(Filter filter){
						this.filter = filter;
						pcs.firePropertyChange(FILTER_BOX_EVENT,null,filter);
		}
		public Filter getFilter(){
						return filter;
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
