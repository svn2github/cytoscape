package rowan.browser;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import filter.model.*;
import javax.swing.border.*;
import java.beans.*;
import javax.swing.event.SwingPropertyChangeSupport;
 
import cytoscape.data.*;

import ViolinStrings.Strings;

public class AttributePanel 
  extends JPanel
  implements PropertyChangeListener,
             ListSelectionListener,
             ListDataListener,
             ActionListener {
  
  CytoscapeData data;
  DataTableModel tableModel;
  
  // create new attribute
  JTextField newAttField;
  JButton newAttButton;
  JComboBox newAttType;

  // attributes
  JList attributeList;
  
  // labels
  JList labelList;
  JButton addToLabel;
  JButton removeFromLabel;
  JTextField newLabel;
  JButton newLabelButton;

  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport( this );
    
  public AttributePanel ( CytoscapeData data, AttributeModel a_model, LabelModel l_model ) {
  
    
    this.data = data;

    setLayout( new BorderLayout() );
    
    // new attribute
    JPanel new_att_panel = new JPanel();
    newAttField = new JTextField( 10 );
    newAttField.addActionListener( this );
    newAttButton = new JButton( "Create" );
    newAttButton.addActionListener( this );
    newAttType = new JComboBox( new String[] {"String", "Floating Point", "Integer", "Boolean"} );
    new_att_panel.setLayout( new BorderLayout() );
    new_att_panel.setBorder( new TitledBorder( "Create New Attribute" ) );
    new_att_panel.add( newAttField, BorderLayout.WEST );
    new_att_panel.add( newAttType, BorderLayout.CENTER );
    JPanel bp = new JPanel();
    bp.add( newAttButton );
    new_att_panel.add( bp, BorderLayout.SOUTH );


    // attributes
    JPanel attPanel = new JPanel();
    attPanel.setBorder( new TitledBorder( "Attributes" ) );
    attributeList = new JList( a_model );
    attributeList.addListSelectionListener( this );
    attributeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
    JScrollPane a_scroll = new JScrollPane( attributeList );
    attPanel.add( a_scroll, BorderLayout.CENTER );
    a_scroll.setPreferredSize(new Dimension(200,180));


    // labels
    JPanel labPanel = new JPanel();
    labelList = new JList( l_model );
    labelList.addListSelectionListener( this );
    labelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane l_scroll = new JScrollPane( labelList );
    labPanel.add( l_scroll, BorderLayout.CENTER );
    l_scroll.setPreferredSize(new Dimension(200,50));
    
    // label control
    JPanel lcp = new JPanel();
    addToLabel = new JButton( "+" );
    addToLabel.addActionListener( this );
    removeFromLabel = new JButton( "-" );
    removeFromLabel.addActionListener( this );
    lcp.add( addToLabel );
    lcp.add( removeFromLabel );

    // new Label
    newLabel = new JTextField( 10 );
    newLabel.addActionListener( this );
    newLabelButton = new JButton( "New" );
    newLabelButton.addActionListener( this );
    JPanel ln = new JPanel();
    ln.add( newLabel );
    ln.add( newLabelButton );
    
    JPanel one = new JPanel();
    one.setBorder( new TitledBorder( "Labels" ) );
    one.setLayout( new BorderLayout() );
    one.add( ln, BorderLayout.SOUTH );
    one.add( labPanel, BorderLayout.CENTER );
    one.add( lcp, BorderLayout.NORTH );

    setLayout( new BorderLayout() );
    add( new_att_panel, BorderLayout.NORTH );
    add( attPanel, BorderLayout.CENTER );
    add( one, BorderLayout.SOUTH );

  }

  public void setTableModel( DataTableModel tableModel ) {
    this.tableModel = tableModel;
  }
  
  public void actionPerformed ( ActionEvent e ) {
    
    if ( e.getSource() == newLabel || e.getSource() == newLabelButton ) {
      // create a new label, and add the attributes to it
      String label_name = newLabel.getText();
      Object[] atts = attributeList.getSelectedValues();
      for ( int i = 0; i < atts.length; ++i ) {
        data.applyLabel( (String)atts[i], label_name );
      }

    } else  if ( e.getSource() == addToLabel ) {
      String label = labelList.getSelectedValue().toString();
      Object[] atts = attributeList.getSelectedValues();
      for ( int i = 0; i < atts.length; ++i ) {
        data.applyLabel( (String)atts[i], label );
      }

    } else  if ( e.getSource() == removeFromLabel ) {
      String label = labelList.getSelectedValue().toString();
      Object[] atts = attributeList.getSelectedValues();
      for ( int i = 0; i < atts.length; ++i ) {
        data.removeLabel( (String)atts[i], label );
      }

    } else  if ( e.getSource() == newAttButton || e.getSource() == newAttField ) {
      String name = newAttField.getText();
      if ( name.length() < 1 ) 
        return;

      String type = (String)newAttType.getSelectedItem();
      byte t;
      if ( type.equals( "String" ) )
        t = CytoscapeData.TYPE_STRING;
      else if ( type.equals( "Floating Point" ) )
        t = CytoscapeData.TYPE_FLOATING_POINT;
      else if ( type.equals( "Integer" ) )
        t = CytoscapeData.TYPE_INTEGER;
      else if ( type.equals( "Boolean" ) )
        t = CytoscapeData.TYPE_BOOLEAN;
      else
        t = CytoscapeData.TYPE_STRING;

      data.initializeAttributeType( name, t );

    }

  }

  public String getSelectedAttribute () {
    return attributeList.getSelectedValue().toString();
  }

  public void valueChanged ( ListSelectionEvent e ) {

    try {

    if ( e.getSource() == attributeList ) {
      Object[] atts = attributeList.getSelectedValues();
      tableModel.setTableDataAttributes( Arrays.asList( atts ) );
    }

    if ( e.getSource() == labelList ) {
      String label = labelList.getSelectedValue().toString();
      Set atts = data.getAttributesByLabel( label );
      int[] indices = new int[ atts.size() ];

      int count = 0;
      for ( Iterator i = atts.iterator(); i.hasNext(); ) {
        int ind = attributeList.getNextMatch( (String)i.next(), 0, javax.swing.text.Position.Bias.Forward);
        indices[count] = ind;
        count++;
      }

      attributeList.setSelectedIndices( indices );

    }
    
    } catch ( Exception ex ) {
      ex.printStackTrace();
    }
  }

  public void contentsChanged(ListDataEvent e){}

  public void intervalAdded(ListDataEvent e){
    //handleEvent(e);
  }

  public void intervalRemoved(ListDataEvent e){
    //handleEvent(e);
  }
  public void propertyChange ( PropertyChangeEvent e ) {
    //updateLists();
  }
}
