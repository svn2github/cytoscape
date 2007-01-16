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
import cytoscape.data.CyAttributes;
import filter.view.*;
import filter.model.*;
import ViolinStrings.Strings;

/**
 * This is a Cytoscape specific filter that will pass nodes if
 * a selected attribute matches a specific value.
 */

public class StringPatternFilterEditor 
  extends FilterEditor 
  implements ActionListener,FocusListener,ItemListener {
         
  /**
   * This is the Name that will go in the Tab 
   * and is returned by the "toString" method
   */
  protected String identifier;

  protected JTextField nameField;
  protected JComboBox classBox;
  protected JTextField searchField;
  protected JComboBox attributeBox;

  protected StringPatternFilter filter;

  protected String DEFAULT_SEARCH_STRING = "";
  protected String DEFAULT_FILTER_NAME = "Pattern: ";
  protected String DEFAULT_SELECTED_ATTRIBUTE = "";

  protected Class NODE_CLASS;
  protected Class EDGE_CLASS;
  protected Class STRING_CLASS;
  protected String DEFAULT_CLASS = StringPatternFilter.NODE; 
  protected Class filterClass;

  protected ComboBoxModel nodeAttributeModel;
  protected ComboBoxModel edgeAttributeModel;


  public StringPatternFilterEditor () {
    super();
    try{
      STRING_CLASS = Class.forName("java.lang.String");
      NODE_CLASS = Class.forName("giny.model.Node");
      EDGE_CLASS = Class.forName("giny.model.Edge");
      filterClass = Class.forName("filter.cytoscape.StringPatternFilter");
      nodeAttributeModel = new NodeAttributeComboBoxModel(STRING_CLASS);
      edgeAttributeModel = new EdgeAttributeComboBoxModel(STRING_CLASS);
    }catch(Exception e){
      e.printStackTrace();
    }
						
    setLayout(new GridBagLayout());
    identifier = "String Filter";
    setBorder( new TitledBorder( getFilterID() ) );

    java.awt.GridBagConstraints gridBagConstraints;

    JLabel lbFilterName = new JLabel( "Filter Name" );
    lbFilterName.setFocusable(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
    add(lbFilterName, gridBagConstraints);
    
    nameField = new JTextField(15);
    nameField.setEditable(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    //gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
    add(nameField, gridBagConstraints);
    
    nameField.addActionListener(this);
    nameField.addFocusListener(this);

    JLabel lbSelectType = new JLabel("Select graph objects of type ");
    lbSelectType.setFocusable(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
    add(lbSelectType, gridBagConstraints);
   
    classBox = new JComboBox();
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    //gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
    add(classBox, gridBagConstraints);

    classBox.addItem(StringPatternFilter.NODE);
    classBox.addItem(StringPatternFilter.EDGE);
    classBox.setEditable( false );
    classBox.addItemListener(this);

    JLabel lbTextAttr = new JLabel(" with a value for text attribute ");
    lbTextAttr.setFocusable(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
    add(lbTextAttr, gridBagConstraints);

    attributeBox = new JComboBox();
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    //gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
    add(attributeBox, gridBagConstraints);

    attributeBox.setEditable(false);
    attributeBox.addItemListener(this);

    JLabel lbMatchesPattern = new JLabel(" that matches the pattern ");
    lbMatchesPattern.setFocusable(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
    add(lbMatchesPattern, gridBagConstraints);
	
    searchField = new JTextField(10);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    //gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
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

  public String getDescription(){
    return StringPatternFilter.FILTER_DESCRIPTION;
  }

  public String getFilterID () {
    return StringPatternFilter.FILTER_ID;
  }

  /**
   * Creates a new filter initialized to the default values with the given name
   */
  public Filter createDefaultFilter(){
    return new StringPatternFilter(DEFAULT_CLASS,DEFAULT_SELECTED_ATTRIBUTE,DEFAULT_SEARCH_STRING,DEFAULT_FILTER_NAME);
  }
  /**
   * Accepts a Filter for editing
   * Note that this Filter must be a Filter that can be edited
   * by this Filter editor. 
   */
  public void editFilter ( Filter filter ) {
    if ( filter instanceof StringPatternFilter ) {
      // good, this Filter is of the right type
      this.filter = ( StringPatternFilter )filter;      
      setSearchString(this.filter.getSearchString());
      setFilterName(this.filter.toString());
      setSelectedAttribute(this.filter.getSelectedAttribute());
      setSelectedClass(this.filter.getClassType());
      updateName();
    }
  }

  //----------------------------------------//
  // StringPatternFilter Methods
  //----------------------------------------//

  // There should be getter and setter methods for
  // every editable property that the Filter needs to
  // to find out from the Editor. In this case there is only one
  // the search string.

  // Filter Name ///////////////////////////////////////

  public String getFilterName () {
    return identifier;
  }

  public void setFilterName ( String name ) {
    nameField.setText( name );
    filter.setIdentifier(name);
  }

  // Search String /////////////////////////////////////
  
  public String getSearchString () {
    return filter.getSearchString();
  }

  public void setSearchString ( String search_string ) {
    filter.setSearchString(search_string);
    searchField.setText(search_string);
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
      attributeBox.setSelectedItem(filter.getSelectedAttribute());    
    } // end of if ()
    else {
      attributeBox.setModel(edgeAttributeModel);
      attributeBox.setSelectedItem(filter.getSelectedAttribute());
    } // end of else
    attributeBox.addItemListener(this);
    classBox.removeItemListener(this);
    classBox.setSelectedItem(newClass);
    classBox.addItemListener(this);
    setSelectedAttribute((String)attributeBox.getSelectedItem());
  }

  public void handleEvent ( AWTEvent e ) {
    if ( e.getSource() == nameField){
      setFilterName(nameField.getText());
    }
    else {
      if ( e.getSource() == searchField ) {
	setSearchString(searchField.getText());
      } else if ( e.getSource() == nameField ) {
      setFilterName(nameField.getText());
      } else if ( e.getSource() == attributeBox ) {
	setSelectedAttribute((String)attributeBox.getSelectedItem());
      } else if( e.getSource() == classBox ){
	setSelectedClass((String)classBox.getSelectedItem());
      }
      updateName();
    }
  }

  public void actionPerformed(ActionEvent ae){
    handleEvent(ae);
  }

  public void itemStateChanged(ItemEvent e){
    handleEvent(e);
  }

  public void focusGained(FocusEvent e){};

  public void focusLost(FocusEvent e){
    handleEvent(e);
  }

  public void updateName () {
    StringBuffer buffer = new StringBuffer();
    buffer.append( getSelectedClass() + " : " );
    buffer.append( getSelectedAttribute()+" ~ " );
    buffer.append( getSearchString() );
    setFilterName(buffer.toString());
  }
}
