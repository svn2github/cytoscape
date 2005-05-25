package rowan.browser;

import cytoscape.data.CytoscapeData;

import filter.model.FilterManager;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import cytoscape.data.attr.*;

public class AttributeModel 
  implements ListModel,
             ComboBoxModel,
             CyDataDefinitionListener {

  Vector listeners = new Vector();
  CytoscapeData data;

  Vector attributes;
  Object selection = null;
  
  public AttributeModel ( CytoscapeData data ) {
    this.data = data;
    data.addDataDefinitionListener( this );
    sortAtttributes();
  }

  protected void sortAtttributes () {
    CountedIterator ci = data.getDefinedAttributes();
    attributes = new Vector( ci.numRemaining() );
    while ( ci.hasNext() ) {
      attributes.add( ci.next() );
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
