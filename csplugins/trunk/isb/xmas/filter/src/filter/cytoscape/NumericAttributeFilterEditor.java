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
 * This is a Cytoscape specific filter that will pass nodes if
 * a selected attribute matches a specific value.
 */

public class NumericAttributeFilterEditor 
  extends FilterEditor 
  implements ActionListener {
         
  /**
   * This is the Name that will go in the Tab 
   * and is returned by the "toString" method
   */
  protected String identifier;

  protected JTextField nameField;
  protected JComboBox classBox;
		protected JTextField searchField;
  protected JComboBox attributeBox;
		protected JComboBox comparisonBox;

  protected Number searchNumber;
  protected String selectedAttribute;

  protected NumericAttributeFilter filter;

  protected CyNetwork network;
  protected GraphObjAttributes objectAttributes;

  protected Number DEFAULT_SEARCH_NUMBER = new Double(0);
  protected Number RESET_SEARCH_NUMBER;

  protected String DEFAULT_FILTER_NAME = "Numeric: ";
  protected String RESET_FITLER_NAME;

  protected String DEFAULT_SELECTED_ATTRIBUTE = "";
  protected String RESET_SELECTED_ATTRIBUTE;

		protected String DEFAULT_COMPARISON = NumericAttributeFilter.EQUAL;
		protected String RESET_COMPARISON = "";

		protected Class RESET_CLASS;
		protected Class NODE_CLASS;
		protected Class EDGE_CLASS;
		protected Class NUMBER_CLASS;
  protected Class DEFAULT_CLASS; 

  public NumericAttributeFilterEditor ( CyNetwork network ) {
    super();
    this.network = network;
    this.objectAttributes = network.getNodeAttributes();
				setLayout(new BorderLayout());
    identifier = "Numeric Comparison";
    setBorder( new TitledBorder( "Numeric Comparison Filter - Select nodes or edges based on the value of numeric attributes" ) );

    JPanel namePanel = new JPanel();
    nameField = new JTextField(15);
    namePanel.add( new JLabel( "Filter Name" ) );
    namePanel.add( nameField );
    add( namePanel,BorderLayout.NORTH );

				JPanel all_panel = new JPanel();
				//all_panel.setLayout(new GridLayout(3,4,20,20));
				//all_panel.add(new JLabel("Select Object Type"));
				//all_panel.add(new JLabel("Select Attribute"));
				//all_panel.add(new JLabel("Select Comparison"));
				//all_panel.add(new JLabel("Comparison Value"));
				//JPanel class_panel = new JPanel();
				//class_panel.setLayout(new BorderLayout());
				all_panel.add(new JLabel("Select graph objects of type "));
				Vector classes = new Vector();
				try{
								NUMBER_CLASS = Class.forName("java.lang.Number");
								NODE_CLASS = Class.forName("giny.model.Node");
								EDGE_CLASS = Class.forName("giny.model.Edge");
								DEFAULT_CLASS = NODE_CLASS;
								classes.add(NODE_CLASS);
								classes.add(EDGE_CLASS);
				}catch(Exception e){
								e.printStackTrace();
				}
				classBox = new JComboBox(classes);
				classBox.setEditable( false );
				classBox.addActionListener(this);
				all_panel.add(classBox);
				//class_panel.add(new JLabel( "Select Object Type"),BorderLayout.NORTH);
				//class_panel.add(classBox,BorderLayout.CENTER);
				//add( class_panel);

				//JPanel attribute_panel = new JPanel();
				//attribute_panel.setLayout(new BorderLayout());
    all_panel.add(new JLabel(" with a value for numeric attribute "));
				
				attributeBox = new JComboBox();
				attributeBox.setEditable(false);
				attributeBox.addActionListener(this);
				all_panel.add(attributeBox);
				//attribute_panel.add( attributeBox ,BorderLayout.CENTER);
				//attribute_panel.add( new JLabel( "Attribute Selection"),BorderLayout.NORTH);
    //add( attribute_panel );

				all_panel.add(new JLabel(" that is "));
				//JPanel comparison_panel = new JPanel();
				//comparison_panel.setLayout(new BorderLayout());
				comparisonBox = new JComboBox();
				comparisonBox.addItem(NumericAttributeFilter.LESS);
				comparisonBox.addItem(NumericAttributeFilter.EQUAL);
				comparisonBox.addItem(NumericAttributeFilter.GREATER);
				comparisonBox.setSelectedIndex(0);
				comparisonBox.setEditable(false);
				comparisonBox.addActionListener(this);
				all_panel.add(comparisonBox);
				//comparison_panel.add(comparisonBox,BorderLayout.CENTER);
				//comparison_panel.add(new JLabel("Comparison Type"),BorderLayout.NORTH);
				//add( comparison_panel);

    //JPanel search_panel = new JPanel();
				//search_panel.setLayout(new BorderLayout());
    searchField = new JTextField(10);
    searchField.setEditable( true );
    searchField.addActionListener( this );
    all_panel.add(searchField);
				//search_panel.add( searchField, BorderLayout.CENTER);
				//search_panel.add( new JLabel("Comparison Value"),BorderLayout.NORTH);
    //add( search_panel );

				updateAttributeBox(NODE_CLASS);
				all_panel.add(new JLabel(""));
    add( new JButton (new AbstractAction( "Update List of Attributes" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  /*String[] atts = objectAttributes.getAttributeNames();
                  System.out.println( "There are: "+atts.length+" attributes." );
                  for ( int i = 0; i < atts.length; ++i ) {
                    System.out.println( i+". "+atts[i] );
                  }
                  attributeBox.setModel( new DefaultComboBoxModel( objectAttributes.getAttributeNames() ) );
                  //( ( DefaultComboBoxModel )attributeBox.getModel() ).addElement( "canonicalName" );
                */  classBox.setSelectedItem(NODE_CLASS);
																				updateAttributeBox(NODE_CLASS);
																		}
              } ); } } ),BorderLayout.SOUTH );

    setDefaults();
				add(all_panel,BorderLayout.CENTER);
  }


  //----------------------------------------//
  // Implements Filter Editor
  //----------------------------------------//

  public String toString () {
    return identifier;
  }

  public String getFilterID () {
    return NumericAttributeFilter.FILTER_ID;
  }

  /** 
   * Returns a new Filter, or the Modified Filter 
   */
  public Filter getFilter() {

    Number search_item = getSearchNumber(); 
    String attr_item = ( String )attributeBox.getSelectedItem();
				Class currentClass = (Class)classBox.getSelectedItem(); 
    String currentComparison = (String)comparisonBox.getSelectedItem();
				if ( currentClass == null || currentComparison == null ||search_item == null || attr_item == null || nameField.getText() == null ) {
      return null;
    }
    return new NumericAttributeFilter( network, currentComparison, currentClass, attr_item, search_item, nameField.getText() );
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
  // NumericAttributeFilter Methods
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
  
  public Number getSearchNumber () {
						String numberString = searchField.getText();
						try{
										searchNumber = new Double(numberString);
						}catch(Exception e){
										searchNumber = new Double(0);
										searchField.setText(searchNumber.toString());
						}
						return searchNumber;
  }

  public void setSearchNumber ( Number searchNumber ) {
						this.searchNumber = searchNumber;
						searchField.setText( searchNumber.toString() );
  }

  // Selected Attribute ////////////////////////////////
  
  public String getSelectedAttribute () {
    return ( String )attributeBox.getSelectedItem();
  }

  public void setSelectedAttribute ( String new_attr ) {
    attributeBox.setSelectedItem( new_attr );
  }

		public Class getSelectedClass(){
						return (Class)classBox.getSelectedItem();
		}

		public void setSelectedClass(Class newClass){
						classBox.setSelectedItem(newClass);
		}

		public String getComparison(){
						return (String)comparisonBox.getSelectedItem();
		}

		public void setComparison(String comparison){
						comparisonBox.setSelectedItem(comparison);
		}

  public void actionPerformed ( ActionEvent e ) {
    if ( e.getSource() == searchField ) {
      fireSearchNumberChanged();
    } else if ( e.getSource() == nameField ) {
      fireFilterNameChanged();
    } else if ( e.getSource() == attributeBox ) {
      fireAttributeChanged();
    } else if( e.getSource() == classBox ){
								fireClassChanged();
				} else if( e.getSource() == comparisonBox){
								fireComparisonChanged();
				}
  }

  public void fireSearchNumberChanged () {

    pcs.firePropertyChange( NumericAttributeFilter.SEARCH_NUMBER_EVENT, null, getSearchNumber() );
  }

  public void fireFilterNameChanged () {
    pcs.firePropertyChange( NumericAttributeFilter.FILTER_NAME_EVENT, null, nameField.getText() );
  }

		public void fireComparisonChanged(){
						pcs.firePropertyChange(NumericAttributeFilter.COMPARISON_EVENT, null, comparisonBox.getSelectedItem() );
		}
		public void fireClassChanged(){
						Class type = (Class)classBox.getSelectedItem();
						updateAttributeBox(type);
						pcs.firePropertyChange( NumericAttributeFilter.CLASS_TYPE_EVENT,null, classBox.getSelectedItem());
		
		}
  public void fireAttributeChanged () {
    String new_attr = getSelectedAttribute();
    //searchBox.setModel( new DefaultComboBoxModel( objectAttributes.getUniqueValues( new_attr ) ) );
    pcs.firePropertyChange( NumericAttributeFilter.SELECTED_ATTRIBUTE_EVENT, null, getSelectedAttribute() );
  }

		public void updateAttributeBox(Class type){
						if(type.equals(NODE_CLASS)){
										objectAttributes = network.getNodeAttributes();
						}
						else{
										objectAttributes = network.getEdgeAttributes();
						}
						String [] attributeNames = objectAttributes.getAttributeNames();
						Vector stringAttributes = new Vector();
						for(int idx=0;idx<attributeNames.length;idx++){
										if(NUMBER_CLASS.isAssignableFrom(objectAttributes.getClass(attributeNames[idx]))){
														stringAttributes.add(attributeNames[idx]);
										}	
						}
						attributeBox.removeAllItems();
						Iterator attrIt = stringAttributes.iterator();
						while(attrIt.hasNext()){
										attributeBox.addItem(attrIt.next());
						}
						if(attributeBox.getItemCount() != 0){
										attributeBox.setSelectedIndex(0);
						}
						fireAttributeChanged();
	
		}
  public void setDefaults () {
    setSelectedAttribute( DEFAULT_SELECTED_ATTRIBUTE );
    setSearchNumber( DEFAULT_SEARCH_NUMBER );
    setFilterName( DEFAULT_FILTER_NAME );
				setSelectedClass(DEFAULT_CLASS); 
				setComparison(DEFAULT_COMPARISON);	
		}

  public void readInFilter () {
    RESET_SEARCH_NUMBER = filter.getSearchNumber();
    RESET_FITLER_NAME = filter.toString();
    RESET_SELECTED_ATTRIBUTE = filter.getSelectedAttribute();
    RESET_CLASS = NODE_CLASS;
				RESET_COMPARISON = filter.getComparison();
				resetFilter();
  }

  public void resetFilter () {
    setSelectedAttribute( RESET_SELECTED_ATTRIBUTE );
    setSearchNumber( RESET_SEARCH_NUMBER );
    setFilterName( RESET_FITLER_NAME );
				setSelectedClass(RESET_CLASS);
				setComparison(RESET_COMPARISON);
    setSelectedAttribute(RESET_SELECTED_ATTRIBUTE);
				fireFilterNameChanged();
  }
}
