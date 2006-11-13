/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
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
package csplugins.dataviewer.mapper;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.CyAttributes;
import org.mskcc.dataservices.bio.Matrix;
import org.mskcc.dataservices.bio.StateInformation;
import org.mskcc.dataservices.mapper.Mapper;
import org.mskcc.dataservices.mapper.MapperException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import giny.model.Node;

/**
 * Maps State Information Object to Cytoscape Graph Nodes.
 *
 * @author Ethan Cerami
 */
public class MapStateInformationToGraph implements Mapper {
    /**
     * State Information Object.
     */
    private StateInformation stateInformation;

    /**
     * Cytoscape Network Object.
     */
    private CyNetwork cyNetwork;

    /**
     * HashMap of Matching Nodes.
     */
    private HashMap matchMap;

    /**
     * Constructor.
     *
     * @param stateInformation State Information object.
     * @param cyNetwork        CyNetwork Object.
     */
    public MapStateInformationToGraph(StateInformation stateInformation,
            CyNetwork cyNetwork) {
        this.stateInformation = stateInformation;
        this.cyNetwork = cyNetwork;
        this.matchMap = new HashMap();
    }

    /**
     * Do Mapping.
     *
     * @throws MapperException Indicates problem in mapping.
     */
    public void doMapping() throws MapperException {
        try {
            HashMap nodeMap = createNodeMap(cyNetwork);
            ArrayList matrices = stateInformation.getMatrices();
            for (int i = 0; i < matrices.size(); i++) {
                Matrix matrix = (Matrix) matrices.get(i);
                //  Only process matrices with actual data.
                if (matrix.getNumRows() > 0) {
                    int idColumn = matrix.getIdColumnNumber();
                    mapData(matrix, idColumn, nodeMap);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new MapperException(e, "This File "
                    + "does not contain any SOFT entities.  "
                    + "Please check the file or URL, and "
                    + "try again.");
        }
    }

    /**
     * Gets Number of Matches between SOFT Data and Network data.
     *
     * @return number of matches.
     */
    public int getNumMatches() {
        return matchMap.size();
    }

    /**
     * Maps Matrix Data.
     *
     * @param matrix  Object Matrix.
     * @param idCol   Column Number containing Identifer Information.
     * @param nodeMap HashMap of all Graph Nodes.
     */
    private void mapData(Matrix matrix, int idCol, HashMap nodeMap) {
        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
        for (int row = 0; row < matrix.getNumRows(); row++) {
            String id = matrix.getId(row);
            Node node = (Node) nodeMap.get(id);
            if (node != null) {
                matchMap.put(id, node);
                //  Assumption:  Real data begins directly after the
                //  ID Column.
                for (int i = idCol + 1; i < matrix.getNumColumns(); i++) {
                    String headerName = matrix.getDataHeaderName(i);
                    if (matrix.isDouble(row, i)) {
                        double value = matrix.getDataDouble(row, i);
                        nodeAttributes.setAttribute(headerName, id, value);
                    } else {
                        String value = matrix.getDataString(row, i);
                        nodeAttributes.setAttribute(headerName, id, value);
                    }
                }
            }
        }
    }

    /**
     * Creates a map of all existing nodes.
     *
     * @param network CyNetwork Object.
     * @return HashMap of existing nodes, indexed by name.
     */
    private HashMap createNodeMap(CyNetwork network) {
        HashMap nodeMap = new HashMap();
        List nodes = network.nodesList();
        for (int i = 0; i < nodes.size(); i++) {
            CyNode node = (CyNode) nodes.get(i);
            String id = node.getIdentifier();
            nodeMap.put(id, node);
        }
        return nodeMap;
    }
}
