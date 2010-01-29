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
package cytoscape.data.readers;

import giny.model.Edge;
import giny.model.GraphObject;
import giny.model.Node;
import giny.view.EdgeView;
import giny.view.GraphView;
import giny.view.NodeView;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntIntHashMap;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.init.CyInitParams;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.LayoutAdapter;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;
import cytoscape.util.FileUtil;
import cytoscape.util.PercentUtil;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.ArrowShape;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;

/**
 * This class is responsible for converting a gml object tree into cytoscape
 * objects New features to the current version: 1. Small bug fixes. 2. Translate
 * all features in the GML file. This includes 3. New Visual Style will be
 * generated when you call this class. The new style saves all visual features
 * (like node shape) and will not be lost even after other style selected.
 */
public class GMLReader extends AbstractGraphReader {
	/**
	 * The following are all taken to be reserved keywords for gml (note that
	 * not all of them are actually keywords according to the spec)
	 * 
	 * Currently, only keywords below are supported by the Visual Style
	 * generation methods.
	 * 
	 * (Maybe we need some documents on "cytoscape-style" GML format...)
	 */

	// Global tags
	protected static final String ID = "id";
	protected static final String NAME = "name";
	protected static final String LABEL = "label";
	protected static final String COMMENT = "comment";
	protected static final String VERSION = "Version";
	protected static final String CREATOR = "Creator";

	// Graph Tags
	protected static final String GRAPH = "graph";
	protected static final String NODE = "node";
	protected static final String EDGE = "edge";
	protected static final String GRAPHICS = "graphics";
	protected static final String SOURCE = "source";
	protected static final String TARGET = "target";

	// The following elements are in "graphics" section of GML
	protected static final String X = "x";
	protected static final String Y = "y";
	protected static final String H = "h";
	protected static final String W = "w";
	protected static final String TYPE = "type";

	protected static final String ROOT_INDEX = "root_index";

	// Shapes used in Cytoscape (not GML standard)
	// In GML, they are called "type"
	protected static final String RECTANGLE = "rectangle";
	protected static final String ELLIPSE = "ellipse";
	protected static final String LINE = "Line"; // This is the Polyline object.
	// no support for now...
	protected static final String POINT = "point";
	protected static final String DIAMOND = "diamond";
	protected static final String HEXAGON = "hexagon";
	protected static final String OCTAGON = "octagon";
	protected static final String PARALELLOGRAM = "parallelogram";
	protected static final String TRIANGLE = "triangle";

	// Other GML "graphics" attributes
	protected static final String FILL = "fill";
	protected static final String WIDTH = "width";
	protected static final String STRAIGHT_LINES = "line";
	protected static final String CURVED_LINES = "curved";
	protected static final String SOURCE_ARROW = "source_arrow";
	protected static final String TARGET_ARROW = "target_arrow";

	// Support for yEd GML dialect
	protected static final String YED_SOURCE_ARROW = "sourceArrow";
	protected static final String YED_TARGET_ARROW = "targetArrow";
	protected static final String YED_DELTA = "delta";
	protected static final String YED_STANDARD = "standard";
	protected static final String YED_DIAMOND = "diamond";
	protected static final String YED_SHORT = "short";
	protected static final String YED_WHITE_DELTA = "white_delta";
	protected static final String YED_WHITE_DIAMOND = "white_diamond";

	// States of the ends of arrows
	protected static final String ARROW = "arrow";
	protected static final String ARROW_NONE = "none";
	protected static final String ARROW_FIRST = "first";
	protected static final String ARROW_LAST = "last";
	protected static final String ARROW_BOTH = "both";
	protected static final String OUTLINE = "outline";
	protected static final String OUTLINE_WIDTH = "outline_width";
	protected static final String DEFAULT_EDGE_INTERACTION = "pp";
	
	private static final String VIZMAP_PREFIX = "vizmap:";

	private static final Color DEF_COLOR = new Color(153, 153, 255);

	private String vsbSwitch = CytoscapeInit.getProperties().getProperty(
			"visualStyleBuilder");
	private VisualStyleBuilder graphStyle = null;

	private static CyLogger logger = CyLogger.getLogger(GMLReader.class);

	// Entries in the file
	private List<KeyValue> keyVals;

	// Node ID's
	OpenIntIntHashMap nodeIDMap;
	IntArrayList nodes;
	IntArrayList sources;
	IntArrayList targets;

	private List<String> nodeLabels;
	private List<String> edgeLabels;

	// Storage for CyAttributes
	private List<Map<String, Object>> nodeAttributes;
	private List<Map<String, Object>> edgeAttributes;

	Vector edge_root_index_pairs;
	Vector node_root_index_pairs;

	private List<String> edgeNames;
	private List<String> nodeNames;

	IntArrayList giny_nodes;
	IntArrayList giny_edges;
	private TaskMonitor taskMonitor;
	private PercentUtil percentUtil;

	// Name for the new visual style
	private String styleName;

	// Hashes for node & edge attributes
	HashMap nodeW;

	// Hashes for node & edge attributes
	HashMap nodeH;
	// Hashes for node & edge attributes
	HashMap<String, NodeShape> nodeShape;

	// Hashes for node & edge attributes
	HashMap nodeCol;
	// Hashes for node & edge attributes
	HashMap<String, Double> nodeBWidth;

	// Hashes for node & edge attributes
	HashMap nodeBCol;
	HashMap edgeCol;
	HashMap<String, Float> edgeWidth;
	HashMap<String, String> edgeArrow;
	HashMap edgeShape;

	// The InputStream
	InputStream inputStream = null;

	/**
	 * Constructor.
	 * 
	 * @param filename
	 *            File name.
	 */
	public GMLReader(final String filename) {
		this(filename, null);
	}

	/**
	 * Constructor.<br>
	 * This is usually used for remote file loading.
	 * 
	 * @param is
	 *            Input stream of GML file,
	 * 
	 */
	public GMLReader(final InputStream is, final String name) {
		super(name);

		// Set new style name
		styleName = createVSName();
		initializeHash();
		initStyle();

		this.inputStream = is;
	}

	/**
	 * Constructor.
	 * 
	 * @param filename
	 *            File name.
	 * @param taskMonitor
	 *            TaskMonitor Object.
	 */
	public GMLReader(final String filename, final TaskMonitor taskMonitor) {
		super(filename);
		inputStream = FileUtil.getInputStream(filename);

		// Set new style name
		styleName = createVSName();
		initializeHash();
		initStyle();

		if (taskMonitor != null) {
			this.taskMonitor = taskMonitor;
			percentUtil = new PercentUtil(5);
		}
	}

	/**
	 * Sets the task monitor we want to use
	 * 
	 * @param monitor
	 *            the TaskMonitor to use
	 */
	public void setTaskMonitor(TaskMonitor monitor) {
		this.taskMonitor = monitor;
		percentUtil = new PercentUtil(3);
	}

	private String createVSName() {
		return getNetworkName();
	}

	private void initializeHash() {
		edgeNames = new ArrayList<String>();
		nodeNames = new ArrayList<String>();
		nodeAttributes = new ArrayList<Map<String, Object>>();
		edgeAttributes = new ArrayList<Map<String, Object>>();
	}

	// Initialize variables for the new style created from GML
	private void initStyle() {
		graphStyle = new VisualStyleBuilder(styleName, false);
		graphStyle.setNodeSizeLocked(false);
	}

	/**
	 * Read GML file contents
	 */
	public void read() {
		try {
			try {
				keyVals = (new GMLParser(inputStream)).parseList();
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		} catch (Exception io) {
			logger.warn("Error reading GML file: " + io.getMessage(), io);

			if (taskMonitor != null)
				taskMonitor.setException(io, io.getMessage());

			throw new RuntimeException(io.getMessage());
		}

		initializeStructures();

		readGML(keyVals); // read the GML file
		createGraph(); // create the graph AND new visual style

		//
		// New features are called here:
		// 1 Extract (virtually) all attributes from the GML file
		// 2 Generate new VS
		// 3 Apply the new VS to the current window of Cytoscape
		//
		extract();

		releaseStructures();
	}

	/**
	 * Returns a list containing the gml object tree
	 */
	public List<KeyValue> getList() {
		return keyVals;
	}

	protected void initializeStructures() {
		nodes = new IntArrayList();
		sources = new IntArrayList();
		targets = new IntArrayList();
		nodeLabels = new ArrayList<String>();
		edgeLabels = new ArrayList<String>();
		edge_root_index_pairs = new Vector();
		node_root_index_pairs = new Vector();
	}

	protected void releaseStructures() {
		nodes = null;
		sources = null;
		targets = null;
		nodeLabels = null;
		edgeLabels = null;
		edge_root_index_pairs = null;
		node_root_index_pairs = null;
	}

	private void mapAttributes(final GraphObject obj,
			final CyAttributes attrs, final Map<String, Object> attrMap) {

		for (String attrName : attrMap.keySet()) {
			final Object attrVal = attrMap.get(attrName);
			if (attrVal == null)
				continue;

			try {
				if (attrVal instanceof Double) {
					attrs.setAttribute(obj.getIdentifier(), attrName,
							(Double) attrVal);
				} else if (attrVal instanceof Integer) {
					attrs.setAttribute(obj.getIdentifier(), attrName,
							(Integer) attrVal);
				} else {
					attrs.setAttribute(obj.getIdentifier(), attrName,
							attrVal.toString());
				}
			} catch (IllegalArgumentException e) {
				continue;
			}

		}
	}

	/**
	 * This will create the graph model objects. This function expects node
	 * labels to be unique and edge labels to be unique between a particular
	 * source and target If this condition is not met, an error will be printed
	 * to the console, and the object will be skipped. That is, it is as though
	 * that particular object never existed in the gml file. If an edge depends
	 * on a node that was skipped, then that edge will be skipped as well.
	 */
	protected void createGraph() {
		Cytoscape.ensureCapacity(nodes.size(), sources.size());
		nodeIDMap = new OpenIntIntHashMap(nodes.size());
		giny_nodes = new IntArrayList(nodes.size());

		OpenIntIntHashMap gml_id2order = new OpenIntIntHashMap(nodes.size());
		Set<String> nodeNameSet = new HashSet<String>(nodes.size());

		final CyAttributes nodeAttr = Cytoscape.getNodeAttributes();

		// Add All Nodes to Network
		for (int idx = 0; idx < nodes.size(); idx++) {
			// Report Status Value
			if (taskMonitor != null)
				taskMonitor.setPercentCompleted(percentUtil.getGlobalPercent(2,
						idx, nodes.size()));

			final String label = nodeLabels.get(idx);

			Node node;

			if (nodeNameSet.add(label)) {
				node = Cytoscape.getCyNode(label, true);
				if (nodeLabels.get(idx) != null)
					nodeAttr.setAttribute(node.getIdentifier(), LABEL,
							nodeLabels.get(idx));
				if (nodeNames.get(idx) != null)
					nodeAttr.setAttribute(node.getIdentifier(), NAME, nodeNames
							.get(idx));

				// Map attributes
				mapAttributes(node, nodeAttr, nodeAttributes.get(idx));

				giny_nodes.add(node.getRootGraphIndex());
				nodeIDMap.put(nodes.get(idx), node.getRootGraphIndex());
				gml_id2order.put(nodes.get(idx), idx);
				((KeyValue) node_root_index_pairs.get(idx)).value = (new Integer(
						node.getRootGraphIndex()));
			} else {
				throw new GMLException("GML id " + nodes.get(idx)
						+ " has a duplicated ID: " + label);
			}
		}

		nodeNameSet = null;

		giny_edges = new IntArrayList(sources.size());

		Set edgeNameSet = new HashSet(sources.size());

		final CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();

		// Add All Edges to Network
		for (int idx = 0; idx < sources.size(); idx++) {
			// Report Status Value
			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted(percentUtil.getGlobalPercent(3,
						idx, sources.size()));
			}

			if (gml_id2order.containsKey(sources.get(idx))
					&& gml_id2order.containsKey(targets.get(idx))) {
				String label = (String) edgeLabels.get(idx);
				String sourceName = (String) nodeLabels.get(gml_id2order
						.get(sources.get(idx)));
				String targetName = (String) nodeLabels.get(gml_id2order
						.get(targets.get(idx)));
				String edgeName = CyEdge.createIdentifier(sourceName, label,
						targetName);

				int duplicate_count = 1;

				while (!edgeNameSet.add(edgeName)) {
					edgeName = CyEdge.createIdentifier(sourceName, label,
							targetName)
							+ "_" + duplicate_count;

					duplicate_count += 1;
				}

				// String tempstr = "E name is :" + idx + "==" + edgeName;
				edgeNames.add(idx, edgeName);

				Edge edge = Cytoscape.getRootGraph().getEdge(edgeName);

				if (edge == null) {
					Node node_1 = Cytoscape.getCyNode(sourceName);
					Node node_2 = Cytoscape.getCyNode(targetName);
					// edge = (Edge) rootGraph.getEdge
					// (rootGraph.createEdge(node_1, node_2));
					edge = Cytoscape.getCyEdge(node_1, node_2,
							Semantics.INTERACTION, label, true, true);
				}

				// Set correct ID, canonical name and interaction name
				edge.setIdentifier(edgeName);
				edgeAttr.setAttribute(edgeName, Semantics.INTERACTION, label);
				mapAttributes(edge, edgeAttr, edgeAttributes.get(idx));

				giny_edges.add(edge.getRootGraphIndex());
				((KeyValue) edge_root_index_pairs.get(idx)).value = (new Integer(
						edge.getRootGraphIndex()));
			} else {
				throw new GMLException(
						"Non-existant source/target node for edge with gml (source,target): "
								+ sources.get(idx) + "," + targets.get(idx));
			}
		}

		edgeNameSet = null;
	}

	/**
	 * This function takes the root level list which defines a gml objec tree
	 */
	@SuppressWarnings("unchecked")
	protected void readGML(final List<KeyValue> list) {
		// Report Progress Message
		int counter = 0;
		final int size = list.size();

		for (KeyValue keyVal : list) {
			// Report Progress Value
			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted(percentUtil.getGlobalPercent(1,
						counter, size));
				counter++;
			}

			if (keyVal.key.equals(GRAPH))
				readGraph((List<KeyValue>) keyVal.value);
		}
	}

	/**
	 * This function takes in a list which was given as the value to a "graph"
	 * key underneath the main gml list
	 */
	@SuppressWarnings("unchecked")
	protected void readGraph(final List<KeyValue> list) {
		for (KeyValue keyVal : list) {
			if (keyVal.key.equals(NODE))
				readNode((List<KeyValue>) keyVal.value);

			if (keyVal.key.equals(EDGE))
				readEdge((List<KeyValue>) keyVal.value);
		}
	}

	/**
	 * This will extract the model information from the list which is matched a
	 * "node" key
	 */
	private void readNode(final List<KeyValue> list) {
		String label = "";
		String name = null;
		final Map<String, Object> attr = new HashMap<String, Object>();

		boolean contains_id = false;
		int id = 0;
		KeyValue root_index_pair = null;

		for (final KeyValue keyVal : list) {
			if (keyVal.key.equals(ID)) {
				contains_id = true;
				id = (Integer) keyVal.value;
			} else if (keyVal.key.equals(LABEL)) {
				label = keyVal.value.toString();
			} else if (keyVal.key.equals(NAME)) {
				name = keyVal.value.toString();
			} else if (keyVal.key.equals(ROOT_INDEX)) {
				root_index_pair = keyVal;
			} else if (!keyVal.key.equals(GRAPHICS) && !keyVal.key.startsWith(VIZMAP_PREFIX)) {
				// This is a regular attribute value
				attr.put(keyVal.key, keyVal.value);
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
			final StringWriter stringWriter = new StringWriter();

			try {
				GMLParser.printList(list, stringWriter);
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}

			throw new GMLException("The node-associated list\n" + stringWriter
					+ "is missing an id field");
		} else {
			node_root_index_pairs.add(root_index_pair);
			nodes.add(id);
			nodeLabels.add(label);
			nodeNames.add(name);

			nodeAttributes.add(attr);
		}
	}

	/**
	 * This will extract the model information from the list which is matched to
	 * an "edge" key.
	 */
	protected void readEdge(final List<KeyValue> list) {
		String label = DEFAULT_EDGE_INTERACTION;
		boolean contains_source = false;
		boolean contains_target = false;
		int source = 0;
		int target = 0;
		KeyValue root_index_pair = null;
		final Map<String, Object> attr = new HashMap<String, Object>();

		for (final KeyValue keyVal: list) {
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
			} else if (!keyVal.key.equals(GRAPHICS) && !keyVal.key.startsWith(VIZMAP_PREFIX)) {
				attr.put(keyVal.key, keyVal.value);
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

			throw new GMLException("The edge-associated list\n" + stringWriter
					+ " is missing a source or target key");
		} else {
			sources.add(source);
			targets.add(target);

			edgeLabels.add(label);
			edge_root_index_pairs.add(root_index_pair);
			
			edgeAttributes.add(attr);
		}
	}

	/**
	 * getLayoutAlgorithm is called to get the Layout Algorithm that will be
	 * used to layout the resulting graph. In our case, we just return a stub
	 * that will call our internal layout routine, which will just use the
	 * default layout, but with our task monitor
	 * 
	 * @return the CyLayoutAlgorithm to use
	 */
	public CyLayoutAlgorithm getLayoutAlgorithm() {
		return new LayoutAdapter() {
			public void doLayout(CyNetworkView networkView, TaskMonitor monitor) {
				layout(networkView);
			}
		};
	}

	/**
	 * layout the graph based on the GML values we read
	 * 
	 * @param myView
	 *            the view of the network we want to layout
	 */
	public void layout(CyNetworkView myView) {
		if ((myView == null) || (myView.nodeCount() == 0)) {
			return;
		}

		if (keyVals == null) {
			throw new RuntimeException(
					"Failed to read gml file on initialization");
		}

		for (Iterator it = keyVals.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();

			if (keyVal.key.equals(GRAPH)) {
				layoutGraph(myView, (List) keyVal.value);
			}
		}
	}

	//
	/**
	 * DOCUMENT ME!
	 */
	public void extract() {
		if (keyVals == null) {
			throw new RuntimeException(
					"Failed to read gml file on initialization");
		}

		for (Iterator it = keyVals.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();

			if (keyVal.key.equals(GRAPH)) {
				extractGraph((List) keyVal.value);
			}
		}
	}

	protected void extractGraph(List list) {
		String edgeName = null;

		// Count the current edge
		int ePtr = 0;

		for (Iterator it = list.iterator(); it.hasNext();) {
			final KeyValue keyVal = (KeyValue) it.next();

			if (keyVal.key.equals(NODE)) {
				extractNode((List) keyVal.value);
			} else if (keyVal.key.equals(EDGE)) {
				edgeName = (String) edgeNames.get(ePtr);
				ePtr++;
				extractEdge((List) keyVal.value, edgeName);
			}
		}
	}

	protected void extractNode(List list) {
		List graphics_list = null;
		String label = null;

		int tempid = 0;

		for (Iterator it = list.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();

			if (keyVal.key.equals(ROOT_INDEX)) {
				if (keyVal.value == null) {
					return;
				}
			} else if (keyVal.key.equals(GRAPHICS)) {
				graphics_list = (List) keyVal.value;
			} else if (keyVal.key.equals(LABEL)) {
				label = (String) keyVal.value;
			} else if (keyVal.key.equals(ID)) {
				tempid = ((Integer) keyVal.value).intValue();
			}
		}

		if (graphics_list != null) {
			if (label == null) {
				label = "node" + tempid;
				logger.info("Warning: node label is missing for node ID: "
						+ tempid);
			}

			extractNodeAttributes(graphics_list, label);
		}
	}

	protected void extractEdge(List<KeyValue> list, String edgeName) {
		List graphics_list = null;

		for (KeyValue keyVal : list) {
			if (keyVal.key.equals(ROOT_INDEX)) {
				if (keyVal.value == null) {
					return;
				}
			} else if (keyVal.key.equals(GRAPHICS)) {
				graphics_list = (List) keyVal.value;
			}
		}

		if (graphics_list != null)
			extractEdgeAttributes(graphics_list, edgeName);
	}

	/**
	 * Lays Out the Graph, based on GML.
	 */
	protected void layoutGraph(final GraphView myView, List list) {
		String edgeName = null;

		// Count the current edge
		int ePtr = 0;

		for (Iterator it = list.iterator(); it.hasNext();) {
			final KeyValue keyVal = (KeyValue) it.next();

			if (keyVal.key.equals(NODE))
				layoutNode(myView, (List) keyVal.value);
		}
	}

	/**
	 * Assign node properties based on the values in the list matched to the
	 * "node" key. Mostly just a wrapper around layoutNodeGraphics
	 */
	protected void layoutNode(GraphView myView, List list) {
		Integer root_index = null;
		List graphics_list = null;
		String label = null;
		int tempid = 0;

		NodeView view = null;

		for (Iterator it = list.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();

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
				graphics_list = (List) keyVal.value;
			} else if (keyVal.key.equals(LABEL)) {
				label = (String) keyVal.value;
			} else if (keyVal.key.equals(ID)) {
				tempid = ((Integer) keyVal.value).intValue();
			}
		}

		// logger.info( "In layout, Root index is: " + root_index );
		// logger.info( " Checking label: " + label );
		view = myView.getNodeView(root_index.intValue());

		if (label != null) {
			view.getLabel().setText(label);
		} else {
			view.getLabel().setText("node(" + tempid + ")");
		}

		if (graphics_list != null)
			layoutNodeGraphics(myView, graphics_list, view);
	}

	/**
	 * This will assign node graphic properties based on the values in the list
	 * matches to the "graphics" key word
	 */
	protected void layoutNodeGraphics(GraphView myView, List list,
			NodeView nodeView) {
		for (Iterator it = list.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();

			if (keyVal.key.equals(X)) {
				nodeView.setXPosition(((Number) keyVal.value).doubleValue());
			} else if (keyVal.key.equals(Y)) {
				nodeView.setYPosition(((Number) keyVal.value).doubleValue());
			} else if (keyVal.key.equals(H)) {
				nodeView.setHeight(((Number) keyVal.value).doubleValue());
			} else if (keyVal.key.equals(W)) {
				nodeView.setWidth(((Number) keyVal.value).doubleValue());
			} else if (keyVal.key.equals(FILL)) {
				nodeView.setUnselectedPaint(getColor((String) keyVal.value));
			} else if (keyVal.key.equals(OUTLINE)) {
				nodeView.setBorderPaint(getColor((String) keyVal.value));
			} else if (keyVal.key.equals(OUTLINE_WIDTH)) {
				nodeView.setBorderWidth(((Number) keyVal.value).floatValue());
			} else if (keyVal.key.equals(TYPE)) {
				String type = (String) keyVal.value;

				if (type.equals(ELLIPSE)) {
					nodeView.setShape(NodeView.ELLIPSE);
				} else if (type.equals(RECTANGLE)) {
					nodeView.setShape(NodeView.RECTANGLE);
				} else if (type.equals(DIAMOND)) {
					nodeView.setShape(NodeView.DIAMOND);
				} else if (type.equals(HEXAGON)) {
					nodeView.setShape(NodeView.HEXAGON);
				} else if (type.equals(OCTAGON)) {
					nodeView.setShape(NodeView.OCTAGON);
				} else if (type.equals(PARALELLOGRAM)) {
					nodeView.setShape(NodeView.PARALELLOGRAM);
				} else if (type.equals(TRIANGLE)) {
					nodeView.setShape(NodeView.TRIANGLE);
				}
			}
		}
	}

	//
	// Extract node attributes from GML file
	protected void extractNodeAttributes(List list, String nodeName) {
		// Put all attributes into hashes.
		// Key is the node name
		// (Assume we do not have duplicate node name.)
		for (Iterator it = list.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();

			if (keyVal.key.equals(X) || keyVal.key.equals(Y)) {
				// Do nothing.
			} else if (keyVal.key.equals(H)) {
				graphStyle.addProperty(nodeName,
						VisualPropertyType.NODE_HEIGHT, "" + keyVal.value);
			} else if (keyVal.key.equals(W)) {
				graphStyle.addProperty(nodeName, VisualPropertyType.NODE_WIDTH,
						"" + keyVal.value);
			} else if (keyVal.key.equals(FILL)) {
				graphStyle.addProperty(nodeName,
						VisualPropertyType.NODE_FILL_COLOR, "" + keyVal.value);
			} else if (keyVal.key.equals(OUTLINE)) {
				graphStyle
						.addProperty(nodeName,
								VisualPropertyType.NODE_BORDER_COLOR, ""
										+ keyVal.value);
			} else if (keyVal.key.equals(WIDTH)) {
				graphStyle.addProperty(nodeName,
						VisualPropertyType.NODE_LINE_WIDTH, "" + keyVal.value);
			} else if (keyVal.key.equals(TYPE)) {
				String type = (String) keyVal.value;
				graphStyle.addProperty(nodeName, VisualPropertyType.NODE_SHAPE,
						type);
			}
		}
	}

	//
	// Extract edge attributes from GML input
	//
	protected void extractEdgeAttributes(List<KeyValue> list, String edgeName) {
		String value = null;

		boolean isArrow = false;
		String edgeFill = DEF_COLOR.toString();
		String arrowShape = ARROW_NONE;

		for (KeyValue keyVal : list) {
			if (keyVal.key.equals(LINE)) {
				// This represents "Polyline," which is a line (usually an edge)
				// with arbitrary number of anchors.
				// Current version of CS does not support this, so ignore this
				// at this point of time...
			} else if (keyVal.key.equals(WIDTH)) {
				graphStyle.addProperty(edgeName,
						VisualPropertyType.EDGE_LINE_WIDTH, new String(
								keyVal.value.toString()));
			} else if (keyVal.key.equals(FILL)) {
				graphStyle.addProperty(edgeName, VisualPropertyType.EDGE_COLOR,
						new String(keyVal.value.toString()));
				edgeFill = keyVal.value.toString();
			} else if (keyVal.key.equals(ARROW)) {
				isArrow = true;

				ArrowShape shape = ArrowShape.ARROW;
				String arrowName = shape.getName();

				if (keyVal.value.toString().equalsIgnoreCase(ARROW_FIRST)) {
					arrowShape = ARROW_FIRST;
					graphStyle.addProperty(edgeName,
							VisualPropertyType.EDGE_SRCARROW_SHAPE, arrowName);
				} else if (keyVal.value.toString().equalsIgnoreCase(ARROW_LAST)) {
					arrowShape = ARROW_LAST;
					graphStyle.addProperty(edgeName,
							VisualPropertyType.EDGE_TGTARROW_SHAPE, arrowName);
				} else if (keyVal.value.toString().equalsIgnoreCase(ARROW_BOTH)) {
					arrowShape = ARROW_BOTH;
					graphStyle.addProperty(edgeName,
							VisualPropertyType.EDGE_SRCARROW_SHAPE, arrowName);
					graphStyle.addProperty(edgeName,
							VisualPropertyType.EDGE_TGTARROW_SHAPE, arrowName);
				}
			} else if (keyVal.key.equals(TYPE)) {
				value = (String) keyVal.value;

				if (value.equals(STRAIGHT_LINES)) {
					// edgeShape.put(edgeName, (String) keyVal.value);
				} else if (value.equals(CURVED_LINES)) {
					// edgeView.setLineType(EdgeView.CURVED_LINES);
				}
			} else if (keyVal.key.equals(SOURCE_ARROW)) {
				graphStyle.addProperty(edgeName,
						VisualPropertyType.EDGE_SRCARROW_SHAPE, ArrowShape
								.getArrowShape(
										((Number) keyVal.value).intValue())
								.getName());
				graphStyle.addProperty(edgeName,
						VisualPropertyType.EDGE_SRCARROW_COLOR, edgeFill);
			} else if (keyVal.key.equals(TARGET_ARROW)) {
				graphStyle.addProperty(edgeName,
						VisualPropertyType.EDGE_TGTARROW_SHAPE, ArrowShape
								.getArrowShape(
										((Number) keyVal.value).intValue())
								.getName());
				graphStyle.addProperty(edgeName,
						VisualPropertyType.EDGE_TGTARROW_COLOR, edgeFill);
			} else if (keyVal.key.equals(YED_SOURCE_ARROW)) {
				graphStyle.addProperty(edgeName,
						VisualPropertyType.EDGE_SRCARROW_SHAPE,
						convertYEDArrowShape(keyVal.value.toString()));
				graphStyle.addProperty(edgeName,
						VisualPropertyType.EDGE_SRCARROW_COLOR, edgeFill);
			} else if (keyVal.key.equals(YED_TARGET_ARROW)) {
				graphStyle.addProperty(edgeName,
						VisualPropertyType.EDGE_TGTARROW_SHAPE,
						convertYEDArrowShape(keyVal.value.toString()));
				graphStyle.addProperty(edgeName,
						VisualPropertyType.EDGE_TGTARROW_COLOR, edgeFill);
			}
		}

		// make the arrow color the same as edge
		if (isArrow) {
			if (arrowShape.equals(ARROW_FIRST)) {
				graphStyle.addProperty(edgeName,
						VisualPropertyType.EDGE_SRCARROW_COLOR, edgeFill);
			} else if (arrowShape.equals(ARROW_LAST)) {
				graphStyle.addProperty(edgeName,
						VisualPropertyType.EDGE_TGTARROW_COLOR, edgeFill);
			} else if (arrowShape.equals(ARROW_BOTH)) {
				graphStyle.addProperty(edgeName,
						VisualPropertyType.EDGE_SRCARROW_COLOR, edgeFill);
				graphStyle.addProperty(edgeName,
						VisualPropertyType.EDGE_TGTARROW_COLOR, edgeFill);
			}
		}
	}

	private String convertYEDArrowShape(String yedArrow) {
		String shape = ArrowShape.NONE.getName();

		if (yedArrow.equals(YED_DELTA) || yedArrow.equals(YED_WHITE_DELTA))
			shape = ArrowShape.DELTA.getName();
		else if (yedArrow.equals(YED_DIAMOND)
				|| yedArrow.equals(YED_WHITE_DIAMOND))
			shape = ArrowShape.DIAMOND.getName();
		else if (yedArrow.equals(YED_STANDARD) || yedArrow.equals(YED_SHORT))
			shape = ArrowShape.ARROW.getName();

		return shape;
	}

	/**
	 * DOCUMENT ME!
	 */
	public void showMaps() {
		String e = null;
		String n = null;
		String temp = null;

		for (int i = 0; i < edgeNames.size(); i++) {
			e = (String) edgeNames.get(i);
			temp = e + ": ";
			temp = temp + edgeCol.get(e) + ", ";
			temp = temp + edgeWidth.get(e) + ", ";
			temp = temp + edgeArrow.get(e) + ", ";
			temp = temp + edgeShape.get(e) + ", ";
			logger.info(temp);
			temp = null;
		}
	}

	/**
	 * Assign edge visual properties based on pairs in the list matched to the
	 * "edge" key world
	 */
	protected void layoutEdge(GraphView myView, List list, String edgeName) {
		EdgeView edgeView = null;
		List graphics_list = null;

		for (Iterator it = list.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();

			if (keyVal.key.equals(ROOT_INDEX)) {
				/*
				 * Previously, we didn't make an object for this edge for some
				 * reason. Don't try to go any further.
				 */
				if (keyVal.value == null) {
					return;
				}

				edgeView = myView.getEdgeView(((Integer) keyVal.value)
						.intValue());
			} else if (keyVal.key.equals(GRAPHICS)) {
				graphics_list = (List) keyVal.value;
			}
		}

		if ((edgeView != null) && (graphics_list != null))
			layoutEdgeGraphics(myView, graphics_list, edgeView);
	}

	/**
	 * Assign edge graphics properties
	 */
	protected void layoutEdgeGraphics(GraphView myView, List<KeyValue> list,
			EdgeView edgeView) {
		// Local vars.
		String value = null;

		// KeyValue keyVal = null;
		for (KeyValue keyVal : list) {
			// This is a polyline obj. However, it will be translated into
			// straight line.
			if (keyVal.key.equals(LINE)) {
				layoutEdgeGraphicsLine(myView, (List) keyVal.value, edgeView);
			} else if (keyVal.key.equals(WIDTH)) {
				edgeView.setStrokeWidth(((Number) keyVal.value).floatValue());
			} else if (keyVal.key.equals(FILL)) {
				edgeView.setUnselectedPaint(getColor((String) keyVal.value));
			} else if (keyVal.key.equals(TYPE)) {
				value = (String) keyVal.value;

				if (value.equals(STRAIGHT_LINES)) {
					edgeView.setLineType(EdgeView.STRAIGHT_LINES);
				} else if (value.equals(CURVED_LINES)) {
					edgeView.setLineType(EdgeView.CURVED_LINES);
				}
			} else if (keyVal.key.equals(ARROW)) {
				// The position of the arrow.
				// There are 4 states: no arrows, both ends have arrows, source,
				// or target.
				//
				// The arrow type below is hard-coded since GML does not
				// support shape of the arrow.
				if (keyVal.value.equals(ARROW_FIRST)) {
					edgeView.setSourceEdgeEnd(2);
				} else if (keyVal.value.equals(ARROW_LAST)) {
					edgeView.setTargetEdgeEnd(2);
				} else if (keyVal.value.equals(ARROW_BOTH)) {
					edgeView.setSourceEdgeEnd(2);
					edgeView.setTargetEdgeEnd(2);
				} else if (keyVal.value.equals(ARROW_NONE)) {
					// Do nothing. No arrows.
				}
			} else if (keyVal.key.equals(SOURCE_ARROW)) {
				edgeView.setSourceEdgeEnd(((Number) keyVal.value).intValue());
			} else if (keyVal.key.equals(TARGET_ARROW)) {
				edgeView.setTargetEdgeEnd(((Number) keyVal.value).intValue());
			}
		}
	}

	/**
	 * Assign bend points based on the contents of the list associated with a
	 * "Line" key We make sure that there is both an x,y present in the
	 * underlying point list before trying to generate a bend point
	 */
	protected void layoutEdgeGraphicsLine(GraphView myView, List list,
			EdgeView edgeView) {
		for (Iterator it = list.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();

			if (keyVal.key.equals(POINT)) {
				Number x = null;
				Number y = null;

				for (Iterator pointIt = ((List) keyVal.value).iterator(); pointIt
						.hasNext();) {
					KeyValue pointVal = (KeyValue) pointIt.next();

					if (pointVal.key.equals(X)) {
						x = (Number) pointVal.value;
					} else if (pointVal.key.equals(Y)) {
						y = (Number) pointVal.value;
					}
				}

				if (!((x == null) || (y == null))) {
					Point2D.Double pt = new Point2D.Double(x.doubleValue(), y
							.doubleValue());
					edgeView.getBend().addHandle(pt);
				}
			}
		}
	}

	/**
	 * Part of interface contract
	 */
	public int[] getNodeIndicesArray() {
		giny_nodes.trimToSize();

		return giny_nodes.elements();
	}

	/**
	 * Part of interace contract
	 */
	public int[] getEdgeIndicesArray() {
		giny_edges.trimToSize();

		return giny_edges.elements();
	}

	/**
	 * Create a color object from the string like it is stored in a gml file
	 */
	public Color getColor(String colorString) {
		return new Color(Integer.parseInt(colorString.substring(1), 16));
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param net
	 *            DOCUMENT ME!
	 */
	public void doPostProcessing(final CyNetwork net) {

		CyInitParams init = CytoscapeInit.getCyInitParams();

		if (init == null)
			return;

		if ((init.getMode() == CyInitParams.GUI)
				|| (init.getMode() == CyInitParams.EMBEDDED_WINDOW)) {
			if (!((vsbSwitch != null) && vsbSwitch.equals("off"))) {
				graphStyle.buildStyle();
			}
		}
	}
}
