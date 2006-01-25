package filter.cytoscape;

import java.util.*;
import filter.view.*;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.*;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.attr.*;

public class EdgeAttributeComboBoxModel extends AttributeComboBoxModel{
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
  
  public void attributeDefined ( String attributeName ) {
    updateAttributes();
  }
    
  public void attributeUndefined ( String attributeName ) {
    updateAttributes();
  }

  protected void updateAttributes(){
    byte type;
    if ( attributeClass == String.class )
      type = CyAttributes.TYPE_STRING;
    else if ( attributeClass == Double.class )
      type = CyAttributes.TYPE_FLOATING;
    else if ( attributeClass == Integer.class )
      type = CyAttributes.TYPE_INTEGER;
    else 
      return;

    String [] ea = Cytoscape.getEdgeAttributesList();
    attributeList = new Vector();
    for ( int idx = 0; idx < ea.length; idx++) {
      if ( edgeAttributes.getType( ea[idx] ) == type ) {
        attributeList.add(ea[idx]);
      } 
      Collections.sort( attributeList );
      notifyListeners();
    }
  }
}