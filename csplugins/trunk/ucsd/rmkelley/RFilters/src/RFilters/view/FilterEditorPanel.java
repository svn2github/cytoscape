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
  implements PropertyChangeListener{

  FilterEditor currentEditor;
  public FilterEditorPanel () {
    super();
    initialize();
  }

  public void initialize() {
    currentEditor = (FilterEditor)FilterEditorManager.defaultManager().getEditors().next();
    add(currentEditor);
    currentEditor.setEnabled(false);
  }
  
  public void setActiveEditor ( FilterEditor editor ) {
    remove(currentEditor);
    add(editor);
    editor.setEnabled(true);
    validate();
    currentEditor = editor;
    System.err.println("Set editor active");
  }
  
  public void propertyChange ( PropertyChangeEvent e ) {
    if ( e.getPropertyName() == FilterListPanel.FILTER_SELECTED ) {
      Filter f = ((FilterListPanel)e.getSource()).getSelectedFilter();
      FilterEditor editor = FilterEditorManager.defaultManager().getEditorForFilter(f);
      editor.editFilter(f);
      setActiveEditor(editor);
    }else if( e.getPropertyName() == FilterListPanel.NO_SELECTION ){
      currentEditor.setEnabled(false);
    }
  }

}
    

