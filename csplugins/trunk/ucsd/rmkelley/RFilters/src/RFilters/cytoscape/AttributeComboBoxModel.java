package filter.cytoscape;

import java.util.*;
import filter.view.*;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.*;
import cytoscape.Cytoscape;
import cytoscape.data.GraphObjAttributes;

public abstract class AttributeComboBoxModel implements ComboBoxModel, PropertyChangeListener{

  protected Object selectedObject;
  protected Vector attributeList;
 
  protected AttributeComboBoxModel () {
    attributeList = new Vector();
    Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
  }


  public void notifyListeners(){
    for(Iterator listenIt = listeners.iterator();listenIt.hasNext();){
      ((ListDataListener)listenIt.next()).contentsChanged(new ListDataEvent(this,ListDataEvent.CONTENTS_CHANGED,0,attributeList.size()));
    }
  }
  
  //implements PropertyChange
  public abstract void propertyChange(PropertyChangeEvent pce);


  //implements ListModel
  Vector listeners = new Vector();
  public void addListDataListener(ListDataListener l){
    listeners.add(l);
  }
    
  public Object getElementAt(int index){
    return attributeList.elementAt(index);
  }
   
  public int getSize(){
    return attributeList.size();
  }
  
  public void removeListDataListener(ListDataListener l){
    listeners.remove(l);
  }

  public void setSelectedItem(Object item){
    selectedObject = item;
  }

  public Object getSelectedItem(){
    return selectedObject;
  }
      

}

