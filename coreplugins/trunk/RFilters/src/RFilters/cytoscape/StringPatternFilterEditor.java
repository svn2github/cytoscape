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
				
				
    setLayout(new BorderLayout());
    identifier = "String Filter";
    setBorder( new TitledBorder( getFilterID() ) );

    JPanel namePanel = new JPanel();
    nameField = new JTextField(15);
    nameField.addActionListener(this);
    nameField.addFocusListener(this);
    namePanel.add( new JLabel( "Filter Name" ) );
    namePanel.add( nameField );
   
    add( namePanel,BorderLayout.NORTH );

    JPanel all_panel = new JPanel();
    all_panel.setLayout(new GridLayout(3,1));	
    JPanel topPanel = new JPanel();
    topPanel.add(new JLabel("Select graph objects of type "));
    classBox = new JComboBox();
    classBox.addItem(StringPatternFilter.NODE);
    classBox.addItem(StringPatternFilter.EDGE);
    classBox.setEditable( false );
    classBox.addItemListener(this);
    topPanel.add(classBox);
				
    JPanel middlePanel = new JPanel();
    middlePanel.add(new JLabel(" with a value for text attribute "));
    
    attributeBox = new JComboBox();
    attributeBox.setEditable(false);
    attributeBox.addItemListener(this);
    middlePanel.add(attributeBox);

    JPanel bottomPanel = new JPanel();
    bottomPanel.add(new JLabel(" that matches the pattern "));
				
    searchField = new JTextField(10);
    searchField.setEditable( true );
    searchField.addActionListener( this );
    searchField.addFocusListener(this);
    bottomPanel.add(searchField);

    all_panel.add(topPanel);
    all_panel.add(middlePanel);
    all_panel.add(bottomPanel);
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
