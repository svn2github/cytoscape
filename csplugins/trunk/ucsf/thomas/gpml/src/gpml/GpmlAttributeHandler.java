package gpml;

import giny.view.GraphView;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.pathvisio.debug.Logger;
import org.pathvisio.model.GpmlFormat;
import org.pathvisio.model.PathwayElement;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;

/**
 * Class that handles the GPML representation of nodes and edges, stored
 * as attributes
 * @author thomas
 *
 */
public class GpmlAttributeHandler {
	CyAttributes nAttributes = Cytoscape.getNodeAttributes();
	CyAttributes eAttributes = Cytoscape.getEdgeAttributes();
	
	Map<String, GpmlNode> nodes =  new HashMap<String, GpmlNode>();
	Map<String, GpmlEdge> edges = new HashMap<String, GpmlEdge>();
	
	public GpmlNode getNode(String nodeId) {
		return nodes.get(nodeId);
	}
	
	public GpmlEdge getEdge(String edgeId) {
		return edges.get(edgeId);
	}
	
	/**
	 * Add a node that will be linked to GPML information
	 * @param n	The Cytoscape node to give GPML information
	 * @param pwElm The GPML information
	 */
	public void addNode(CyNode n, PathwayElement pwElm) {
		nodes.put(n.getIdentifier(), new GpmlNode(n, pwElm));
	}

	/**
	 * Unlink the GPML information from the given node
	 * @param n
	 */
	public void unlinkNode(CyNode n) {
		nodes.remove(n.getIdentifier());
	}
		
	/**
	 * Add an edge that will be linked to GPML information
	 * @param n	The Cytoscape edge to give GPML information
	 * @param pwElm The GPML information
	 */
	public void addEdge(CyEdge e, PathwayElement pwElm) {
		edges.put(e.getIdentifier(), new GpmlEdge(e, pwElm));
	}
	
	/**
	 * Transfer the GPML information to the given Cytoscape attributes
	 * @param id		The identifier of the node to assign the attributes to
	 * @param o		The PathwayElement that contains the GPML information
	 * @param attr	The attributes to transfer the information to
	 */
	static void transferAttributes(String id, PathwayElement o, CyAttributes attr) {
    	try {
			Element e = GpmlFormat.createJdomElement(o, Namespace.getNamespace(""));
			attr.setAttribute(id, "GpmlElement", e.getName());
			transferAttributes(id, e, attr, null);
    	} catch(Exception e) {
			Logger.log.error("Unable to add attributes for " + o, e);
		}	
    }
    
    private static void transferAttributes(String id, Element e, CyAttributes attr, String key) {
    	List attributes = e.getAttributes();
    	for(int i = 0; i < attributes.size(); i++) {
    		Attribute a = (Attribute)attributes.get(i);
    		if(key == null) {
    			attr.setAttribute(id, a.getName(), a.getValue());
    		} else {
        	    attr.setAttribute(id, key + '.' + a.getName(), a.getValue());
    		}
    	}

    	List children = e.getChildren();
    	for(int i = 0; i < children.size(); i++) {
    		Element child = (Element)children.get(i);
    		transferAttributes(id, child, attr, (key == null ? "" : key + '.') + child.getName());
    	}
    }
    
    /**
     * Adds an annotation to the foreground canvas of the given view for
     * each node in the list that is linked to GPML information. An annotation wil not
     * be created when the GPML element could be fully converted to a Cytoscape 
     * node or edge (e.g. for data nodes or lines linked between two data nodes.
     * @param view
     * @param nodeList
     */
    public void addAnnotations(GraphView view, Collection<CyNode> nodeList) {
    	for(CyNode n : nodeList) {
    		GpmlNode gn = nodes.get(n.getIdentifier());
			if(gn != null && !edges.containsKey(gn)) { //Don't draw background line if it is an edge
				gn.addAnnotation(view);
			}
		}
    }
   
    public void applyGpmlVisualStyle() {
    	VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
    	CalculatorCatalog catalog = vmm.getCalculatorCatalog();
    	VisualStyle gpmlStyle = catalog.getVisualStyle("GPML");
    	if(gpmlStyle == null) { //Create the GPML visual style
    		try {
				gpmlStyle = (VisualStyle)vmm.getVisualStyle().clone();
			} catch (CloneNotSupportedException e) {
				gpmlStyle = new VisualStyle("GPML");
			}
    		gpmlStyle.setName("GPML");
    		gpmlStyle.setNodeAppearanceCalculator(new GpmlNodeAppearanceCalculator(this));
    		gpmlStyle.setEdgeAppearanceCalculator(new GpmlEdgeAppearanceCalculator(this));
    		catalog.addVisualStyle(gpmlStyle);
    	}
    	
    	vmm.setVisualStyle(gpmlStyle);
    }
    
    /**
     * Lays out the given nodes to the coordinates as stored in the linked GPML information
     * @param view
     * @param nodeList
     */
    public void applyGpmlLayout(GraphView view, Collection<CyNode> nodeList) {
    	for(CyNode node : nodeList) {
    		GpmlNode gn = nodes.get(node.getIdentifier());
    		if(gn == null) continue; //Not a GPML node
    		gn.resetPosition(view);
    	}
		view.updateView();	
    }

}
