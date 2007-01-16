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
import cytoscape.CyNetwork;

/**
 * This is a Cytoscape specific filter that will pass nodes if
 * a selected attribute matches a specific value.
 */

public class NumericAttributeFilterEditor 
  extends FilterEditor 
  implements ActionListener,FocusListener, ItemListener {
         
  /**
   * This is the Name that will go in the Tab 
   * and is returned by the "toString" method
   */
  
  protected JTextField nameField;
  protected JComboBox classBox;
  protected JTextField searchField;
  protected JComboBox attributeBox;
  protected JComboBox comparisonBox;

  protected String identifier;

  protected NumericAttributeFilter filter;

  protected Number DEFAULT_SEARCH_NUMBER = new Double(0);
  protected String DEFAULT_FILTER_NAME = "Numeric: ";
  protected String DEFAULT_SELECTED_ATTRIBUTE = "";
  protected String DEFAULT_COMPARISON = NumericAttributeFilter.EQUAL;
  protected String DEFAULT_CLASS = NumericAttributeFilter.NODE;

  protected Class NODE_CLASS;
  protected Class EDGE_CLASS;
  protected Class NUMBER_CLASS;
  protected Class filterClass;
  protected ComboBoxModel nodeAttributeModel;
  protected ComboBoxModel edgeAttributeModel;

  public NumericAttributeFilterEditor () {
    super();
    try{
      NUMBER_CLASS = Class.forName("java.lang.Number");
      NODE_CLASS = Class.forName("giny.model.Node");
      EDGE_CLASS = Class.forName("giny.model.Edge");
      filterClass = Class.forName("filter.cytoscape.NumericAttributeFilter");
      nodeAttributeModel = new NodeAttributeComboBoxModel(NUMBER_CLASS);
      edgeAttributeModel = new EdgeAttributeComboBoxModel(NUMBER_CLASS);
    }catch(Exception e){
      e.printStackTrace();
    }

    //this.objectAttributes = network.getNodeAttributes();
    setLayout(new GridBagLayout());
    identifier = "Numeric Filter";
    setBorder( new TitledBorder( getFilterID() ) );
    
    GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
    
    JLabel lbFilterName = new JLabel( "Filter Name" ); 
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
    add(lbFilterName, gridBagConstraints);

    nameField = new JTextField(15);
    nameField.setEditable(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
    add(nameField, gridBagConstraints);

    nameField.addActionListener(this);
    nameField.addFocusListener(this);
    
    JLabel lbSelectType =new JLabel("Select graph objects of type "); 
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
    add(lbSelectType, gridBagConstraints);

    classBox = new JComboBox();
    //classBox.setPreferredSize(new Dimension(50,22));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
    add(classBox, gridBagConstraints);

    classBox.addItem(NumericAttributeFilter.NODE);
    classBox.addItem(NumericAttributeFilter.EDGE);
    classBox.setEditable( false );
    classBox.addItemListener(this);
    
    JLabel lbNumberAttr = new JLabel(" with a value for numeric attribute ");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
    add(lbNumberAttr, gridBagConstraints);

    attributeBox = new JComboBox();
    attributeBox.setMinimumSize(new java.awt.Dimension(100, 18));
    attributeBox.setPreferredSize(new java.awt.Dimension(100, 22));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
    add(attributeBox, gridBagConstraints);

    attributeBox.setEditable(false);
    attributeBox.addItemListener(this);
 
    JLabel lbThatIs = new JLabel(" that is ");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
    add(lbThatIs, gridBagConstraints);
	    
    comparisonBox = new JComboBox();
    comparisonBox.setPreferredSize(new Dimension(50,22));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    add(comparisonBox, gridBagConstraints);

    comparisonBox.addItem(NumericAttributeFilter.LESS);
    comparisonBox.addItem(NumericAttributeFilter.EQUAL);
    comparisonBox.addItem(NumericAttributeFilter.GREATER);
    comparisonBox.setSelectedIndex(0);
    comparisonBox.setEditable(false);
    comparisonBox.addItemListener(this);
    
    searchField = new JTextField(10);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
    add(searchField, gridBagConstraints);

    searchField.setEditable( true );
    searchField.addActionListener( this );
    searchField.addFocusListener(this);
 
  }


  //----------------------------------------//
  // Implements Filter Editor
  //----------------------------------------//

  public Class getFilterClass(){
    return filterClass;
  }
  public String toString () {
    return identifier;
  }

  public String getFilterID () {
    return NumericAttributeFilter.FILTER_ID;
  }

  public String getDescription() {
    return NumericAttributeFilter.FILTER_DESCRIPTION;
  }

  /**
   * Create a new filter with the given name initialized to the default values
   */
  public Filter createDefaultFilter(){
    return new NumericAttributeFilter(DEFAULT_COMPARISON,DEFAULT_CLASS,DEFAULT_SELECTED_ATTRIBUTE,DEFAULT_SEARCH_NUMBER,DEFAULT_FILTER_NAME);
  }

  /**
   * Accepts a Filter for editing
   * Note that this Filter must be a Filter that can be edited
   * by this Filter editor. 
   */
  public void editFilter ( Filter filter ) {
    if ( filter instanceof NumericAttributeFilter ) {
      // good, this Filter is of the right type
      this.filter = ( NumericAttributeFilter )filter;
      setFilterName(this.filter.toString());
      setSearchNumber(this.filter.getSearchNumber());
      setSelectedAttribute(this.filter.getSelectedAttribute());
      setSelectedClass(this.filter.getClassType());
      setSelectedComparison(this.filter.getComparison());
      updateName();
    }
  }

  //----------------------------------------//
  // NumericAttributeFilter Methods
  //----------------------------------------//

  // There should be getter and setter methods for
  // every editable property that the Filter needs to
  // to find out from the Editor. In this case there is only one
  // the search string.

  // Filter Name ///////////////////////////////////////

  public String getFilterName () {
    return filter.toString();
  }

  public void setFilterName ( String name ) {
    nameField.setText( name );
    filter.setIdentifier(name);
  }

  // Search String /////////////////////////////////////
  
  public Number getSearchNumber () {
    return filter.getSearchNumber();
  }

  public void setSearchNumber ( Number searchNumber ) {
    filter.setSearchNumber(searchNumber);
    searchField.setText( searchNumber.toString() );
  }

  // Selected Attribute ////////////////////////////////
  
  public String getSelectedAttribute () {
    return filter.getSelectedAttribute();
  }

  public void setSelectedAttribute ( String new_attr ) {
    filter.setSelectedAttribute(new_attr);
    attributeBox.removeItemListener(this);
    attributeBox.setSelectedItem(new_attr);
    attributeBox.addItemListener(this);
  }

  public String getSelectedClass(){
    return filter.getClassType();
  }

  public void setSelectedClass(String newClass){
    filter.setClassType(newClass);
    attributeBox.removeItemListener(this);
    if ( newClass == NumericAttributeFilter.NODE) {
      attributeBox.setModel(nodeAttributeModel);
      attributeBox.setSelectedItem(getSelectedAttribute());
    } // end of if ()
    else {
      attributeBox.setModel(edgeAttributeModel);
      attributeBox.setSelectedItem(getSelectedAttribute());
    } // end of else
    attributeBox.addItemListener(this);
    classBox.removeItemListener(this);
    classBox.setSelectedItem(newClass);
    classBox.addItemListener(this);
    setSelectedAttribute((String)attributeBox.getSelectedItem());
  }

  public String getSelectedComparison(){
    return filter.getComparison();
  }

  public void setSelectedComparison(String comparison){
    filter.setComparison(comparison);
    comparisonBox.removeItemListener(this);
    comparisonBox.setSelectedItem(comparison);
    comparisonBox.addItemListener(this);
  }

  public void actionPerformed ( ActionEvent e ) {
    handleEvent(e);
  }

  public void itemStateChanged(ItemEvent e){
    handleEvent(e);
  }

  private void handleEvent(AWTEvent e){
    if ( e.getSource() == nameField) {
      setFilterName(nameField.getText());
    } // end of if ()
    else {
      if ( e.getSource() == searchField ) {
	String numberString = searchField.getText();
	Number searchNumber = null;
	try{
	  searchNumber = new Double(numberString);
	}catch(Exception except){
	  searchNumber = DEFAULT_SEARCH_NUMBER;
	  searchField.setText(searchNumber.toString());
	}
	setSearchNumber(searchNumber);
      } else if ( e.getSource() == attributeBox ) {
	setSelectedAttribute((String)attributeBox.getSelectedItem());
      } else if( e.getSource() == classBox ){
	setSelectedClass((String)classBox.getSelectedItem());
      } else if( e.getSource() == comparisonBox){
	setSelectedComparison((String)comparisonBox.getSelectedItem());
      }
      updateName();
    }
  }

  public void focusGained(FocusEvent e){};
  public void focusLost(FocusEvent e){
    handleEvent(e);
  }
 
  public void updateName () {
    StringBuffer buffer = new StringBuffer();
    buffer.append( getSelectedClass() + " : " );
    buffer.append( getSelectedAttribute() );
    buffer.append( getSelectedComparison() );
    buffer.append( getSearchNumber() );
    setFilterName(buffer.toString());
  }
  
}
