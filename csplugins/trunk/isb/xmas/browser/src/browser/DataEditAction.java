package browser;

import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.beans.*;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.*;
import javax.swing.undo.*; 

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.data.attr.*;

import giny.model.GraphObject;


public class DataEditAction extends AbstractUndoableEdit {

  final String object;
  final String attribute;
  final Object old_value;
  final Object new_value;
  final String[] keys;
  final int graphObjectType;
  final DataTableModel table;

  public DataEditAction ( DataTableModel table, 
                          String object, 
                          String attribute,
                          String[] keys,
                          Object old_value, 
                          Object new_value, 
                          int graphObjectType ) {
    this.table = table;
    this.object = object;
    this.attribute = attribute;
    this.keys = keys;
    this.old_value = old_value;
    this.new_value = new_value;
    this.graphObjectType = graphObjectType;

    redo();

  }


  public String	getPresentationName () {
    return object+" attribute "+attribute+" changed.";
  }
          
  public String getRedoPresentationName () {
    return "Redo: "+object+":"+attribute+" to:"+new_value+" from "+old_value;
  }
        
  public String getUndoPresentationName () {
    return "Undo: "+object+":"+attribute+" back to:"+old_value+" from "+new_value;
  }
  
  private void setAttributeValue( CyAttributes data, 
                                  String id, 
                                  String att,
                                  Object object ) {
    if ( object instanceof Integer )
      data.setAttribute( id, att, (Integer)object );
    else if ( object instanceof Double )
      data.setAttribute( id, att, (Double)object );
    else if ( object instanceof Boolean )
      data.setAttribute( id, att, (Boolean)object );
    else if ( object instanceof String )
       data.setAttribute( id, att, (String)object );
    else if ( object instanceof List )
      data.setAttributeList( id, att, (List)object );
    else if ( object instanceof Map )
      data.setAttributeMap( id, att, (Map)object );
  }

  
      
  // this sets the new value
  public void	redo () {
    
    CyAttributes data;
    
    if ( graphObjectType == 0 ) {
      //node
      data = Cytoscape.getNodeAttributes();
    } else {
      //edge
      data = Cytoscape.getEdgeAttributes();
    }
    
    setAttributeValue( data, object, attribute, new_value );
    table.setTable();
  }
        

  


  // this sets the old value
  public void undo () {
    
    CyAttributes data;
    
    if ( graphObjectType == 0 ) {
      //node
      data = Cytoscape.getNodeAttributes();
    } else {
      //edge
      data = Cytoscape.getEdgeAttributes();
    }
    
    setAttributeValue( data, object, attribute, old_value );
    table.setTable();
  }





}
