package rowan.browser;

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

import filter.model.FilterManager;

import cytoscape.data.attr.*;

import giny.model.GraphObject;

public class ModPanel 
  extends JPanel 
  implements ActionListener {

  CytoscapeData data;
  DataTableModel tableModel;
  AttributePanel attPanel;

  JComboBox attributeBox;
  JComboBox filterBox;
  JTextField inputField;
  JButton apply;
  JComboBox actionBox;
  
  static String ADD = "Add";
  static String SET = "Set";
  static String MUL = "Mul";
  static String DIV = "Div";
  static String COPY = "Copy";
  static String DELETE = "Delete";

  public ModPanel ( CytoscapeData data, DataTableModel tableModel, AttributePanel attPanel ) {
   
    this.data = data;
    this.tableModel = tableModel;
    this.attPanel = attPanel;

    setLayout( new BorderLayout() );


    add( new SelectPanel( tableModel ), BorderLayout.NORTH );

    JPanel panel = new JPanel();
    panel.setBorder( new TitledBorder( "Attribute Editing" ) );

    attributeBox = new JComboBox( new AttributeModel( Cytoscape.getNodeNetworkData() ) );
    attributeBox.setEditable( true );
    filterBox = new JComboBox( FilterManager.defaultManager().getComboBoxModel() );
    
    inputField = new JTextField( 8 );

    apply = new JButton( "GO" );
    apply.addActionListener( this );


    actionBox = new JComboBox( new Object[] {SET, ADD, MUL, DIV, COPY, DELETE} );

    panel.add( attributeBox );


    panel.add( actionBox );
    panel.add( inputField );
    panel.add( new JLabel( " if passes " ) );
    panel.add( filterBox );
    panel.add( apply );

    add( panel, BorderLayout.CENTER );

  }

  public void actionPerformed( ActionEvent e ) {
    
    if ( e.getSource() == apply ) {

      String attribute = ( String )attributeBox.getSelectedItem();
      Filter filter = ( Filter )filterBox.getSelectedItem();
      String action = ( String )actionBox.getSelectedItem();
      String input = inputField.getText();

      System.out.println( "For Att: "+attribute+" we are "+action+ "ing  with "+input+" for all that match: "+filter );

      if ( action == COPY ) {
        copyAtt( attribute, filter, attPanel.getSelectedAttribute() );
        return;
      }

      if ( action == DELETE ) {
        List graph_objects = tableModel.getGraphObjects();
        for ( Iterator i = graph_objects.iterator(); i.hasNext(); ) {
          GraphObject go = ( GraphObject)i.next();
          data.removeAllAttributeValues( go.getIdentifier(), attribute );
        }
        return;
      }

      byte att_type;
      try {
        att_type = data.getAttributeValueType( attribute );
      } catch ( Exception ex ) {
        // define the new attribute
        att_type = ( ( CytoscapeDataImpl )data ).wildGuessAndDefineObjectType( input, attribute );
      }
      
      if ( att_type == -1 ) {
         att_type = ( ( CytoscapeDataImpl )data ).wildGuessAndDefineObjectType( input, attribute );
      }


      //System.out.println( "att_type: "+att_type );

      if ( att_type == CyDataDefinition.TYPE_FLOATING_POINT ) {
        Double d = new Double( input );
        dealWithDouble( attribute, d.doubleValue(), action, filter );
      } else if ( att_type == CyDataDefinition.TYPE_INTEGER ) {
        Integer d = new Integer( input );
        dealWithInteger( attribute, d.intValue(), action, filter );
      } else if ( att_type == CyDataDefinition.TYPE_STRING ) {
        dealWithString( attribute, input, action, filter );
      } else if ( att_type == CyDataDefinition.TYPE_BOOLEAN ) {
        dealWithBoolean( attribute, Boolean.valueOf( input ), action, filter );
      } 

    }
  }
  
  private void copyAtt ( String attribute, Filter filter, String from ) {
    List graph_objects = tableModel.getGraphObjects();
    for ( Iterator i = graph_objects.iterator(); i.hasNext(); ) {
      GraphObject go = ( GraphObject)i.next();
      try {
        if ( filter.passesFilter( go ) ) {
          Object value = data.getAttributeValue( go.getIdentifier(),
                                                 from );
          data.setAttributeValue( go.getIdentifier(),
                                  attribute,
                                  value );
        }
      } catch ( Exception ex ) {
        ex.printStackTrace();

      }
    }
  }



  private void dealWithDouble ( String attribute, double input, String action, Filter filter ) {

    List graph_objects = tableModel.getGraphObjects();
    for ( Iterator i = graph_objects.iterator(); i.hasNext(); ) {
      GraphObject go = ( GraphObject)i.next();
      
        try {
          if ( filter.passesFilter( go ) ) {
           
            if ( action == SET ) {
              //System.out.println( "Setting "+input+" for: "+go );

              data.setAttributeValue( go.getIdentifier(),
                                      attribute,
                                      new Double( input ) );
            }

            if ( action == ADD ) {
              Double d = ( Double )data.getAttributeValue( go.getIdentifier(),
                                                           attribute );

              double new_v = input + d.doubleValue();
              data.setAttributeValue( go.getIdentifier(),
                                      attribute,
                                      new Double( new_v ) );
            }

            if ( action == MUL ) {
              Double d = ( Double )data.getAttributeValue( go.getIdentifier(),
                                                           attribute );

              double new_v = input * d.doubleValue();
              data.setAttributeValue( go.getIdentifier(),
                                      attribute,
                                      new Double( new_v ) );
            }
            
            if ( action == DIV ) {
              Double d = ( Double )data.getAttributeValue( go.getIdentifier(),
                                                           attribute );

              double new_v = d.doubleValue() / input;
              data.setAttributeValue( go.getIdentifier(),
                                      attribute,
                                      new Double( new_v ) );
            }
          } //else {
            //System.out.println( go+" does not pass: "+filter );
          //}
      } catch ( Exception ex ) {
        ex.printStackTrace();

      }
    } // iterator
  }

  private void dealWithInteger ( String attribute, int input, String action, Filter filter ) {

    List graph_objects = tableModel.getGraphObjects();
    for ( Iterator i = graph_objects.iterator(); i.hasNext(); ) {
      GraphObject go = ( GraphObject)i.next();
      
        try {
          if ( filter.passesFilter( go ) ) {
           
            if ( action == SET ) {
              //System.out.println( "Setting "+input+" for: "+go );

              data.setAttributeValue( go.getIdentifier(),
                                      attribute,
                                      new Integer( input ) );
            }

            if ( action == ADD ) {
              Integer d = ( Integer )data.getAttributeValue( go.getIdentifier(),
                                                           attribute );

              int new_v = input + d.intValue();
              data.setAttributeValue( go.getIdentifier(),
                                      attribute,
                                      new Integer( new_v ) );
            }

            if ( action == MUL ) {
              Integer d = ( Integer )data.getAttributeValue( go.getIdentifier(),
                                                           attribute );

              int new_v = input * d.intValue();
              data.setAttributeValue( go.getIdentifier(),
                                      attribute,
                                      new Integer( new_v ) );
            }
            
            if ( action == DIV ) {
              Integer d = ( Integer )data.getAttributeValue( go.getIdentifier(),
                                                           attribute );

              int new_v = d.intValue() / input;
              data.setAttributeValue( go.getIdentifier(),
                                      attribute,
                                      new Integer( new_v ) );
            }
          } //else {
            //System.out.println( go+" does not pass: "+filter );
          //}
      } catch ( Exception ex ) {
        ex.printStackTrace();

      }
    } // iterator
  }
  
  private void dealWithString ( String attribute, String input, String action, Filter filter ) {

    if ( action == DIV || action == MUL )
      return;

    List graph_objects = tableModel.getGraphObjects();
    for ( Iterator i = graph_objects.iterator(); i.hasNext(); ) {
      GraphObject go = ( GraphObject)i.next();
      
        try {
          if ( filter.passesFilter( go ) ) {
           
            if ( action == SET ) {
              //System.out.println( "Setting "+input+" for: "+go );

              data.setAttributeValue( go.getIdentifier(),
                                      attribute,
                                      input );
            }

            if ( action == ADD ) {
              String d = ( String )data.getAttributeValue( go.getIdentifier(),
                                                           attribute );

              String new_v = d.concat(input);
              data.setAttributeValue( go.getIdentifier(),
                                      attribute,
                                      new_v );
            }
            
          } 
      } catch ( Exception ex ) {
        ex.printStackTrace();

      }
    } // iterator
  }

  private void dealWithBoolean ( String attribute, Boolean input, String action, Filter filter ) {

    if ( action == DIV || action == MUL || action == ADD)
      return;

    List graph_objects = tableModel.getGraphObjects();
    for ( Iterator i = graph_objects.iterator(); i.hasNext(); ) {
      GraphObject go = ( GraphObject)i.next();
      
        try {
          if ( filter.passesFilter( go ) ) {
           
            if ( action == SET ) {
              //System.out.println( "Setting "+input+" for: "+go );

              data.setAttributeValue( go.getIdentifier(),
                                      attribute,
                                      input );
            }
            
          } 
      } catch ( Exception ex ) {
        ex.printStackTrace();

      }
    } // iterator
  }

}
