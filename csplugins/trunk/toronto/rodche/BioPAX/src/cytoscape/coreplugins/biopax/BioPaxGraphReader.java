// $Id: TestExternalLinkUtil.java,v 1.11 2006/06/15 22:07:49 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross.
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package cytoscape.coreplugins.biopax;


import giny.model.RootGraph;
import giny.view.GraphView;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.CyNetworkNaming;
import cytoscape.coreplugins.biopax.mapping.MapBioPaxToCytoscape;
import cytoscape.coreplugins.biopax.mapping.MapNodeAttributes;
import cytoscape.coreplugins.biopax.style.BioPaxVisualStyleUtil;
import cytoscape.coreplugins.biopax.util.BioPaxUtil;
import cytoscape.coreplugins.biopax.util.cytoscape.CytoscapeWrapper;
import cytoscape.coreplugins.biopax.util.cytoscape.LayoutUtil;
import cytoscape.coreplugins.biopax.util.cytoscape.NetworkListener;
import cytoscape.coreplugins.biopax.view.BioPaxContainer;
import cytoscape.data.CyAttributes;
import cytoscape.data.readers.GraphReader;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.logger.CyLogger;

import org.biopax.paxtools.model.Model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * GraphReader Implementation for BioPAX Files.
 *
 * @author Ethan Cerami.
 * @author Igor Rodchenkov (re-factoring, using PaxTools API)
 */
public class BioPaxGraphReader implements GraphReader {
	private int[] nodeIndices;
	private int[] edgeIndices;
	private String fileName;
	private String networkName;
	private boolean validNetworkName;
	private CyLayoutAlgorithm layoutUtil;

	/**
	 * Constructor
	 *
	 * @param fileName File Name.
	 */
	public BioPaxGraphReader(String fileName) {
		this.fileName = fileName;
		layoutUtil = new LayoutUtil();
	}

	/**
	 * Read file.
	 *
	 * @throws IOException IO Error.
	 */
	public void read() throws IOException {
		// Load up Data
		Model model = BioPaxUtil.readFile(fileName);

		if(model == null) {
			return;
		}
		
		CyLogger.getLogger(BioPaxGraphReader.class).warn(
				"Model contains " + model.getObjects().size()
				+ " BioPAX elements.");
		
		// Set network name (also checks if it exists)
		networkName = getNetworkName(model);

		// Map BioPAX Data to Cytoscape Nodes/Edges
		MapBioPaxToCytoscape mapper = new MapBioPaxToCytoscape(model);
		mapper.doMapping();
		nodeIndices = mapper.getNodeIndices();
		if (nodeIndices.length == 0) {
			throw new IOException(
					"Pathway is empty!  Please check the BioPAX source file.");
		}
		edgeIndices = mapper.getEdgeIndices();
	}

	private String getNetworkName(Model model) {
		// make a network name from pathway name(s) or the file name
		String candidateName;
		String networkViewTitle = System.getProperty("biopax.network_view_title");
		if (networkViewTitle != null && networkViewTitle.length() > 0) {
			validNetworkName = true;
			System.setProperty("biopax.network_view_title", "");
			try {
				networkViewTitle = URLDecoder.decode(networkViewTitle, "UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				// if exception occurs leave encoded string, but cmon, utf-8 not supported ??
			}
			candidateName = networkViewTitle;
		} else {
			candidateName = BioPaxUtil.getName(model);
		}
		
		if(candidateName == null || "".equalsIgnoreCase(candidateName)) {
			int idx = fileName.lastIndexOf('/');
			if(idx==-1) idx = fileName.lastIndexOf('\\');
			candidateName = fileName.substring(idx+1);
		}
		
		// Take appropriate adjustments, if name already exists
		return CyNetworkNaming.getSuggestedNetworkTitle(candidateName);
	}

	/**
	 * Our implementation of GraphReader.getLayoutAlgorithm().
	 */
	public CyLayoutAlgorithm getLayoutAlgorithm() {
		return layoutUtil;
	}

	/**
	 * If yFiles available, perform organic layout,
	 * else matrix layout;  same as that provided by the SIF reader.
	 * Keep this for pre-2.5 support
	 *
	 * @param view CyNetworkView Object.
	 */
	public void layout(GraphView view) {
		layoutUtil.doLayout((CyNetworkView)view);
	}

	/**
	 * Get Node Indices.
	 *
	 * @return array of root graph node indices.
	 */
	public int[] getNodeIndicesArray() {
		return nodeIndices;
	}

	/**
	 * Get Edge Indices.
	 *
	 * @return array of root graph edge indices.
	 */
	public int[] getEdgeIndicesArray() {
		return edgeIndices;
	}

	/**
	 * Gets network name.
	 *
	 * @return network name.
	 */
	public String getNetworkName() {
		return networkName;
	}

	/**
	 * Executes Post-Processing on newly created network.
	 *
	 * @param cyNetwork CyNetwork object.
	 */
	public void doPostProcessing(CyNetwork cyNetwork) {
		/**
		 * Sets a network attribute which indicates this network
		 * is a biopax network
		 */
		CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();

		// get cyNetwork id
		String networkID = cyNetwork.getIdentifier();

		// set biopax network attribute
		networkAttributes.setAttribute(networkID, MapBioPaxToCytoscape.BIOPAX_NETWORK, Boolean.TRUE);

		//  Repair Canonical Name 
		MapBioPaxToCytoscape.repairCanonicalName(cyNetwork);

		// repair network name
		if (!validNetworkName) {
			MapBioPaxToCytoscape.repairNetworkName(cyNetwork);
		}

		//  Set default Quick Find Index
		networkAttributes.setAttribute(cyNetwork.getIdentifier(), "quickfind.default_index",
		                               MapNodeAttributes.BIOPAX_SHORT_NAME);

		// set url to pathway commons -
		// used for pathway commons context menus
		String urlToBioPAXWebServices = System.getProperty("biopax.web_services_url");
		if (urlToBioPAXWebServices != null && urlToBioPAXWebServices.length() > 0) {
			networkAttributes.setAttribute(cyNetwork.getIdentifier(),
										   "biopax.web_services_url",
										   urlToBioPAXWebServices);
			System.setProperty("biopax.web_services_url", "");
		}

		// set data source attribute
		// used for pathway commons context menus
		String dataSources = System.getProperty("biopax.data_sources");
		if (dataSources != null && dataSources.length() > 0) {
			networkAttributes.setAttribute(cyNetwork.getIdentifier(),
										   "biopax.data_sources",
										   dataSources);
			System.setProperty("biopax.data_sources", "");
		}

		//  Set-up the BioPax Visual Style
		final VisualStyle bioPaxVisualStyle = BioPaxVisualStyleUtil.getBioPaxVisualStyle();
		final VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		final CyNetworkView view = Cytoscape.getNetworkView(cyNetwork.getIdentifier());
		view.setVisualStyle(bioPaxVisualStyle.getName());
		manager.setVisualStyle(bioPaxVisualStyle);
		view.applyVizmapper(bioPaxVisualStyle);

		//  Set up BP UI
		CytoscapeWrapper.initBioPaxPlugInUI();

		BioPaxContainer bpContainer = BioPaxContainer.getInstance();
        bpContainer.showLegend();
        NetworkListener networkListener = bpContainer.getNetworkListener();
		networkListener.registerNetwork(cyNetwork);
	}

	/**
	 * Read in graph;  canonicalize all names.
	 * @deprecated Use read() instead.  Will be removed Dec 2006.
	 * @param canonicalizeNodeNames flag for canonicalization.
	 * @throws IOException IO Error.
	 */
	public void read(boolean canonicalizeNodeNames) throws IOException {
	}

	/**
	 * Get root graph.
	 * @deprecated Use Cytoscape.getRootGraph() instead. Will be removed Dec 2006.
	 * @return RootGraph Object.
	 */
	public RootGraph getRootGraph() {
		return null;
	}

	/**
	 * Get node attributes.
	 * @deprecated Use Cytoscape.getNodeAttributes() instead. Will be removed Dec 2006.
	 * @return CyAttributes object.
	 */
	public CyAttributes getNodeAttributes() {
		return null;
	}

	/**
	 * Get edge attributes.
	 * @deprecated Use Cytoscape.getEdgeAttributes() instead. Will be removed Dec 2006.
	 * @return CyAttributes object.
	 */
	public CyAttributes getEdgeAttributes() {
		return null;
	}

}
