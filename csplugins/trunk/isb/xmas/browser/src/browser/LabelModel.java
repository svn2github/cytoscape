package browser;

import cytoscape.data.*;

import filter.model.FilterManager;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import cytoscape.data.attr.*;

public class LabelModel 
  implements ListModel,
             ComboBoxModel,
             CytoscapeDataListener {

  Vector listeners = new Vector();
  CytoscapeData data;

  Vector labels;
  Object selection = null;
  
  public LabelModel ( CytoscapeData data ) {
    this.data = data;
    data.addCytoscapeDataListener( this );
    sortLabels();
  }

  protected void sortLabels () {
    
    labels = new Vector( data.getLabelNames() );
    Collections.sort( labels );
    notifyListeners 
      ( new ListDataEvent ( this,
                            ListDataEvent.CONTENTS_CHANGED,
                            0,
                            labels.size() ) );
  }

  // implements ListModel
  
  public Object getElementAt ( int i ) {
    if ( i > labels.size() )
      return null;

    return labels.get(i);
  }

  public int getSize () {
    return labels.size();
  }

  // implements ComboBoxModel
  
  public void setSelectedItem ( Object anItem ) {
    selection = anItem;
  }
  
  
  public Object getSelectedItem () {
    return selection;
  }
  



  // implements CyDataDefinitionListener

  public void labelStateChange ( ) {
    sortLabels();
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
