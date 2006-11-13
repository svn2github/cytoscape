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
package csplugins.test.mapper;

import csplugins.dataviewer.mapper.MapInteractionsToGraph;
import csplugins.dataviewer.mapper.MapPsiInteractionsToGraph;
import csplugins.dataviewer.mapper.MapStateInformationToGraph;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import cytoscape.data.CyAttributes;
import junit.framework.TestCase;
import org.mskcc.dataservices.bio.StateInformation;
import org.mskcc.dataservices.live.interaction.ReadPsiFromFileOrWeb;
import org.mskcc.dataservices.live.state.ReadSoftFromFileOrWeb;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests the MapStateInformationToGraph Class.
 *
 * @author Ethan Cerami
 */
public class TestMapStateInformationToGraph extends TestCase {

    /**
     * Tests the StateInformation Mapper.
     *
     * @throws Exception All Exceptions.
     */
    public void testMapper() throws Exception {
        //  First, get some interactions from sample data file.
        ReadPsiFromFileOrWeb reader1 = new ReadPsiFromFileOrWeb();
        ArrayList interactions = reader1.getInteractionsFromUrl
                ("testData/psi_sample1.xml");

        //  Now Map to Cytocape Network Objects.
        CyNetwork network = Cytoscape.createNetwork("network1");
        MapInteractionsToGraph mapper1 = new MapPsiInteractionsToGraph
                (interactions, network, MapInteractionsToGraph.MATRIX_VIEW);
        mapper1.doMapping();

        //  Now, get some state information via the SOFTReader
        ReadSoftFromFileOrWeb reader2 = new ReadSoftFromFileOrWeb();
        StateInformation stateInformation =
                reader2.getStateInformation("testData/soft1.txt");
        MapStateInformationToGraph mapper2 = new MapStateInformationToGraph
                (stateInformation, network);
        mapper2.doMapping();

        //  Filter to YHR119W, and verify that Node has newly mapped
        //  state information attributes.
        List nodes = network.nodesList();
        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
        for (int i = 0; i < nodes.size(); i++) {
            CyNode node = (CyNode) nodes.get(i);
            String canonicalName = (String)
                    nodeAttributes.getStringAttribute(node.getIdentifier(),
                            Semantics.CANONICAL_NAME);
            if (canonicalName.equals("YHR119W")) {
                Double value1 = nodeAttributes.getDoubleAttribute("GSM6219",
                        node.getIdentifier());
                assertEquals(268.8, value1.doubleValue(), .001);
                Double value2 = nodeAttributes.getDoubleAttribute("GSM6220",
                        node.getIdentifier());
                assertEquals(141.4, value2.doubleValue(), .001);
                Double value3 = nodeAttributes.getDoubleAttribute("GSM6226",
                        node.getIdentifier());
                assertEquals(121.7, value3.doubleValue(), .001);
            }
        }
    }
}
