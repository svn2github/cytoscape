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
  
  JList attributeList;
  JList labelList;

  JButton addToLabel;
  JButton removeFromLabel;
  
  JTextField newLabel;
  JButton newLabelButton;

  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport( this );
    
  public AttributePanel ( CytoscapeData data, AttributeModel a_model, LabelModel l_model ) {
  
    
    this.data = data;

    setLayout( new BorderLayout() );
    
    // attributes
    JPanel attPanel = new JPanel();
    attPanel.setBorder( new TitledBorder( "Attributes" ) );
    attributeList = new JList( a_model );
    attributeList.addListSelectionListener( this );
    attributeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
    JScrollPane a_scroll = new JScrollPane( attributeList );
    attPanel.add( a_scroll, BorderLayout.CENTER );
    a_scroll.setPreferredSize(new Dimension(200,300));


    // labels
    JPanel labPanel = new JPanel();
    labPanel.setBorder( new TitledBorder( "Labels" ) );
    labelList = new JList( l_model );
    labelList.addListSelectionListener( this );
    labelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane l_scroll = new JScrollPane( labelList );
    labPanel.add( l_scroll, BorderLayout.CENTER );
    l_scroll.setPreferredSize(new Dimension(200,100));
    
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
    one.setLayout( new BorderLayout() );
    one.add( ln, BorderLayout.NORTH );
    one.add( labPanel, BorderLayout.CENTER );
    one.add( lcp, BorderLayout.SOUTH );

    setLayout( new BorderLayout() );
    add( one, BorderLayout.NORTH );
    add( attPanel, BorderLayout.CENTER );


  }

  public void setTableModel( DataTableModel tableModel ) {
    this.tableModel = tableModel;
  }
  
  public void actionPerformed ( ActionEvent e ) {

    try {
    
    if ( e.getSource() == newLabel || e.getSource() == newLabelButton ) {
      // create a new label, and add the attributes to it
      String label_name = newLabel.getText();
      Object[] atts = attributeList.getSelectedValues();
      for ( int i = 0; i < atts.length; ++i ) {
        data.applyLabel( (String)atts[i], label_name );
      }

    }

    if ( e.getSource() == addToLabel ) {
      String label = labelList.getSelectedValue().toString();
      Object[] atts = attributeList.getSelectedValues();
      for ( int i = 0; i < atts.length; ++i ) {
        data.applyLabel( (String)atts[i], label );
      }

    }

    if ( e.getSource() == removeFromLabel ) {
      String label = labelList.getSelectedValue().toString();
      Object[] atts = attributeList.getSelectedValues();
      for ( int i = 0; i < atts.length; ++i ) {
        data.removeLabel( (String)atts[i], label );
      }

    }


    } catch ( Exception ex ) {}

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
    
    } catch ( Exception ex ) {}
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
