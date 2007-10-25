package gpml;

import giny.view.GraphView;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.pathvisio.debug.Logger;
import org.pathvisio.model.ConverterException;
import org.pathvisio.model.GpmlFormat;
import org.pathvisio.model.ObjectType;
import org.pathvisio.model.Pathway;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.GraphLink.GraphRefContainer;
import org.pathvisio.model.PathwayElement.MPoint;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.view.CyNetworkView;

public class GpmlConverter {
	List<CyEdge> edges = new ArrayList<CyEdge>();
	
	HashMap<PathwayElement, CyNode> nodeMap = new HashMap<PathwayElement, CyNode>();
	HashMap<PathwayElement, String[]> edgeMap = new HashMap<PathwayElement, String[]>();

	GpmlHandler gpmlHandler;
	Pathway pathway;
		
	private GpmlConverter(GpmlHandler h) {
		gpmlHandler = h;
	}
	
	public GpmlConverter(GpmlHandler gpmlHandler, Pathway p) {
		this(gpmlHandler);
		pathway = p;
		convert();
	}
		
	public GpmlConverter(GpmlHandler gpmlHandler, String gpml) throws ConverterException {
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
			int type = o.getObjectType();
			if(type == ObjectType.LEGEND || type == ObjectType.INFOBOX || type == ObjectType.MAPPINFO) {
				continue;
			}
			String id = o.getGraphId();
			//Get an id if it's not already there
			if(id == null) {
				id = pathway.getUniqueId();
				o.setGraphId(id);
			}
			CyNode n = null;
			if(type == ObjectType.GROUP) {
				Logger.log.trace("Creating group: " + id);
				n = addGroup(o);
			} else {
				//Create a node for every pathway element
				Logger.log.trace("Creating node: " + id + " for " + o.getGraphId() + "@" + o.getObjectType());
				n = Cytoscape.getCyNode(id, true);
			}
			gpmlHandler.addNode(n, o);
			nodeMap.put(o, n);
		}
		processGroups();
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
			Cytoscape.getRootGraph().removeNode(nodeMap.get(o));		
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
	
	//Add a group node
	private CyNode addGroup(PathwayElement group) {
		CyGroup cyGroup = CyGroupManager.createGroup(group.getGroupId(), null);
		CyNode gn = cyGroup.getGroupNode();
		return gn;
	}
	
	//Add all nodes to the group
	private void processGroups() {
		for(PathwayElement pwElm : pathway.getDataObjects()) {
			if(pwElm.getObjectType() == ObjectType.GROUP) {
				GpmlNode gpmlNode = gpmlHandler.getNode(pwElm.getGraphId());
				CyGroup cyGroup = CyGroupManager.getCyGroup(gpmlNode.getParent());
				if(cyGroup == null) {
					Logger.log.warn("Couldn't create group: CyGroupManager returned null");
					return;
				}
				for(PathwayElement ge : pathway.getGroupElements(pwElm.getGroupId())) {
					GpmlNetworkElement<?> ne = gpmlHandler.getNetworkElement(ge.getGraphId());
					if(ne instanceof GpmlNode) {
						cyGroup.addNode(((GpmlNode)ne).getParent());
					} else if (ne instanceof GpmlEdge) {
						cyGroup.addInnerEdge(((GpmlEdge)ne).getParent());
					} else {
						Logger.log.warn("Can't add network elements of type " + ne + " to group");
					}
				}
			}
		}
	}
	
	private void setGroupViewer(CyNetworkView view, String groupViewer) {
		for(GpmlNode gn : gpmlHandler.getNodes()) {
			if(gn.getPathwayElement().getObjectType() == ObjectType.GROUP) {
				CyGroup group = CyGroupManager.getCyGroup(gn.getParent());
				CyGroupManager.setGroupViewer(group, groupViewer, view, true);
			}
		}
	}
	
	public void layout(GraphView view) {
		gpmlHandler.addAnnotations(view, nodeMap.values());
		gpmlHandler.applyGpmlLayout(view, nodeMap.values());
		gpmlHandler.applyGpmlVisualStyle();
		setGroupViewer((CyNetworkView)view, "namedSelection");
		
		view.fitContent();
	}
}
