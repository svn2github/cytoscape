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

  protected CyWindow cyWindow;
  //protected GraphObjAttributes objectAttributes;

  protected Number DEFAULT_SEARCH_NUMBER = new Double(0);
  protected Number RESET_SEARCH_NUMBER;

  protected String DEFAULT_FILTER_NAME = "Numeric: ";
  protected String RESET_FITLER_NAME;

  protected String DEFAULT_SELECTED_ATTRIBUTE = "";
  protected String RESET_SELECTED_ATTRIBUTE;

		protected String DEFAULT_COMPARISON = NumericAttributeFilter.EQUAL;
		protected String RESET_COMPARISON = "";

		protected String RESET_CLASS;
		protected Class NODE_CLASS;
		protected Class EDGE_CLASS;
		protected Class NUMBER_CLASS;
  protected String DEFAULT_CLASS = NumericAttributeFilter.NODE; 

  public NumericAttributeFilterEditor ( CyWindow cyWindow ) {
    super();
    this.cyWindow = cyWindow;
				try{
								NUMBER_CLASS = Class.forName("java.lang.Number");
								NODE_CLASS = Class.forName("giny.model.Node");
								EDGE_CLASS = Class.forName("giny.model.Edge");
				}catch(Exception e){
								e.printStackTrace();
				}

    //this.objectAttributes = network.getNodeAttributes();
				setLayout(new BorderLayout());
    identifier = "Numeric Filter";
    setBorder( new TitledBorder( "Numeric Filter - Select nodes or edges based on the value of numeric attributes" ) );

    JPanel namePanel = new JPanel();
    nameField = new JTextField(15);
    namePanel.add( new JLabel( "Filter Name" ) );
    namePanel.add( nameField );
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
    bottomPanel.add(searchField);
				
				all_panel.add(topPanel);
				all_panel.add(middlePanel);
				all_panel.add(bottomPanel);
				//updateAttributeBox(NODE_CLASS);
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
																		*/
																		updateAttributeBox();
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
    String attr_item = getSelectedAttribute();
				String currentClass = getSelectedClass(); 
    String currentComparison = getSelectedComparison();
				if ( currentClass == null || currentComparison == null ||search_item == null || attr_item == null || nameField.getText() == null ) {
      return null;
    }
    return new NumericAttributeFilter( cyWindow, currentComparison, currentClass, attr_item, search_item, nameField.getText() );
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
										searchNumber = DEFAULT_SEARCH_NUMBER;
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
						if(attributeBox.getItemCount()==0){
										return null;
						}
						return ( String )attributeBox.getSelectedItem();
  }

  public void setSelectedAttribute ( String new_attr ) {
    updateAttributeBox();
				attributeBox.setSelectedItem( new_attr );
  }

		public String getSelectedClass(){
						return (String)classBox.getSelectedItem();
		}

		public void setSelectedClass(String newClass){
						classBox.setSelectedItem(newClass);
		}

		public String getSelectedComparison(){
						return (String)comparisonBox.getSelectedItem();
		}

		public void setSelectedComparison(String comparison){
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
						pcs.firePropertyChange(NumericAttributeFilter.COMPARISON_EVENT, null, getSelectedComparison() );
		}
		public void fireClassChanged(){
						updateAttributeBox();
						pcs.firePropertyChange( NumericAttributeFilter.CLASS_TYPE_EVENT,null, getSelectedClass());
		
		}
  public void fireAttributeChanged () {
    String new_attr = getSelectedAttribute();
    //searchBox.setModel( new DefaultComboBoxModel( objectAttributes.getUniqueValues( new_attr ) ) );
    pcs.firePropertyChange( NumericAttributeFilter.SELECTED_ATTRIBUTE_EVENT, null, getSelectedAttribute() );
  }

		public void updateAttributeBox(){
						GraphObjAttributes objectAttributes = null;
						String type = getSelectedClass();
						if(type.equals(NumericAttributeFilter.NODE)){
										objectAttributes = cyWindow.getNetwork().getNodeAttributes();
						}
						else{
										objectAttributes = cyWindow.getNetwork().getEdgeAttributes();
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
    setSearchNumber( DEFAULT_SEARCH_NUMBER );
    setFilterName( DEFAULT_FILTER_NAME );
				setSelectedClass(DEFAULT_CLASS); 
    setSelectedAttribute( DEFAULT_SELECTED_ATTRIBUTE );
				setSelectedComparison(DEFAULT_COMPARISON);	
		}

  public void readInFilter () {
    RESET_SEARCH_NUMBER = filter.getSearchNumber();
    RESET_FITLER_NAME = filter.toString();
    RESET_SELECTED_ATTRIBUTE = filter.getSelectedAttribute();
    RESET_CLASS = filter.getClassType();
				RESET_COMPARISON = filter.getComparison();
				resetFilter();
  }

  public void resetFilter () {
    setSearchNumber( RESET_SEARCH_NUMBER );
    setFilterName( RESET_FITLER_NAME );
				setSelectedClass(RESET_CLASS);
				setSelectedComparison(RESET_COMPARISON);
    setSelectedAttribute(RESET_SELECTED_ATTRIBUTE);
				fireFilterNameChanged();
  }
}
