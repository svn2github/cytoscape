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

import ViolinStrings.Strings;

/**
 * This is a Cytoscape specific filter that will pass nodes if
 * a selected attribute matches a specific value.
 */

public class NodeTopologyFilterEditor 
extends FilterEditor 
implements ActionListener {

	/**
	 * This is the Name that will go in the Tab 
	 * and is returned by the "toString" method
	 */
	protected String identifier;

	protected JTextField nameField;
	protected JComboBox filterBox;
	protected JTextField distanceField;
	protected JTextField countField;
	protected Set filters;
	protected GraphPerspective myPerspective;
	protected NodeTopologyFilter filter;	
	protected String DEFAULT_FILTER_NAME = "NodeTopology: ";
	protected String RESET_FITLER_NAME;


	protected Integer DEFAULT_DISTANCE = new Integer(1);
	protected Integer RESET_DISTANCE;

	protected Integer DEFAULT_COUNT = new Integer(1);
	protected Integer RESET_COUNT;

	protected Filter DEFAULT_FILTER = null; 
	protected Filter RESET_FILTER;

	protected Class RESET_CLASS;
	protected Class NODE_CLASS;
	protected Class EDGE_CLASS;
	protected Class NUMBER_CLASS;
	protected Class DEFAULT_CLASS; 

	public NodeTopologyFilterEditor ( GraphPerspective myPerspective,Set filters ) {
		super();
		this.filters = filters;
		this.myPerspective = myPerspective; 
		identifier = "Topology Filter";
		setBorder( new TitledBorder( "Node Topology Filter - Select nodes based on network topology" ) );
		setLayout(new BorderLayout());
		JPanel namePanel = new JPanel();
		nameField = new JTextField(15);
		nameField.setText(identifier);
		namePanel.add( new JLabel( "Filter Name" ) );
		namePanel.add( nameField );
		add( namePanel,BorderLayout.NORTH );

		JPanel all_panel = new JPanel();
		all_panel.add(new JLabel("Select nodes with "));
		
		countField = new JTextField(10);
		countField.setEditable(true);
		countField.addActionListener(this);
		all_panel.add(countField);

		all_panel.add(new JLabel(" neighbors within distance "));

		distanceField = new JTextField(10);
		distanceField.setEditable(true);
		distanceField.addActionListener(this);
		all_panel.add(distanceField);

		all_panel.add(new JLabel(" that pass the filter "));
		filterBox = new JComboBox();
		filterBox.addActionListener(this);
		all_panel.add(filterBox);
		
		add( new JButton (new AbstractAction( "Update List of Filters" ) {
			public void actionPerformed ( ActionEvent e ) {
				// Do this in the GUI Event Dispatch thread...
				SwingUtilities.invokeLater( new Runnable() {
					public void run() {
						updateFilterBox();
					}
				} ); } } ),BorderLayout.SOUTH );
		add(all_panel,BorderLayout.CENTER);
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
		return NodeTopologyFilter.FILTER_ID;
	}

	/** 
	 * Returns a new Filter, or the Modified Filter 
	 */
	public Filter getFilter() {

		Filter currentFilter = getSelectedFilter(); 
		Integer currentCount = getCount(); 
		Integer currentDistance = getDistance();
		if ( currentFilter == null || nameField.getText() == null ) {
			return null;
		}
		return new NodeTopologyFilter( myPerspective, currentCount, currentDistance,currentFilter, nameField.getText() );
	}

	/**
	 * Accepts a Filter for editing
	 * Note that this Filter must be a Filter that can be edited
	 * by this Filter editor. 
	 */
	public void editFilter ( Filter filter ) {
		if ( filter instanceof NodeTopologyFilter ) {
			// good, this Filter is of the right type
			getSwingPropertyChangeSupport().removePropertyChangeListener( this.filter );
			this.filter = ( NodeTopologyFilter )filter;
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
	// NodeTopologyFilter Methods
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


	public Integer getCount(){
		String countString = countField.getText();
		Integer countInteger = DEFAULT_COUNT;
		try{
			countInteger = new Integer(countString);
			if(countInteger.intValue()<1){
				countInteger = DEFAULT_COUNT;
			}
		}catch(Exception e){}
		countField.setText(countInteger.toString());
		return countInteger;
	}

	public void setCount(Integer count){
		countField.setText(count.toString());
	}

	public Integer getDistance(){
		String distanceString = distanceField.getText();
		Integer distanceInteger = DEFAULT_DISTANCE;
		try{
			distanceInteger = new Integer(distanceString);
			if(distanceInteger.intValue()<1){
				distanceInteger = DEFAULT_DISTANCE;
			}
		}catch(Exception e){}
		distanceField.setText(distanceInteger.toString());
		return distanceInteger;
	}

	public void setDistance(Integer distance){
		distanceField.setText(distance.toString());
	}

	public void actionPerformed ( ActionEvent e ) {
		if ( e.getSource() == nameField ) {
			fireFilterNameChanged();
		} else if ( e.getSource() == filterBox ) {
			fireFilterChanged();
		} else if( e.getSource() == countField){
			fireCountChanged();
		} else if( e.getSource() == distanceField){
			fireDistanceChanged();
		}
	}


	public void fireFilterNameChanged () {
		pcs.firePropertyChange( NodeTopologyFilter.FILTER_NAME_EVENT, null, nameField.getText() );
	}

	public void fireFilterChanged(){
		pcs.firePropertyChange( NodeTopologyFilter.FILTER_BOX_EVENT, null, getSelectedFilter() );
	}

	public void fireCountChanged(){
		pcs.firePropertyChange(NodeTopologyFilter.COUNT_EVENT, null, getCount() );
	}

	public void fireDistanceChanged(){
		pcs.firePropertyChange(NodeTopologyFilter.DISTANCE_EVENT, null, getDistance() );
	}


	public void setDefaults () {
		setSelectedFilter(DEFAULT_FILTER);
		setCount(DEFAULT_COUNT);
		setDistance(DEFAULT_DISTANCE);
	}

	public void readInFilter () {
		RESET_COUNT = filter.getCount();
		RESET_DISTANCE = filter.getDistance();
		RESET_FILTER = filter.getFilter();
		resetFilter();
	}

	public void resetFilter () {
		setSelectedFilter( RESET_FILTER );
		setCount(RESET_COUNT);
		setDistance(RESET_DISTANCE);
		fireFilterNameChanged();
	}
}
