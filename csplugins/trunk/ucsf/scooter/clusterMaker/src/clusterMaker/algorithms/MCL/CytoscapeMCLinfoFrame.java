package clusterMaker.algorithms.MCL;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;


public class CytoscapeMCLinfoFrame {
	
	private String text = "<html><head></head><body>" +
			"<h2>MCL clustering plugin for Cytoscape version 1.0</h2>" +
			"<br>" +
			"<h3>Authors</h3><br>" +
			"Leonard Apeltsin (Leonard.Apeltsin@ucsf.edu)<br>" +
			"Tobias Wittkop (Tobias.Wittkop@CeBiTec.Uni-Bielefeld.DE)<br>" +
			"Jan Baumbach (Jan.Baumbach@CeBiTec.Uni-Bielefeld.DE)<br>" +
			"<br>" +
			"<h3>Description</h3><br>" +
			"Given a similarity/distance graph, an inflation parameter and a probability threshold, the plugin computes node clusters by using " +
			"Markov clustering. The main idea of bioinformatics clustering by using MCL has been published in the TribeMCL publication: <br><br>" +
			"Enright AJ, Kunin V, Ouzounis CA (2003) Protein families and TRIBES in genome sequence space. Nucleic Acids Res. 2003 Aug 1;31(15):4632-8.<br><br>" +
			"PubMedID: 12888524<br><br>" +
			"<h3>Parameters</h3><br>" +
			"Edge weight attribute: Choose that edge attribute that corresponds to the " +
			"similarity/distance value (valid attributes: int, float, and double).<br><br>" +
			"Edge weight corresponds to ...: Choose whether the edge weight attribute corresponds " +
			"to similarity or distance values (between two nodes).<br><br>" +
			"Node attribute MCL cluster: Enter the node attribute name that will be used to store the cluster values in Cytoscape.<br><br>" +
			"Inflation parameter: The MCL inflation parameter determines the cluster density. See TribeMCL paper for details.<br><br>" +
			"Iterations: The number of interations for MCL clustering. See TribeMCL paper for details.<br><br>" +
			"Probability threshold: The exponent of the probability threshold to determine a non-overlapping clustering. See TribeMCL paper for details.<br><br>" +
			"Determine optimal threshold: Choose a node attribute name as gold standard cluster ID. Further choose the minimal inflation parameter (inflation: see above) " +
			"and the maximal inflation, as well as the stepsize. Press 'Run comparison' to start the evaluation of MCL Clustering for varying inflation parameters. Press " +
			"'stop comparison' to stop the procedure.<br><br>" +
			"</body></html>";
	
	public CytoscapeMCLinfoFrame() {
		
		JFrame f = new JFrame("Help/Info - MCL Clustering");
		
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





























