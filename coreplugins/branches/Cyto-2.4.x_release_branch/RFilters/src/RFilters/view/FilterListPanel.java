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
             ListSelectionListener,
             ListDataListener{

  
  JList filterList;
  DefaultListModel filterModel;
  public static String FILTER_SELECTED = "FILTER_SELECTED";
  public static String NO_SELECTION = "NO_SELECTION";
  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport( this );

  public FilterListPanel () {
    super();
    initialize();
  }

  public JList getFilterList()
  {
	 return filterList; 
  }

  protected void initialize () {
    FilterManager.defaultManager().getSwingPropertyChangeSupport().addPropertyChangeListener( this );

    JPanel listPanel = new JPanel(new BorderLayout());
    listPanel.setBorder( new TitledBorder( "Available Filters" ) );

    //FilterManager.defaultManager().addListDataListener(this);
    filterList = new JList(FilterManager.defaultManager());
    filterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    filterList.addListSelectionListener( this );
    
    
    JScrollPane scroll = new JScrollPane( filterList );
    listPanel.add( scroll, BorderLayout.CENTER);
    setLayout( new BorderLayout() );
    add( listPanel, BorderLayout.CENTER );
    //scroll.setPreferredSize(new Dimension(250,100));
  }

  public SwingPropertyChangeSupport getSwingPropertyChangeSupport () {
    return pcs;
  }

  protected void fireFilterSelected () {
    pcs.firePropertyChange( FILTER_SELECTED, null, null );
 }


  protected void handleEvent(EventObject e){
    if(filterList.getSelectedValue() == null){
      pcs.firePropertyChange( NO_SELECTION,null,null );
    }
    else{
      pcs.firePropertyChange( FILTER_SELECTED,null,null);
    }
  }

  public void valueChanged ( ListSelectionEvent e ) {
    handleEvent(e);
  }

  public void contentsChanged(ListDataEvent e){}

  public void intervalAdded(ListDataEvent e){
    handleEvent(e);
  }

  public void intervalRemoved(ListDataEvent e){
    handleEvent(e);
  }


  public Filter getSelectedFilter(){
    return (Filter)filterList.getSelectedValue();
  }

  public void propertyChange ( PropertyChangeEvent e ) {
    //updateLists();
  }

}
