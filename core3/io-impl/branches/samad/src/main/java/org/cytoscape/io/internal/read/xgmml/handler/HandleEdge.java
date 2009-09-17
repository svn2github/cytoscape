package org.cytoscape.io.internal.read.xgmml.handler;

import org.cytoscape.io.internal.read.xgmml.ParseState;
import org.cytoscape.model.CyNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class HandleEdge extends AbstractHandler {
	public ParseState handle(String tag, Attributes atts, ParseState current)
			throws SAXException {
		// Get the label, id, source and target
		String label = atts.getValue("label");
		String source = atts.getValue("source");
		String target = atts.getValue("target");
		String isDirected = atts.getValue("cy:directed");
		String sourceAlias = null;
		String targetAlias = null;
		String interaction = ""; // no longer users

		// Parse out the interaction (if this is from Cytoscape)
		// parts[0] = source alias
		// parts[1] = interaction
		// parts[2] = target alias
		String[] parts = label.split("[()]");
		if (parts.length == 3) {
			sourceAlias = parts[0];
			interaction = parts[1];
			targetAlias = parts[2];
			// System.out.println("Edge label parse: interaction = "+interaction);
		}

		boolean directed;
		if (isDirected == null) {
			// xgmml files made by pre-3.0 cytoscape and strictly
			// upstream-XGMML conforming files
			// won't have directedness flag, in which case use the
			// graph-global directedness setting.
			//
			// (org.xml.sax.Attributes.getValue() returns null if attribute does
			// not exists)
			//
			// This is the correct way to read the edge-directionality of
			// non-cytoscape xgmml files as well.
			directed = manager.currentNetworkisDirected;
		} else { // parse directedness flag
			if ("0".equals(isDirected)) {
				directed = false;
			} else {
				directed = true;
			}
		}
		if (manager.idMap.containsKey(source)
				&& manager.idMap.containsKey(target)) {
			CyNode sourceNode = manager.idMap.get(source);
			CyNode targetNode = manager.idMap.get(target);
			manager.currentEdge = attributeValueUtil.createEdge(sourceNode,
					targetNode, label, directed);
		} else if (sourceAlias != null && targetAlias != null) {
			CyNode sourceNode = manager.idMap.get(sourceAlias);
			CyNode targetNode = manager.idMap.get(targetAlias);
			manager.currentEdge = attributeValueUtil.createEdge(sourceNode,
					targetNode, label, directed);
		}

		return current;
	}
}
