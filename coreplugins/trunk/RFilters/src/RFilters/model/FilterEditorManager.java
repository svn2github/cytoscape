package filter.model;

import java.util.*;
import filter.view.*;
import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;

public class FilterEditorManager {

  protected static FilterEditorManager DEFAULT_MANAGER;
  protected Vector editorList;
  protected HashMap class2Editor;
 
  /**
   *  PCS support
   */
  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);

  /**
   * Returns the Default Filter Manager
   */
  public static FilterEditorManager defaultManager () {
    if ( DEFAULT_MANAGER == null ) {
      DEFAULT_MANAGER = new FilterEditorManager();
    }
    return DEFAULT_MANAGER;
  }

  private FilterEditorManager () {
    editorList = new Vector();
    class2Editor = new HashMap();
  }

  
  /**
   * PCS Support
   */
  public SwingPropertyChangeSupport getSwingPropertyChangeSupport () {
    return pcs;
  }

  
  // Editor Methods

  public void addEditor ( FilterEditor editor ) {
    editorList.add(editor);
    class2Editor.put(editor.getFilterClass(),editor);
    //    fireEditorEvent();
    //pcs.firePropertyChange( EDITOR_ADDED, null, editor );
  }

  public boolean removeEditor ( FilterEditor editor ) {
    return editorList.remove(editor);
  }

  public Iterator getEditors () {
    return editorList.iterator();
  }

  public int getEditorCount(){
    return editorList.size();
  }

  public FilterEditor getEditorForFilter(Filter f){
    return (FilterEditor)class2Editor.get(f.getClass());
  }
}
  
