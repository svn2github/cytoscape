/*
 Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.io.internal.read.sif;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.io.internal.util.ReadUtils;
import org.cytoscape.io.internal.read.AbstractNetworkViewReader;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.view.layout.CyLayouts;


/**
 * Reader for graphs in the interactions file format. Given the filename,
 * provides the graph and attributes objects constructed from the file.
 */
public class SIFNetworkViewReader extends AbstractNetworkViewReader {
	private static final String DEF_DELIMITER = " ";
	private static final String LINE_SEP = System.getProperty("line.separator");
	private static final String INTERACTION = "interaction";

	private final Set<Interaction> interactions = new HashSet<Interaction>();
	private final ReadUtils readUtil;
	private final CyLayouts layouts;
	private boolean cancelled = false;

	public SIFNetworkViewReader(InputStream is, ReadUtils readUtil, CyLayouts layouts, CyNetworkViewFactory cyNetworkViewFactory, CyNetworkFactory cyNetworkFactory) {
		super(is,cyNetworkViewFactory, cyNetworkFactory);
		this.readUtil = readUtil;
		this.layouts = layouts;
	}

	@Override
	public void run(TaskMonitor tm) throws IOException {
		try {
			readInput(tm);
			createNetwork(tm);
		} finally { 
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
		}
	}

	@Override
	public void cancel() {
		cancelled = true;
	}

	private void readInput(TaskMonitor tm) throws IOException {
		tm.setProgress(0.00);
		String delimiter = DEF_DELIMITER;

		final String rawText = readUtil.getInputString(inputStream);

		tm.setProgress(0.10);
		if (rawText.indexOf("\t") >= 0)
			delimiter = "\t";

		final String[] lines = rawText.split(LINE_SEP);

		tm.setProgress(0.15);
		final int size = lines.length;
		for (int i = 0; i < size; i++) {
			if (lines[i].length() <= 0)
				continue;
			interactions.add(new Interaction(lines[i], delimiter));
		}
		tm.setProgress(0.20);
	}

	private void createNetwork(TaskMonitor tm) {
		
		// Create network model.  At this point, there are no nodes/edges.
		final CyNetwork network = cyNetworkFactory.getInstance();
		
		Map<String, CyNode> nodeMap = new HashMap<String, CyNode>();

		// put all node names in the Set
		for (Interaction interaction : interactions) {
			nodeMap.put(interaction.getSource(), null);
			for (String target : interaction.getTargets())
				nodeMap.put(target, null);
		}

		tm.setProgress(0.25);
				
		for (String nodeName : nodeMap.keySet()) {
			if (cancelled)
				return;

			//tm.setProgress(progress);
			
			final CyNode node = network.addNode();
			node.attrs().set(NODE_NAME_ATTR_LABEL, nodeName);
			nodeMap.put(nodeName, node);
		}

		tm.setProgress(0.65);
		
		// Now loop over the interactions again, this time creating edges
		// between
		// all sources and each of their respective targets.
		String srcName;
		String interactionType;
		CyEdge edge;
		
		for (Interaction interaction : interactions) {
			if (cancelled)
				return;

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

		tm.setProgress(0.90);
		
		final CyNetworkView view = cyNetworkViewFactory.getNetworkView(network);
		
		layouts.getDefaultLayout().doLayout(view);
		
		// SIF always creates only one network.
		this.cyNetworkViews = new CyNetworkView[] { view };
		
		nodeMap.clear();
		nodeMap = null;

		tm.setProgress(1.0);
	}
}
