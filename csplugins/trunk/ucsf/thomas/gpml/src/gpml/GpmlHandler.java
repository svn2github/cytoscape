package gpml;

import giny.model.Edge;
import giny.model.Node;
import giny.view.EdgeView;
import giny.view.GraphView;
import giny.view.NodeView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.pathvisio.debug.Logger;
import org.pathvisio.model.Pathway;
import org.pathvisio.model.PathwayElement;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;

/**
 * Class that handles the GPML representation of nodes and edges, stored
 * as attributes
 * @author thomas
 *
 */
public class GpmlHandler {
	CyAttributes nAttributes = Cytoscape.getNodeAttributes();
	CyAttributes eAttributes = Cytoscape.getEdgeAttributes();
	
	Map<String, GpmlNode> nodes =  new HashMap<String, GpmlNode>();
	Map<String, GpmlEdge> edges = new HashMap<String, GpmlEdge>();
	
	AttributeMapper attributeMapper = new DefaultAttributeMapper();
	
	public void setAttributeMapper(AttributeMapper attributeMapper) {
		this.attributeMapper = attributeMapper;
	}
	
	public AttributeMapper getAttributeMapper() {
		return attributeMapper;
	}
	
	public GpmlNode getNode(String nodeId) {
		return nodes.get(nodeId);
	}
	
	public GpmlNode getNode(Node node) {
		return getNode(node.getIdentifier());
	}
	
	/**
	 * Creates and adds a GpmlNode for the given NodeView, if it
	 * doesn't exist yet
	 * @param nview
	 * @return The GpmlNode for the given NodeView
	 */
	public GpmlNode createNode(NodeView nview) {
		String nid = nview.getNode().getIdentifier();
		GpmlNode gn = nodes.get(nid);
		if(gn == null) {
			nodes.put(nid, gn = new GpmlNode(nview, getAttributeMapper()));
		}
		return gn;
	}
	
	public GpmlEdge getEdge(String edgeId) {
		return edges.get(edgeId);
	}
	
	public GpmlEdge getEdge(Edge e) {
		return getEdge(e.getIdentifier());
	}
	
	public GpmlEdge createEdge(EdgeView eview) {
		GraphView gview = eview.getGraphView();
		String eid = eview.getEdge().getIdentifier();
		GpmlEdge ge = edges.get(eid);
		if(ge == null) {
			GpmlNode gsource = createNode(gview.getNodeView(eview.getEdge().getSource()));
			GpmlNode gtarget = createNode(gview.getNodeView(eview.getEdge().getTarget()));
			edges.put(eid, ge = new GpmlEdge(eview, gsource, gtarget, getAttributeMapper()));
		}
		return ge;
	}
	
	/**
	 * Add a node that will be linked to GPML information
	 * @param n	The Cytoscape node to give GPML information
	 * @param pwElm The GPML information
	 */
	public void addNode(CyNode n, PathwayElement pwElm) {
		nodes.put(n.getIdentifier(), new GpmlNode(n, pwElm, getAttributeMapper()));
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
		GpmlNode gsource = getNode(e.getSource());
		GpmlNode gtarget = getNode(e.getTarget());
		edges.put(e.getIdentifier(), new GpmlEdge(e, pwElm, gsource, gtarget, getAttributeMapper()));
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
    
    /**
     * Show or hide the annotations (non node/edge elements) on the annotation canvas
     * of the given view
     * @param view
     * @param visible
     */
    public void showAnnotations(GraphView view, boolean visible) {
    	for(GpmlNode gn : nodes.values()) {
    		gn.showAnnotations(view, visible);
    	}
    }
    
    public Pathway createPathway(GraphView view) {
    	Pathway pathway = new Pathway();
    	pathway.getMappInfo().setMapInfoName(view.getIdentifier());
    	for(GpmlNode gn : nodes.values()) {
    		pathway.add(gn.getPathwayElement(view, attributeMapper));
    	}
    	for(GpmlEdge ge : edges.values()) {
    		pathway.add(ge.getPathwayElement(view, attributeMapper));
    	}
    	return pathway;
    }
    
    public void applyGpmlVisualStyle() {
    	VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
    	CalculatorCatalog catalog = vmm.getCalculatorCatalog();
    	VisualStyle gpmlStyle = catalog.getVisualStyle("GPML");
    	if(gpmlStyle == null) { //Create the GPML visual style
    		Logger.log.trace("VisualStyle: creating GPML style");
    		try {
				gpmlStyle = (VisualStyle)vmm.getVisualStyle().clone();
			} catch (CloneNotSupportedException e) {
				gpmlStyle = new VisualStyle("GPML");
			}
    		gpmlStyle.setName("GPML");
    		gpmlStyle.setNodeAppearanceCalculator(new GpmlNodeAppearanceCalculator(this));
    		gpmlStyle.setEdgeAppearanceCalculator(new GpmlEdgeAppearanceCalculator(this));
    		catalog.addVisualStyle(gpmlStyle);
    	} else {
    		Logger.log.trace("VisualStyle: reusing GPML style");
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
    		if(gn == null) {
    			Logger.log.trace("Layout: skipping " + gn + ", not a GPML node");
    			continue; //Not a GPML node
    		}
    		gn.resetToGpml(getAttributeMapper(), view);
    	}
		view.updateView();	
    }

}
