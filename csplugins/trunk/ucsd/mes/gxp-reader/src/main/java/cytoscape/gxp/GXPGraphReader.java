
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

package cytoscape.gxp;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import org.jdom.input.SAXBuilder;


import cytoscape.data.readers.AbstractGraphReader;
import cytoscape.data.readers.GraphReader;

import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;

import cytoscape.visual.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.*;


/**
 * @author  Mike Smoot
 */
public class GXPGraphReader extends AbstractGraphReader implements GraphReader {
	// node id from file -> node
	private Map<String,CyNode> nodeIdMap; 

	// module id from file -> list of nodes in module
	private Map<String,List<CyNode>> moduleMap; 

	// regulator id from file -> list of nodes in regulator
	private Map<String,List<CyNode>> regulatorMap; 


	private List<Integer> nodeIds;
	private List<Integer> edgeIds;
	private URL fileURL;

	/**
	 * Creates a new SBMLGraphReader object.
	 *
	 * @param filename  DOCUMENT ME!
	 */
	public GXPGraphReader(String filename) {
		super(filename);
		fileURL = null;
	}

	/**
	 * Creates a new SBMLGraphReader object.
	 *
	 * @param url  DOCUMENT ME!
	 */
	public GXPGraphReader(URL url) {
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

		nodeIds = new ArrayList<Integer>();
		edgeIds = new ArrayList<Integer>();

		nodeIdMap = new HashMap<String,CyNode>();
		// module -> list of nodes in module
		moduleMap = new HashMap<String,List<CyNode>>();
		// regulator -> list of nodes in regulator
		regulatorMap = new HashMap<String,List<CyNode>>();

		try {

			// Actual work
			SAXBuilder builder = new SAXBuilder(false);
			Document doc = builder.build(instream);
	
			createNodes( getTable(doc,"Genes") );
			createModules( getTable(doc,"Genes_Modules") );
			createRegulators( getTable(doc,"Regulators_Regulators") );
			createEdges( getTable(doc, "Modules_Regulator") );

		} catch (JDOMException je) { 
			throw new IOException("JDOM failure parsing file.",je); 
		}
	}

	private void createEdges(Element moduleRegulators) {
		for ( Object o : moduleRegulators.getChildren("Module_Regulator") ) {
			Element moduleRegulator = (Element)o;
			String moduleId = moduleRegulator.getAttributeValue("Module_Id");
			String regId = moduleRegulator.getAttributeValue("Regulator_Id");
			if ( moduleId == null || regId == null || !moduleMap.containsKey(moduleId) || !regulatorMap.containsKey(regId) )
				continue;

			for ( CyNode na : regulatorMap.get(regId) ) {
				for (CyNode nb : moduleMap.get(moduleId) ) {
					CyEdge e = Cytoscape.getCyEdge(na,nb,Semantics.INTERACTION,"regulates",true,true);
					edgeIds.add( e.getRootGraphIndex() );
				}
			}
		}
	}

	private void createRegulators(Element regulators) {
		for ( Object o : regulators.getChildren("Regulator_Gene") ) {
			Element regulator = (Element) o;
			String regId = regulator.getAttributeValue("Regulator_Id");
			if ( !regulatorMap.containsKey(regId) )
				regulatorMap.put(regId,new ArrayList<CyNode>());
			CyNode node = nodeIdMap.get( regulator.getAttributeValue("Gene_Id") );
			if ( node == null ) {
				System.out.println("unable to createRegulator for gene_id " + regulator.getAttributeValue("Gene_Id"));
				continue;
			}
			regulatorMap.get(regId).add( node );
			Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(),"GXP_Regulator_Id",regId);
		}
	}

	private void createModules(Element geneModules) {
		for ( Object o : geneModules.getChildren("Gene_Module") ) {
			Element geneModule = (Element) o;
			CyNode node = nodeIdMap.get( geneModule.getAttributeValue("Gene_Id") );
			if ( node != null ) {
				String moduleId = geneModule.getAttributeValue("Module_Id");
				Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(),"GXP_Module_Id",moduleId);
				if ( !moduleMap.containsKey(moduleId) )
					moduleMap.put(moduleId,new ArrayList<CyNode>());
				moduleMap.get(moduleId).add(node);
			}
		}
	}

	private void createNodes(Element genes) {
		for ( Object o : genes.getChildren("Gene") ) {
			Element gene = (Element) o;
			CyNode node = Cytoscape.getCyNode(gene.getAttributeValue("ORF"),true);
			String id = gene.getAttributeValue("Id");
			System.out.println("creating node: " + id + "  " + node.getIdentifier());
			nodeIdMap.put(id,node);
			nodeIds.add( node.getRootGraphIndex() );	
		}
	}

	private Element getTable(Document doc, String tableName) {
		for ( Object e : doc.getRootElement().getChildren("Table") )
			if ( tableName.equals(((Element)e).getAttributeValue("Type")) )
				return (Element)e;

		return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param network DOCUMENT ME!
	 */
	public void doPostProcessing(CyNetwork network) {
	/*
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
		*/
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
