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

class NodeAttributeComboBoxModel extends AttributeComboBoxModel{
  Class attributeClass;
  GraphObjAttributes nodeAttributes;
  public NodeAttributeComboBoxModel(Class attributeClass){
    super();
    nodeAttributes = Cytoscape.getNodeNetworkData();
    this.attributeClass = attributeClass;
    updateAttributes();
  }
  public void propertyChange(PropertyChangeEvent pce){
    updateAttributes();
  }

  protected void updateAttributes(){
    String [] nodeAttributes = Cytoscape.getNodeAttributesList();
    attributeList = new Vector();
    for ( int idx = 0; idx < nodeAttributes.length; idx++) {
      if (attributeClass.isAssignableFrom(this.nodeAttributes.getClass(nodeAttributes[idx]))) {
	attributeList.add(nodeAttributes[idx]);
      } // end of for ()
      notifyListeners();
    }
  }
}

class EdgeAttributeComboBoxModel extends AttributeComboBoxModel{
  Class attributeClass;
  GraphObjAttributes edgeAttributes;
  public EdgeAttributeComboBoxModel(Class attributeClass){
    super();
    edgeAttributes = Cytoscape.getEdgeNetworkData();
    this.attributeClass = attributeClass;
    updateAttributes();
  }
  public void propertyChange(PropertyChangeEvent pce){
    updateAttributes();
  }
  
  protected void updateAttributes(){
    String [] edgeAttributes = Cytoscape.getEdgeAttributesList();
    attributeList = new Vector();
    for ( int idx = 0; idx < edgeAttributes.length; idx++) {
      if (attributeClass.isAssignableFrom(this.edgeAttributes.getClass(edgeAttributes[idx]))) {
	attributeList.add(edgeAttributes[idx]);
      } // end of for ()
      notifyListeners();
    }
  }
}
