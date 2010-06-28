package cytoscape.util;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

public class AttributeUtil {

	// Check if all the values for an attribute are NULL
	public static boolean isNullAttribute(String attributeType, String attributeName){
		
		boolean retValue = true;
						
		CyAttributes attributes = null;
		if (attributeType.equalsIgnoreCase("node")){
			attributes = Cytoscape.getNodeAttributes();
			int[] nodeIndices = Cytoscape.getCurrentNetwork().getNodeIndicesArray();
			for (int i=0; i<nodeIndices.length; i++){
				String nodeID = Cytoscape.getRootGraph().getNode(nodeIndices[i]).getIdentifier();
				Object valueObj = attributes.getAttribute(nodeID, attributeName);
				if (valueObj != null){
					retValue = false;
					break;
				}
			}
		}
		else {// edge
			attributes = Cytoscape.getEdgeAttributes();			
			int[] edgeIndices = Cytoscape.getCurrentNetwork().getEdgeIndicesArray();
			for (int i=0; i<edgeIndices.length; i++){
				String edgeID = Cytoscape.getRootGraph().getEdge(edgeIndices[i]).getIdentifier();
				Object valueObj = attributes.getAttribute(edgeID, attributeName);
				if (valueObj != null){
					retValue = false;
					break;
				}
			}
		}

		return retValue;
	}
}
