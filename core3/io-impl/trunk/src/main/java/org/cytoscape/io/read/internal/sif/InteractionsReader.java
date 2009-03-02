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

package org.cytoscape.io.read.internal.sif;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.io.internal.util.ReadUtils;
import org.cytoscape.io.read.CyReader;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;

/**
 * Reader for graphs in the interactions file format. Given the filename,
 * provides the graph and attributes objects constructed from the file.
 */
public class InteractionsReader implements CyReader {
	
	private static final String DEF_DELIMITER = " ";
	private static final String LINE_SEP = System.getProperty("line.separator");
	
	private static final String NODE_NAME_ATTR_LABEL = "name";
	
	private static final String INTERACTION = "interaction";
	
	private ReadUtils readUtil;

	private List<Interaction> interactions;
	private InputStream inputStream;
	private CyNetworkFactory networkFactory;

	private Map<Class<?>, Object> readObjects;

	public InteractionsReader(CyNetworkFactory factory, ReadUtils readUtil) {
		this.interactions = new ArrayList<Interaction>();
		this.networkFactory = factory;
		this.readUtil = readUtil;
		this.readObjects = new HashMap<Class<?>, Object>();
	}

	public void read() throws IOException {
		refresh();
		
		String delimiter = DEF_DELIMITER;
		
		final String rawText = readUtil.getInputString(inputStream);
		
		if (rawText.indexOf("\t") >= 0)
			delimiter = "\t";

		final String[] lines = rawText.split(LINE_SEP);

		final int size = lines.length;
		for (int i=0; i < size; i++) {
			if (lines[i].length() <= 0)
				continue;
			interactions.add(new Interaction(lines[i], delimiter));
		}
		
		if(inputStream != null) {
			inputStream.close();
			inputStream = null;
		}

		createNetwork();
	}

	private void refresh() {
		readObjects.clear();
		interactions.clear();
		interactions = new ArrayList<Interaction>();
		readObjects = new HashMap<Class<?>, Object>();
	}
	
	private void createNetwork() {

		final CyNetwork network = networkFactory.getInstance();

		Map<String, CyNode> nodeMap = new HashMap<String, CyNode>();

		// put all node names in the Set
		for (Interaction interaction : interactions) {
			nodeMap.put(interaction.getSource(), null);
			for (String target : interaction.getTargets())
				nodeMap.put(target, null);
		}

		CyNode node;
		for (String nodeName : nodeMap.keySet()) {
			node = network.addNode();
			node.attrs().set(NODE_NAME_ATTR_LABEL, nodeName);
			nodeMap.put(nodeName, node);
		}

		// Now loop over the interactions again, this time creating edges between
		// all sources and each of their respective targets.
		String srcName;
		String interactionType;
		CyEdge edge;
		
		for (Interaction interaction : interactions) {

			srcName = interaction.getSource();
			interactionType = interaction.getType();

			for (String tgtName : interaction.getTargets()) {
				edge = network.addEdge(nodeMap.get(srcName), nodeMap
						.get(tgtName), true);
				edge.attrs().set(NODE_NAME_ATTR_LABEL,
						srcName + " (" + interactionType + ") " + tgtName);
				edge.attrs().set(INTERACTION, interactionType);
			}
		}
		
		readObjects.put(CyNetwork.class, network);
		
		nodeMap.clear();
		nodeMap = null;
	}

	public <T> T getReadData(Class<T> type) {
		return type.cast(readObjects.get(type));
	}

	public void setInputStream(InputStream is) {
		if (is == null)
			throw new NullPointerException("Input stream is null");
		inputStream = is;
	}

	public Set<Class<?>> getSupportedDataTypes() {
		return readObjects.keySet();
	}
}
