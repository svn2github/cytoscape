package gpml;

import giny.view.GraphView;
import giny.view.NodeView;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.pathvisio.model.GpmlFormat;
import org.pathvisio.model.ObjectType;
import org.pathvisio.model.Pathway;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.GraphLink.GraphRefContainer;
import org.pathvisio.model.PathwayElement.MPoint;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.ImportHandler;
import cytoscape.data.readers.AbstractGraphReader;
import cytoscape.data.readers.GraphReader;
import cytoscape.layout.LayoutAdapter;
import cytoscape.layout.LayoutAlgorithm;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.task.TaskMonitor;
import cytoscape.util.CyFileFilter;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;
import ding.view.DingCanvas;

public class GpmlImporter extends CytoscapePlugin {
	static PrintStream out = System.out;
	
    public GpmlImporter() {
        Cytoscape.getImportHandler().addFilter(new GpmlFilter());
    }
        
    void mapGpmlData(CyNetwork cyn, Pathway data) {
    	for(PathwayElement o : data.getDataObjects()) {
    		if(o.getObjectType() == ObjectType.DATANODE) {
    			Cytoscape.getCyNode(o.getTextLabel());
    		}
    	}
    }
    
    class GpmlReader extends AbstractGraphReader {
    	CyAttributes nAttributes = Cytoscape.getNodeAttributes();
    	CyAttributes eAttributes = Cytoscape.getEdgeAttributes();
        Pathway pathway;
        
    	HashMap<PathwayElement, CyNode> nodes = new HashMap<PathwayElement, CyNode>();
    	HashMap<PathwayElement, String[]> edges = new HashMap<PathwayElement, String[]>();
    	
		public GpmlReader(String fileName) {
			super(fileName);
			pathway = new Pathway();
		}

		public void read() throws IOException {
			try {
				pathway.readFromXml(new File(fileName), true);				
			} catch(Exception ex) {
				throw new IOException(ex.getMessage());
			}
			findNodes();
			findEdges();
		}
		
		private void findNodes() {
			for(PathwayElement o : pathway.getDataObjects()) {
				String id = o.getGraphId();
				if(id == null) {
					id = pathway.getUniqueId();
					o.setGraphId(id);
				}
				out.println("Creating node: " + id);
				CyNode n = Cytoscape.getCyNode(id, true);
				transferAttributes(n.getIdentifier(), o, nAttributes);
				nodes.put(o, n);
			}
		}
		
		private void findEdges() {
			for(PathwayElement o : pathway.getDataObjects()) {
				for(GraphRefContainer r : o.getReferences()) {
					if(r instanceof MPoint) {
						int i = 0;
						PathwayElement l = ((MPoint)r).getParent();
						if(r == l.getMEnd()) i = 1;
						String[] str = edges.get(l);
						if(str == null) edges.put(l, str = new String[3]);
						str[i] = o.getGraphId();
						str[2] = l.getLineType().toString();
					}
				}
			}
			
		}
		
		public int[] getNodeIndicesArray() {
			int[] inodes = new int[nodes.size()];
			int i = 0;
			for(CyNode n : nodes.values()) {
					inodes[i++] = n.getRootGraphIndex();
			}
			return inodes;
		}
		
		public int[] getEdgeIndicesArray() {
			ArrayList<CyEdge> realedges = new ArrayList<CyEdge>();
			for(PathwayElement o : edges.keySet()) {
				String[] einfo = edges.get(o);
				if(einfo[0] == null || einfo[1] == null) {
					out.println("Incomplete edge found " + einfo[0] + ", " + einfo[1] + "; skipping");
					continue;
				}
				out.println("Creating edge: " + einfo[0] + " , " + einfo[1]);
				CyEdge e = Cytoscape.getCyEdge(einfo[0], o.getGraphId(), einfo[1], einfo[2]);
				realedges.add(e);
				transferAttributes(e.getIdentifier(), o, eAttributes);
			}
			int[] iedges = new int[realedges.size()];
			for(int i = 0; i<realedges.size(); i++) iedges[i] = realedges.get(i).getRootGraphIndex();
			return iedges;
		}
		
		public void layout(GraphView view) {
			for(PathwayElement o : nodes.keySet()) {
				NodeView nv = view.getNodeView(nodes.get(o));
				nv.setXPosition(mToV(o.getMCenterX()), false);
				nv.setYPosition(mToV(o.getMCenterY()), false);
			}
			for(PathwayElement o : pathway.getDataObjects()) {
				DGraphView dview = (DGraphView) view;
				DingCanvas backgroundLayer = dview.getCanvas(DGraphView.Canvas.BACKGROUND_CANVAS);
				switch(o.getObjectType()) {
				case ObjectType.SHAPE:
					backgroundLayer.add(new Shape(o, dview));
					view.hideGraphObject(view.getNodeView(nodes.get(o)));
					break;
				case ObjectType.LINE:
					backgroundLayer.add(new Line(o, dview));
					break;
				}
			}
			view.updateView();
		}

		public LayoutAlgorithm getLayoutAlgorithm() {
			return new LayoutAdapter() {
				public void doLayout(CyNetworkView networkView, TaskMonitor monitor) {
					layout(networkView);
				}
			};
		}
    }
    
    static void transferAttributes(String id, PathwayElement o, CyAttributes attr) {
    	try {
			Element e = GpmlFormat.createJdomElement(o, Namespace.getNamespace(""));
			attr.setAttribute(id, "GpmlElement", e.getName());
			transferAttributes(id, e, attr, null);
    	} catch(Exception e) {
			out.println("Unable to add attributes for " + o);
			e.printStackTrace();
		}	
    }
    
    static void transferAttributes(String id, Element e, CyAttributes attr, String key) {
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
    
    class GpmlFilter extends CyFileFilter {
    	public GpmlFilter() {
			super("gpml", "GPML file", ImportHandler.GRAPH_NATURE);
		}
    	
    	public GraphReader getReader(String fileName) {
    		return new GpmlReader(fileName);
    	}
    }
    
    static double mToV(double m) {
    	return m * 1.0/15;
    }
}
