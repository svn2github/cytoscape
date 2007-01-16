/*
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.coreplugin.psi_mi.plugin;

import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.data.readers.GraphReader;
import giny.model.RootGraph;
import giny.view.GraphView;
import giny.view.NodeView;
import org.cytoscape.coreplugin.psi_mi.cyto_mapper.MapToCytoscape;
import org.cytoscape.coreplugin.psi_mi.data_mapper.MapPsiOneToInteractions;
import org.cytoscape.coreplugin.psi_mi.data_mapper.MapPsiTwoFiveToInteractions;
import org.cytoscape.coreplugin.psi_mi.util.ContentReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * GraphReader Implementation for PSI-MI Files.
 *
 * @author Ethan Cerami.
 */
public class PsiMiGraphReader implements GraphReader {
    private int nodeIndices[];
    private int edgeIndices[];
    private String fileName;
    private String networkName;

    /**
     * Constructor
     *
     * @param fileName File Name.
     */
    public PsiMiGraphReader(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Read file.
     *
     * @throws IOException IO Error.
     */
    public void read() throws IOException {
        try {
            //  set network name - use pathway name
            networkName = fileName;

            ContentReader reader = new ContentReader();
            String xml = reader.retrieveContent(fileName);

            //  Map BioPAX Data to Cytoscape Nodes/Edges
            ArrayList interactions = new ArrayList();

            //  Pick one of two mappers
            int level2 = xml.indexOf("level=\"2\"");
            if (level2 > 0 && level2 < 500) {
                MapPsiTwoFiveToInteractions mapper = new MapPsiTwoFiveToInteractions(xml,
                        interactions);
                mapper.doMapping();
            } else {
                MapPsiOneToInteractions mapper = new MapPsiOneToInteractions(xml, interactions);
                mapper.doMapping();
            }

            //  Now Map to Cytocape Network Objects.
            MapToCytoscape mapper2 = new MapToCytoscape
                    (interactions, MapToCytoscape.SPOKE_VIEW);
            mapper2.doMapping();
            nodeIndices = mapper2.getNodeIndices();
            edgeIndices = mapper2.getEdgeIndices();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Perform matrix layout;  same as that provided by the SIF reader.
     *
     * @param view GraphView Object.
     */
    public void layout(GraphView view) {
        double distanceBetweenNodes = 50.0d;
        int columns = (int) Math.sqrt(view.nodeCount());
        Iterator nodeViews = view.getNodeViewsIterator();
        double currX = 0.0d;
        double currY = 0.0d;
        int count = 0;
        while (nodeViews.hasNext()) {
            NodeView nView = (NodeView) nodeViews.next();
            nView.setOffset(currX, currY);
            count++;
            if (count == columns) {
                count = 0;
                currX = 0.0d;
                currY += distanceBetweenNodes;
            } else {
                currX += distanceBetweenNodes;
            }
        }
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
        //  Currently no-op
    }

    /**
     * Read in graph;  canonicalize all names.
     *
     * @param canonicalizeNodeNames flag for canonicalization.
     * @throws IOException IO Error.
     * @deprecated Use read() instead.  Will be removed Dec 2006.
     */
    public void read(boolean canonicalizeNodeNames) throws IOException {
    }

    /**
     * Get root graph.
     *
     * @return RootGraph Object.
     * @deprecated Use Cytoscape.getRootGraph() instead. Will be removed Dec 2006.
     */
    public RootGraph getRootGraph() {
        return null;
    }

    /**
     * Get node attributes.
     *
     * @return CyAttributes object.
     * @deprecated Use Cytoscape.getNodeAttributes() instead. Will be removed Dec 2006.
     */
    public CyAttributes getNodeAttributes() {
        return null;
    }

    /**
     * Get edge attributes.
     *
     * @return CyAttributes object.
     * @deprecated Use Cytoscape.getEdgeAttributes() instead. Will be removed Dec 2006.
     */
    public CyAttributes getEdgeAttributes() {
        return null;
    }
}
