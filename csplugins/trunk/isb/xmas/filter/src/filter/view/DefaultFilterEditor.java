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

/**
 * The Default Filter is a Simple Filter that will match based 
 * on a SearchField to an Object's toString method
 */
public class DefaultFilterEditor extends FilterEditor 
  implements ActionListener {

  /**
   * This is the Name that will go in the Tab 
   * and is returned by the "toString" method
   */
  protected String identifier;
  
  /**
   * This is the TextField where one can modify the
   * search String
   */
  protected JTextField searchField;
  
  protected JTextField nameField;

  /**
   * This is the SearchString that will be used.
   * Note that this is the only property of this Editor that will
   * be passed back and forth between the Editor and the Filter.
   */
  protected String searchString;

  /**
   * This Editor can hold a reference to a Filter, but only one that 
   * it can Edit.
   */
  protected DefaultFilter filter;

  protected String DEFAULT_SEARCH_STRING = "";
  protected String RESET_SEARCH_STRING;

  protected String DEFAULT_FILTER_NAME = "toString Match: ";
  protected String RESET_FITLER_NAME;

 

  public DefaultFilterEditor () {
    super();
    
    //   System.out.println( "Default Filter Editor Created" );

    // Set the Identifier
    identifier = "Default";

    setBorder( new TitledBorder( "Simple String Filter" ) );
    
    JPanel namePanel = new JPanel();
    nameField = new JTextField(15);
    namePanel.add( new JLabel( "Filter Name" ) );
    namePanel.add( nameField );
    add( namePanel );

    // Create the Text Field that will accept the Search String
    searchField = new JTextField(30);
    add( new JLabel( "Search Field" ) );
    add( searchField );

    setDefaults();

  }

  //----------------------------------------//
  // Implements Filter Editor
  //----------------------------------------//

  public String toString () {
    return identifier;
  }

  public String getFilterID() {
    return DefaultFilter.FILTER_ID;
  }
  

  /** 
   * Returns a new Filter, or the Modified Filter 
   */
  public Filter getFilter() {

    if ( searchField.getText() == null || nameField.getText() == null ) {
      return null;
    }
    return new DefaultFilter( searchField.getText(), nameField.getText() );

   //  if ( filter == null ) {
//       // System.out.println( "New Deafult Filter Created" );
//       return new DefaultFilter( searchField.getText(), nameField.getText() );
//     } 
//     return filter;
  }

  /**
   * Accepts a Filter for editing
   * Note that this Filter must be a Filter that can be edited
   * by this Filter editor. 
   */
  public void editFilter ( Filter filter ) {
    if ( filter instanceof DefaultFilter ) {
      // good, this Filter is of the right type
      //   System.out.println( "FILTER EDITOR is editing: "+filter );
      getSwingPropertyChangeSupport().removePropertyChangeListener( this.filter );
      this.filter = ( DefaultFilter )filter;
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
  // Default Filter Methods
  //----------------------------------------//

  // There should be getter and setter methods for
  // every editable property that the Filter needs to
  // to find out from the Editor. In this case there is only one
  // the search string.

  public String getSearchString () {
    return searchField.getText();
  }

  public void setSearchString ( String search_string ) {
    searchField.setText( search_string );
  }

  public String getFilterName () {
    return nameField.getText();
  }

  public void setFilterName ( String name ) {
    nameField.setText( name );
  }

  public void actionPerformed ( ActionEvent e ) {
    if ( e.getSource() == searchField ) {
      fireSearchStringChanged();
    } else if ( e.getSource() == nameField ) {
      fireFilterNameChanged();
    }
  }

  public void fireSearchStringChanged () {
    pcs.firePropertyChange( DefaultFilter.SEARCH_STRING_EVENT, null, searchField.getText() );
  }

  public void fireFilterNameChanged () {
     pcs.firePropertyChange( DefaultFilter.FILTER_NAME_EVENT, null, nameField.getText() );
  }

  

  public void setDefaults () {
    searchField.setText( DEFAULT_SEARCH_STRING );
    nameField.setText( DEFAULT_FILTER_NAME );
  }

  public void readInFilter () {
    RESET_SEARCH_STRING = filter.getSearchString();
    RESET_FITLER_NAME = filter.toString();
    resetFilter();
  }

  public void resetFilter () {
    setSearchString( RESET_SEARCH_STRING );
    fireSearchStringChanged();
    setFilterName( RESET_FITLER_NAME );
    fireFilterNameChanged();
  }
}
