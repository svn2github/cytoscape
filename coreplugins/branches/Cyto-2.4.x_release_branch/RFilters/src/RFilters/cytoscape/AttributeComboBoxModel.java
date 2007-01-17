package filter.cytoscape;

import java.util.*;
import filter.view.*;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.*;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

public abstract class AttributeComboBoxModel implements ComboBoxModel, PropertyChangeListener{

  protected Object selectedObject;
  protected Vector attributeList;

		protected Class [] type2Class = new Class[]{Boolean.class,Double.class,Integer.class,String.class};

		
  protected AttributeComboBoxModel () {
    attributeList = new Vector();
    Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
  }

		/**
			* This function will map from a type in the CyAttributes class to an actual
			* class instance
			*/
		protected Class type2Class(int type){
						switch(type){
						case CyAttributes.TYPE_BOOLEAN: 
										return Boolean.class;
						case CyAttributes.TYPE_COMPLEX: 
										return Object.class;
						case CyAttributes.TYPE_FLOATING:
										return Double.class;
						case CyAttributes.TYPE_INTEGER:
										return Integer.class;
						case CyAttributes.TYPE_SIMPLE_LIST:
										return java.util.List.class;
						case CyAttributes.TYPE_SIMPLE_MAP:
										return java.util.Map.class;
						case CyAttributes.TYPE_STRING:
										return String.class;
						case CyAttributes.TYPE_UNDEFINED:
										return Object.class;
						default:
										return Object.class;
						}
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
  
  CyAttributes nodeAttributes;
  public NodeAttributeComboBoxModel(Class attributeClass){
    super();
    nodeAttributes = Cytoscape.getNodeAttributes();
    this.attributeClass = attributeClass;
    updateAttributes();
  }
  public void propertyChange(PropertyChangeEvent pce){
    updateAttributes();
  }

  protected void updateAttributes(){
    /*byte type;
    if ( attributeClass == Double.class )
      type = CyAttributes.TYPE_FLOATING;
    else if ( attributeClass == Integer.class )
      type = CyAttributes.TYPE_INTEGER;
    else if ( attributeClass == String.class )
      type = CyAttributes.TYPE_STRING;
    else 
      return;
						*/

				String [] na = Cytoscape.getNodeAttributes().getAttributeNames();
				attributeList = new Vector();
				for ( int idx = 0; idx < na.length; idx++) {
						if(attributeClass.isAssignableFrom(type2Class(nodeAttributes.getType(na[idx])))){
										attributeList.add(na[idx]);
						}
      notifyListeners();
    }
  }
}

class EdgeAttributeComboBoxModel extends AttributeComboBoxModel{
  Class attributeClass;
  

  CyAttributes edgeAttributes;
  public EdgeAttributeComboBoxModel(Class attributeClass){
    super();
    edgeAttributes = Cytoscape.getEdgeAttributes();
    this.attributeClass = attributeClass;
    updateAttributes();
  }
  public void propertyChange(PropertyChangeEvent pce){
    updateAttributes();
  }
  
  protected void updateAttributes(){
    /*
					* This part isn't really necessary  anymore
					* now that we have the class lookup
					* table
				byte type;
    if ( attributeClass == String.class )
      type = CyAttributes.TYPE_STRING;
    else if ( attributeClass == Double.class )
      type = CyAttributes.TYPE_FLOATING;
    else if ( attributeClass == Integer.class )
      type = CyAttributes.TYPE_INTEGER;
    else 
      return;
						*/
    String [] ea = Cytoscape.getEdgeAttributes().getAttributeNames(); 
    attributeList = new Vector();
      for ( int idx = 0; idx < ea.length; idx++) {
		if(attributeClass.isAssignableFrom(type2Class(edgeAttributes.getType(ea[idx])))){
		attributeList.add(ea[idx]);
      }
      notifyListeners();
    }
  }
}
