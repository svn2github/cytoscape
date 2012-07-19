
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

package cytoscape.ndb;

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
public class NDBGraphReader extends AbstractGraphReader implements GraphReader {
	// node id from file -> node
	private Map<String,CyNode> geneIdMap; 

	private List<Integer> nodeIds;
	private List<Integer> edgeIds;
	private URL fileURL;

	/**
	 * Creates a new SBMLGraphReader object.
	 *
	 * @param filename  DOCUMENT ME!
	 */
	public NDBGraphReader(String filename) {
		super(filename);
		fileURL = null;
	}

	/**
	 * Creates a new SBMLGraphReader object.
	 *
	 * @param url  DOCUMENT ME!
	 */
	public NDBGraphReader(URL url) {
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

		geneIdMap = new HashMap<String,CyNode>();

		try {

			// Actual work
			SAXBuilder builder = new SAXBuilder(false);
			Document doc = builder.build(instream);

			createGeneNodes( getTable(doc,"Genes") );
			createRegulatorsRegulatorsEdges( getTable(doc,"Regulators_Regulators") );
			createModuleNodeAttrs( getTable(doc,"Genes_Modules") );

		} catch (JDOMException je) { 
			throw new IOException("JDOM failure parsing file.",je); 
		}
	}

	private void createRegulatorsRegulatorsEdges(Element regulatorsRegulators) {
		for ( Object o : regulatorsRegulators.getChildren("Regulator_Gene") ) {
			Element regulatorGene = (Element)o;
			String regulatorId = regulatorGene.getAttributeValue("Regulator_Id");
			String geneId = regulatorGene.getAttributeValue("Gene_Id");
			boolean regulator = parseBoolean(regulatorGene.getAttributeValue("Regulator"));
			if ( regulatorId == null || 
			     geneId == null || 
			     !geneIdMap.containsKey(regulatorId) || 
				 !geneIdMap.containsKey(geneId) )
				continue;

			CyNode na = geneIdMap.get(regulatorId);
			CyNode nb = geneIdMap.get(geneId);
			CyEdge e = Cytoscape.getCyEdge(na,nb,Semantics.INTERACTION,"regulates",true,true);
			Cytoscape.getEdgeAttributes().setAttribute(e.getIdentifier(),"NDB Regulator",regulator);
			edgeIds.add( e.getRootGraphIndex() );
		}
	}

	private void createModuleNodeAttrs(Element genes) {
		for ( Object o : genes.getChildren("Gene_Module") ) {
			Element geneModule = (Element) o;
			String geneId = geneModule.getAttributeValue("Gene_Id");
			String moduleId = geneModule.getAttributeValue("Module_Id");
			CyNode n = geneIdMap.get(geneId);
			if ( n != null ) 
				Cytoscape.getNodeAttributes().setAttribute(n.getIdentifier(),"NDB Module ID",moduleId);
		}
	}

	private void createGeneNodes(Element genes) {
		for ( Object o : genes.getChildren("Gene") ) {
			Element gene = (Element) o;
			CyNode node = Cytoscape.getCyNode(gene.getAttributeValue("ORF"),true);
			String id = gene.getAttributeValue("Id");
			geneIdMap.put(id,node);
			nodeIds.add( node.getRootGraphIndex() );	
		}
	}

	private Element getTable(Document doc, String tableName) {
		for ( Object e : doc.getRootElement().getChildren("Table") )
			if ( tableName.equals(((Element)e).getAttributeValue("Type")) )
				return (Element)e;

		return null;
	}

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

	public int[] getNodeIndicesArray() {
		int[] nodes = new int[nodeIds.size()];

		for (int i = 0; i < nodes.length; i++)
			nodes[i] = nodeIds.get(i).intValue();

		return nodes;
	}

	public int[] getEdgeIndicesArray() {
		int[] edges = new int[edgeIds.size()];

		for (int i = 0; i < edges.length; i++)
			edges[i] = edgeIds.get(i).intValue();

		return edges;
	}

	private boolean parseBoolean(String s) {
		if ( s == null )
			return false;
		return s.toLowerCase().equals("true");
	}
}
