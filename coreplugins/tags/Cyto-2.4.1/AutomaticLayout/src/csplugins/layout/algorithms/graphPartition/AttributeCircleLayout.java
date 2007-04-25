package csplugins.layout.algorithms.graphPartition;

import java.util.*;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.data.*;
import giny.view.*;
import giny.model.*;

import filter.cytoscape.*;


public class AttributeCircleLayout extends AbstractLayout {
  
  CyNetwork network;
  CyAttributes data;
  String attribute;

  public AttributeCircleLayout ( CyNetwork network, 
                                 CyAttributes data, 
                                 String attribute ) {
    super( network );
    this.network = network;
    this.attribute = attribute;
    this.data = data;
    initialize();
  }

  protected void initialize () {
      
  }

  public void layoutPartion ( GraphPerspective net ) {

    int count = net.getNodeCount();
    int r = (int)Math.sqrt(count);
    r*=100;

    // nodesList is deprecated, so we need to create our own so
    // that we can hand it off to the sort routine
    List nodes = net.nodesList();

    Collections.sort( nodes, new AttributeComparator() );


    // Compute angle step
    double phi = 2 * Math.PI / nodes.size();
    
    // Arrange vertices in a circle
    for (int i = 0; i < nodes.size(); i++) {
      Node node = (Node)nodes.get( i );
      layout.setX( node, r + r * Math.sin(i * phi) );
      layout.setY( node, r + r * Math.cos(i * phi) );

    }

  }

  private class AttributeComparator implements Comparator {
    
    private AttributeComparator () {}
    
    public int compare ( Object oo1, Object oo2 ) {
      
      GraphObject o1 = (GraphObject)oo1;
      GraphObject o2 = (GraphObject)oo2;
      
      byte type = data.getType( attribute );
      if ( type == CyAttributes.TYPE_STRING ) {
        String v1 = data.getStringAttribute( o1.getIdentifier(), attribute );
        String v2 = data.getStringAttribute( o2.getIdentifier(), attribute );

				if (v1 != null && v2 != null)
        	return v1.compareToIgnoreCase( v2 );
				else if (v1 == null && v2 != null) return -1;
				else if (v1 == null && v2 == null) return 0;
				else if (v1 != null && v2 == null) return 1;
 
      } else if ( type == CyAttributes.TYPE_FLOATING ) {
        Double v1 = data.getDoubleAttribute( o1.getIdentifier(), attribute );
        Double v2 = data.getDoubleAttribute( o2.getIdentifier(), attribute );

				if (v1 != null && v2 != null)
        	return v1.compareTo( v2 );
				else if (v1 == null && v2 != null) return -1;
				else if (v1 == null && v2 == null) return 0;
				else if (v1 != null && v2 == null) return 1;
        
      } else if ( type == CyAttributes.TYPE_INTEGER ) {
        Integer v1 = data.getIntegerAttribute( o1.getIdentifier(), attribute );
        Integer v2 = data.getIntegerAttribute( o2.getIdentifier(), attribute );
        
				if (v1 != null && v2 != null)
        	return v1.compareTo( v2 );
				else if (v1 == null && v2 != null) return -1;
				else if (v1 == null && v2 == null) return 0;
				else if (v1 != null && v2 == null) return 1;
      } else if ( type == CyAttributes.TYPE_BOOLEAN ) {
        Boolean v1 = data.getBooleanAttribute( o1.getIdentifier(), attribute );
        Boolean v2 = data.getBooleanAttribute( o2.getIdentifier(), attribute );
        
				if (v1 != null && v2 != null) {
        	if ( ( v1.booleanValue() && v2.booleanValue() ) || !v1.booleanValue() && !v2.booleanValue() )
         	 return 0;
        	else if ( v1.booleanValue() && !v2.booleanValue() )
         	 return 1;
        	else if ( !v1.booleanValue() && v2.booleanValue() )
         	 return -1;
				}
				else if (v1 == null && v2 != null) return -1;
				else if (v1 == null && v2 == null) return 0;
				else if (v1 != null && v2 == null) return 1;
      }
      return 0;
    }
  }

  


}
