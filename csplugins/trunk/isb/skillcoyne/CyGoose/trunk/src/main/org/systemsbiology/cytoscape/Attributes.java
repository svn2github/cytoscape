/**
 * 
 */
package org.systemsbiology.cytoscape;

import java.util.HashMap;

import org.systemsbiology.gaggle.core.datatypes.Network;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

/**
 * @author skillcoy
 * 
 */
public class Attributes {

	public static void addAttributes(Network gNet, NetworkObject obj) {
		System.out.println("Adding attributes");
		switch (obj) {
		case NODE:
			System.out.println("Adding NODE attributes");
			for (String att : gNet.getNodeAttributeNames()) {
				HashMap<String, Object> Attributes = gNet
						.getNodeAttributes(att);
				for (String nodeName : Attributes.keySet())
					setAttribute(Cytoscape.getNodeAttributes(), nodeName, att,
							Attributes.get(nodeName));
			}
			break;
		case EDGE:
			System.out.println("Adding EDGE attributes");
			for (String att : gNet.getEdgeAttributeNames()) {
				HashMap<String, Object> Attributes = gNet
						.getEdgeAttributes(att);
				for (String edgeName : Attributes.keySet())
					setAttribute(Cytoscape.getEdgeAttributes(), edgeName, att,
							Attributes.get(edgeName));
			}
			break;
		}
		;
		Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
	}

	
	private static void setAttribute(CyAttributes cyAtts, String networkObjId,
			String attributeName, Object attributeValue)
		{
		    //System.out.println("networkObjId: " + networkObjId + ", attributeName: " + attributeName + 
		    //", attributeValue: " + attributeValue + ", class: " + attributeValue.getClass().getName());
		if (attributeValue.getClass().equals(java.lang.String.class)) {
    		cyAtts.setAttribute(networkObjId, attributeName, (String) attributeValue);
		} else if (attributeValue.getClass().equals(Integer.class)) {
		    try {
    		    cyAtts.setAttribute(networkObjId, attributeName, (Integer) attributeValue); 
		    } catch (IllegalArgumentException ex) {
		        double d = new Double(attributeValue.toString());
		        cyAtts.setAttribute(networkObjId, attributeName, d); 
		    }
		}  else if (attributeValue.getClass().equals(Double.class)) {
		    cyAtts.setAttribute(networkObjId, attributeName, (Double) attributeValue);
		}
		
		}
		

	
}
