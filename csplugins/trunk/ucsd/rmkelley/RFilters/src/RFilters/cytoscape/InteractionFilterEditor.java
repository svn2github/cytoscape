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
import cytoscape.view.CyWindow;
import filter.view.*;
import filter.model.*;
import cytoscape.CyNetwork;
import ViolinStrings.Strings;

/**
 * This filter will pass nodes based on the edges that 
 * they have.
 */

public class InteractionFilterEditor 
  extends FilterEditor
  implements ActionListener,FocusListener {

  /**
   * This is the Name that will go in the Tab 
   * and is returned by the "toString" method
   */
  protected String identifier;
  protected Class filterClass;

  protected JTextField nameField;
  protected JComboBox filterBox;
  protected JComboBox targetBox;

  protected InteractionFilter filter;

  
  protected CyWindow cyWindow;

  protected String DEFAULT_FILTER_NAME = "Node Interaction: ";
  protected int DEFAULT_FILTER = -1;
  protected String DEFAULT_TARGET = InteractionFilter.SOURCE;

  public InteractionFilterEditor ( CyWindow cyWindow ) {
    super();
    try{
      filterClass = Class.forName("filter.cytoscape.InteractionFilter");
    }catch(Exception e){
      e.printStackTrace();
    }
    this.cyWindow = cyWindow;
    identifier = "Node Interactions";
    setBorder( new TitledBorder( "Node Interaction Filter"));
    setLayout( new BorderLayout() );
    setPreferredSize(new Dimension(600,250));	
    JPanel namePanel = new JPanel();
    nameField = new JTextField(15);
    nameField.addActionListener(this);
    nameField.addFocusListener(this);
    namePanel.add( new JLabel( "Filter Name" ) );
    namePanel.add( nameField );
    add( namePanel, BorderLayout.NORTH  );
								
    JPanel all_panel = new JPanel();
    all_panel.setLayout(new GridLayout(2,1));
								
    JPanel topPanel = new JPanel();
    topPanel.add(new JLabel("Select nodes which are the "));
    targetBox = new JComboBox();
    targetBox.addItem(InteractionFilter.SOURCE);
    targetBox.addItem(InteractionFilter.TARGET);
    targetBox.addActionListener(this);
    topPanel.add( targetBox );
								
    JPanel bottomPanel = new JPanel();
    bottomPanel.add(new JLabel("of at least one edge which passes the filter "));	
								
    filterBox = new JComboBox(FilterManager.defaultManager().getComboBoxModel());
    filterBox.addActionListener(this);
    bottomPanel.add(filterBox);
								
    all_panel.add(topPanel);
    all_panel.add(bottomPanel);
								
    add( all_panel, BorderLayout.CENTER );
  }

    //----------------------------------------//
  // Implements Filter Editor
  //----------------------------------------//

  public String toString () {
    return identifier;
  }

  public String getFilterID () {
    return InteractionFilter.FILTER_ID;
  }

  public String getDescription(){
    return InteractionFilter.FILTER_DESCRIPTION;
  }

  public Class getFilterClass(){
    return filterClass;
  }

  public Filter createDefaultFilter(){
    return new InteractionFilter(DEFAULT_FILTER,DEFAULT_TARGET,DEFAULT_FILTER_NAME);
  }
 
  /**
   * Accepts a Filter for editing
   * Note that this Filter must be a Filter that can be edited
   * by this Filter editor. 
   */
  public void editFilter ( Filter filter ) {
    if ( filter instanceof InteractionFilter ) {
      // good, this Filter is of the right type
      this.filter = ( InteractionFilter )filter;
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
    targetBox.setSelectedItem(target);
  }


  public int getSelectedFilter(){
    return filter.getFilter();
  }

  public void setSelectedFilter(int  newFilter){
    filterBox.setSelectedItem(FilterManager.defaultManager().getFilter(newFilter));
    filter.setFilter(FilterManager.defaultManager().getFilterID((Filter)filterBox.getSelectedItem()));
  }

  public void actionPerformed ( ActionEvent e ) {
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
