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
import cytoscape.view.CyWindow;
import cytoscape.data.GraphObjAttributes;
import filter.view.*;
import filter.model.*;
import cytoscape.CyNetwork;

/**
 * This is a Cytoscape specific filter that will pass nodes if
 * a selected attribute matches a specific value.
 */

public class NumericAttributeFilterEditor 
  extends FilterEditor 
  implements ActionListener,FocusListener {
         
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

  protected String filterName;
  protected Number searchNumber;
  protected String selectedAttribute;
  protected String selectedClass;
  protected String comparison;

  protected NumericAttributeFilter filter;

  protected CyWindow cyWindow;
  //protected GraphObjAttributes objectAttributes;

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

  public NumericAttributeFilterEditor ( CyWindow cyWindow ) {
    super();
    this.cyWindow = cyWindow;
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
    setLayout(new BorderLayout());
    identifier = "Numeric Filter";
    setBorder( new TitledBorder( getFilterID()+" - "+getDescription()));

    JPanel namePanel = new JPanel();
    nameField = new JTextField(15);
    namePanel.add( new JLabel( "Filter Name" ) );
    namePanel.add( nameField );
    nameField.addActionListener(this);
    nameField.addFocusListener(this);
    add( namePanel,BorderLayout.NORTH );

    JPanel all_panel = new JPanel();
    all_panel.setLayout(new GridLayout(3,1));	
				
    JPanel topPanel = new JPanel();
    topPanel.add(new JLabel("Select graph objects of type "));
			
    classBox = new JComboBox();
    classBox.addItem(NumericAttributeFilter.NODE);
    classBox.addItem(NumericAttributeFilter.EDGE);
    classBox.setEditable( false );
    classBox.addActionListener(this);
    topPanel.add(classBox);
    
    JPanel middlePanel = new JPanel();
    middlePanel.add(new JLabel(" with a value for numeric attribute "));
				
    attributeBox = new JComboBox();
    attributeBox.setEditable(false);
    attributeBox.addActionListener(this);
    attributeBox.setModel(AttributeManager.nodeAttributeManager());
    middlePanel.add(attributeBox);

    JPanel bottomPanel = new JPanel();	
    bottomPanel.add(new JLabel(" that is "));
				
    comparisonBox = new JComboBox();
    comparisonBox.addItem(NumericAttributeFilter.LESS);
    comparisonBox.addItem(NumericAttributeFilter.EQUAL);
    comparisonBox.addItem(NumericAttributeFilter.GREATER);
    comparisonBox.setSelectedIndex(0);
    comparisonBox.setEditable(false);
    comparisonBox.addActionListener(this);
    bottomPanel.add(comparisonBox);
    
    searchField = new JTextField(10);
    searchField.setEditable( true );
    searchField.addActionListener( this );
    searchField.addFocusListener(this);
    bottomPanel.add(searchField);
				
    all_panel.add(topPanel);
    all_panel.add(middlePanel);
    all_panel.add(bottomPanel);
    //updateAttributeBox(NODE_CLASS);
    add(all_panel,BorderLayout.CENTER);
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
    return new NumericAttributeFilter(cyWindow,DEFAULT_COMPARISON,DEFAULT_CLASS,DEFAULT_SELECTED_ATTRIBUTE,DEFAULT_SEARCH_NUMBER,DEFAULT_FILTER_NAME);
  }

  /**
   * Accepts a Filter for editing
   * Note that this Filter must be a Filter that can be edited
   * by this Filter editor. 
   */
  public void editFilter ( Filter filter ) {
    if ( filter instanceof NumericAttributeFilter ) {
      // good, this Filter is of the right type
      getSwingPropertyChangeSupport().removePropertyChangeListener( this.filter );
      this.filter = ( NumericAttributeFilter )filter;
      setFilterName(this.filter.toString());
      setSearchNumber(this.filter.getSearchNumber());
      setSelectedAttribute(this.filter.getSelectedAttribute());
      setSelectedClass(this.filter.getClassType());
      setSelectedComparison(this.filter.getComparison());
      updateName();
      getSwingPropertyChangeSupport().addPropertyChangeListener( this.filter );
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
    return filterName;
  }

  public void setFilterName ( String name ) {
    nameField.setText( name );
    filterName = name;
    fireFilterNameChanged();
  }

  // Search String /////////////////////////////////////
  
  public Number getSearchNumber () {
    return searchNumber;
    
  }

  public void setSearchNumber ( Number searchNumber ) {
    this.searchNumber = searchNumber;
    searchField.setText( searchNumber.toString() );
    fireSearchNumberChanged();
  }

  // Selected Attribute ////////////////////////////////
  
  public String getSelectedAttribute () {
    return selectedAttribute;
  }

  public void setSelectedAttribute ( String new_attr ) {
    selectedAttribute = new_attr;
    attributeBox.setSelectedItem( new_attr );
    fireAttributeChanged();
  }

  public String getSelectedClass(){
    return selectedClass;
  }

  public void setSelectedClass(String newClass){
    selectedClass = newClass;
    if ( selectedClass == NumericAttributeFilter.NODE) {
      attributeBox.setModel(nodeAttributeModel);
      attributeBox.setSelectedItem(selectedAttribute);    
    } // end of if ()
    else {
      attributeBox.setModel(edgeAttributeModel);
      attributeBox.setSelectedItem(selectedAttribute);
    } // end of else
    classBox.setSelectedItem(newClass);
    fireClassChanged();
    setSelectedAttribute((String)attributeBox.getSelectedItem());
  }

  public String getSelectedComparison(){
    return comparison;
  }

  public void setSelectedComparison(String comparison){
    this.comparison = comparison;
    comparisonBox.setSelectedItem(comparison);
    fireComparisonChanged();
  }

  public void actionPerformed ( ActionEvent e ) {
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


  public void fireSearchNumberChanged () {
    pcs.firePropertyChange( NumericAttributeFilter.SEARCH_NUMBER_EVENT, null, getSearchNumber() );
  }

  public void fireFilterNameChanged () {
    pcs.firePropertyChange( NumericAttributeFilter.FILTER_NAME_EVENT, null, getFilterName() );
  }

  public void fireComparisonChanged(){
    pcs.firePropertyChange(NumericAttributeFilter.COMPARISON_EVENT, null, getSelectedComparison() );
  }
  public void fireClassChanged(){
    pcs.firePropertyChange( NumericAttributeFilter.CLASS_TYPE_EVENT,null, getSelectedClass());
  }
  public void fireAttributeChanged () {
    pcs.firePropertyChange( NumericAttributeFilter.SELECTED_ATTRIBUTE_EVENT, null, getSelectedAttribute() );
  }

  
 
}
