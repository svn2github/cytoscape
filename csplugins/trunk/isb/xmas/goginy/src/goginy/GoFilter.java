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

  public GoFilter ( GoGinyView view,
                    Ontology onto,
                    String type ) {

    this.view = view;
    this.onto = onto;
    this.type = type;
    pcs = new SwingPropertyChangeSupport( this );
  }
        
  public void graphViewChanged ( GraphViewChangeEvent event ) {
    update_cache = true;
  } 

  public String toString () {
    return "Selected "+type;
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
    return false;
  }

  public Object clone () {
    return null;
  }

  public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
    return pcs;
  }
        
  public boolean passesFilter ( Object object ) {
    if ( object instanceof Node ) {
      Node node = ( Node )object;
      
      if ( update_cache ) {
        go_terms = onto.getTerms( view.getSelectedNodeIndices() );
        update_cache = false;
      }

      List cc = ( List )Cytoscape.getNodeAttributeValue( node, type );
      for ( Iterator j = cc.iterator(); j.hasNext(); ) {
        try {
          String go = ( String )j.next();
          int term = Integer.parseInt( go.substring( 4 ) );
          for ( int i = 0; i < go_terms.length; ++i ) {
            if ( go_terms[i] == term ) {
              return true;
            }
          }
        } catch ( Exception e ) {}
      }
      return false;
    }
    // not a node
    return false;
  }
}



