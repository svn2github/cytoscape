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

import filter.view.*;
import filter.model.*;

import ViolinStrings.Strings;

/**
 * This is a Cytoscape specific filter that will pass edges if
 * a selected attribute matches a specific value.
 */

public class CsEdgeTypeFilterEditor 
  extends FilterEditor 
  implements ActionListener {
         
  /**
   * This is the Name that will go in the Tab 
   * and is returned by the "toString" method
   */
  protected String identifier;

  protected JTextField nameField;
  protected JComboBox searchBox;
  protected JComboBox attributeBox;

  protected String searchString;
  protected String selectedAttribute;

  protected CsEdgeTypeFilter filter;

  protected CyNetwork network;
  protected GraphObjAttributes edgeAttributes;

  protected String DEFAULT_SEARCH_STRING = "";
  protected String RESET_SEARCH_STRING;

  protected String DEFAULT_FILTER_NAME = "EdgeType: ";
  protected String RESET_FITLER_NAME;

  protected String DEFAULT_SELECTED_ATTRIBUTE = "";
  protected String RESET_SELECTED_ATTRIBUTE;
  

  public CsEdgeTypeFilterEditor ( CyNetwork network ) {
    super();
    this.network = network;
    this.edgeAttributes = network.getEdgeAttributes();

    identifier = "Edge Attribute";
    setBorder( new TitledBorder( "Edge Attribute Filter" ) );

    JPanel namePanel = new JPanel();
    nameField = new JTextField(15);
    namePanel.add( new JLabel( "Filter Name" ) );
    namePanel.add( nameField );
    add( namePanel );

    JPanel attribute_panel = new JPanel();
    attributeBox = new JComboBox( edgeAttributes.getAttributeNames() );
    attributeBox.setEditable( false );
    attributeBox.addActionListener( this );
    attribute_panel.add( attributeBox );
    add( attribute_panel );

    JPanel search_panel = new JPanel();
    searchBox = new JComboBox();
    searchBox.setEditable( true );
    searchBox.addActionListener( this );
    search_panel.add( searchBox );
    add( search_panel );

    add( new JButton (new AbstractAction( "Update" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  String[] atts = edgeAttributes.getAttributeNames();
                  System.out.println( "There are: "+atts.length+" attributes." );
                  for ( int i = 0; i < atts.length; ++i ) {
                    System.out.println( i+". "+atts[i] );
                  }
                  attributeBox.setModel( new DefaultComboBoxModel( edgeAttributes.getAttributeNames() ) );
                  //( ( DefaultComboBoxModel )attributeBox.getModel() ).addElement( "canonicalName" );
                }
              } ); } } ) );

    setDefaults();

  }


  //----------------------------------------//
  // Implements Filter Editor
  //----------------------------------------//

  public String toString () {
    return identifier;
  }

  public String getFilterID () {
    return CsEdgeTypeFilter.FILTER_ID;
  }

  /** 
   * Returns a new Filter, or the Modified Filter 
   */
  public Filter getFilter() {

    String search_item = ( String )searchBox.getSelectedItem();
    String attr_item = ( String )attributeBox.getSelectedItem();
    
    if ( search_item == null || attr_item == null || nameField.getText() == null ) {
      return null;
    }
    return new CsEdgeTypeFilter( network, attr_item, search_item, nameField.getText() );
  }

  /**
   * Accepts a Filter for editing
   * Note that this Filter must be a Filter that can be edited
   * by this Filter editor. 
   */
  public void editFilter ( Filter filter ) {
    if ( filter instanceof CsEdgeTypeFilter ) {
      // good, this Filter is of the right type
      getSwingPropertyChangeSupport().removePropertyChangeListener( this.filter );
      this.filter = ( CsEdgeTypeFilter )filter;
      readInFilter();
      getSwingPropertyChangeSupport().addPropertyChangeListener( this.filter );
    }
  }

  /**
   * If the Filter is null, then set all values to it, otherwise reset 
   * to the Defaults.
   */
  public void reset () {
    if ( filter == null ) {
      setDefaults();
    } else {
      resetFilter();
    }
  }

  /**
   * Clears the Filter, and sets to Defaults.
   */
  public void clear () {
    filter = null;
    getSwingPropertyChangeSupport().removePropertyChangeListener( filter );
    setDefaults();
  }


  //----------------------------------------//
  // CsEdgeTypeFilter Methods
  //----------------------------------------//

  // There should be getter and setter methods for
  // every editable property that the Filter needs to
  // to find out from the Editor. In this case there is only one
  // the search string.

  // Filter Name ///////////////////////////////////////

  public String getFilterName () {
    return nameField.getText();
  }

  public void setFilterName ( String name ) {
    nameField.setText( name );
  }

  // Search String /////////////////////////////////////
  
  public String getSearchString () {
    return ( String )searchBox.getSelectedItem();
  }

  public void setSearchString ( String search_string ) {
    searchBox.setSelectedItem( search_string );
  }

  // Selected Attribute ////////////////////////////////
  
  public String getSelectedAttribute () {
    return ( String )attributeBox.getSelectedItem();
  }

  public void setSelectedAttribute ( String new_attr ) {
    attributeBox.setSelectedItem( new_attr );
  }

  public void actionPerformed ( ActionEvent e ) {
    if ( e.getSource() == searchBox ) {
      fireSearchStringChanged();
    } else if ( e.getSource() == nameField ) {
      fireFilterNameChanged();
    } else if ( e.getSource() == attributeBox ) {
      fireAttributeChanged();
    }
  }

  public void fireSearchStringChanged () {
    pcs.firePropertyChange( CsEdgeTypeFilter.SEARCH_STRING_EVENT, null, getSearchString() );
  }

  public void fireFilterNameChanged () {
    pcs.firePropertyChange( CsEdgeTypeFilter.FILTER_NAME_EVENT, null, nameField.getText() );
  }
  
  public void fireAttributeChanged () {
    String new_attr = getSelectedAttribute();
    searchBox.setModel( new DefaultComboBoxModel( edgeAttributes.getUniqueValues( new_attr ) ) );
    pcs.firePropertyChange( CsEdgeTypeFilter.SELECTED_ATTRIBUTE_EVENT, null, getSelectedAttribute() );
  }

  public void setDefaults () {
    setSelectedAttribute( DEFAULT_SELECTED_ATTRIBUTE );
    setSearchString( DEFAULT_SEARCH_STRING );
    setFilterName( DEFAULT_FILTER_NAME );
  }

  public void readInFilter () {
    RESET_SEARCH_STRING = filter.getSearchString();
    RESET_FITLER_NAME = filter.toString();
    RESET_SELECTED_ATTRIBUTE = filter.getSelectedAttribute();
    resetFilter();
  }

  public void resetFilter () {
    setSelectedAttribute( RESET_SELECTED_ATTRIBUTE );
    setSearchString( RESET_SEARCH_STRING );
    setFilterName( RESET_FITLER_NAME );
    fireFilterNameChanged();
  }
}
