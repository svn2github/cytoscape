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
public class FilterListBox 
  extends JPanel 
  implements PropertyChangeListener,
             ListSelectionListener,
             ListDataListener{

  
  JComboBox filterBox;
   
  public static String FILTER_SELECTED = "FILTER_SELECTED";
  public static String NO_SELECTION = "NO_SELECTION";
  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport( this );

  public FilterListBox () {
    super();
    initialize();
  }



  protected void initialize () {
    FilterManager.defaultManager().getSwingPropertyChangeSupport().addPropertyChangeListener( this );

    JPanel listPanel = new JPanel();
    //listPanel.setBorder( new TitledBorder( "Available Filters" ) );

    //FilterManager.defaultManager().addListDataListener(this);
    filterBox = new JComboBox(FilterManager.defaultManager().getComboBoxModel());
    //filterBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    //filterBox.addListSelectionListener( this );
    
    
    //JScrollPane scroll = new JScrollPane( filterBox );
    //listPanel.add( scroll, BorderLayout.CENTER);
    //setLayout( new BorderLayout() );
    //add( listPanel, BorderLayout.CENTER );
    //scroll.setPreferredSize(new Dimension(250,100));
    add( filterBox );
  }

  public SwingPropertyChangeSupport getSwingPropertyChangeSupport () {
    return pcs;
  }

  protected void fireFilterSelected () {
    pcs.firePropertyChange( FILTER_SELECTED, null, null );
 }


  protected void handleEvent(EventObject e){
    if(filterBox.getSelectedItem() == null){
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
    return (Filter)filterBox.getSelectedItem();
  }

  public void propertyChange ( PropertyChangeEvent e ) {
    //updateLists();
  }

}
