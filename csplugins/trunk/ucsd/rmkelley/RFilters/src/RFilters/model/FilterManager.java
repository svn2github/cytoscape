package filter.model;

import java.util.*;
import filter.view.*;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.*;

public class FilterManager implements ListModel, PropertyChangeListener{

  protected static FilterManager DEFAULT_MANAGER;
  public static String FILTER_EVENT = "FILTER_EVENT";
  protected Vector filterList;
 
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
  }


  //----------------------------------------//
  // PCS Methods

  /**
   * PCS Support
   */
  public SwingPropertyChangeSupport getSwingPropertyChangeSupport () {
    return pcs;
  }

  public void fireFilterEvent () {
    pcs.firePropertyChange( FILTER_EVENT, null, null );
  }
  
  public void notifyListeners(){
    for(Iterator listenIt = listeners.iterator();listenIt.hasNext();){
      ((ListDataListener)listenIt.next()).contentsChanged(new ListDataEvent(this,ListDataEvent.CONTENTS_CHANGED,0,filterList.size()));
    }
  }
  
  /**
   * Add/Replace
   */
  public void addFilter ( Filter filter ) {
    filterList.add(filter);
    filter.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
    fireFilterEvent();
    notifyListeners();
  }

  public boolean removeFilter(Filter filter){
    boolean result = filterList.remove(filter);
    fireFilterEvent();
    notifyListeners();
    return result;
  }

  public Iterator getFilters(){
    return filterList.iterator();
  }

  public int getFilterCount(){
    return filterList.size();
  }

  public Filter createFilterFromString ( String desc ) {
    String[] array = desc.split( "," );
    if ( array[0].equals( "filter.cytoscape.StringPatternFilter" ) ) {
      //System.out.println( "Found String Filter" );
      Filter new_filter = new filter.cytoscape.StringPatternFilter( array[1], array[2], array[3], array[4] );
      addFilter( new_filter );
      return new_filter;
    } else if ( array[0].equals( "filter.cytoscape.NumericAttributeFilter" ) ) {
      //System.out.println( "Found Numeric Filter" );
      Filter new_filter = new filter.cytoscape.NumericAttributeFilter( array[1], array[2], array[3], array[4], array[5] );
      addFilter( new_filter );
      return new_filter;
    }
    return null;
  }
  
  //implements PropertyChange
  public void propertyChange(PropertyChangeEvent pce){
    notifyListeners();
    fireFilterEvent();
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
