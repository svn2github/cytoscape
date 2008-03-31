package clusterMaker.algorithms.FORCE;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;


public class CytoscapeFORCEinfoFrame {
	
	private String text = "<html><head></head><body>" +
			"<h2>FORCE clustering plugin for Cytoscape version 1.0</h2>" +
			"<br>" +
			"<h3>Authors</h3><br>" +
			"Jan Baumbach (Jan.Baumbach@CeBiTec.Uni-Bielefeld.DE)<br>" +
			"Tobias Wittkop (Tobias.Wittkop@CeBiTec.Uni-Bielefeld.DE)<br>" +
			"Sita Lange (Sita.Lange@CeBiTec.Uni-Bielefeld.DE)<br>" +
			"<br>" +
			"<h3>Description</h3><br>" +
			"Given a similarity/distance graph and a threshold, the plugin computes node clusters by heuristically solving the " +
			"weighted graph cluster editing problem (WGCEP; also known as wegihted transitive graph projection). The main idea of " +
			"bioinformatics clustering by using WGCEP has been published in the following publication: <br><br>" +
			"Rahmann S, Wittkop T, Baumbach J, Martin M, Truss A, Boecker S (2007) Exact and Heuristic Algorithms for Weighted Cluster Editing. In Proc. 6th CSB, volume 6 of Computational Systems Bioinformatics. Imperial College Press, 2007.<br><br>" +
			"PubMedID: 17951842<br><br>" +
			"The FORCE algorithm itself was used for protein (super) family detection. The study including a detailed description of " +
			"the basic principle is published in the following article. Please cite:<br><br>" +
			"Wittkop T, Baumbach J, Lobo FP, Rahmann S (2007) Large scale clustering of protein sequences with FORCE -- A layout based heuristic for weighted cluster editing. BMC Bioinformatics 2007, 8:396.<br><br>" +
			"PubMedID:17941985<br><br>" +
			"<h3>Parameters</h3><br>" +
			"Edge weight attribute: Choose that edge attribute that corresponds to the " +
			"similarity/distance value (valid attributes: int, float, and double).<br><br>" +
			"Edge weight corresponds to ...: Choose whether the edge weight attribute corresponds " +
			"to similarity or distance values (between two nodes).<br><br>" +
			"Threshold: The WGCEP threshold. In case of a similarity function, the higher the threshold, the smaller the clusters and the larger " +
			"the number of clusters (vice versa for a distance function). See FORCE paper for details.<br><br>" +
			"TMP dir: A temp directory used to write/read/delete temporary files.<br><br>" +
			"Assign connected components: Start a connected component analysis. A node attribute specified in 'Node attribute connected component' (see below) is createded for each node in the selected graph. " +
			"All nodes of one connected component are assigned a number.<br><br>" +
			"Run FORCE: Apply the FORCE clustering algorithm to the selected graph. A node attribute specified in 'Node attribute FORCE cluster' (see below) is createded for each node. " +
			"All nodes of one cluster are assigned a number.<br><br>" +
			"Layouter options: Choose the number of dimensions and iterions used for the force-based graph layouting. Also " +
			"choose attraction/repultsion factor. Refer to the FORCE paper for detailed information.<br><br>" +
			"Merge nodes: Very similar nodes (for instance in case of a similarity function: edges with a similarity higher than the given " +
			"threshold) can be handled as one node. This increases speed and can help to increase the accuracy.<br><br>" +
			"Parameter training: Choose whether to use evolutionary training for the given number of generations in order to optimize" +
			"several fine tuning parameters (also see the FORCE paper for more details). Checking this option may increase run time drastically.<br><br>" +
			"Cytoscape: Enter the node attribute names that will be used to store the connected component and cluster values in Cytoscape.<br><br>" +
			"Determine optimal threshold: Choose a node attribute name as gold standard cluster ID. Further choose the minimal threshold (threshold: see above) " +
			"and the maximal threshold, as well as the stepsize. Press 'Run comparison' to start the evaluation of FORCE for varying thresholds. Press " +
			"'stop comparison' to stop the procedure.<br><br>" +
			"</body></html>";
	
	public CytoscapeFORCEinfoFrame() {
		
		JFrame f = new JFrame("Help/Info - FORCE");
		
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





























