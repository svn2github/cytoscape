package org.cytoscape.io.internal.write.sif;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.TaskMonitor;

public class SifWriter implements CyWriter {

	// TODO this should come from model-api
	private static final String NODE_NAME_ATTR_LABEL = "name";
	private static final String INTERACTION_ATTR_LABEL = "interaction";
	
	private File outputFile;
	private CyNetwork network;

	public SifWriter(File outputFile, CyNetwork network) {
		this.outputFile = outputFile;
		this.network = network;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		final Writer writer = new PrintWriter(new FileWriter(outputFile));
		try {
			final String lineSep = System.getProperty("line.separator");
			final List<CyNode> nodeList = network.getNodeList();
	
			int i = 0;
			for ( CyNode node : nodeList ) {
				if (taskMonitor != null) {
					//  Report on Progress
					double percent = ((double) i++ / nodeList.size()) * 100.0;
					taskMonitor.setProgress(percent);
				}
	
				String canonicalName = node.attrs().get(NODE_NAME_ATTR_LABEL, String.class);
				List<CyEdge> edges = network.getAdjacentEdgeList(node, CyEdge.Type.ANY);
	
				if (edges.size() == 0) {
					writer.write(canonicalName + lineSep);
				} else {
					for ( CyEdge edge : edges ) {
	
						if (node == edge.getSource()) { //do only for outgoing edges
							CyNode target = edge.getTarget();
							String canonicalTargetName = target.attrs().get(NODE_NAME_ATTR_LABEL,String.class);
							String interactionName = edge.attrs().get(INTERACTION_ATTR_LABEL,String.class);
	
							if (interactionName == null) {
								interactionName = "xx";
							}
	
							writer.write(canonicalName);
							writer.write("\t");
							writer.write(interactionName);
							writer.write("\t");
							writer.write(canonicalTargetName);
							writer.write(lineSep);
						}
					} 
				} 
			} 
		} finally {
			writer.close();
		}
	}
	
	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

}
