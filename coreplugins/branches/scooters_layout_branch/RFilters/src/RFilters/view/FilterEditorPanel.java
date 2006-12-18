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

  public FilterEditor nullEdit;
  public static String ACTIVE_PANEL_CHANGED = "Active Panel Changed";
  JPanel currentEditor;
  JPanel defaultPanel;
  PropertyChangeSupport pcs;
  public FilterEditorPanel () {
    super();
    initialize();
    pcs = new PropertyChangeSupport(this);
  }
  
  public void initialize() {
    defaultPanel = new DefaultPanel(); 
    currentEditor = defaultPanel;
    nullEdit = new NullFilterEditor();
    //Change the layout manager,to let the panel stretch to fill the available space
    setLayout(new BorderLayout()); 
    add(currentEditor);
  }
  
  public PropertyChangeSupport getPropertyChangeSupport(){
    return pcs;
  }

  public void setActivePanel ( JPanel editor ) {
    remove(currentEditor);
    add(editor);
    validate();
    paint(getGraphics());
    currentEditor = editor;
    pcs.firePropertyChange(ACTIVE_PANEL_CHANGED, null,null);
  }
  
  public void propertyChange ( PropertyChangeEvent e ) {
    if ( e.getPropertyName() == FilterListPanel.FILTER_SELECTED ) {
      Filter f = ((FilterListPanel)e.getSource()).getSelectedFilter();
      FilterEditor editor = FilterEditorManager.defaultManager().getEditorForFilter(f);
      if ( editor == null ) {
        editor = nullEdit;
      } 
      editor.editFilter(f);
      setActivePanel(editor);
    }else if( e.getPropertyName() == FilterListPanel.NO_SELECTION ){
      setActivePanel(defaultPanel);
    }
  }
}

class DefaultPanel extends JPanel{
  public DefaultPanel(){
    JTextArea text = new JTextArea();
    text.setLineWrap(true);
    text.setWrapStyleWord(true);
    text.setEditable(false);
    text.setText("There is no filter currently selected. To edit a filter, select it from the \"Available filters\" list. If the list is empty, you can create a new filter with the \"Create new filter\" button.");
    text.setColumns(25);
    text.setBackground(this.getBackground());
    setLayout(new BorderLayout());
    add(text,BorderLayout.CENTER);
  }
}
    

