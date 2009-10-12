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

package org.cytoscape.io.internal.read.sif;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.io.internal.util.ReadUtils;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyDataTable;

import org.cytoscape.io.read.CyNetworkViewReaderFactory;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

/**
 * Reader for graphs in the interactions file format. Given the filename,
 * provides the graph and attributes objects constructed from the file.
 */
public class InteractionsReaderFactory implements CyNetworkViewReaderFactory {

	private static final String DEF_DELIMITER = " ";
	private static final String LINE_SEP = System.getProperty("line.separator");

	private static final String INTERACTION = "interaction";

	private final ReadUtils readUtil;
	private final CyFileFilter fileFilter;

	public InteractionsReaderFactory(ReadUtils readUtil,
		CyFileFilter fileFilter) {

		this.readUtil = readUtil;
		this.fileFilter = fileFilter;
	}

	public CyFileFilter getCyFileFilter()
	{
		return fileFilter;
	}

	public Task getReader(InputStream stream, CyNetworkView networkView)
	{
		return new InteractionsReader(stream, networkView.getSource());
	}

	class InteractionsReader implements Task
	{
		final InputStream inputStream;
		final CyNetwork network;
		boolean cancel = false;

		public InteractionsReader(InputStream inputStream, CyNetwork network)
		{
			this.inputStream = inputStream;
			this.network = network;
		}

		public void run(TaskMonitor taskMonitor) throws IOException {

			Set<Interaction> interactions = new HashSet<Interaction>();
			String delimiter = DEF_DELIMITER;

			taskMonitor.setStatusMessage("Reading contents of file");
			final String rawText = readUtil.getInputString(inputStream);
			if (cancel) return;

			if (rawText.indexOf("\t") >= 0)
				delimiter = "\t";

			final String[] lines = rawText.split(LINE_SEP);
			if (cancel) return;

			taskMonitor.setStatusMessage("Parsing contents of file");
			final int size = lines.length;
			for (int i = 0; i < size; i++) {
				taskMonitor.setProgress(i / ((double) size));
				if (cancel) return;
				if (lines[i].length() <= 0)
					continue;
				interactions.add(new Interaction(lines[i], delimiter));
			}

			taskMonitor.setStatusMessage("Creating network topology");
			Map<String, CyNode> nodeMap = new HashMap<String, CyNode>();

			// put all node names in the Set
			for (Interaction interaction : interactions) {
				if (cancel) return;
				nodeMap.put(interaction.getSource(), null);
				for (String target : interaction.getTargets())
					nodeMap.put(target, null);
			}

			taskMonitor.setStatusMessage("Assigning node attributes");
			CyNode node;
			for (String nodeName : nodeMap.keySet()) {
				if (cancel) return;
				node = network.addNode();
				node.attrs().set("name", nodeName);
				nodeMap.put(nodeName, node);
			}

			taskMonitor.setStatusMessage("Assigning edge attributes");
			// Now loop over the interactions again, this time creating edges
			// between
			// all sources and each of their respective targets.
			String srcName;
			String interactionType;
			CyEdge edge;

			for (Interaction interaction : interactions) {
				if (cancel) return;

				srcName = interaction.getSource();
				interactionType = interaction.getType();

				for (String tgtName : interaction.getTargets()) {
					edge = network.addEdge(nodeMap.get(srcName), nodeMap
							.get(tgtName), true);
					edge.attrs().set("name",
							srcName + " (" + interactionType + ") " + tgtName);
					edge.attrs().set(INTERACTION, interactionType);
				}
			}
		}

		public void cancel()
		{
			cancel = true;
		}
	}
}
