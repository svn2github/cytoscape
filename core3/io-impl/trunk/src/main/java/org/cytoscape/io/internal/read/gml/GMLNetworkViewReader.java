/*
 File: GMLReader.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package org.cytoscape.io.internal.read.gml;

import java.awt.Color;
import java.awt.Paint;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.cytoscape.io.internal.read.VisualStyleBuilder;
import org.cytoscape.io.read.CyNetworkViewReader;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;


/**
 * This class is responsible for converting a gml object tree into cytoscape
 * objects New features to the current version: 1. Small bug fixes. 2. Translate
 * all features in the GML file. This includes 3. New Visual Style will be
 * generated when you call this class. The new style saves all visual features
 * (like node shape) and will not be lost even after other style selected.
 */
public class GMLNetworkViewReader extends AbstractTask implements CyNetworkViewReader {
	/**
	 * The following are all taken to be reserved keywords for gml (note that
	 * not all of them are actually keywords according to the spec)
	 *
	 * Currently, only keywords below are supported by the Visual Style
	 * generation methods.
	 *
	 * (Maybe we need some documents on "cytoscape-style" GML format...)
	 */

	// Graph Tags
	public static String GRAPH = "graph";
	public static String NODE = "node";
	public static String EDGE = "edge";
	public static String GRAPHICS = "graphics";
	public static String LABEL = "label";
	public static String SOURCE = "source";
	public static String TARGET = "target";
	public static String IS_DIRECTED = "directed";
	
	// The following elements are in "graphics" section of GML
	public static String X = "x";
	public static String Y = "y";
	public static String H = "h";
	public static String W = "w";
	public static String TYPE = "type";
	public static String ID = "id";
	public static String ROOT_INDEX = "root_index";

	// Shapes used in Cytoscape (not GML standard)
	// In GML, they are called "type"
	public static String RECTANGLE = "rectangle";
	public static String ELLIPSE = "ellipse";
	public static String LINE = "Line"; // This is the Polyline object.
	                                       // no support for now...
	public static String POINT = "point";
	public static String DIAMOND = "diamond";
	public static String HEXAGON = "hexagon";
	public static String OCTAGON = "octagon";
	public static String PARALELLOGRAM = "parallelogram";
	public static String TRIANGLE = "triangle";

	// Other GML "graphics" attributes
	public static String FILL = "fill";
	public static String WIDTH = "width";
	public static String STRAIGHT_LINES = "line";
	public static String CURVED_LINES = "curved";
	public static String SOURCE_ARROW = "source_arrow";
	public static String TARGET_ARROW = "target_arrow";

	// States of the ends of arrows
	public static String ARROW = "arrow";
	public static String ARROW_NONE = "none";
	public static String ARROW_FIRST = "first";
	public static String ARROW_LAST = "last";
	public static String ARROW_BOTH = "both";
	public static String OUTLINE = "outline";
	public static String OUTLINE_WIDTH = "outline_width";
	public static String DEFAULT_EDGE_INTERACTION = "pp";
	public static String VERSION = "Version";
	public static String CREATOR = "Creator";

	@SuppressWarnings("unused")
	private Color DEF_COLOR = new Color(153, 153, 255);

	private VisualStyleBuilder graphStyle = null;
	
	// Entries in the file
	List<KeyValue> keyVals;

	// Node ID's
	Map<String,CyNode> nodeIDMap;
	List<Integer> nodes;
	List<Integer> sources;
	List<Integer> targets;
	List<Boolean> directionality_flags;
	Vector<String> node_labels;
	Vector<String> edge_labels;
	Vector<KeyValue> edge_root_index_pairs;
	Vector<KeyValue> node_root_index_pairs;
	Vector<CyEdge> edge_names;
	Vector<String> node_names;

	// Name for the new visual style
	String styleName;

	// New Visual Style comverted from GML file.
	//VisualStyle gmlstyle;

	// Hashes for node & edge attributes
	Map<String,Double> nodeW;

	// Hashes for node & edge attributes
	Map<String,Double> nodeH;

	// Hashes for node & edge attributes
//	Map<String,NodeShape> nodeShape;

	// Hashes for node & edge attributes
	Map<String,String> nodeCol;

	// Hashes for node & edge attributes
	Map<String,Double> nodeBWidth;

	// Hashes for node & edge attributes
	Map<String,String> nodeBCol;
	Map<String,String> edgeCol;
	Map<String,Float> edgeWidth;
	Map<String,String> edgeArrow;
	Map<String,String> edgeShape;

	private final InputStream inputStream;
	private final CyNetworkFactory networkFactory;
	private final CyNetworkViewFactory viewFactory;
	private final CyNetworkManager networkManager;
	
	private CyNetwork network;
	private CyNetworkView view;

	/**
	 * Constructor.<br>
	 * This is usually used for remote file loading.
	 * @param networkManager 
	 *
	 * @param is
	 *            Input stream of GML file,
	 *
	 */
	public GMLNetworkViewReader(InputStream inputStream, CyNetworkFactory networkFactory, CyNetworkViewFactory viewFactory, CyNetworkManager networkManager) {
		this.inputStream = inputStream;
		this.networkFactory = networkFactory;
		this.viewFactory = viewFactory;
		this.networkManager = networkManager;
		
		// Set new style name
		edge_names = new Vector<CyEdge>();
		node_names = new Vector<String>();
		// TODO fix name
		graphStyle = new VisualStyleBuilder("fixme", false);
		graphStyle.setNodeSizeLocked(false);

	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		try {
			keyVals = (new GMLParser(inputStream)).parseList();
		} catch (Exception io) {
			io.printStackTrace();
			throw new RuntimeException(io.getMessage());
		}

		initializeStructures();

		readGML(keyVals, taskMonitor); // read the GML file
		
		network = networkFactory.getInstance();
		view = viewFactory.getNetworkView(network);
		createGraph(taskMonitor); // create the graph AND new visual style

		//
		// New features are called here:
		// 1 Extract (virtually) all attributes from the GML file
		// 2 Generate new VS
		// 3 Apply the new VS to the current window of Cytoscape
		//
		// Extract node & edge attributes
		extract(); 

		releaseStructures();
		
		layout(view);
	}

	@Override
	public void cancel() {
	}

	@Override
	public CyNetworkView[] getNetworkViews() {
		return new CyNetworkView[] { view };
	}

	@Override
	public VisualStyle[] getVisualStyles() {
		return null;
	}

	protected void initializeStructures() {
		nodes = new ArrayList<Integer>();
		sources = new ArrayList<Integer>();
		targets = new ArrayList<Integer>();
		directionality_flags = new ArrayList<Boolean>();
		node_labels = new Vector<String>();
		edge_labels = new Vector<String>();
		edge_root_index_pairs = new Vector<KeyValue>();
		node_root_index_pairs = new Vector<KeyValue>();
	}

	protected void releaseStructures() {
		nodes = null;
		sources = null;
		targets = null;
		directionality_flags = null;
		node_labels = null;
		edge_labels = null;
		edge_root_index_pairs = null;
		node_root_index_pairs = null;
	}

	/**
	 * This will create the graph model objects. This function expects node
	 * labels to be unique and edge labels to be unique between a particular
	 * source and target If this condition is not met, an error will be printed
	 * to the console, and the object will be skipped. That is, it is as though
	 * that particular object never existed in the gml file. If an edge depends
	 * on a node that was skipped, then that edge will be skipped as well.
	 */
	protected void createGraph(TaskMonitor taskMonitor) {
		
		nodeIDMap = new HashMap<String,CyNode>(nodes.size());

		Map<Integer,Integer> gml_id2order = new HashMap<Integer,Integer>(nodes.size());
		Set<String> nodeNameSet = new HashSet<String>(nodes.size());

		// Add All Nodes to Network
		for (int idx = 0; idx < nodes.size(); idx++) {
			// Report Status Value
			if (taskMonitor != null) {
				// TODO: set proper number
				//taskMonitor.setPercentCompleted(percentUtil.getGlobalPercent(2, idx, nodes.size()));
			}

			String label = node_labels.get(idx);

			if (nodeNameSet.add(label)) {
				CyNode node = network.addNode();
				node.getCyRow().set("name",label);

				nodeIDMap.put(label, node);
				gml_id2order.put(nodes.get(idx), idx);
				node_root_index_pairs.get(idx).value = Integer.valueOf(node.getIndex());
			} else {
				throw new RuntimeException("GML id " + nodes.get(idx) + " has a duplicated label: " + label);
			}
		}

		nodeNameSet = null;

		Set<String> edgeNameSet = new HashSet<String>(sources.size());

		// Add All Edges to Network
		for (int idx = 0; idx < sources.size(); idx++) {
			// Report Status Value
			if (taskMonitor != null) {
				// TODO: set proper number
				//taskMonitor.setPercentCompleted(percentUtil.getGlobalPercent(3, idx, sources.size()));
			}

			if (gml_id2order.containsKey(sources.get(idx)) && gml_id2order.containsKey(targets.get(idx))) {
				String label = edge_labels.get(idx);
				String sourceName = node_labels.get(gml_id2order.get(sources.get(idx)));
				String targetName = node_labels.get(gml_id2order.get(targets.get(idx)));
				String edgeName = sourceName + " (" + label + ") " + targetName;
				Boolean isDirected = directionality_flags.get(idx);
				
				int duplicate_count = 1;

				while (!edgeNameSet.add(edgeName)) {
					edgeName = sourceName + " (" + label + ") " + targetName + "_" + duplicate_count;
					duplicate_count += 1;
				}


				CyNode node_1 = nodeIDMap.get( sourceName );
				CyNode node_2 = nodeIDMap.get( targetName ); 
				CyEdge edge = network.addEdge(node_1, node_2, isDirected.booleanValue());
				edge.getCyRow().set("name", edgeName);
				edge.getCyRow().set("interaction", label);
				edge_names.add(idx, edge);

				edge_root_index_pairs.get(idx).value = Integer.valueOf(edge.getIndex());
			} else {
				throw new RuntimeException("Non-existant source/target node for edge with gml (source,target): "
				                       + sources.get(idx) + "," + targets.get(idx));
			}
		}

		edgeNameSet = null;
	}

	/**
	 * This function takes the root level list which defines a gml objec tree
	 */
	@SuppressWarnings("unchecked")
	protected void readGML(List<KeyValue> list, TaskMonitor taskMonitor) {
		// Report Progress Message
		int counter = 0;

		for (KeyValue keyVal : list) {
			// Report Progress Value
			if (taskMonitor != null) {
				//TODO: set proper number
				//taskMonitor.setPercentCompleted(percentUtil.getGlobalPercent(1, counter, list.size()));
				counter++;
			}

			if (keyVal.key.equals(GRAPH)) {
				readGraph((List<KeyValue>) keyVal.value);
			}
		}
	}

	/**
	 * This function takes in a list which was given as the value to a "graph"
	 * key underneath the main gml list
	 */
	@SuppressWarnings("unchecked") // KeyValue.value cast
	protected void readGraph(List<KeyValue> list) {
		for (KeyValue keyVal : list) {
			if (keyVal.key.equals(NODE)) {
				readNode((List<KeyValue>) keyVal.value);
			}

			if (keyVal.key.equals(EDGE)) {
				readEdge((List<KeyValue>) keyVal.value);
			}
		}
	}

	/**
	 * This will extract the model information from the list which is matched a
	 * "node" key
	 */
	protected void readNode(List<KeyValue> list) {
		String label = "";
		boolean contains_id = false;
		int id = 0;
		KeyValue root_index_pair = null;

		for (KeyValue keyVal : list) {
			if (keyVal.key.equals(ID)) {
				contains_id = true;
				id = ((Integer) keyVal.value).intValue();
			} else if (keyVal.key.equals(LABEL)) {
				label = (String) keyVal.value;
			} else if (keyVal.key.equals(ROOT_INDEX)) {
				root_index_pair = keyVal;
			}
		}

		if (label.equals("") || label.matches("\\s+")) {
			label = String.valueOf(id);
		}

		if (root_index_pair == null) {
			root_index_pair = new KeyValue(ROOT_INDEX, null);
			list.add(root_index_pair);
		}

		if (!contains_id) {
			StringWriter stringWriter = new StringWriter();

			try {
				GMLParser.printList(list, stringWriter);
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}

			throw new RuntimeException("The node-associated list\n" + stringWriter
			                       + "is missing an id field");
		} else {
			node_root_index_pairs.add(root_index_pair);
			nodes.add(id);
			node_labels.add(label);
			node_names.add(label);
		}
	}

	/**
	 * This will extract the model information from the list which is matched to
	 * an "edge" key.
	 */
	protected void readEdge(List<KeyValue> list) {
		String label = DEFAULT_EDGE_INTERACTION;
		boolean contains_source = false;
		boolean contains_target = false;
		Boolean isDirected = Boolean.TRUE; // use pre-3.0 cytoscape's as default
		int source = 0;
		int target = 0;
		KeyValue root_index_pair = null;

		for (KeyValue keyVal : list) {
			if (keyVal.key.equals(SOURCE)) {
				contains_source = true;
				source = ((Integer) keyVal.value).intValue();
			} else if (keyVal.key.equals(TARGET)) {
				contains_target = true;
				target = ((Integer) keyVal.value).intValue();
			} else if (keyVal.key.equals(LABEL)) {
				label = (String) keyVal.value;
			} else if (keyVal.key.equals(ROOT_INDEX)) {
				root_index_pair = keyVal;
			} else if (keyVal.key.equals(IS_DIRECTED)) {
				if (((Integer)keyVal.value) == 1){
					isDirected = Boolean.FALSE;
				} else {
					isDirected = Boolean.TRUE;
				}
			}
		}

		if (root_index_pair == null) {
			root_index_pair = new KeyValue(ROOT_INDEX, null);
			list.add(root_index_pair);
		}

		if (!contains_source || !contains_target) {
			StringWriter stringWriter = new StringWriter();

			try {
				GMLParser.printList(list, stringWriter);
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}

			throw new RuntimeException("The edge-associated list\n" + stringWriter
			                       + " is missing a source or target key");
		} else {
			sources.add(source);
			targets.add(target);
			directionality_flags.add(isDirected);
			
			edge_labels.add(label);
			edge_root_index_pairs.add(root_index_pair);
		}
	}

	/**
	 * layout the graph based on the GML values we read
	 *
	 * @param myView the view of the network we want to layout
	 */
	@SuppressWarnings("unchecked")
	public void layout(CyNetworkView myView) {
		if ((myView == null) || (network.getNodeCount() == 0)) {
			return;
		}

		if (keyVals == null) {
			throw new RuntimeException("Failed to read gml file on initialization");
		}

		for (KeyValue keyVal : keyVals) {
			if (keyVal.key.equals(GRAPH)) {
				layoutGraph(myView, (List<KeyValue>) keyVal.value);
			}
		}
	}

	//
	/**
	 *  DOCUMENT ME!
	 */
	@SuppressWarnings("unchecked")
	public void extract() {
		if (keyVals == null) {
			throw new RuntimeException("Failed to read gml file on initialization");
		}

		for (KeyValue keyVal : keyVals) {
			if (keyVal.key.equals(GRAPH)) {
				extractGraph((List<KeyValue>) keyVal.value);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void extractGraph(List<KeyValue> list) {
		CyEdge edge = null;

		// Count the current edge
		int ePtr = 0;

		for (KeyValue keyVal : list) {
			if (keyVal.key.equals(NODE)) {
				extractNode((List<KeyValue>) keyVal.value);
			} else if (keyVal.key.equals(EDGE)) {
				edge = edge_names.get(ePtr);
				ePtr++;
				extractEdge((List<KeyValue>) keyVal.value, edge);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void extractNode(List<KeyValue> list) {
		List<KeyValue> graphics_list = null;
		String label = null;
		CyNode node = null;

		int tempid = 0;

		for (KeyValue keyVal : list) {
			if (keyVal.key.equals(ROOT_INDEX)) {
				if (keyVal.value == null) {
					return;
				}
			} else if (keyVal.key.equals(GRAPHICS)) {
				graphics_list = (List<KeyValue>) keyVal.value;
			} else if (keyVal.key.equals(LABEL)) {
				label = (String) keyVal.value;
				node = nodeIDMap.get(label);
			} else if (keyVal.key.equals(ID)) {
				tempid = ((Integer) keyVal.value).intValue();
			}
		}

		if (graphics_list != null) {
			if (node == null) {
				System.out.println("ERROR: node is missing for node ID: " + tempid);
				return;
			}

			extractNodeAttributes(graphics_list, node);
		}
	}

	@SuppressWarnings("unchecked")
	private void extractEdge(List<KeyValue> list, CyEdge edge) {
		List<KeyValue> graphics_list = null;

		for (KeyValue keyVal : list) {
			if (keyVal.key.equals(ROOT_INDEX)) {
				if (keyVal.value == null) {
					return;
				}
			} else if (keyVal.key.equals(GRAPHICS)) {
				graphics_list = (List<KeyValue>) keyVal.value;
			}
		}

		if (graphics_list != null) {
			extractEdgeAttributes(graphics_list, edge);
		}
	}

	/**
	 * Lays Out the Graph, based on GML.
	 */
	@SuppressWarnings("unchecked") // KeyValue.value cast
	private void layoutGraph(final CyNetworkView myView, List<KeyValue> list) {
		CyEdge edge = null;

		// Count the current edge
		int ePtr = 0;

		for (KeyValue keyVal : list) {
			if (keyVal.key.equals(NODE)) {
				layoutNode(myView, (List<KeyValue>) keyVal.value);
			} else if (keyVal.key.equals(EDGE)) {
				edge = edge_names.get(ePtr);
				ePtr++;
				layoutEdge(myView, (List<KeyValue>) keyVal.value, edge);
			}
		}
	}

	/**
	 * Assign node properties based on the values in the list matched to the
	 * "node" key. Mostly just a wrapper around layoutNodeGraphics
	 */
	@SuppressWarnings("unchecked")
	private void layoutNode(CyNetworkView myView, List<KeyValue> list) {
		Integer root_index = null;
		List<KeyValue> graphics_list = null;
		@SuppressWarnings("unused")
		String label = null;
		@SuppressWarnings("unused")
		int tempid = 0;

		for (KeyValue keyVal : list) {
			if (keyVal.key.equals(ROOT_INDEX)) {
				/*
				 * For some reason we didn't make an object for this node give
				 * up now
				 */
				if (keyVal.value == null) {
					return;
				}

				root_index = (Integer) keyVal.value;
			} else if (keyVal.key.equals(GRAPHICS)) {
				graphics_list = (List<KeyValue>) keyVal.value;
			} else if (keyVal.key.equals(LABEL)) {
				label = (String) keyVal.value;
			} else if (keyVal.key.equals(ID)) {
				tempid = ((Integer) keyVal.value).intValue();
			}
		}

		// System.out.print( "In layout, Root index is: " + root_index );
		// System.out.print( " Checking label: " + label );
		View<CyNode> view = myView.getNodeView(network.getNode(root_index.intValue()));

// TODO update for new view
//		if (label != null) {
//			view.getLabel().setText(label);
//		} else {
//			view.getLabel().setText("node(" + tempid + ")");
//		}

		if (graphics_list != null && view != null) {
			layoutNodeGraphics(myView, graphics_list, view);

		}
	}

	/**
	 * This will assign node graphic properties based on the values in the list
	 * matches to the "graphics" key word
	 */
	private void layoutNodeGraphics(CyNetworkView myView, List<KeyValue> list, View<CyNode> nodeView) {
		RenderingEngine<CyNetwork> engine = networkManager.getCurrentRenderingEngine();
		if (engine == null) {
			// TODO: Remove this once CyNetworkManager can provide an engine
			//       instance with zero networks loaded.
			return;
		}
		VisualLexicon lexicon = engine.getVisualLexicon();
		
		Collection<VisualProperty<?>> properties = lexicon.getAllDescendants(TwoDVisualLexicon.NODE);
		for (VisualProperty<?> property : properties) {
			String id = property.getIdString();
			
			for (KeyValue keyVal : list) {
				if (keyVal.key.equals(X) && id.equals("NODE_X_LOCATION")) {
					nodeView.setVisualProperty(property, (Object) asDouble(keyVal.value));
				} else if (keyVal.key.equals(Y) && id.equals("NODE_Y_LOCATION")) {
					nodeView.setVisualProperty(property, (Object) asDouble(keyVal.value));
				} else if (keyVal.key.equals(W) && id.equals("NODE_X_SIZE")) {
					nodeView.setVisualProperty(property, (Object) asDouble(keyVal.value));
				} else if (keyVal.key.equals(H) && id.equals("NODE_Y_SIZE")) {
					nodeView.setVisualProperty(property, (Object) asDouble(keyVal.value));
				} else if (keyVal.key.equals(FILL) && id.equals("NODE_COLOR")) {
					nodeView.setVisualProperty(property, (Object) asPaint(keyVal.value));
				} else if (keyVal.key.equals(OUTLINE) && id.equals("NODE_BORDER_PAINT")) {
					nodeView.setVisualProperty(property, (Object) asPaint(keyVal.value));
				} else if (keyVal.key.equals(OUTLINE_WIDTH) && id.equals("NODE_BORDER_WIDTH")) {
					nodeView.setVisualProperty(property, (Object) asDouble(keyVal.value));
				}
			}
		}
//			} else if (keyVal.key.equals(TYPE)) {
//				String type = (String) keyVal.value;
//
//				if (type.equals(ELLIPSE)) {
//					nodeView.setShape(NodeView.ELLIPSE);
//				} else if (type.equals(RECTANGLE)) {
//					nodeView.setShape(NodeView.RECTANGLE);
//				} else if (type.equals(DIAMOND)) {
//					nodeView.setShape(NodeView.DIAMOND);
//				} else if (type.equals(HEXAGON)) {
//					nodeView.setShape(NodeView.HEXAGON);
//				} else if (type.equals(OCTAGON)) {
//					nodeView.setShape(NodeView.OCTAGON);
//				} else if (type.equals(PARALELLOGRAM)) {
//					nodeView.setShape(NodeView.PARALELLOGRAM);
//				} else if (type.equals(TRIANGLE)) {
//					nodeView.setShape(NodeView.TRIANGLE);
//				}
	}

	private Paint asPaint(Object object) {
		if (object instanceof String) {
			return getColor((String) object);
		}
		return null;
	}

	private Double asDouble(Object object) {
		if (object instanceof Number) {
			return ((Number) object).doubleValue();
		}
		return null;
	}
	
	//
	// Extract node attributes from GML file
	private void extractNodeAttributes(List<KeyValue> list, CyNode node) {
		// Put all attributes into hashes.
		// Key is the node name
		// (Assume we do not have duplicate node name.)
		/*
		CyRow attrs = node.attrs();
		for (Iterator it = list.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();

			if (keyVal.key.equals(X) || keyVal.key.equals(Y)) {
				// Do nothing.
			} else if (keyVal.key.equals(H)) {
				graphStyle.addProperty(attrs, VisualPropertyType.NODE_HEIGHT, ""+keyVal.value);
			} else if (keyVal.key.equals(W)) {
				graphStyle.addProperty(attrs, VisualPropertyType.NODE_WIDTH, ""+keyVal.value);
			} else if (keyVal.key.equals(FILL)) {
				graphStyle.addProperty(attrs, VisualPropertyType.NODE_FILL_COLOR, ""+keyVal.value);
			} else if (keyVal.key.equals(OUTLINE)) {
				graphStyle.addProperty(attrs, VisualPropertyType.NODE_BORDER_COLOR, ""+keyVal.value);
			} else if (keyVal.key.equals(WIDTH)) {
				graphStyle.addProperty(attrs, VisualPropertyType.NODE_LINE_WIDTH, ""+keyVal.value);
			} else if (keyVal.key.equals(TYPE)) {
				String type = (String) keyVal.value;
				graphStyle.addProperty(attrs, VisualPropertyType.NODE_SHAPE,type);
			}
		}
		*/
	}


	//
	// Extract edge attributes from GML input
	//
	private void extractEdgeAttributes(List<KeyValue> list, CyEdge edge) {
/*
		String value = null;
		boolean isArrow = false;
		String edgeFill = DEF_COLOR.toString();
		String arrowShape = ARROW_NONE;
		CyRow attrs = edge.attrs();
		
		for (Iterator it = list.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();

			if (keyVal.key.equals(LINE)) {
				// This represents "Polyline," which is a line (usually an edge)
				// with arbitrary number of anchors.
				// Current version of CS does not support this, so ignore this
				// at this point of time...
			} else if (keyVal.key.equals(WIDTH)) {
				graphStyle.addProperty(attrs, VisualPropertyType.EDGE_LINE_WIDTH, new String(keyVal.value.toString()));
			} else if (keyVal.key.equals(FILL)) {
				graphStyle.addProperty(attrs, VisualPropertyType.EDGE_COLOR, new String(keyVal.value.toString()));
				edgeFill = keyVal.value.toString();
			} else if (keyVal.key.equals(ARROW)) {
				isArrow = true;
				ArrowShape shape = ArrowShape.ARROW;
				String arrowName = shape.getName();

				if (keyVal.value.toString().equalsIgnoreCase(ARROW_FIRST)) {
					arrowShape = ARROW_FIRST;
					graphStyle.addProperty(attrs, VisualPropertyType.EDGE_SRCARROW_SHAPE, arrowName);
				}				
				else if (keyVal.value.toString().equalsIgnoreCase(ARROW_LAST)) {
					arrowShape = ARROW_LAST;
					graphStyle.addProperty(attrs, VisualPropertyType.EDGE_TGTARROW_SHAPE, arrowName);	
				}
				else if (keyVal.value.toString().equalsIgnoreCase(ARROW_BOTH)) {
					arrowShape = ARROW_BOTH;
					graphStyle.addProperty(attrs, VisualPropertyType.EDGE_SRCARROW_SHAPE, arrowName);
					graphStyle.addProperty(attrs, VisualPropertyType.EDGE_TGTARROW_SHAPE, arrowName);		
				}
				else {// none
				}
				
			} else if (keyVal.key.equals(TYPE)) {
				value = (String) keyVal.value;

				if (value.equals(STRAIGHT_LINES)) {
					// edgeShape.put(edgeName, (String) keyVal.value);
				} else if (value.equals(CURVED_LINES)) {
					// edgeView.setLineType(EdgeView.CURVED_LINES);
				}
			} else if (keyVal.value.equals(SOURCE_ARROW)) {
				// edgeView.setSourceEdgeEnd(((Number)keyVal.value).intValue());
			} else if (keyVal.value.equals(TARGET_ARROW)) {
				// edgeView.setTargetEdgeEnd(((Number)keyVal.value).intValue());
			}
		}
		
		// make the arrow color the same as edge
		if (isArrow) {
			if (arrowShape.equals(ARROW_FIRST)) {
				graphStyle.addProperty(attrs, VisualPropertyType.EDGE_SRCARROW_COLOR, edgeFill);				
			}
			else if (arrowShape.equals(ARROW_LAST)) {
				graphStyle.addProperty(attrs, VisualPropertyType.EDGE_TGTARROW_COLOR, edgeFill);
			}
			else if (arrowShape.equals(ARROW_BOTH)) {
				graphStyle.addProperty(attrs, VisualPropertyType.EDGE_SRCARROW_COLOR, edgeFill);
				graphStyle.addProperty(attrs, VisualPropertyType.EDGE_TGTARROW_COLOR, edgeFill);
			}
		}
		*/
	}

	/**
	 * Assign edge visual properties based on pairs in the list matched to the
	 * "edge" key world
	 */
	@SuppressWarnings("unchecked") // KeyValue.value cast
	private void layoutEdge(CyNetworkView myView, List<KeyValue> list, CyEdge edge) {
		View<CyEdge> edgeView = null;
		List<KeyValue> graphics_list = null;

		for (KeyValue keyVal : list) {
			if (keyVal.key.equals(ROOT_INDEX)) {
				/*
				 * Previously, we didn't make an object for this edge for some
				 * reason. Don't try to go any further.
				 */
				if (keyVal.value == null) {
					return;
				}

				edgeView = myView.getEdgeView(network.getEdge(((Integer) keyVal.value).intValue()));
			} else if (keyVal.key.equals(GRAPHICS)) {
				graphics_list = (List<KeyValue>) keyVal.value;
			}
		}

		if ((edgeView != null) && (graphics_list != null)) {
			layoutEdgeGraphics(myView, graphics_list, edgeView);

		}
	}

	/**
	 * Assign edge graphics properties
	 */

	// Bug fix by Kei
	// Some of the conditions used "value."
	// They should be key.
	// Now this method correctly translate the GML input file
	// into graphics.
	//
	@SuppressWarnings("unchecked")
	private void layoutEdgeGraphics(CyNetworkView myView, List<KeyValue> list, View<CyEdge> edgeView) {
		RenderingEngine<CyNetwork> engine = networkManager.getCurrentRenderingEngine();
		if (engine == null) {
			// TODO: Remove this once CyNetworkManager can provide an engine
			//       instance with zero networks loaded.
			return;
		}
		VisualLexicon lexicon = engine.getVisualLexicon();
		Collection<VisualProperty<?>> properties = lexicon.getAllDescendants(TwoDVisualLexicon.NODE);
		for (VisualProperty<?> property : properties) {
			String id = property.getIdString();
			
			for (KeyValue keyVal : list) {
				// This is a polyline obj. However, it will be translated into
				// straight line.
				if (keyVal.key.equals(LINE)) {
					layoutEdgeGraphicsLine(myView, (List<KeyValue>) keyVal.value, edgeView);
				} else if (keyVal.key.equals(WIDTH) && id.equals("EDGE_WIDTH")) {
					edgeView.setVisualProperty(property, (Object) asDouble(keyVal.value));
				} else if (keyVal.key.equals(FILL) && id.equals("EDGE_COLOR")) {
					edgeView.setVisualProperty(property, (Object) asPaint(keyVal.value));
				}
			}
		}
		
//			} else if (keyVal.key.equals(TYPE)) {
//				String value = (String) keyVal.value;
//
//				if (value.equals(STRAIGHT_LINES)) {
//					edgeView.setLineType(EdgeView.STRAIGHT_LINES);
//				} else if (value.equals(CURVED_LINES)) {
//					edgeView.setLineType(EdgeView.CURVED_LINES);
//				}
//			} else if (keyVal.key.equals(ARROW)) {
//				// The position of the arrow.
//				// There are 4 states: no arrows, both ends have arrows, source,
//				// or target.
//				//
//				// The arrow type below is hard-coded since GML does not
//				// support shape of the arrow.
//				if (keyVal.value.equals(ARROW_FIRST)) {
//					edgeView.setSourceEdgeEnd(2);
//				} else if (keyVal.value.equals(ARROW_LAST)) {
//					edgeView.setTargetEdgeEnd(2);
//				} else if (keyVal.value.equals(ARROW_BOTH)) {
//					edgeView.setSourceEdgeEnd(2);
//					edgeView.setTargetEdgeEnd(2);
//				} else if (keyVal.value.equals(ARROW_NONE)) {
//					// Do nothing. No arrows.
//				}
//
//				if (keyVal.key.equals(SOURCE_ARROW)) {
//					edgeView.setSourceEdgeEnd(((Number) keyVal.value).intValue());
//				} else if (keyVal.value.equals(TARGET_ARROW)) {
//					edgeView.setTargetEdgeEnd(((Number) keyVal.value).intValue());
//				}
	}

	/**
	 * Assign bend points based on the contents of the list associated with a
	 * "Line" key We make sure that there is both an x,y present in the
	 * underlying point list before trying to generate a bend point
	 */
	private void layoutEdgeGraphicsLine(CyNetworkView myView, List<KeyValue> list, View<CyEdge> edgeView) {
	/*
		for (Iterator it = list.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();

			if (keyVal.key.equals(POINT)) {
				Number x = null;
				Number y = null;

				for (Iterator pointIt = ((List) keyVal.value).iterator(); pointIt.hasNext();) {
					KeyValue pointVal = (KeyValue) pointIt.next();

					if (pointVal.key.equals(X)) {
						x = (Number) pointVal.value;
					} else if (pointVal.key.equals(Y)) {
						y = (Number) pointVal.value;
					}
				}

				if (!((x == null) || (y == null))) {
					Point2D.Double pt = new Point2D.Double(x.doubleValue(), y.doubleValue());
					edgeView.getBend().addHandle(pt);
				}
			}
		}
		*/
	}

	/**
	 * Create a color object from the string like it is stored in a gml file
	 */
	private Color getColor(String colorString) {
		return new Color(Integer.parseInt(colorString.substring(1), 16));
	}
}
