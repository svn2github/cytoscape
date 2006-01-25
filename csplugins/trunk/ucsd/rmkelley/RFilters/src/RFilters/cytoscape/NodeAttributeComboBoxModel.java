package filter.cytoscape;

import java.util.*;
import filter.view.*;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.*;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.attr.*;


public class NodeAttributeComboBoxModel extends AttributeComboBoxModel{
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

  public void attributeDefined ( String attributeName ) {
    updateAttributes();
  }
    
  public void attributeUndefined ( String attributeName ) {
    updateAttributes();
  }

  protected void updateAttributes(){
    byte type;
    if ( attributeClass == Double.class )
      type = CyAttributes.TYPE_FLOATING;
    else if ( attributeClass == Integer.class )
      type = CyAttributes.TYPE_INTEGER;
    else if ( attributeClass == String.class )
      type = CyAttributes.TYPE_STRING;
    else 
      return;

    String [] na = Cytoscape.getNodeAttributesList();
    attributeList = new Vector();
    for ( int idx = 0; idx < na.length; idx++) {
      if ( nodeAttributes.getType( na[idx] ) == type ) {
        attributeList.add(na[idx]);
      } 
      Collections.sort( attributeList );
      notifyListeners();
    }
  }
}
