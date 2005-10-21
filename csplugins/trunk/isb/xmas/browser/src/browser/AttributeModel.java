package browser;

import cytoscape.data.CyAttributes;

import filter.model.FilterManager;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import cytoscape.data.attr.*;

public class AttributeModel 
  implements ListModel,
             ComboBoxModel,
             MultiHashMapDefinitionListener {

  Vector listeners = new Vector();
  CyAttributes data;

  Vector attributes;
  Object selection = null;
  
  public AttributeModel ( CyAttributes data ) {
    this.data = data;
    data.getMultiHashMapDefinition().addDataDefinitionListener( this );
    sortAtttributes();
  }

  protected void sortAtttributes () {
    
    String[] att_names = data.getAttributeNames();
    attributes = new Vector( att_names.length );
    for ( int i = 0; i < att_names.length; ++i ) {
      attributes.add( att_names[i] );
    }
    Collections.sort( attributes );
    notifyListeners 
      ( new ListDataEvent ( this,
                            ListDataEvent.CONTENTS_CHANGED,
                            0,
                            attributes.size() ) );
  }

  // implements ListModel
  
  public Object getElementAt ( int i ) {
    if ( i > attributes.size() )
      return null;

    return attributes.get(i);
  }

  public int getSize () {
    return attributes.size();
  }

  // implements ComboBoxModel
  
  public void setSelectedItem ( Object anItem ) {
    selection = anItem;
  }
  
  
  public Object getSelectedItem () {
    return selection;
  }
  



  // implements CyDataDefinitionListener

  public void attributeDefined ( String attributeName ) {
    sortAtttributes();
  }
    
  public void attributeUndefined ( String attributeName ) {
    sortAtttributes();
  }

  //implements ListModel

  public void addListDataListener(ListDataListener l){
    listeners.add(l);
  }
    
  public void removeListDataListener(ListDataListener l){
    listeners.remove(l);
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

}
