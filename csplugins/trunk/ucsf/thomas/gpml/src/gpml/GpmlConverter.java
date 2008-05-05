package gpml;

import giny.view.GraphView;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.pathvisio.debug.Logger;
import org.pathvisio.model.ConverterException;
import org.pathvisio.model.GpmlFormat;
import org.pathvisio.model.GroupStyle;
import org.pathvisio.model.ObjectType;
import org.pathvisio.model.Pathway;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.GraphLink.GraphRefContainer;
import org.pathvisio.model.PathwayElement.MAnchor;
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
		edgeMap.clear();
		edges.clear();
		nodeMap.clear();
		
		findNodes();
		findEdges();
	}
	
	public Pathway getPathway() {
		return pathway;
	}
	
	private void findNodes() {
		for(PathwayElement o : pathway.getDataObjects()) {
			int type = o.getObjectType();
			if(type == ObjectType.LEGEND || type == ObjectType.INFOBOX || type == ObjectType.MAPPINFO) {
				continue;
			}
			String id = o.getGraphId();
			//Get an id if it's not already there
			if(id == null) {
				id = pathway.getUniqueGraphId();
				o.setGraphId(id);
			}
			CyNode n = null;
			if(type == ObjectType.GROUP) {
				Logger.log.trace("Creating group: " + id);
				n = addGroup(o);
				if(n == null) {
					Logger.log.error("Group node is null");
				} else {
					Logger.log.trace("Created group node: " + n.getIdentifier());
				}
			} else if(type == ObjectType.LINE) {
				//Process anchors
				for(MAnchor a : o.getMAnchors()) {
					Logger.log.trace("Creating anchor: " + id);
					addAnchor(a);
				}
				Logger.log.trace("Creating node: " + id + " for " + o.getGraphId() + "@" + o.getObjectType());
				n = Cytoscape.getCyNode(id, true);
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
		Logger.log.trace("Start finding edges");
		for(PathwayElement o : pathway.getDataObjects()) {
			System.out.println("\tProcessing " + o.getGraphId());
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
			String graphId = o.getGraphId();
			if(graphId == null) {
				graphId = o.getParent().getUniqueGraphId();
				o.setGraphId(graphId);
			}
			CyEdge e = Cytoscape.getCyEdge(einfo[0], graphId, einfo[1], einfo[2] + ", " + einfo[3]);
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
		CyGroup cyGroup = CyGroupManager.findGroup(group.getGroupId());
		if(cyGroup == null) {
			cyGroup = CyGroupManager.createGroup(group.getGroupId(), null);
		}
		CyNode gn = cyGroup.getGroupNode();
		gn.setIdentifier(group.getGraphId());
		return gn;
	}
	
	private CyNode addAnchor(MAnchor anchor) {
		return Cytoscape.getCyNode(anchor.getGraphId(), true);
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
				
				//The interaction name
				GroupStyle groupStyle = pwElm.getGroupStyle();
				String interaction = groupStyle.name();
				if(groupStyle == GroupStyle.NONE) {
					interaction = "group";
				}
				
				PathwayElement[] groupElements = pathway.getGroupElements(
						pwElm.getGroupId()
					).toArray(new PathwayElement[0]);

				//Create the cytoscape parts of the group
				for(int i = 0; i < groupElements.length; i++) {
					PathwayElement pe_i = groupElements[i];
					GpmlNetworkElement<?> ne_i = gpmlHandler.getNetworkElement(pe_i.getGraphId());
					//Only add links to nodes, not to annotations
					if(ne_i instanceof GpmlNode) {
						cyGroup.addNode(((GpmlNode)ne_i).getParent());

						//Add links between all elements of the group
						for(int j = i + 1; j < groupElements.length; j++) {
							PathwayElement pe_j = groupElements[j];
							GpmlNetworkElement<?> ne_j = gpmlHandler.getNetworkElement(pe_j.getGraphId());
							if(ne_j instanceof GpmlNode) {
								edges.add(Cytoscape.getCyEdge(
										ne_i.getParentIdentifier(), 
										"inGroup: " + cyGroup.getGroupName(),
										ne_j.getParentIdentifier(), interaction)
								);
							}
						}
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
		String viewerName = "metaNode";
		Logger.log.trace(CyGroupManager.getGroupViewers() + "");
		if(CyGroupManager.getGroupViewer(viewerName) != null) {
			setGroupViewer((CyNetworkView)view, viewerName);
		}
		gpmlHandler.addAnnotations(view, nodeMap.values());
		gpmlHandler.applyGpmlLayout(view, nodeMap.values());
		gpmlHandler.applyGpmlVisualStyle();
		view.fitContent();
	}
}
