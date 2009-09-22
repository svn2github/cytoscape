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
import java.awt.geom.Point2D;
import java.io.IOException;
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

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.CyNetworkReaderFactory;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;


/**
 * This class is responsible for converting a gml object tree into cytoscape
 * objects New features to the current version: 1. Small bug fixes. 2. Translate
 * all features in the GML file. This includes 3. New Visual Style will be
 * generated when you call this class. The new style saves all visual features
 * (like node shape) and will not be lost even after other style selected.
 */
public class GMLReaderFactory implements CyNetworkReaderFactory {
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

	CyFileFilter fileFilter;
	public GMLReaderFactory(CyFileFilter fileFilter)
	{
		this.fileFilter = fileFilter;
	}

	public Task getReader(InputStream input, CyNetwork network, CyDataTable dataTable)
	{
		return new GMLReader(input, network);
	}

	public CyFileFilter getCyFileFilter()
	{
		return fileFilter;
	}

	class GMLReader implements Task
	{
		private Color DEF_COLOR = new Color(153, 153, 255);

		// Entries in the file
		List keyVals;

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

		private TaskMonitor taskMonitor;

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

		// The InputStream
		InputStream inputStream = null;

		CyNetwork network;

		boolean cancel = false;

		/**
		 * Constructor.<br>
		 * This is usually used for remote file loading.
		 *
		 * @param is
		 *            Input stream of GML file,
		 *
		 */
		public GMLReader(InputStream inputStream, CyNetwork net) {
			
			network = net;
			this.inputStream = inputStream;

			// Set new style name
			edge_names = new Vector<CyEdge>();
			node_names = new Vector<String>();
			// TODO fix name

		}

		/**
		 *  DOCUMENT ME!
		 */
		public void run(TaskMonitor taskMonitor) throws IOException {
			try {
				keyVals = (new GMLParser(inputStream)).parseList();
			} catch (Exception io) {
				io.printStackTrace();
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
			// Extract node & edge attributes
			extract(); 

			releaseStructures();
		}

		public void cancel()
		{
			cancel = true;
		}

		/**
		 * Returns a list containing the gml object tree
		 */
		public List getList() {
			return keyVals;
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
		protected void createGraph() {
			
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
					node.attrs().set("name",label);

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
					edge.attrs().set("name", edgeName);
					edge.attrs().set("interaction", label);
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
		protected void readGML(List list) {
			// Report Progress Message
			int counter = 0;

			for (Iterator it = list.iterator(); it.hasNext();) {
				// Report Progress Value
				if (taskMonitor != null) {
					//TODO: set proper number
					//taskMonitor.setPercentCompleted(percentUtil.getGlobalPercent(1, counter, list.size()));
					counter++;
				}

				KeyValue keyVal = (KeyValue) it.next();

				if (keyVal.key.equals(GRAPH)) {
					readGraph((List) keyVal.value);
				}
			}
		}

		/**
		 * This function takes in a list which was given as the value to a "graph"
		 * key underneath the main gml list
		 */
		@SuppressWarnings("unchecked") // KeyValue.value cast
		protected void readGraph(List list) {
			for (Iterator it = list.iterator(); it.hasNext();) {
				KeyValue keyVal = (KeyValue) it.next();

				if (keyVal.key.equals(NODE)) {
					readNode((List) keyVal.value);
				}

				if (keyVal.key.equals(EDGE)) {
					readEdge((List) keyVal.value);
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

			for (Iterator it = list.iterator(); it.hasNext();) {
				KeyValue keyVal = (KeyValue) it.next();

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

			for (Iterator it = list.iterator(); it.hasNext();) {
				KeyValue keyVal = (KeyValue) it.next();

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


		//
		/**
		 *  DOCUMENT ME!
		 */
		public void extract() {
			if (keyVals == null) {
				throw new RuntimeException("Failed to read gml file on initialization");
			}

			for (Iterator it = keyVals.iterator(); it.hasNext();) {
				KeyValue keyVal = (KeyValue) it.next();

				if (keyVal.key.equals(GRAPH)) {
					extractGraph((List) keyVal.value);
				}
			}
		}

		protected void extractGraph(List list) {
			CyEdge edge = null;

			// Count the current edge
			int ePtr = 0;

			for (Iterator it = list.iterator(); it.hasNext();) {
				final KeyValue keyVal = (KeyValue) it.next();

				if (keyVal.key.equals(NODE)) {
					extractNode((List) keyVal.value);
				} else if (keyVal.key.equals(EDGE)) {
					edge = edge_names.get(ePtr);
					ePtr++;
					extractEdge((List) keyVal.value, edge);
				}
			}
		}

		@SuppressWarnings("unchecked") // KeyValue.value cast
		protected void extractNode(List list) {
			List graphics_list = null;
			String label = null;
			CyNode node = null;

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

				//extractNodeAttributes(graphics_list, node);
			}
		}

		@SuppressWarnings("unchecked") // KeyValue.value cast
		protected void extractEdge(List list, CyEdge edge) {
			List graphics_list = null;

			for (Iterator it = list.iterator(); it.hasNext();) {
				KeyValue keyVal = (KeyValue) it.next();

				if (keyVal.key.equals(ROOT_INDEX)) {
					if (keyVal.value == null) {
						return;
					}
				} else if (keyVal.key.equals(GRAPHICS)) {
					graphics_list = (List) keyVal.value;
				}
			}

			if (graphics_list != null) {
				//extractEdgeAttributes(graphics_list, edge);
			}
		}

		/**
		 * Create a color object from the string like it is stored in a gml file
		 */
		private Color getColor(String colorString) {
			return new Color(Integer.parseInt(colorString.substring(1), 16));
		}
	}
}
