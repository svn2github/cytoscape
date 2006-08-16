package csplugins.test.quickfind.test;

import csplugins.widgets.autocomplete.index.Hit;
import csplugins.widgets.autocomplete.index.TextIndex;
import csplugins.quickfind.util.IndexType;
import csplugins.quickfind.util.QuickFind;
import csplugins.quickfind.util.QuickFindFactory;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import junit.framework.TestCase;

/**
 * Unit Test for Quick Find.
 *
 * @author Ethan Cerami.
 */
public class TestQuickFind extends TestCase {
    private static final String LOCATION = "location";
    private static final String NUCLEUS = "nucleus";
    private static final String CYTOPLASM = "cytoplasm";

    /**
     * Runs basic tests to verify node indexing.
     */
    public void testNodeIndexing() {

        //  Create Sample Network
        CyNetwork cyNetwork = Cytoscape.createNetwork("network1");
        CyNode node0 = Cytoscape.getCyNode("rain", true);
        CyNode node1 = Cytoscape.getCyNode("rainbow", true);
        CyNode node2 = Cytoscape.getCyNode("rabbit", true);
        CyNode node3 = Cytoscape.getCyNode("yellow", true);
        cyNetwork.addNode(node0);
        cyNetwork.addNode(node1);
        cyNetwork.addNode(node2);
        cyNetwork.addNode(node3);

        //  Create Sample Attributes
        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
        nodeAttributes.setAttribute(node0.getIdentifier(), LOCATION, CYTOPLASM);
        nodeAttributes.setAttribute(node1.getIdentifier(), LOCATION, CYTOPLASM);
        nodeAttributes.setAttribute(node2.getIdentifier(), LOCATION, NUCLEUS);
        nodeAttributes.setAttribute(node3.getIdentifier(), LOCATION, NUCLEUS);

        //  Index this network
        TaskMonitorBase monitor = new TaskMonitorBase();
        QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
        quickFind.addNetwork(cyNetwork, monitor);

        //  Verify default values
        String attributeKey = quickFind.getCurrentAttributeKey();
        assertEquals(QuickFind.UNIQUE_IDENTIFIER, attributeKey);
        IndexType indexType = quickFind.getCurrentIndexType();
        assertEquals(IndexType.NODE_INDEX, indexType);

        //  Verify that nodes have been indexed
        TextIndex textIndex = quickFind.getTextIndex(cyNetwork);
        Hit hits[] = textIndex.getHits("ra", Integer.MAX_VALUE);
        assertEquals(3, hits.length);
        assertEquals("rabbit", hits[0].getKeyword());
        assertEquals("rain", hits[1].getKeyword());
        assertEquals("rainbow", hits[2].getKeyword());

        //  Verify Embedded Nodes
        hits = textIndex.getHits("rain", Integer.MAX_VALUE);
        assertEquals(1, hits[0].getAssociatedObjects().length);
        assertEquals(node0, hits[0].getAssociatedObjects()[0]);

        //  Verify TaskMonitor data
        assertEquals("Indexing node attributes", monitor.getStatus());
        assertEquals(100, monitor.getPercentComplete());

        //  Now, try reindexing by LOCATION
        quickFind.reindexAllNetworks(IndexType.NODE_INDEX, LOCATION, monitor);

        //  Verify that nodes have been indexed
        textIndex = quickFind.getTextIndex(cyNetwork);
        hits = textIndex.getHits("nu", Integer.MAX_VALUE);
        assertEquals(1, hits.length);
        assertEquals(NUCLEUS, hits[0].getKeyword());

        //  Verify Embedded Nodes
        hits = textIndex.getHits(NUCLEUS, Integer.MAX_VALUE);
        assertEquals(2, hits[0].getAssociatedObjects().length);
        assertEquals(node3, hits[0].getAssociatedObjects()[0]);
        assertEquals(node2, hits[0].getAssociatedObjects()[1]);

        //  Try indexing on a non-existent attribute key.  This should
        //  do nothing silently, and should not throw any exceptions.
        quickFind.reindexAllNetworks(IndexType.NODE_INDEX, "TYPE", monitor);
    }
}

