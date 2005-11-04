package goginy;

import cytoscape.*;
import cytoscape.util.*;
import cytoscape.plugin.*;
import cytoscape.data.Semantics;

import giny.model.*;
import giny.view.*;


import cern.colt.list.*;

import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.beans.*;
import java.io.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import filter.model.*;

public class GoFilter 
  implements 
    Filter,
    GraphViewChangeListener {

  GoGinyView view;
  Ontology onto;
  String type;

  int[] go_terms;
  boolean update_cache = true;
  SwingPropertyChangeSupport pcs;

  String identifier = "default";

  public GoFilter ( GoGinyView view,
                    Ontology onto,
                    String type ) {

    this.view = view;
    this.onto = onto;
    this.type = type;
    pcs = new SwingPropertyChangeSupport( this );
  }

  /**
   * sets a new name for this filter
   */
  public void setIdentifier ( String new_id ) {
    this.identifier = new_id;
    //pcs.firePropertyChange(FILTER_NAME_EVENT,null,new_id);
  }
        
  public void graphViewChanged ( GraphViewChangeEvent event ) {
    update_cache = true;
  } 

  public String toString () {
    return type;
  }

  public Class[] getPassingTypes () {
    return new Class[]{Node.class};
  }

  public String getFilterID() {
    return type;
  }

  public String getDescription() {
    return type;
  }
 
  public String output () {
    return "";
  }

  public void input ( String desc ) {
  }
        
  public boolean equals ( Object other_object ) {
    if ( other_object instanceof GoFilter ) 
      if ( ( ( GoFilter )other_object ).toString().equals( type ) )
        return true;
    return false;
  }

  public Object clone () {
    return null;
  }

  public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
    return pcs;
  }
        
  public boolean passesFilter ( Object object ) {
    update_cache = true;
    if ( object instanceof Node ) {
      Node node = ( Node )object;
      
      if ( update_cache ) {
        go_terms = onto.getTerms( view.getSelectedNodeIndices() );
        update_cache = false;
      }

      List cc = Cytoscape.getNodeAttributes().getAttributeList( node.getIdentifier(), type );
      for ( Iterator j = cc.iterator(); j.hasNext(); ) {
        try {
          String go = ( String )j.next();
          int term = Integer.parseInt( go.substring( 4 ) );
          for ( int i = 0; i < go_terms.length; ++i ) {
            if ( go_terms[i] == term ) {
              //System.out.println( "Evidence: "+go_terms[i]+" == "+term );
              return true;
            }
          }
        } catch ( Exception e ) {
          System.out.println( "Filter Error");
          e.printStackTrace();
        }
      }
      return false;
    }
    // not a node
    return false;
  }
}



