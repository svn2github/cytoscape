package clusterExplorerPlugin;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;


public class ClusterExplorerInfoFrame {
	
	private String text = "<html><head></head><body>" +
			"<h2>ClusterExplorer plugin for Cytoscape version 1.0</h2>" +
			"<br>" +
			"<h3>Authors</h3><br>" +
			"Jan Baumbach (Jan.Baumbach@CeBiTec.Uni-Bielefeld.DE)<br>" +
			"Tobias Wittkop (Tobias.Wittkop@CeBiTec.Uni-Bielefeld.DE)<br>" +
			"<br>" +
			"<h3>Description</h3><br>" +
			"Given a similarity/distance graph, the plugin computes list of clusters/elements " +
			"ordered by a user-defined ranking method. Furthermore, the plugin plots the cluster " +
			"size distribution and the edge weight distributions for inter-cluster and intra-cluster " +
			"edges.<br><br>" +
			"<h3>Parameters</h3><br>" +
			"Edge weight attribute: Choose that edge attribute that corresponds to the " +
			"similarity/distance value (valid attributes: int, float, and double).<br><br>" +
			"Cluster ID attribute: Choose that node attribute which corresponds to the identifier " +
			"of the cluster, i.e. the attribute that assignes a node to a certain cluster.<br><br>" +
			"Edge weight corresponds to ...: Choose whether the edge weight attribute corresponds " +
			"to similarity or distance values (between two nodes). This affects the order " +
			"(ascending/descending) of the presented results tables.<br><br>" +
			"Weight of missing edges: Specify the edge weight of missing edges.<br><br>" +
			"Method:" +
			"<ul>" +
			"<li>Cluster - central element: Click at one node. Click at 'apply method to graph'. The plugin calculates for the " +
			"cluster of the clicked node a table. It shows all elements (nodes) within the " +
			"this cluster ordered by their mean similarity/distance to all other nodes in the cluster.</li>" +
			"<li>Cluster - sim. to other clusters: Click at one node. Click at 'apply method to graph'. The results table shows " +
			"a list of all clusters ordered by their mean similarity/distance to the selected " +
			"cluster (that cluster the clicked node is in).</li>" +
			"<li>Cluster - sim. to other elements: Click at one node. Click at 'apply method to graph'. The results table shows " +
			"a list of all nodes in the graph ordered by their mean similarity/distance to the " +
			"selected cluster (that cluster the clicked node is in)</li>" +
			"<li>Element - sim. to other clusters: Click at one node. Click at 'apply method to graph'. The results table shows " +
			"a list of all clusters in the graph ordered by their mean similarity/distance to " +
			"the selected element (node).</li>" +
			"<li>Elements - sim. to other elements: Click at one node. Click at 'apply method to graph'. The results table shows " +
			"a list of all nodes in the graph ordered by their mean similarity/distance to the " +
			"selected element (node).</li>" +
			"</ul><br>" +
			"Plot histograms: Choose the number of buckets for the histogram and click the corresponding button " +
			"to plot the cluster size or the edge weight distributions.<br><br>" +
			"Clustering comparison: Choose two node attributes corresponding to the gold standard cluster ID and to the cluster ID " +
			"to calculate precision, specifity and the F-measure of the clustering given the gold standard. precision = TP / (TP + FP), " +
			"recall = TP / (TP + FP), F-measure = 2*precision*recall/(precision+recall), with TP = true positive, FP = false positive, and " +
			"FN = false negative" +
			"</body></html>";
	
	public ClusterExplorerInfoFrame() {
		
		JFrame f = new JFrame("Help/Info - ClusterExplorer");
		
		JEditorPane e = new JEditorPane("text/html", this.text);
		
		f.getContentPane().add(new JScrollPane(e));
		
		f.setSize(new Dimension(600, 400));
		
		f.setVisible(true);
		
		e.setCaretPosition(0);
		e.setEditable(false);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width-f.getSize().width)/2;
		int y = (screenSize.height-f.getSize().height)/2;
		f.setLocation(x, y);
		
		
		
	}
	
	
}





























