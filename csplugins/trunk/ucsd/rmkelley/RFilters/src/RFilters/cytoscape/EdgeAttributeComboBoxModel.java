package filter.cytoscape;

import java.util.*;
import filter.view.*;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.*;
import cytoscape.Cytoscape;
import cytoscape.data.GraphObjAttributes;


public class EdgeAttributeComboBoxModel extends AttributeComboBoxModel{
  Class attributeClass;
  GraphObjAttributes edgeAttributes;
  public EdgeAttributeComboBoxModel(Class attributeClass){
    edgeAttributes = Cytoscape.getEdgeNetworkData();
    this.attributeClass = attributeClass;
  }
  public void propertyChange(PropertyChangeEvent pce){
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
