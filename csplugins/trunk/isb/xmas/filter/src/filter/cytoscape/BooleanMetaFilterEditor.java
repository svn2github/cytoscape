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
  implements ActionListener, ListSelectionListener {
         
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
  protected String RESET_FITLER_NAME;


		protected String DEFAULT_COMPARISON = BooleanMetaFilter.AND;
		protected String RESET_COMPARISON;

		protected Object [] DEFAULT_FILTERS = new Object[0];
		protected Object [] RESET_FILTERS;
		
		protected Class RESET_CLASS;
		protected Class NODE_CLASS;
		protected Class EDGE_CLASS;
		protected Class NUMBER_CLASS;
  protected Class DEFAULT_CLASS; 

  public BooleanMetaFilterEditor ( Set filters ) {
    super();
				this.filters = filters;
    identifier = "Boolean Filter";
    setBorder( new TitledBorder( "Boolean Meta-Filter - Select nodes or edges based on a combination of other filters" ) );
				setLayout(new BorderLayout());
    JPanel namePanel = new JPanel();
    nameField = new JTextField(15);
				nameField.setText(identifier);
    namePanel.add( new JLabel( "Filter Name" ) );
    namePanel.add( nameField );
    add( namePanel,BorderLayout.NORTH );

				JPanel all_panel = new JPanel();
				all_panel.setLayout(new BorderLayout());
    
				
				filterList = new JList();
				updateFilterList();
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
				comparisonBox.addActionListener(this);
				comparisonPanel.add(comparisonBox);
				comparisonPanel.add(new JLabel(" selected filters"));
				
				all_panel.add(comparisonPanel,BorderLayout.NORTH);


				all_panel.add( new JButton (new AbstractAction( "Update List of Filters" ) {
								public void actionPerformed ( ActionEvent e ) {
												// Do this in the GUI Event Dispatch thread...
												SwingUtilities.invokeLater( new Runnable() {
																public void run() {
																				updateFilterList();
																}
												} ); } } ),BorderLayout.SOUTH );
				add(all_panel,BorderLayout.CENTER);
				setDefaults();

  }

		private void updateFilterList(){
						//filterBox.removeAllItems();
						listModel = new Vector();
						Iterator filterNameIt = this.filters.iterator();
						while(filterNameIt.hasNext()){
										Filter nextFilter = FilterManager.defaultManager().getFilter((String)filterNameIt.next());
										if(nextFilter != null){
														listModel.add(nextFilter);
										}
						}
						filterList.setListData(listModel);
						filterList.addListSelectionListener(this);
						filterList.clearSelection();	
		}


  //----------------------------------------//
  // Implements Filter Editor
  //----------------------------------------//

  public String toString () {
    return identifier;
  }

  public String getFilterID () {
    return BooleanMetaFilter.FILTER_ID;
  }

  /** 
   * Returns a new Filter, or the Modified Filter 
   */
  public Filter getFilter() {

    Object [] filterArray = getFilters(); 
    String currentComparison = (String)comparisonBox.getSelectedItem();
				if ( filterArray == null || filterArray.length == 0 || nameField.getText() == null ) {
      return null;
    }
    return new BooleanMetaFilter( filterArray, currentComparison, nameField.getText() );
  }

  /**
   * Accepts a Filter for editing
   * Note that this Filter must be a Filter that can be edited
   * by this Filter editor. 
   */
  public void editFilter ( Filter filter ) {
    if ( filter instanceof BooleanMetaFilter ) {
      // good, this Filter is of the right type
      getSwingPropertyChangeSupport().removePropertyChangeListener( this.filter );
      this.filter = ( BooleanMetaFilter )filter;
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
  // BooleanMetaFilter Methods
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
 
		public Object [] getFilters(){
						return filterList.getSelectedValues(); 
		}

		public void setFilters(Object [] array){
						updateFilterList();
						//filterList.setValueIsAdjusting(true);
						for(int idx=0;idx<array.length;idx++){
										int index = listModel.indexOf(array[idx]);
										if(index > -1){
														filterList.addSelectionInterval(index,index);
										}
						}
						//filterList.setValueIsAdjusting(false);
		}

		public String getComparison(){
						return (String)comparisonBox.getSelectedItem();
		}

		public void setComparison(String comparison){
						comparisonBox.setSelectedItem(comparison);
		}


  public void actionPerformed ( ActionEvent e ) {
    if ( e.getSource() == nameField ) {
      fireFilterNameChanged();
    } else if ( e.getSource() == filterList ) {
      fireFilterChanged();
				} else if( e.getSource() == comparisonBox){
								fireComparisonChanged();
				}
  }

	 public void valueChanged (ListSelectionEvent e){
						fireFilterChanged();		
		}

  public void fireFilterNameChanged () {
						pcs.firePropertyChange( BooleanMetaFilter.FILTER_NAME_EVENT, null, nameField.getText() );
		}

		public void fireFilterChanged(){
						pcs.firePropertyChange( BooleanMetaFilter.FILTER_BOX_EVENT, null, getFilters() );
  }

		public void fireComparisonChanged(){
						pcs.firePropertyChange(BooleanMetaFilter.COMPARISON_EVENT, null, comparisonBox.getSelectedItem() );
		}

		public void setDefaults () {
						setFilters(DEFAULT_FILTERS);
						setComparison(DEFAULT_COMPARISON);	
		}

  public void readInFilter () {
				RESET_COMPARISON = filter.getComparison();
				RESET_FILTERS = filter.getFilters();
				resetFilter();
  }

  public void resetFilter () {
    setFilters( RESET_FILTERS );
				setComparison(RESET_COMPARISON);
    fireFilterNameChanged();
  }
}
