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
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.util.CyNetworkNaming;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.level2.pathway;
import org.biopax.paxtools.io.jena.JenaIOHandler;

import java.io.FileInputStream;
import java.io.IOException;

import giny.view.GraphView;

public class PaxtoolsReader implements GraphReader {
    private Model biopaxModel = null;
    private final String fileName;

    public PaxtoolsReader(String fileName) {
        this.fileName = fileName;
    }

    public void read() throws IOException {
        FileInputStream ioStream = new FileInputStream(fileName);
        biopaxModel = new JenaIOHandler().convertFromOWL(ioStream);
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
        // TODO
        return new int[0];
    }

    public int[] getEdgeIndicesArray() {
        // TODO
        return new int[0];
    }

    public void doPostProcessing(CyNetwork network) {
        // TODO
    }

    public String getNetworkName() {
        String backupName = "Unknown", networkName = null;

        for(pathway aPathway: biopaxModel.getObjects(pathway.class)) {
            String aName = (aPathway.getNAME() == null
                                ? aPathway.getSHORT_NAME()
                                : aPathway.getNAME());

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
