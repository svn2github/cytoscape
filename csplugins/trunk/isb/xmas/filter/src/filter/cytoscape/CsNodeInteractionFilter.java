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

import ViolinStrings.Strings;

/**
 * This filter will pass nodes based on the edges that 
 * they have.
 */
public class CsNodeInteractionFilter 
  implements Filter {

  //----------------------------------------//
  // Filter specific properties 
  //----------------------------------------//
  protected String selectedEdgeAttribute;
  protected String searchString;
  protected boolean sourceNode;
  protected boolean targetNode;

  public static String SOURCE_NODE_EVENT ="SOURCE_NODE_EVENT";
  public static String TARGET_NODE_EVENT ="TARGET_NODE_EVENT";
  public static String SEARCH_STRING_EVENT = "SEARCH_STRING_EVENT";
  public static String SELECTED_EDGE_ATTRIBUTE_EVENT = "SELECTED_EDGE_ATTRIBUTE_EVENT";
  public static String FILTER_NAME_EVENT = "FILTER_NAME_EVENT";

  public static String FILTER_ID = "CsNodeInteractionFilter";

  //----------------------------------------//
  // Cytoscape specific Variables
  //----------------------------------------//
  protected CyNetwork network;
  protected GraphObjAttributes edgeAttributes;


  //----------------------------------------//
  // Needed Variables
  //----------------------------------------//
  protected String identifier = "CsNodeInteractionFilter";
  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);
  
  
  //---------------------------------------//
  // Constructor
  //----------------------------------------//

  /**
   * Creates a new CsNodeInteractionFilter
   */  
  public CsNodeInteractionFilter ( CyNetwork network,  
                                   String selectedEdgeAttribute, 
                                   String searchString,
                                   boolean sourceNode,
                                   boolean targetNode,
                                   String identifier ) {
    this.network = network;
    this.edgeAttributes = network.getEdgeAttributes();
    this.selectedEdgeAttribute = selectedEdgeAttribute;  
    this.searchString = searchString;
    this.sourceNode = sourceNode;
    this.targetNode = targetNode;
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

    Node node;
    if ( object instanceof Node ) {
      node = ( Node )object;
    } else {
      return false;
    }
    
    System.out.println( "Network: "+network );

    boolean passes = false;
    List adjacent_edges = network.getGraphPerspective().getAdjacentEdgesList( node, true, targetNode, sourceNode );
    if ( adjacent_edges == null ) {
      System.out.println( "Was NUll" );
      return true;
    }
    Iterator aei = adjacent_edges.iterator();
    while ( aei.hasNext() && !passes ) {
      Edge edge = ( Edge )aei.next();
      Object value;
      value = edgeAttributes.getValue( selectedEdgeAttribute, edge.getIdentifier() );
     
       System.out.println( "Testing node: "+node+" against edge: "+edge+" value: "+value+" of attr: "+selectedEdgeAttribute +" ss: "+searchString);
      
      if ( value instanceof String == false ) {
        continue;
      }
      
     

      String[] pattern = searchString.split("\\s");
      for ( int p = 0; p < pattern.length; ++p ) {
        if ( Strings.isLike( ( String )value, pattern[p], 0, true ) ) {
          // this is an OR function
          passes =  true;
        }
      }
    }
    return passes;
  }


  public Class[] getPassingTypes () {
    return null;
  }
  
  public boolean equals ( Object other_object ) {
    if ( other_object instanceof CsNodeInteractionFilter ) {
      if ( ( ( CsNodeInteractionFilter )other_object).getSearchString().equals( getSearchString() ) ) {
        return true;
      }
    }
    return false;
  }
  
  public Object clone () {
    return new CsNodeInteractionFilter ( network, selectedEdgeAttribute, searchString, sourceNode, targetNode, identifier+"_new" );
  }
  
  public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
    return pcs;
  }

  //----------------------------------------//
  // CsNodeInteractionFilter methods
  //----------------------------------------//

  public void propertyChange ( PropertyChangeEvent e ) {
    if ( e.getPropertyName() == SEARCH_STRING_EVENT ) {
      System.out.println( "Search String Changed to "+( String )e.getNewValue() );
      setSearchString( ( String )e.getNewValue() );
    } else if ( e.getPropertyName() == FILTER_NAME_EVENT ) {
      setIdentifier( ( String )e.getNewValue() );
    } else if ( e.getPropertyName() == SELECTED_EDGE_ATTRIBUTE_EVENT ) {
      setSelectedEdgeAttribute( ( String )e.getNewValue() );
    } else if (  e.getPropertyName() == SOURCE_NODE_EVENT ) {
      setSourceNode( ( Boolean )e.getNewValue() );
    } else if ( e.getPropertyName() == TARGET_NODE_EVENT ) {
      setTargetNode( ( Boolean )e.getNewValue() );
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

  // Selected_Edge_Attribute ///////////////////////////

  public String getSelectedEdgeAttribute () {
    return selectedEdgeAttribute;
  }

  public void setSelectedEdgeAttribute ( String new_attr ) {
    this.selectedEdgeAttribute = new_attr;
    fireSelectedEdgeAttributeModified();
  }

  public void fireSelectedEdgeAttributeModified () {
    pcs.firePropertyChange( SELECTED_EDGE_ATTRIBUTE_EVENT, null, selectedEdgeAttribute );
  } 
  
  // SourceNode /////////////////////////////////////////

  public boolean getSourceNode () {
    return sourceNode;
  }

  public void setSourceNode ( Boolean new_val ) {
    this.sourceNode = new_val.booleanValue();
    fireSourceNodeModified();
  }

  public void setSourceNode ( boolean new_val ) {
    this.sourceNode = new_val;
    fireSourceNodeModified();
  }

  public void fireSourceNodeModified () {
    pcs.firePropertyChange(  SOURCE_NODE_EVENT, null, new Boolean( sourceNode ) );
  }
  
  // TargetNode /////////////////////////////////////////

  public boolean getTargetNode () {
    return targetNode;
  }

  public void setTargetNode ( Boolean new_val ) {
    this.targetNode = new_val.booleanValue();
    fireTargetNodeModified();
  }

  public void setTargetNode ( boolean new_val ) {
    this.targetNode = new_val;
    fireTargetNodeModified();
  }

  public void fireTargetNodeModified () {
    pcs.firePropertyChange(  TARGET_NODE_EVENT, null, new Boolean( targetNode ) );
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
