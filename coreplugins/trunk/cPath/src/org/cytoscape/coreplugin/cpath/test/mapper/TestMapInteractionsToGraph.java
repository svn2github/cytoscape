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
package org.cytoscape.coreplugin.cpath.test.mapper;

import org.cytoscape.coreplugin.cpath.mapper.MapInteractionsToGraph;
import org.cytoscape.coreplugin.cpath.mapper.MapPsiInteractionsToGraph;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import cytoscape.data.CyAttributes;
import giny.model.Edge;
import junit.framework.TestCase;
import org.mskcc.dataservices.bio.vocab.CommonVocab;
import org.mskcc.dataservices.bio.vocab.InteractorVocab;
import org.mskcc.dataservices.mapper.MapPsiToInteractions;
import org.mskcc.dataservices.util.ContentReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Tests the MapInteractionsToGraph Class.
 *
 * @author Ethan Cerami.
 */
public class TestMapInteractionsToGraph extends TestCase {

    /**
     * Tests the MapPsiInteractionsTo Graph mapper.
     * This test assumes a new empty CyNetwork.
     *
     * @throws Exception All Exceptions.
     */
    public void testMapper1() throws Exception {
        //  First, get some interactions from sample data file.
        ArrayList interactions = new ArrayList();
        ContentReader reader = new ContentReader();
        String xml = reader.retrieveContent("testData/psi_sample1.xml");

        //  Map from PSI to DataService Interaction Objects.
        MapPsiToInteractions mapper1 = new MapPsiToInteractions
                (xml, interactions);
        mapper1.doMapping();

        //  Now Map to Cytocape Network Objects.
        CyNetwork network = Cytoscape.createNetwork("network1");
        MapInteractionsToGraph mapper2 = new MapPsiInteractionsToGraph
                (interactions, network, MapInteractionsToGraph.MATRIX_VIEW);
        mapper2.doMapping();

        //  Verify Number of Nodes and Number of Edges
        int nodeCount = network.getNodeCount();
        int edgeCount = network.getEdgeCount();
        assertEquals(7, nodeCount);
        assertEquals(6, edgeCount);

        List nodeList = network.nodesList();
        List edgeList = network.edgesList();

        //  Verify one of the nodes in the graph
        //  First find correct index value
        int index = -1;
        for (int i = 0; i < nodeList.size(); i++) {
            CyNode node = (CyNode) nodeList.get(i);
            if (node.getIdentifier().equals("YDL065C")) {
                index = i;
            }
        }

        CyNode node1 = (CyNode) nodeList.get(index);
        String nodeId1 = node1.getIdentifier();
        assertEquals("YDL065C", nodeId1);

        //  Verify edge in the graph
        //  First find correct index value
        index = -1;
        for (int i = 0; i < edgeList.size(); i++) {
            CyEdge edge = (CyEdge) edgeList.get(i);
            if (edge.getIdentifier().equals
                    ("YCR038C (classical two hybrid, pmid:  11283351) "
                            + "YDR532C")) {
                index = i;
            }
        }
        CyEdge edge1 = (CyEdge) edgeList.get(index);
        String edgeId1 = edge1.getIdentifier();
        assertEquals("YCR038C (classical two hybrid, pmid:  11283351) YDR532C",
                edgeId1);

        //  Verify source / target nodes of edge
        CyNode sourceNode = (CyNode) edge1.getSource();
        CyNode targetNode = (CyNode) edge1.getTarget();
        assertEquals("YCR038C", sourceNode.getIdentifier());
        assertEquals("YDR532C", targetNode.getIdentifier());

	CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();

        //  Verify that Attributes were mapped over too...
        String taxonomyId = nodeAttrs.getStringAttribute(sourceNode.getIdentifier(),
                InteractorVocab.ORGANISM_NCBI_TAXONOMY_ID);
        assertEquals("4932", taxonomyId);
        String fullName = nodeAttrs.getStringAttribute(sourceNode.getIdentifier(), 
	        InteractorVocab.FULL_NAME);
        assertTrue(fullName.indexOf("GTP/GDP exchange factor") > -1);

        //  Verify that DB Names were mapped over correctly.
        //  There are multiple DB Names in an array of Strings.
        List dbNamesList = nodeAttrs.getAttributeList
                (sourceNode.getIdentifier(), CommonVocab.XREF_DB_NAME);
        assertEquals(15, dbNamesList.size());
        assertEquals("RefSeq GI", dbNamesList.get(0));

        //  Verify that Interaction Xrefs were mapped over correctly.
        dbNamesList = Cytoscape.getEdgeAttributes().getAttributeList(edge1.getIdentifier(),
                CommonVocab.XREF_DB_NAME);
        List dbIdList = Cytoscape.getEdgeAttributes().getAttributeList
                (edge1.getIdentifier(), CommonVocab.XREF_DB_ID);
        assertEquals(2, dbNamesList.size());
        assertEquals(2, dbIdList.size());
        assertEquals("DIP", dbNamesList.get(0));
        assertEquals("CPATH", dbNamesList.get(1));
        assertEquals("61E", dbIdList.get(0));
        assertEquals("12345", dbIdList.get(1));
    }

    /**
     * Tests the MapPsiInteractionsTo Graph mapper.
     * This test assumes a pre-existing CyNetwork with existing nodes/edges.
     *
     * @throws Exception All Exceptions.
     */
    public void testMapper2() throws Exception {
        //  First, get some interactions from sample data file.
        ArrayList interactions = new ArrayList();
        ContentReader reader = new ContentReader();
        String xml = reader.retrieveContent("testData/psi_sample1.xml");

        //  Map from PSI to DataService Interaction Objects.
        MapPsiToInteractions mapper1 = new MapPsiToInteractions
                (xml, interactions);
        mapper1.doMapping();

        //  Create CyNetwork, and pre-populate it with some existing data.
        CyNetwork network = Cytoscape.createNetwork("network2");
        CyNode node1 = Cytoscape.getCyNode("YDL065C", true);
        CyNode node2 = Cytoscape.getCyNode("YCR038C", true);
        network.addNode(node1);
        network.addNode(node2);

        //  Create Edge between node1 and node2.
        CyEdge edge = Cytoscape.getCyEdge(node1, node2,
                Semantics.INTERACTION, "pp", true);
        edge.setIdentifier("YDL065C (classical two hybrid, pmid:  11283351) "
                + "YCR038C");

        //  Now map interactions to cyNetwork.
        MapInteractionsToGraph mapper2 = new MapPsiInteractionsToGraph
                (interactions, network, MapInteractionsToGraph.MATRIX_VIEW);
        mapper2.doMapping();

        //  Verify Number of Nodes;  it should still be 7.
        //  If the mapper failed to check for pre-existing nodes, it would be 9.
        int nodeCount = network.getNodeCount();
        assertEquals(7, nodeCount);

        //  Verify Number of Edges;  it should still be 6.
        //  If the mapper failed to check for pre-existing edges, it would be 7.
        int edgeCount = network.getEdgeCount();
        assertEquals(6, edgeCount);
    }

    /**
     * Tests the MapPsiInteractionsTo Graph mapper.
     * This time, we test that the MATRIX_VIEW works with # interactors > 2.
     *
     * @throws Exception All Exceptions.
     */
    public void testMapper3() throws Exception {
        //  First, get some interactions from sample data file.
        ArrayList interactions = new ArrayList();
        ContentReader reader = new ContentReader();
        String xml = reader.retrieveContent("testData/psi_sample2.xml");

        //  Map from PSI to DataService Interaction Objects.
        MapPsiToInteractions mapper1 = new MapPsiToInteractions
                (xml, interactions);
        mapper1.doMapping();

        //  Create CyNetwork
        CyNetwork network = Cytoscape.createNetwork("network3");

        //  Now map interactions to cyNetwork.
        MapInteractionsToGraph mapper2 = new MapInteractionsToGraph
                (interactions, network, MapInteractionsToGraph.MATRIX_VIEW);
        mapper2.doMapping();

        //  Verify Number of Nodes;  there should be 4.
        int nodeCount = network.getNodeCount();
        assertEquals(4, nodeCount);

        //  Verify Number of Edges; there should be 6
        int edgeCount = network.getEdgeCount();
        assertEquals(6, edgeCount);

        Iterator edgeIterator = network.edgesIterator();
        int counter = 0;
        while (edgeIterator.hasNext()) {
            Edge edge = (Edge) edgeIterator.next();
            String id = edge.getIdentifier();
            if (id.equals("A <--> C")) {
                counter++;
            } else if (id.equals("A <--> D")) {
                counter++;
            } else if (id.equals("B <--> C")) {
                counter++;
            } else if (id.equals("B <--> D")) {
                counter++;
            } else if (id.equals("C <--> D")) {
                counter++;
            } else if (id.equals("A <--> B")) {
                counter++;
            }
        }
        assertEquals(6, counter);
    }

    /**
     * Tests the MapPsiInteractionsTo Graph mapper.
     * This time, we test that the SPOKE_VIEW works with # interactors > 2.
     *
     * @throws Exception All Exceptions.
     */
    public void testMapper4() throws Exception {
        //  First, get some interactions from sample data file.
        ArrayList interactions = new ArrayList();
        ContentReader reader = new ContentReader();
        String xml = reader.retrieveContent("testData/psi_sample2.xml");

        //  Map from PSI to DataService Interaction Objects.
        MapPsiToInteractions mapper1 = new MapPsiToInteractions
                (xml, interactions);
        mapper1.doMapping();

        //  Create CyNetwork
        CyNetwork network = Cytoscape.createNetwork("network3");

        //  Now map interactions to cyNetwork.
        MapInteractionsToGraph mapper2 = new MapInteractionsToGraph
                (interactions, network, MapInteractionsToGraph.SPOKE_VIEW);
        mapper2.doMapping();

        //  Verify Number of Nodes;  there should be 4.
        int nodeCount = network.getNodeCount();
        assertEquals(4, nodeCount);

        //  Verify Number of Edges; there should be 3
        int edgeCount = network.getEdgeCount();
        assertEquals(3, edgeCount);

        Iterator edgeIterator = network.edgesIterator();
        int counter = 0;
        while (edgeIterator.hasNext()) {
            Edge edge = (Edge) edgeIterator.next();
            String id = edge.getIdentifier();
            if (id.equals("A <--> B")) {
                counter++;
            } else if (id.equals("A <--> C")) {
                counter++;
            } else if (id.equals("A <--> D")) {
                counter++;
            }
        }
        assertEquals(3, counter);
    }

    /**
     * Profile Loading of HPRD Data.
     *
     * @throws Exception All Exceptions.
     */
    public void profileHprd() throws Exception {
        ArrayList allInteractions = new ArrayList();

        //  First, get some interactions from sample data file.
        ContentReader reader = new ContentReader();
        String xml = reader.retrieveContent("testData/hprd.xml");

        //  Map from PSI to DataService Interaction Objects.
        for (int i = 0; i < 25; i++) {
            ArrayList interactions = new ArrayList();
            MapPsiToInteractions mapper1 = new MapPsiToInteractions
                    (xml, interactions);
            mapper1.doMapping();
            allInteractions.addAll(interactions);
        }

        //  Now Map to Cytocape Network Objects.
        System.out.println("Mapping to Cytoscape Network");
        System.out.println("Number of Interactions:  "
                + allInteractions.size());
        CyNetwork network = Cytoscape.createNetwork("network1");
        MapInteractionsToGraph mapper2 = new MapPsiInteractionsToGraph
                (allInteractions, network, MapInteractionsToGraph.MATRIX_VIEW);
        mapper2.doMapping();
        System.out.println("DONE");
    }

    /**
     * Main Method.  Used for JProfiler.
     *
     * @param args Command Line Arguments.
     * @throws Exception All Exceptions.
     */
    public static void main(String args[]) throws Exception {
        Date start = new Date();
        TestMapInteractionsToGraph test = new TestMapInteractionsToGraph();
        test.profileHprd();
        Date stop = new Date();
        long time = stop.getTime() - start.getTime();
        System.out.println("Time:  " + time);
    }
}
