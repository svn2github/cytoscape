package cytoscape.plugins.igraph;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public class NodeCount extends CytoscapeAction {

    public NodeCount(IgraphPlugin myPlugin) {
	super("Count Nodes");
	setPreferredMenu("Plugins");
    }

    public void actionPerformed(ActionEvent e) {
	JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Node count: " + countNodes());
    }

    public static int countNodes() {

	// igraph_vector_init(&edgesVector, 20);	
	// fillEdgesVector(&edgesVector)
	// igraph_empty(&graph, 0, 0);
	// igraph_add_edges(&graph, edgesVector, 0);
	// int nodesCount = igraph_vcount(&graph);
	// igraph_vector_destroy(edgesVector);
	// igraph_destroy(&graph);
	// return nodesCount;

	return 0;
    }
    
}