package filter.view;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import filter.model.*;
import javax.swing.border.*;
import java.beans.*;
import javax.swing.event.SwingPropertyChangeSupport;
 
import ViolinStrings.Strings;

/**
 * A FilterListPanel will
 * be able to search through its contents based on the Filter title using
 * wildcard search
 */
public class FilterListPanel 
  extends JPanel 
  implements PropertyChangeListener,
             ListSelectionListener {

  
  JList filterList;
  DefaultListModel filterModel;
  public static String FILTER_SELECTED = "FILTER_SELECTED";
  public static String NO_SELECTION = "NO_SELECTION";
  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport( this );

  public FilterListPanel () {
    super();
    initialize();
  }



  protected void initialize () {
    FilterManager.defaultManager().getSwingPropertyChangeSupport().addPropertyChangeListener( this );

    JPanel listPanel = new JPanel();
    listPanel.setBorder( new TitledBorder( "Available Filters" ) );

    filterList = new JList();
    filterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    filterList.addListSelectionListener( this );
    updateLists();
    
    JScrollPane scroll = new JScrollPane( filterList );
    listPanel.add( scroll, BorderLayout.CENTER);
    setLayout( new BorderLayout() );
    add( listPanel, BorderLayout.CENTER );

  }

  public SwingPropertyChangeSupport getSwingPropertyChangeSupport () {
    return pcs;
  }

  protected void fireFilterSelected () {
    pcs.firePropertyChange( FILTER_SELECTED, null, null );
    System.err.println("Fire filter selected");
 }


  public void valueChanged ( ListSelectionEvent e ) {
    if(filterList.getSelectedValue() == null){
      pcs.firePropertyChange( NO_SELECTION,null,null );
    }
    else{
      pcs.firePropertyChange( FILTER_SELECTED,null,null);
    }
  }

  public Filter getSelectedFilter(){
    return (Filter)filterList.getSelectedValue();
  }

  //public Filter[] getSelectedFilters () {
  //  Object[] selected_filters = compoundList.getSelectedValues();
  //  return (Filter []) selected_filters;
  //}

  public void propertyChange ( PropertyChangeEvent e ) {
    updateLists();
  }

  protected void updateLists () {
    filterModel = new DefaultListModel();
    filterModel.ensureCapacity(FilterManager.defaultManager().getFilterCount());
    for(Iterator i = FilterManager.defaultManager().getFilters();i.hasNext();){
      filterModel.addElement(i.next());
    }
    //filterList.setModel(filterModel);
    filterList.setModel(FilterManager.defaultManager());
  }
}
