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
import cytoscape.CyNetwork;

import filter.view.*;
import filter.model.*;

import ViolinStrings.Strings;

/**
 * This is a Cytoscape specific filter that will pass nodes if
 * a selected attribute matches a specific value.
 */

public class BooleanMetaFilterEditor 
  extends FilterEditor 
  implements ItemListener, FocusListener, ActionListener, ListSelectionListener {
         
  /**
   * This is the Name that will go in the Tab 
   * and is returned by the "toString" method
   */
  protected String identifier;

  protected JTextField nameField;
  protected JList filterList;
  protected JComboBox comparisonBox;
  protected Set filters;
  protected Vector listModel;
  protected BooleanMetaFilter filter;	
  protected String DEFAULT_FILTER_NAME = "BooleanMeta: ";
  protected String DEFAULT_COMPARISON = BooleanMetaFilter.AND;
  protected int [] DEFAULT_FILTERS = new int[0];
  protected Class filterClass;
 
  public BooleanMetaFilterEditor () {
    super();
    try{
      filterClass = Class.forName("filter.cytoscape.BooleanMetaFilter");
    }catch(Exception e){
      e.printStackTrace();
    }
    this.filters = filters;
    identifier = "Boolean Meta-Filter";
    setBorder( new TitledBorder( "Boolean Meta-Filter") );
    setLayout(new BorderLayout());
    JPanel namePanel = new JPanel();
    nameField = new JTextField(15);
    nameField.setText(identifier);
    nameField.addActionListener(this);
    nameField.addFocusListener(this);
    namePanel.add( new JLabel( "Filter Name" ) );
    namePanel.add( nameField );
    add( namePanel,BorderLayout.NORTH );

    JPanel all_panel = new JPanel();
    all_panel.setLayout(new BorderLayout());
    
				
    filterList = new JList(FilterManager.defaultManager());
    filterList.addListSelectionListener(this);
    JScrollPane scrollPane = new JScrollPane(filterList);
    all_panel.add(scrollPane,BorderLayout.CENTER);
				
    JPanel comparisonPanel = new JPanel();
    comparisonPanel.add(new JLabel("Select objects that pass "));
    comparisonBox = new JComboBox();
    comparisonBox.addItem(BooleanMetaFilter.AND);
    comparisonBox.addItem(BooleanMetaFilter.OR);
    comparisonBox.addItem(BooleanMetaFilter.XOR);
    comparisonBox.setSelectedIndex(0);
    comparisonBox.setEditable(false);
    comparisonBox.addItemListener(this);
    comparisonPanel.add(comparisonBox);
    comparisonPanel.add(new JLabel(" of the selected filters"));
				
    all_panel.add(comparisonPanel,BorderLayout.NORTH);
    add(all_panel,BorderLayout.CENTER);
  }

  public String toString () {
    return identifier;
  }

  public String getDescription(){
    return BooleanMetaFilter.FILTER_DESCRIPTION;
  }
  
  public Class getFilterClass(){
    return filterClass;
  }

  public String getFilterID () {
    return BooleanMetaFilter.FILTER_ID;
  }

  /**
   * Accepts a Filter for editing
   * Note that this Filter must be a Filter that can be edited
   * by this Filter editor. 
   */
  public void editFilter ( Filter filter ) {
    if ( filter instanceof BooleanMetaFilter ) {
      // good, this Filter is of the right type
      this.filter = ( BooleanMetaFilter )filter;
      setFilters(this.filter.getFilters());
      setComparison(this.filter.getComparison());
      setFilterName(this.filter.toString());
    }
  }

  public Filter createDefaultFilter(){
    return new BooleanMetaFilter(DEFAULT_FILTERS,DEFAULT_COMPARISON,DEFAULT_FILTER_NAME);
  }

  //----------------------------------------//
  // BooleanMetaFilter Methods
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
 
  public int [] getFilters(){
    return filter.getFilters();
  }

  public void setFilters(int [] array){
    filterList.removeListSelectionListener(this);
    filterList.clearSelection();
    for(int idx=0;idx<array.length;idx++){
      int index = FilterManager.defaultManager().indexOf(FilterManager.defaultManager().getFilter(array[idx]));
      if(index > -1){
	filterList.addSelectionInterval(index,index);
      }
    }
    filterList.addListSelectionListener(this);
    
    Object [] selectedObjects = filterList.getSelectedValues();
    int [] selectedFilters = new int[selectedObjects.length];
    for ( int idx = 0;idx < selectedFilters.length;idx++) {
      selectedFilters[idx] = FilterManager.defaultManager().getFilterID((Filter)selectedObjects[idx]);
    } // end of for ()
    filter.setFilters(selectedFilters);
    
  }

  public String getComparison(){
    return filter.getComparison();
  }

  public void setComparison(String comparison){
    filter.setComparison(comparison);
    comparisonBox.removeItemListener(this);
    comparisonBox.setSelectedItem(comparison);
    comparisonBox.addItemListener(this);
  }


  public void actionPerformed ( ActionEvent e ) {
    handleEvent(e);
  }

  public void focusGained(FocusEvent e){}
  public void focusLost(FocusEvent e){
    handleEvent(e);
  }

  public void itemStateChanged(ItemEvent e){
    handleEvent(e);
  }
  
  public void handleEvent(EventObject e){
    if ( e.getSource() == nameField ) {
      setFilterName(nameField.getText());
    } else if ( e.getSource() == filterList ) {
      Object [] selectedObjects = filterList.getSelectedValues();
      int [] selectedFilters = new int[selectedObjects.length];
      for ( int idx=0;idx<selectedFilters.length;idx++) {
	selectedFilters[idx] = FilterManager.defaultManager().getFilterID((Filter)selectedObjects[idx]);
      } // end of for ()
      
      setFilters(selectedFilters);
    } else if( e.getSource() == comparisonBox){
      setComparison((String)comparisonBox.getSelectedItem());
    }
  }
  
  public void valueChanged (ListSelectionEvent e){
    handleEvent(e);
  }
}
