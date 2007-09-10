package gpml;

import giny.view.GraphView;
import giny.view.NodeView;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.pathvisio.debug.Logger;
import org.pathvisio.model.ConverterException;
import org.pathvisio.model.GpmlFormat;
import org.pathvisio.model.LineType;
import org.pathvisio.model.ObjectType;
import org.pathvisio.model.Pathway;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.GraphLink.GraphRefContainer;
import org.pathvisio.model.PathwayElement.MPoint;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import ding.view.DGraphView;
import ding.view.DingCanvas;

public class GpmlConverter {
	List<CyEdge> edges = new ArrayList<CyEdge>();
	
	HashMap<PathwayElement, CyNode> nodeMap = new HashMap<PathwayElement, CyNode>();
	HashMap<PathwayElement, String[]> edgeMap = new HashMap<PathwayElement, String[]>();

	GpmlAttributeHandler gpmlHandler;
	Pathway pathway;
		
	private GpmlConverter(GpmlAttributeHandler h) {
		gpmlHandler = h;
	}
	
	public GpmlConverter(GpmlAttributeHandler gpmlHandler, Pathway p) {
		this(gpmlHandler);
		pathway = p;
		convert();
	}
		
	public GpmlConverter(GpmlAttributeHandler gpmlHandler, String gpml) throws ConverterException {
		this(gpmlHandler);
		pathway = new Pathway();
		GpmlFormat.readFromXml(pathway, new StringReader(gpml), true);
		convert();
	}
	
	private void convert() {
		findNodes();
		findEdges();
	}
	
	public Pathway getPathway() {
		return pathway;
	}
	
	private void findNodes() {
		nodeMap.clear();
		for(PathwayElement o : pathway.getDataObjects()) {
			String id = o.getGraphId();
			//Get an id if it's not already there
			if(id == null) {
				id = pathway.getUniqueId();
				o.setGraphId(id);
			}
			//Create a node for every pathway element
			Logger.log.trace("Creating node: " + id);
			CyNode n = Cytoscape.getCyNode(id, true);
			gpmlHandler.addNode(n, o);
			nodeMap.put(o, n);
		}
	}
	
	private void findEdges() {
		edgeMap.clear();
		edges.clear();
		for(PathwayElement o : pathway.getDataObjects()) {
			//no edges between annotations
			int type = o.getObjectType();
			if(type != ObjectType.DATANODE && type != ObjectType.GROUP) {
				continue;
			}
			for(GraphRefContainer r : o.getReferences()) {
				if(r instanceof MPoint) {
					int i = 0;
					PathwayElement l = ((MPoint)r).getParent();
											
					if(r == l.getMEnd()) i = 1;
					String[] str = edgeMap.get(l);
					if(str == null) edgeMap.put(l, str = new String[4]);
					str[i] = o.getGraphId();
					str[2] = l.getStartLineType().toString();
					str[3] = l.getEndLineType().toString();
				}
			}
		}
		
		for(PathwayElement o : edgeMap.keySet()) {
			String[] einfo = edgeMap.get(o);
			if(einfo[0] == null || einfo[1] == null) {
				Logger.log.trace("Incomplete edge found " + einfo[0] + ", " + einfo[1] + "; skipping");
				//Invalid edge, remove
				continue;
			}
			
			Logger.log.trace("Creating edge: " + einfo[0] + " , " + einfo[1]);
			
			//Remove the node that represented the GPML element for this edge
			gpmlHandler.unlinkNode(nodeMap.get(o));
			nodeMap.remove(o);
			
			CyEdge e = Cytoscape.getCyEdge(einfo[0], o.getGraphId(), einfo[1], einfo[2] + ", " + einfo[3]);
			gpmlHandler.addEdge(e, o);
			edges.add(e);
		}
	}
	
	public int[] getNodeIndicesArray() {
		int[] inodes = new int[nodeMap.size()];
		int i = 0;
		for(CyNode n : nodeMap.values()) {
				inodes[i++] = n.getRootGraphIndex();
		}
		return inodes;
	}
	
	public int[] getEdgeIndicesArray() {
		int[] iedges = new int[edges.size()];
		for(int i = 0; i< edges.size(); i++) iedges[i] = edges.get(i).getRootGraphIndex();
		return iedges;
	}
	
	public void layout(GraphView view) {
		gpmlHandler.addAnnotations(view, nodeMap.values());
		gpmlHandler.applyGpmlLayout(view, nodeMap.values());
		gpmlHandler.applyGpmlVisualStyle();
	}
}
