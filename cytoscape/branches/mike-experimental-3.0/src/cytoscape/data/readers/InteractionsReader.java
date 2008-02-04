/*
 File: InteractionsReader.java

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

// InteractionsReader:  from semi-structured text file, into an array of Interactions
//------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------
package cytoscape.data.readers;

import cern.colt.list.IntArrayList;

import cern.colt.map.OpenIntIntHashMap;

import cytoscape.CyEdge;
import cytoscape.Cytoscape;

import cytoscape.data.Interaction;

import cytoscape.data.servers.BioDataServer;

import cytoscape.task.TaskMonitor;

import cytoscape.util.FileUtil;
import cytoscape.util.PercentUtil;

import cytoscape.view.CyNetworkView;

import giny.model.Edge;
import giny.model.Node;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Reader for graphs in the interactions file format. Given the filename,
 * provides the graph and attributes objects constructed from the file.
 */
public class InteractionsReader extends AbstractGraphReader {
	private TaskMonitor taskMonitor;
	private PercentUtil percentUtil;

	/**
	 * A Vector that holds all of the Interactions
	 */
	protected List<Interaction> allInteractions = new ArrayList<Interaction>();
	
	private String zip_entry;
	private boolean is_zip = false;
	private IntArrayList node_indices;
	private OpenIntIntHashMap edges;
	private InputStream inputStream;

	/**
	 * Creates an interaction reader based on a string consisting of data that has
	 * been read from a zip file.
	 * @param zip_entry The zip entry data.
	 * @param monitor An optional task monitor.  May be null.
	 * @param is_zip Indicates that the data is from a zip file - Should almost
	 * always be true.
	 */
	public InteractionsReader(String zip_entry, TaskMonitor monitor, boolean is_zip) {
		super("zip_data");
		this.zip_entry = zip_entry;
		this.is_zip = is_zip;
		this.taskMonitor = monitor;
	}

	/**
	 * Creates an interaction reader based on the string file name.
	 * @param filename The filename that contains the interaction data
	 * to be read.
	 */
	public InteractionsReader(String filename) {
		this(filename, null);
		this.inputStream = FileUtil.getInputStream(filename);
	}

	/**
	 * Creates an interaction reader based on the string file name.
	 * @param filename The filename that contains the interaction data
	 * to be read.
	 * @param monitor An optional task monitor.  May be null.
	 */
	public InteractionsReader(String filename, TaskMonitor monitor) {
		super(filename);
		this.taskMonitor = monitor;
		this.inputStream = FileUtil.getInputStream(filename);
	}

	/**
	 * Constructor.<br>
	 * This is usually used for remote file loading.
	 *
	 * @param is
	 *            Input stream of GML file,
	 *
	 */
	public InteractionsReader(InputStream is, String name) {
		super(name);

		this.inputStream = is;
	}

	/**
 	 * Sets the task monitor we want to use
 	 *
 	 * @param monitor the TaskMonitor to use
 	 */
	public void setTaskMonitor(TaskMonitor monitor) {
		this.taskMonitor = monitor;
	}

	// -----------------------------------------------------------------------------------------
	/**
	 * Calls read(false)
	 */
	public void read() throws IOException {
		String rawText;

		if (!is_zip) {
			rawText = FileUtil.getInputString(inputStream);
		} else {
			rawText = zip_entry;
		}

		String delimiter = " ";

		if (rawText.indexOf("\t") >= 0)
			delimiter = "\t";

		final String[] lines = rawText.split(System.getProperty("line.separator"));

		// There are a total of 6 steps to read in a complete SIF File
		if (taskMonitor != null) {
			percentUtil = new PercentUtil(6);
		}

		final int linesLength = lines.length;
		String newLine;
		Interaction newInteraction;

		for (int i = 0; i < linesLength; i++) {
			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted(percentUtil.getGlobalPercent(1, i, lines.length));
			}

			newLine = lines[i];

			if (newLine.length() <= 0) {
				continue;
			}

			newInteraction = new Interaction(newLine, delimiter);
			allInteractions.add(newInteraction);
		}

		createRootGraphFromInteractionData();
	} // readFromFile

	// -------------------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getCount() {
		return allInteractions.size();
	}

	// -------------------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Interaction[] getAllInteractions() {
		//		Interaction[] result = new Interaction[allInteractions.size()];
		//
		//		for (int i = 0; i < allInteractions.size(); i++) {
		//			Interaction inter = (Interaction) allInteractions.elementAt(i);
		//			result[i] = inter;
		//		}
		return allInteractions.toArray(new Interaction[0]);
	}

	// -------------------------------------------------------------------------------------------
	protected void createRootGraphFromInteractionData() {
		final Interaction[] interactions = getAllInteractions();

		// figure out how many nodes and edges we need before we create the
		// graph;
		// this improves performance for large graphs
		final Set<String> nodeNameSet = new HashSet<String>();
		int edgeCount = 0;
		final int intSize = interactions.length;

		Interaction interaction;
		String sourceName;
		String[] targets;
		int targetLength;

		for (int i = 0; i < intSize; i++) {
			interaction = interactions[i];
			sourceName = interaction.getSource();

			nodeNameSet.add(sourceName); // does nothing if already there

			targets = interaction.getTargets();
			targetLength = targets.length;

			for (int t = 0; t < targetLength; t++) {
				nodeNameSet.add(targets[t]);
				edgeCount++;
			}

			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted(percentUtil.getGlobalPercent(2, i,
				                                                             interactions.length));
			}
		}

		Cytoscape.ensureCapacity(nodeNameSet.size(), edgeCount);
		node_indices = new IntArrayList(nodeNameSet.size());
		edges = new OpenIntIntHashMap(edgeCount);

		// now create all of the nodes, storing a hash from name to node
		// Map nodes = new HashMap();
		int counter = 0;
		Node node;

		for (String nodeName : nodeNameSet) {
			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted(percentUtil.getGlobalPercent(3, counter,
				                                                             nodeNameSet.size()));
				counter++;
			}

			// use the static method
			node = Cytoscape.getCyNode(nodeName, true);
			node_indices.add(node.getRootGraphIndex());
		}

		// ---------------------------------------------------------------------------
		// now loop over the interactions again, this time creating edges
		// between
		// all sources and each of their respective targets.
		// for each edge, save the source-target pair, and their interaction
		// type,
		// in Cytoscape.getEdgeNetworkData() -- a hash of a hash of name-value
		// pairs, like this:
		// Cytoscape.getEdgeNetworkData() ["interaction"] = interactionHash
		// interactionHash [sourceNode::targetNode] = "pd"
		// ---------------------------------------------------------------------------
		String targetNodeName;
		String nodeName;
		String interactionType;
		String edgeName;
		Edge edge;

		for (int i = 0; i < interactions.length; i++) {
			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted(percentUtil.getGlobalPercent(4, i,
				                                                             interactions.length));
			}

			interaction = interactions[i];
			nodeName = interaction.getSource();
			interactionType = interaction.getType();
			targets = interaction.getTargets();
			targetLength = targets.length;

			for (int t = 0; t < targetLength; t++) {
				targetNodeName = targets[t];

				edgeName = CyEdge.createIdentifier(nodeName, interactionType, targetNodeName);
				edge = Cytoscape.getCyEdge(nodeName, edgeName, targetNodeName, interactionType);
				edges.put(edge.getRootGraphIndex(), 0);
			} // for t
		} // for i
	} // createRootGraphFromInteractionData

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int[] getNodeIndicesArray() {
		node_indices.trimToSize();

		return node_indices.elements();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int[] getEdgeIndicesArray() {
		final IntArrayList edge_indices = new IntArrayList(edges.size());
		edges.keys(edge_indices);
		edge_indices.trimToSize();

		return edge_indices.elements();
	}
} // InteractionsReader
