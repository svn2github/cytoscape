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
 * Provides a tabbed Interface for creating filters of all available 
 * filter editors that have been provided.
 */
public class FilterEditorPanel 
  extends JPanel 
  implements PropertyChangeListener,
             ActionListener {

  JTabbedPane editorTabs;
  Map editorIndexMap;
  int editorCount;
  protected JButton addButton, removeButton, resetButton;

  public FilterEditorPanel () {
    super();
    initialize();

  }

  public void initialize() {

    //setBorder( new TitledBorder( "Edit and Create Filters" ) );

    editorIndexMap = new HashMap();
    editorCount = 0;

    //JPanel controlPanel = new JPanel();
    //controlPanel.setBorder( new TitledBorder( "Filter Control" ) );
    //controlPanel.setLayout( new GridLayout( 0, 1 ) );

    editorTabs = new JTabbedPane();

    //addButton =    new JButton( "Add/Replace" );
    //removeButton = new JButton( "   Remove  " );
    //resetButton =  new JButton( "   Reset   " );
    //addButton.addActionListener( this );
    //removeButton.addActionListener( this );
    //resetButton.addActionListener( this );
    //controlPanel.add( addButton );
    //controlPanel.add( removeButton );
    //controlPanel.add( resetButton );

    //add( controlPanel );
    add( editorTabs );
  

    //make sure that we add all the editors that have already been added
    for ( Iterator i = FilterManager.defaultManager().getEditors(); i.hasNext(); ) {
      FilterEditor fe = ( FilterEditor )i.next();
      addEditor( fe );
    }

    FilterManager.defaultManager().getSwingPropertyChangeSupport().addPropertyChangeListener( this );


  }
  
  public void addEditor ( FilterEditor new_editor ) {
    //System.out.println( "New Editor Added: "+new_editor+" "+editorCount );
    editorIndexMap.put( new_editor.getFilterID() , new Integer( editorCount ) );
    editorTabs.insertTab( new_editor.toString(), null, new_editor, new_editor.toString(), editorCount );
    editorCount++;
  }


  public void setEditorActive ( String name ) {
    int index = ( ( Integer )editorIndexMap.get( name ) ).intValue();
    editorTabs.setSelectedIndex( index );
  }
  
  public FilterEditor getSelectedEditor () {
    return ( FilterEditor )editorTabs.getSelectedComponent();
  }


  public void propertyChange ( PropertyChangeEvent e ) {
    // get notified when new Filter is up for Editing
    if ( e.getPropertyName() == FilterManager.EDITOR_ADDED ) {
      addEditor( ( FilterEditor )e.getNewValue() );
    } else if ( e.getPropertyName() == FilterListPanel.FILTER_SELECTED ) {
      Filter f = FilterManager.defaultManager().getFilter( ( String )e.getNewValue() );
      if ( f == null ) {
        return;
      }
      setEditorActive( f.getFilterID() );
      FilterEditor fe = ( FilterEditor )editorTabs.getSelectedComponent();
      fe.editFilter( f );
    }
   
  } 

  public void actionPerformed ( ActionEvent e ) {
    if ( e.getSource() == addButton ) {
      //System.out.println( "Adding Filter from selected editor: "+getSelectedEditor() );
      FilterManager.defaultManager().addFilter( getSelectedEditor().getFilter() );
    }
    if ( e.getSource() == removeButton ) {
      FilterManager.defaultManager().removeFilter( getSelectedEditor().getFilter() );
      getSelectedEditor().clear();
    }
     if ( e.getSource() == resetButton ) {
      FilterManager.defaultManager().removeFilter( getSelectedEditor().getFilter() );
      getSelectedEditor().reset();
    }
  }



}
    

