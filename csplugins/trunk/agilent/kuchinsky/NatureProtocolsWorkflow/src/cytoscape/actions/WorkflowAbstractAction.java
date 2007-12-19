package cytoscape.actions;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import cytoscape.Cytoscape;

public class WorkflowAbstractAction extends WorkflowPanelAction {

	public WorkflowAbstractAction(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public void actionPerformed(ActionEvent e)
	{

			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), 
					"<html> Cytoscape is a free software package for visualizing, " +
					"modeling, and analyzing <br>" +
"molecular and genetic interaction networks. This protocol explains how to use <br>" +
"Cytoscape to analyze the results of mRNA expression profiling, and other functional <br>" +
"genomics and proteomics experiments, in the context of an interaction network <br>" +
"obtained for genes of interest. Five major steps are described: " +
"<ol><li> Obtaining a network for genes of interest, </li>" +
"<li> Displaying the network through layout algorithms, </li>" +
"<li> Integrating with gene expression and other functional attributes, </li>" +
"<li> Identifying putative complexes and functional modules, and </li> " + 
"<li> Identifying enriched Gene Ontology annotations in the network. </li></ol>" +
"These steps provide a broad sample of the types " +
"of analyses performed by Cytoscape." + 
"<p><p> This protocol is described in detail in the following article: " + 
"<br> Cline, M. <em>et al</em>, " +
"<strong>Integration of biological networks and gene expression " + 
"<br>data using Cytoscape, </strong>" + 
//"<br> Melissa S Cline, Michael Smoot, Ethan Cerami, Allan Kuchinsky, Nerius Landys, Chris Workman, " + 
//"<br> Rowan Christmas, Iliana Avila-Campilo, Michael Creech, Benjamin Gross, Kristina Hanspers, " + 
//"<br> Ruth Isserlin, Ryan Kelley, Sarah Killcoyne, Samad Lotia, Steven Maere, John Morris, " + 
//"<br> Keiichiro Ono, Vuk Pavlovic, Alexander R Pico, Aditya Vailaya, Peng-Liang Wang, Annette Adler, " +
//"<br> Bruce R Conklin, Leroy Hood, Martin Kuiper, Chris Sander, Ilya Schmulevich, Benno Schwikowski, " + 
//"<br> Guy J Warner, Trey Ideker & Gary D Bader" +    
"<em>Nature Protocols</em>, 2, 2366 - 2382 (2007)" +
"<br>Published online: 27 September 2007, " + 
"doi:10.1038/nprot.2007.324<html>");
	}	

}
