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

public class MultiDataEditAction extends AbstractUndoableEdit {

  final List objects;
  final String attributeTo;
  final String attributeFrom;
  List old_values;
  List new_values;
  final String[] keys;
  final int graphObjectType;
  final DataTableModel table;
  final String action;
  final String input;
  CytoscapeData data;

  static String ADD = "Add";
  static String SET = "Set";
  static String MUL = "Mul";
  static String DIV = "Div";
  static String COPY = "Copy";
  static String DELETE = "Delete";
  
  public MultiDataEditAction ( String input,
                               String action, 
                               List objects,
                               String attributeTo,
                               String attributeFrom,
                               String[] keys,
                               int graphObjectType,
                               DataTableModel table ) {
    
    this.input = input;
    this.action = action;
    this.table = table;
    this.objects = objects;
    this.attributeTo = attributeTo;
    this.attributeFrom = attributeFrom;
    this.keys = keys;
    this.graphObjectType = graphObjectType;

    initEdit();

  }


  public String	getPresentationName () {
    return "Attribute "+attributeTo+" changed.";
  }
          
  public String getRedoPresentationName () {
    return "Redo: "+action;
  }
        
  public String getUndoPresentationName () {
    return "Undo: "+action;
  }
   
  // put back the new_values
  public void	redo () {
    for ( int i = 0; i < objects.size(); ++i ) {
      GraphObject go = ( GraphObject)objects.get(i);
      if ( new_values.get(i) == null ) {
        data.removeAllAttributeValues( go.getIdentifier(), attributeTo );
      } else {
        data.setAttributeValue( go.getIdentifier(), attributeTo, new_values.get(i) );
      }
    }
    table.setTable();
  }
        
  // put back the old_values
  public void undo () {
    for ( int i = 0; i < objects.size(); ++i ) {
      GraphObject go = ( GraphObject)objects.get(i);
      if ( old_values.get(i) == null ) {
        data.removeAllAttributeValues( go.getIdentifier(), attributeTo );
      } else {
      data.setAttributeValue( go.getIdentifier(), attributeTo, old_values.get(i) );
      }
    }
    table.setTable();
  }



  public void initEdit () {
    
    // get proper Global CytoscapeData object
    if ( graphObjectType == 0 ) {
      //node
      data = Cytoscape.getNodeNetworkData();
    } else {
      //edge
      data = Cytoscape.getEdgeNetworkData();
    }

    
    if ( action == COPY ) {
      copyAtt();
    }  else if ( action == DELETE ) {
      deleteAtt();
    } else {
   
      byte att_type;
      try {
        att_type = data.getAttributeValueType( attributeTo );
      } catch ( Exception ex ) {
        // define the new attribute
        att_type = ( ( CytoscapeDataImpl )data ).wildGuessAndDefineObjectType( input, attributeTo );
      }
      
      if ( att_type == -1 ) {
        att_type = ( ( CytoscapeDataImpl )data ).wildGuessAndDefineObjectType( input, attributeTo );
      }

      if ( att_type == CyDataDefinition.TYPE_FLOATING_POINT ) {
        Double d = new Double( input );
        doubleAction( d.doubleValue() );
      } else if ( att_type == CyDataDefinition.TYPE_INTEGER ) {
        Integer d = new Integer( input );
        integerAction( d.intValue()  );
      } else if ( att_type == CyDataDefinition.TYPE_STRING ) {
        stringAction( input );
      } else if ( att_type == CyDataDefinition.TYPE_BOOLEAN ) {
        booleanAction( Boolean.valueOf( input ) );
      } 
    }
    table.setTable();
  } // initEdit


  /**
   * Use the global edit variables to copy the attribute in attributeFrom to attributeTo
   * the values that were copied will be saved to "new_values"
  */
  private void copyAtt () {

    new_values = new ArrayList( objects.size() );
    old_values = new ArrayList( objects.size() );
    for ( Iterator i = objects.iterator(); i.hasNext(); ) {
      GraphObject go = ( GraphObject)i.next();
      
      Object value = data.getAttributeValue( go.getIdentifier(),
                                             attributeFrom );
      new_values.add( value );
      data.setAttributeValue( go.getIdentifier(),
                              attributeTo,
                              value );
      old_values.add( null );
    }
  }
  
  /**
   * Use the global edit variables to delete the values from the given attribute.
   * the deleted values will be stored in "old_values"
   */
  private void deleteAtt () {
    new_values = new ArrayList( objects.size() );
      old_values = new ArrayList( objects.size() );
      for ( Iterator i = objects.iterator(); i.hasNext(); ) {
        GraphObject go = ( GraphObject)i.next();
        old_values.add( data.getAttributeValue( go.getIdentifier(), attributeTo ) ); 
        data.removeAllAttributeValues( go.getIdentifier(), attributeTo );
        new_values.add( null );
      }
  }


  /**
   * save the old and new values, subsequent redo/undo will only use these values.
   */
  private void doubleAction ( double input ) {

    old_values = new ArrayList( objects.size() );
    new_values = new ArrayList( objects.size() );
    for ( Iterator i = objects.iterator(); i.hasNext(); ) {
      GraphObject go = ( GraphObject)i.next();
     
      // get the current value and set the old_value to it
      Double d = (Double)data.getAttributeValue( go.getIdentifier(),
                                                 attributeTo );
      old_values.add( d );
     
      double new_v;
      if ( action == SET ) 
        new_v = input;
      else if ( action == ADD )
        new_v = input + d.doubleValue();
      else if ( action == MUL ) 
        new_v = input * d.doubleValue();
      else if ( action == DIV ) 
        new_v = d.doubleValue() / input;
      else 
        new_v = input;
      
      new_values.add( new Double(new_v) );
      data.setAttributeValue( go.getIdentifier(),
                              attributeTo,
                              new Double( new_v ) );
    } // iterator
  } // doubleAction

  /**
   * save the old and new values, subsequent redo/undo will only use these values.
   */
  private void integerAction ( int input) {

    old_values = new ArrayList( objects.size() );
    new_values = new ArrayList( objects.size() );
    for ( Iterator i = objects.iterator(); i.hasNext(); ) {
      GraphObject go = ( GraphObject)i.next();
     
      // get the current value and set the old_value to it
      Integer d = (Integer)data.getAttributeValue( go.getIdentifier(),
                                                 attributeTo );
      old_values.add( d );
     
      int new_v;
      if ( action == SET ) 
        new_v = input;
      else if ( action == ADD )
        new_v = input + d.intValue();
      else if ( action == MUL ) 
        new_v = input * d.intValue();
      else if ( action == DIV ) 
        new_v = d.intValue() / input;
      else 
        new_v = input;
      
      new_values.add( new Integer(new_v) );
      data.setAttributeValue( go.getIdentifier(),
                              attributeTo,
                              new Integer( new_v ) );
    } // iterator
  } // integerAction

  /**
   * save the old and new values, subsequent redo/undo will only use these values.
   */
  private void stringAction ( String input ) {
    // return if number only action
    if ( action == DIV || action == MUL )
      return;

    old_values = new ArrayList( objects.size() );
    new_values = new ArrayList( objects.size() );
    for ( Iterator i = objects.iterator(); i.hasNext(); ) {
      GraphObject go = ( GraphObject)i.next();
                
       // get the current value and set the old_value to it
      String s = (String)data.getAttributeValue( go.getIdentifier(),
                                                 attributeTo );
      old_values.add( s );
      String new_v;
      if ( action == SET ) 
        new_v = input;
      else 
        new_v = s.concat(input);
        
      new_values.add( new_v );
      data.setAttributeValue( go.getIdentifier(),
                              attributeTo,
                              new_v );
      
    } // iterator
  } // stringAction

  private void booleanAction (  Boolean input  ) {

    if ( action == DIV || action == MUL || action == ADD)
      return;

    old_values = new ArrayList( objects.size() );
    new_values = new ArrayList( objects.size() );
    for ( Iterator i = objects.iterator(); i.hasNext(); ) {
      GraphObject go = ( GraphObject)i.next();
      
      // get the current value and set the old_value to it
      Boolean b = (Boolean)data.getAttributeValue( go.getIdentifier(),
                                                   attributeTo );
      old_values.add( b );
      data.setAttributeValue( go.getIdentifier(),
                              attributeTo,
                              input );
      new_values.add( input );
    } // iterator
  } // booleanAction


 




}
