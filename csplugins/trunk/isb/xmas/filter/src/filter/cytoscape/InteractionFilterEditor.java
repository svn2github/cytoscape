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

import ViolinStrings.Strings;

/**
	* This filter will pass nodes based on the edges that 
	* they have.
	*/

public class InteractionFilterEditor 
extends FilterEditor
implements ActionListener {

				/**
					* This is the Name that will go in the Tab 
					* and is returned by the "toString" method
					*/
				protected String identifier;

				protected JTextField nameField;
				protected JComboBox filterBox;
				protected JComboBox targetBox;

				protected InteractionFilter filter;

				protected Set filters;
				protected CyWindow cyWindow;

				protected String DEFAULT_FILTER_NAME = "NodeInteraction: ";
				protected String RESET_FITLER_NAME;

				protected Filter DEFAULT_FILTER = null;
				protected Filter RESET_FILTER;

				protected String DEFAULT_TARGET = InteractionFilter.SOURCE;
				protected String RESET_TARGET;

				public InteractionFilterEditor ( CyWindow cyWindow,Set filters ) {
								super();
								this.filters = filters;
								this.cyWindow = cyWindow;
								identifier = "Node Interactions";
								setBorder( new TitledBorder( "Select nodes based on adjacent edges" ) );
								setLayout( new BorderLayout() );

								
								JPanel namePanel = new JPanel();
								nameField = new JTextField(15);
								namePanel.add( new JLabel( "Filter Name" ) );
								namePanel.add( nameField );
								add( namePanel, BorderLayout.NORTH  );
								
								add(  new JButton (new AbstractAction( "Update List of Filters" ) {
												public void actionPerformed ( ActionEvent e ) {
																// Do this in the GUI Event Dispatch thread...
																SwingUtilities.invokeLater( new Runnable() {
																				public void run() {
																								updateFilterBox();	
																				}
																} ); } } ),BorderLayout.SOUTH );

								JPanel all_panel = new JPanel();
							
								all_panel.add(new JLabel("Select nodes which are the "));
								targetBox = new JComboBox();
								targetBox.addItem(InteractionFilter.SOURCE);
								targetBox.addItem(InteractionFilter.TARGET);
								targetBox.addActionListener(this);
								all_panel.add( targetBox );
								
								all_panel.add(new JLabel(" of at least one edge which passes the filter "));	
								
								filterBox = new JComboBox();
								filterBox.addActionListener(this);
								all_panel.add(filterBox);

								
								add( all_panel, BorderLayout.CENTER );


								setDefaults();
								

				}

				private void updateFilterBox(){
								//filterBox.removeAllItems();
								filterBox.removeAllItems();
								Iterator filterNameIt = this.filters.iterator();
								while(filterNameIt.hasNext()){
												Filter nextFilter = FilterManager.defaultManager().getFilter((String)filterNameIt.next());
												if(nextFilter != null){
																filterBox.addItem(nextFilter);
												}
								}
								if(filterBox.getItemCount()>0){
												filterBox.setSelectedIndex(0);
								}
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

				/** 
					* Returns a new Filter, or the Modified Filter 
					*/
				public Filter getFilter() {
								Filter currentFilter = getSelectedFilter();
								String target = getTarget();
								
								if ( target == null || currentFilter == null || nameField.getText() == null ) {
												return null;
								}else{
												return new InteractionFilter( cyWindow,currentFilter, target, nameField.getText() );
								}
				}

				/**
					* Accepts a Filter for editing
					* Note that this Filter must be a Filter that can be edited
					* by this Filter editor. 
					*/
				public void editFilter ( Filter filter ) {
								if ( filter instanceof InteractionFilter ) {
												// good, this Filter is of the right type
												getSwingPropertyChangeSupport().removePropertyChangeListener( this.filter );
												this.filter = ( InteractionFilter )filter;
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
				// CsNodeTypeFilter Methods
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

				public String getTarget(){
								return (String)targetBox.getSelectedItem();
				}

				public void setTarget(String target){
								targetBox.setSelectedItem(target);
				}


				public Filter getSelectedFilter(){
								if(filterBox.getItemCount()==0){
												return null;
								}
								return (Filter)filterBox.getSelectedItem();
				}

				public void setSelectedFilter(Filter newFilter){
								updateFilterBox();
								if(newFilter != null){
												filterBox.setSelectedItem(newFilter);
								}
				}



				public void actionPerformed ( ActionEvent e ) {
								if ( e.getSource() == nameField ) {
												fireFilterNameChanged();
								} else if ( e.getSource() == filterBox ) {
												fireFilterBoxChanged();
								} else if ( e.getSource() == targetBox ) {
												fireTargetBoxChanged();
								}
				}


				public void fireFilterNameChanged () {
								pcs.firePropertyChange( InteractionFilter.FILTER_NAME_EVENT, null, nameField.getText() );
				}

				public void fireFilterBoxChanged(){
								pcs.firePropertyChange( InteractionFilter.FILTER_BOX_EVENT, null, getSelectedFilter() );
				}

				public void fireTargetBoxChanged(){
								pcs.firePropertyChange(InteractionFilter.TARGET_BOX_EVENT,null,getTarget());
				}

				public void setDefaults () {
								setFilterName( DEFAULT_FILTER_NAME );
								setSelectedFilter(DEFAULT_FILTER);
								setTarget(DEFAULT_TARGET);
				}

				public void readInFilter () {
								RESET_FITLER_NAME = filter.toString();
								RESET_FILTER = filter.getFilter();
								RESET_TARGET = filter.getTarget();
								resetFilter();
				}

				public void resetFilter () {
								setFilterName( RESET_FITLER_NAME );
								setSelectedFilter( RESET_FILTER );
								setTarget( RESET_TARGET );
								fireFilterNameChanged();
				}

}
