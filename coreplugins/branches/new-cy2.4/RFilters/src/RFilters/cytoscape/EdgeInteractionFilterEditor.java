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
import giny.model.GraphPerspective;

import filter.view.*;
import filter.model.*;
import cytoscape.CyNetwork;
import ViolinStrings.Strings;

/**
 * This filter will pass nodes based on the edges that 
 * they have.
 */

public class EdgeInteractionFilterEditor 
  extends FilterEditor
  implements ActionListener,FocusListener,ItemListener {

  /**
   * This is the Name that will go in the Tab 
   * and is returned by the "toString" method
   */
  protected String identifier;
  protected Class filterClass;

  protected JTextField nameField;
  protected JComboBox filterBox;
  protected JComboBox targetBox;

  protected EdgeInteractionFilter filter;

  

  protected String DEFAULT_FILTER_NAME = "Edge Interaction: ";
  protected int DEFAULT_FILTER = -1;
  protected String DEFAULT_TARGET = EdgeInteractionFilter.SOURCE;

  public EdgeInteractionFilterEditor ( ) {
    super();
    try{
      filterClass = Class.forName("filter.cytoscape.EdgeInteractionFilter");
    }catch(Exception e){
      e.printStackTrace();
    }
    identifier = "Edge Interactions";
    setBorder( new TitledBorder( "Edge Interaction Filter"));
    setLayout( new GridBagLayout() );
    setPreferredSize(new Dimension(450,125));	
  
    GridBagConstraints gridBagConstraints;

    JLabel lbFilterName = new JLabel( "Filter Name" );
    lbFilterName.setFocusable(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
    add(lbFilterName, gridBagConstraints);

    nameField = new JTextField(15);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
    add(nameField, gridBagConstraints);
    
    nameField.addActionListener(this);
    nameField.addFocusListener(this);
    
    JLabel SelectEdge = new JLabel("Select edges with a node ");
    SelectEdge.setFocusable(false);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
    add(SelectEdge, gridBagConstraints);
        
    targetBox = new JComboBox();
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
    add(targetBox, gridBagConstraints);
    
    targetBox.addItem(EdgeInteractionFilter.SOURCE);
    targetBox.addItem(EdgeInteractionFilter.TARGET);
    targetBox.addItem(EdgeInteractionFilter.EITHER);
    targetBox.addItemListener(this);
								
    JLabel lbPassesFilter = new JLabel("which passes the filter ");	
    lbPassesFilter.setFocusable(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
    add(lbPassesFilter, gridBagConstraints);
							

    filterBox = new JComboBox(FilterManager.defaultManager().getComboBoxModel());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
    add(filterBox, gridBagConstraints);


    filterBox.addItemListener(this);

  }

  //----------------------------------------//
  // Implements Filter Editor
  //----------------------------------------//

  public String toString () {
    return identifier;
  }

  public String getFilterID () {
    return EdgeInteractionFilter.FILTER_ID;
  }

  public String getDescription(){
    return EdgeInteractionFilter.FILTER_DESCRIPTION;
  }

  public Class getFilterClass(){
    return filterClass;
  }

  public Filter createDefaultFilter(){
    return new EdgeInteractionFilter(DEFAULT_FILTER,DEFAULT_TARGET,DEFAULT_FILTER_NAME);
  }
 
  /**
   * Accepts a Filter for editing
   * Note that this Filter must be a Filter that can be edited
   * by this Filter editor. 
   */
  public void editFilter ( Filter filter ) {
    if ( this.filter == null) {
      
    } // end of if ()
    
    if ( filter instanceof EdgeInteractionFilter ) {
      // good, this Filter is of the right type
      this.filter = ( EdgeInteractionFilter )filter;
      setFilterName(this.filter.toString());
      setSelectedFilter(this.filter.getFilter());
      setTarget(this.filter.getTarget());
    }
  }

  //----------------------------------------//
  // CsNodeTypeFilter Methods
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

  public String getTarget(){
    return filter.getTarget();
  }

  public void setTarget(String target){
    filter.setTarget(target);
    targetBox.removeItemListener(this);
    targetBox.setSelectedItem(target);
    targetBox.addItemListener(this);
  }


  public int getSelectedFilter(){
    return filter.getFilter();
  }

  public void setSelectedFilter(int  newFilter){
    if ( filter != null) {
      filterBox.removeItemListener(this);
      filterBox.setSelectedItem(FilterManager.defaultManager().getFilter(newFilter));
      filterBox.addItemListener(this);
      filter.setFilter(FilterManager.defaultManager().getFilterID((Filter)filterBox.getSelectedItem()));
    }
  }

  public void actionPerformed ( ActionEvent e ) {
    handleEvent(e);
  }

  public void itemStateChanged(ItemEvent e){
    handleEvent(e);
  }

  public void focusGained(FocusEvent e){}
  public void focusLost(FocusEvent e){
    handleEvent(e);
  }
  
  public void handleEvent(EventObject e){
    if ( e.getSource() == nameField ) {
      setFilterName(nameField.getText());
    } else if ( e.getSource() == filterBox ) {
      setSelectedFilter(FilterManager.defaultManager().getFilterID((Filter)filterBox.getSelectedItem()));
    } else if ( e.getSource() == targetBox ) {
      setTarget((String)targetBox.getSelectedItem());
    }
  }
  
}
