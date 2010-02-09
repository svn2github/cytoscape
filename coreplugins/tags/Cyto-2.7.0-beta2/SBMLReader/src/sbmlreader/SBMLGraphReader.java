
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package sbmlreader;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

import cytoscape.data.readers.AbstractGraphReader;
import cytoscape.data.readers.GraphReader;

import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;

import cytoscape.visual.*;

import giny.model.Node;

import giny.view.GraphView;
import giny.view.NodeView;

import jigcell.sbml2.*;

import java.awt.Dimension;
import java.awt.Toolkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


/*
 * Created on September 27, 2005, 9:23 AM
 * This generates the Dialog for the loading of an SBML file and does the processing
 * of the SBML file using the java SBML2 library.
 */

/**
 * @author  W.P.A. Ligtenberg, Eindhoven University of Technology
 * @author  Mike Smoot
 */
public class SBMLGraphReader extends AbstractGraphReader implements GraphReader {
	ArrayList<Integer> nodeIds;
	ArrayList<Integer> edgeIds;
	URL fileURL;

	/**
	 * Creates a new SBMLGraphReader object.
	 *
	 * @param filename  DOCUMENT ME!
	 */
	public SBMLGraphReader(String filename) {
		super(filename);
		fileURL = null;
	}

	/**
	 * Creates a new SBMLGraphReader object.
	 *
	 * @param url  DOCUMENT ME!
	 */
	public SBMLGraphReader(URL url) {
		super(null);
		fileURL = url;
		fileName = null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	public void read() throws IOException {
		InputStream instream;

		if ((fileURL == null) && (fileName != null))
			instream = new FileInputStream(fileName);
		else if ((fileURL != null) && (fileName == null))
			instream = fileURL.openStream();
		else
			throw new IOException("No file to open!");

		// proces file
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

		nodeIds = new ArrayList<Integer>();
		edgeIds = new ArrayList<Integer>();

		SBMLLevel2Document document;

		try {
			document = SBMLLevel2Document.readDocument(instream);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw (ioe);
		}

		Model model = document.getModel();

		// Get all the species in the network
		List listOfSpecies = model.getSpecies();

		for (Iterator i = listOfSpecies.iterator(); i.hasNext();) {
			Species species = (Species) i.next();
			CyNode node = Cytoscape.getCyNode(species.getId(), true);
			nodeAttributes.setAttribute(species.getId(), "sbml name", species.getName());
			nodeAttributes.setAttribute(species.getId(), "sbml type", "species");
			nodeAttributes.setAttribute(species.getId(), "sbml id", species.getId());

			// optional attrs, but they'll never be null
			nodeAttributes.setAttribute(species.getId(), "sbml initial concentration",
			                            new Double(species.getInitialConcentration()));
			nodeAttributes.setAttribute(species.getId(), "sbml initial amount",
			                            new Double(species.getInitialAmount()));
			nodeAttributes.setAttribute(species.getId(), "sbml charge",
			                            new Integer(species.getCharge()));

			String comp = species.getCompartment();
			if ( comp != null )
				nodeAttributes.setAttribute(species.getId(), "sbml compartment", comp);

			nodeIds.add(node.getRootGraphIndex());
		}

		// Get all reactions and create a node
		List listOfReactions = model.getReactions();

		for (Iterator i = listOfReactions.iterator(); i.hasNext();) {
			Reaction reaction = (Reaction) i.next();
			CyNode node = Cytoscape.getCyNode(reaction.getId(), true);
			nodeAttributes.setAttribute(reaction.getId(), "sbml type", "reaction");
			nodeAttributes.setAttribute(reaction.getId(), "sbml id", reaction.getId());

			String rname = reaction.getName();
			if ( rname != null )
				nodeAttributes.setAttribute(reaction.getId(), "sbml name", rname);

			nodeIds.add(node.getRootGraphIndex());

			//Get all products and link them to the reaction node
			List products = reaction.getProduct();

			for (Iterator j = products.iterator(); j.hasNext();) {
				SpeciesReference species = (SpeciesReference) j.next();
				CyNode product = Cytoscape.getCyNode(species.getSpecies(), false);
				CyEdge edge = Cytoscape.getCyEdge(node, product, Semantics.INTERACTION,
				                                  "reaction-product", true);
				edgeIds.add(edge.getRootGraphIndex());
			}

			//Get all reactants and link them to the reaction node
			List reactants = reaction.getReactant();

			for (Iterator j = reactants.iterator(); j.hasNext();) {
				SpeciesReference species = (SpeciesReference) j.next();
				CyNode reactant = Cytoscape.getCyNode(species.getSpecies(), false);
				CyEdge edge = Cytoscape.getCyEdge(node, reactant, Semantics.INTERACTION,
				                                  "reaction-reactant", true);
				edgeIds.add(edge.getRootGraphIndex());
			}

			//Get all modifiers and link them to the reaction node
			List modifiers = reaction.getModifier();

			for (Iterator j = modifiers.iterator(); j.hasNext();) {
				ModifierSpeciesReference species = (ModifierSpeciesReference) j.next();
				CyNode modifier = Cytoscape.getCyNode(species.getSpecies(), false);
				CyEdge edge = Cytoscape.getCyEdge(modifier, node, Semantics.INTERACTION,
				                                  "reaction-modifier", true);
				edgeIds.add(edge.getRootGraphIndex());
			}

			KineticLaw law = reaction.getKineticLaw();

			if ( law == null )
				continue;

			List<Parameter> parameters = law.getParameter();

			if ( parameters == null )
				continue;

			for (Parameter p: parameters) {
				String id = p.getName();
				String units = p.getUnits();
				double value = p.getValue();
				nodeAttributes.setAttribute(reaction.getId(), "kineticLaw-"+id, new Double(value));
				if (units != null)
					nodeAttributes.setAttribute(reaction.getId(), "kineticLaw-"+id+"-units", units);
			}
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param network DOCUMENT ME!
	 */
	public void doPostProcessing(CyNetwork network) {
		// Set SBML specific visual style
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = manager.getCalculatorCatalog();

		VisualStyle vs = catalog.getVisualStyle(SBMLVisualStyleFactory.SBMLReader_VS);

		if (vs == null) {
			vs = SBMLVisualStyleFactory.createVisualStyle(network);
			catalog.addVisualStyle(vs);
		}

		manager.setVisualStyle(vs);
		Cytoscape.getCurrentNetworkView().setVisualStyle(vs.getName());
		Cytoscape.getCurrentNetworkView().applyVizmapper(vs);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int[] getNodeIndicesArray() {
		int[] nodes = new int[nodeIds.size()];

		for (int i = 0; i < nodes.length; i++)
			nodes[i] = nodeIds.get(i).intValue();

		return nodes;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int[] getEdgeIndicesArray() {
		int[] edges = new int[edgeIds.size()];

		for (int i = 0; i < edges.length; i++)
			edges[i] = edgeIds.get(i).intValue();

		return edges;
	}
}
