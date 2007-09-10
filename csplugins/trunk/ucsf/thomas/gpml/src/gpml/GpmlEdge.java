package gpml;

import giny.model.RootGraph;

import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.PropertyType;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

/**
 * Class that holds a Cytoscape edge that has a GPML representation, which is stored
 * as edge attributes
 * @author thomas
 *
 */
public class GpmlEdge {
	CyEdge parent;
	PathwayElement pwElm;
	
	public GpmlEdge(CyEdge parent, PathwayElement pwElm) {
		this.parent = parent;
		this.pwElm = pwElm;
		GpmlAttributeHandler.transferAttributes(parent.getIdentifier(), pwElm, Cytoscape.getEdgeAttributes());
	}
	
	public PathwayElement getPathwayElement() {
		return pwElm;
	}
}
