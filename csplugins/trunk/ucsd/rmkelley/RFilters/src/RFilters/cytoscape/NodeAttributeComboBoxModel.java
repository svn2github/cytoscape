package filter.cytoscape;

import java.util.*;
import filter.view.*;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.*;
import cytoscape.Cytoscape;
import cytoscape.data.GraphObjAttributes;

public class NodeAttributeComboBoxModel extends AttributeComboBoxModel{
  Class attributeClass;
  GraphObjAttributes nodeAttributes;
  public NodeAttributeComboBoxModel(Class attributeClass){
    nodeAttributes = Cytoscape.getNodeNetworkData();
    this.attributeClass = attributeClass;
  }
  public void propertyChange(PropertyChangeEvent pce){
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

