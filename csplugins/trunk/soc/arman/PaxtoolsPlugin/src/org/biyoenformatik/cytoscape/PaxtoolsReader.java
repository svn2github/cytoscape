/* Copyright 2008 - The Cytoscape Consortium (www.cytoscape.org)
 *
 * The Cytoscape Consortium is:
 * - Institute for Systems Biology
 * - University of California San Diego
 * - Memorial Sloan-Kettering Cancer Center
 * - Institut Pasteur
 * - Agilent Technologies
 *
 * Authors: B. Arman Aksoy, Thomas Kelder, Emek Demir
 * 
 * This file is part of PaxtoolsPlugin.
 *
 *  PaxtoolsPlugin is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PaxtoolsPlugin is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.biyoenformatik.cytoscape;

import cytoscape.data.readers.GraphReader;
import cytoscape.data.CyAttributes;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.util.CyNetworkNaming;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.Cytoscape;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.VisualMappingManager;
import cytoscape.view.CyNetworkView;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.level2.*;
import org.biopax.paxtools.io.jena.JenaIOHandler;
import org.biyoenformatik.cytoscape.util.BioPAXUtil;
import org.mskcc.biopax_plugin.util.cytoscape.CytoscapeWrapper;
import org.mskcc.biopax_plugin.util.cytoscape.NetworkListener;
import org.mskcc.biopax_plugin.style.BioPaxVisualStyleUtil;
import org.mskcc.biopax_plugin.view.BioPaxContainer;

import java.io.FileInputStream;
import java.io.IOException;

import giny.view.GraphView;

public class PaxtoolsReader implements GraphReader {
    private Model biopaxModel = null;
    private final String fileName;

    private int[] nodeIndices, edgeIndices;

    public PaxtoolsReader(String fileName) {
        this.fileName = fileName;
    }

    public void read() throws IOException {
        FileInputStream ioStream = new FileInputStream(fileName);
        biopaxModel = new JenaIOHandler().convertFromOWL(ioStream);

        BioPAXUtil.CytoscapeGraphElements csGraphEls
                        = BioPAXUtil.bioPAXtoCytoscapeGraph(biopaxModel);

        nodeIndices = new int[csGraphEls.nodes.size()];
        edgeIndices = new int[csGraphEls.edges.size()];

        int count = 0;
        for(CyNode node: csGraphEls.nodes)
            nodeIndices[count++] = node.getRootGraphIndex();

        count = 0;
        for(CyEdge edge: csGraphEls.edges)
            edgeIndices[count++] = edge.getRootGraphIndex();

    }

    public void layout(GraphView view) {
        getLayoutAlgorithm().doLayout((CyNetworkView) view);
    }

    public CyLayoutAlgorithm getLayoutAlgorithm() {
        CyLayoutAlgorithm myAlgorithm = cytoscape.layout.CyLayouts.getLayout("Organic");
        if( myAlgorithm == null )
            myAlgorithm = cytoscape.layout.CyLayouts.getDefaultLayout();

        return myAlgorithm;
    }

    public int[] getNodeIndicesArray() {
        return nodeIndices;
    }

    public int[] getEdgeIndicesArray() {
        return edgeIndices;
    }

    public void doPostProcessing(CyNetwork cyNetwork) {
        /**
             * Sets a network attribute which indicates this network
             * is a biopax network
             */
            CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();

            // get cyNetwork id
            String networkID = cyNetwork.getIdentifier();

            // set biopax network attribute
            networkAttributes.setAttribute(networkID, BioPAXUtil.BIOPAX_NETWORK, Boolean.TRUE);

            //  Repair Canonical Name
            BioPAXUtil.repairCanonicalName(cyNetwork);

            // repair network name
            if (getNetworkName().equals("")) {
                BioPAXUtil.repairNetworkName(cyNetwork);
            }

            //  Set default Quick Find Index
            networkAttributes.setAttribute(cyNetwork.getIdentifier(), "quickfind.default_index",
                                           BioPAXUtil.BIOPAX_SHORT_NAME);

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

    public String getNetworkName() {
        String backupName = "Unknown", networkName = null;

        for(pathway aPathway: biopaxModel.getObjects(pathway.class)) {
            String aName = BioPAXUtil.getNameSmart(aPathway);
            if( aName != null && aName.length() != 0 )
                backupName = aName; // back-up name
            else
                continue;

            if( aPathway.isPATHWAY_COMPONENTSof().isEmpty() )
                networkName = backupName;
        }

        return CyNetworkNaming.
                getSuggestedNetworkTitle( (networkName == null
                                            ? backupName
                                            : networkName ));
    }
}
