package filter.model;

import java.util.*;
import filter.view.*;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.*;
import cern.colt.map.OpenIntObjectHashMap;
  public class FilterManager implements ListModel, PropertyChangeListener{

    protected static FilterManager DEFAULT_MANAGER;
    public static String FILTER_EVENT = "FILTER_EVENT";
    protected Vector filterList;
    protected OpenIntObjectHashMap ID2Filter;
    protected HashMap Filter2ID;
    Object selectedItem;
  
    /**
     *  PCS support
     */
    protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);

    /**
     * Returns the Default Filter Manager
     */
    public static FilterManager defaultManager () {
      if ( DEFAULT_MANAGER == null ) {
	DEFAULT_MANAGER = new FilterManager();
      }
      return DEFAULT_MANAGER;
    }

    private FilterManager () {
      filterList = new Vector();
      ID2Filter = new OpenIntObjectHashMap();
      Filter2ID = new HashMap();

      // Add the Select All filter before anything else.
      // This is needed by the NodeTopologyFilter
      addFilter( new filter.cytoscape.SelectAllFilter() );
    }


    //----------------------------------------//
    // PCS Methods

    /**
     * PCS Support
     */
    public SwingPropertyChangeSupport getSwingPropertyChangeSupport () {
      return pcs;
    }

    /**
     *Create a new combobox model
     *that other classes can use to display
     *the list of filters in a combobox. Note that we
     *want a separate model for each combobox, because the data
     *model keeps track of the selected item. The returned combobox will
     * basically be a wrapper around the list model which they all will share
     */
    public ComboBoxModel getComboBoxModel(){
      return new ComboBoxModel(){
          
          Object selectedItem;
          
           //implements ListModel
          Vector listeners = new Vector();
          public void addListDataListener(ListDataListener l){
            listeners.add(l);
          }
          
          
          public void removeListDataListener(ListDataListener l){
            listeners.remove(l);
          }

          public Object getSelectedItem(){
            return selectedItem;
          }
          public void setSelectedItem(java.lang.Object anItem ) {
            selectedItem = anItem;
          }
         
          public Object getElementAt(int index){
            return FilterManager.this.getElementAt(index);
          }
          public int getSize(){
            return FilterManager.this.getSize();
          }
        };
    }

    public void fireFilterEvent () {
      pcs.firePropertyChange( FILTER_EVENT, null, null );
    }
  
    public void notifyListeners(ListDataEvent e){
      for(Iterator listenIt = listeners.iterator();listenIt.hasNext();){
	if(e.getType() == ListDataEvent.CONTENTS_CHANGED){
	  ((ListDataListener)listenIt.next()).contentsChanged(e);
	}else if(e.getType() == ListDataEvent.INTERVAL_ADDED){
	  ((ListDataListener)listenIt.next()).intervalAdded(e);
	}else if(e.getType() == ListDataEvent.INTERVAL_REMOVED){
	  ((ListDataListener)listenIt.next()).intervalRemoved(e);
	}
      }
    }
    /**
     * Add a filter and assign it the given unique ID
     */
    public void addFilter ( Filter filter, int ID ) {
      if(ID < 0){
	throw new IllegalArgumentException("ID must be greater than 0");
      }
      if(ID2Filter.containsKey(ID)){
	throw new IllegalArgumentException("ID map already contains that ID");
      }
      ID2Filter.put(ID,filter);
      Filter2ID.put(filter,new Integer(ID));
      filterList.add(filter);
      //System.out.println( "Filter list added: "+filter );
      filter.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
      notifyListeners(new ListDataEvent(this,ListDataEvent.INTERVAL_ADDED,filterList.size(),filterList.size()));
      fireFilterEvent();
    }

    public void addFilter(Filter filter){
      //let's hope we don't have many filters, otherwise
    
      int ID = 0;
      while(ID2Filter.containsKey(ID)){
	ID++;
      }
      addFilter(filter,ID);
    }
  
    public boolean removeFilter(Filter filter){
      int index = filterList.indexOf(filter);

      // If it's 0, assume that it's the SelectAllFilter, which we don't want to delete.
      if (index <= 0){
	return false;
      }
      int ID = ((Integer)Filter2ID.get(filter)).intValue();
      ID2Filter.removeKey(ID);
      Filter2ID.remove(filter);
      filterList.remove(filter);
      notifyListeners(new ListDataEvent(this,ListDataEvent.INTERVAL_REMOVED,index,index));
      filter.getSwingPropertyChangeSupport().removePropertyChangeListener(this);
      fireFilterEvent();
      return true;
    }

    public int getFilterID(Filter f){
      if(f == null){
	return -1;
      }
      return ((Integer)Filter2ID.get(f)).intValue();
    }

    public Filter getFilter(int ID){
      return (Filter)ID2Filter.get(ID);
    }

    public Iterator getFilters(){
      return filterList.iterator();
    }

    public int getFilterCount(){
      return filterList.size();
    }

    public int indexOf(Object item){
      return filterList.indexOf(item);
    }

    public Filter createFilterFromString ( String desc ) {
      String[] array = desc.split("\t");
      if ( array[1].equals( "class filter.cytoscape.StringPatternFilter" ) ) {
	//System.out.println( "Found String Filter" );
	Filter new_filter = new filter.cytoscape.StringPatternFilter( array[2]);
	addFilter( new_filter);//, (new Integer(array[0])).intValue() );
	return new_filter;
      } else if ( array[1].equals( "class filter.cytoscape.NumericAttributeFilter" ) ) {
	//System.out.println( "Found Numeric Filter" );
	Filter new_filter = new filter.cytoscape.NumericAttributeFilter( array[2] );
	addFilter( new_filter);//, (new Integer(array[0])).intValue());
	return new_filter;
      } else if ( array[1].equals("class filter.cytoscape.NodeInteractionFilter" ) ){
	Filter new_filter = new filter.cytoscape.NodeInteractionFilter( array[2] );
	addFilter( new_filter);//, (new Integer(array[0])).intValue());
	return new_filter;
      	} else if (array[1].equals("class filter.cytoscape.EdgeInteractionFilter" ) ){
	Filter new_filter = new filter.cytoscape.EdgeInteractionFilter( array[2] );
	addFilter( new_filter);//, (new Integer(array[0])).intValue());
	return new_filter;
      } else if ( array[1].equals("class filter.cytoscape.NodeTopologyFilter" )) {
	Filter new_filter = new filter.cytoscape.NodeTopologyFilter( array[2] );
	addFilter( new_filter);//, (new Integer(array[0])).intValue());
	return new_filter;
      } else if ( array[1].equals("class filter.cytoscape.BooleanMetaFilter" )) {
	Filter new_filter = new filter.cytoscape.BooleanMetaFilter( array[2] );
	addFilter(new_filter);//, (new Integer(array[0])).intValue());
	return new_filter;
      } // end of if ()
      return null;
    }

    //implements PropertyChange
    public void propertyChange(PropertyChangeEvent pce){
      //the only thing we're really listening to here shoudl be the filters,
      //we check which one is sending the event, and then let the list know that
      //the contents of that filter have possibly changed
      Filter filter = (Filter)pce.getSource();
      int index = filterList.indexOf(filter);
      if ( index > -1 ){
	notifyListeners(new ListDataEvent(this,ListDataEvent.CONTENTS_CHANGED,0,filterList.size()));
	fireFilterEvent();
      }
    }

    //implements ListModel
    Vector listeners = new Vector();
    public void addListDataListener(ListDataListener l){
      listeners.add(l);
    }
    
    public Object getElementAt(int index){
      return filterList.elementAt(index);
    }
   
    public int getSize(){
      return getFilterCount();
    }
  
    public void removeListDataListener(ListDataListener l){
      listeners.remove(l);
    }
      

  }
