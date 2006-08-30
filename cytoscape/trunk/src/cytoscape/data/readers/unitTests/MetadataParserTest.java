package cytoscape.data.readers.unitTests;

import java.net.URISyntaxException;
import java.util.Map;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.readers.MetadataEntries;
import cytoscape.data.readers.MetadataParser;
import junit.framework.TestCase;

/**
 * Test cases for MetadataParser.<br>
 * 
 * @author kono
 * 
 */
public class MetadataParserTest extends TestCase {

	MetadataParser mdp;
	CyNetwork network;

	protected void setUp() throws Exception {
		super.setUp();
		network = Cytoscape.getCurrentNetwork();
		mdp = new MetadataParser(network);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testMakeNewMetadataMap() {
		Map newMap = mdp.makeNewMetadataMap();
		assertNotNull(newMap);
		assertEquals(MetadataEntries.values().length, newMap.size());
		assertEquals("http://www.cytoscape.org/", newMap
				.get(MetadataEntries.SOURCE.toString()));
		assertEquals("N/A", newMap.get(MetadataEntries.IDENTIFIER.toString()));
	}

	public void testSetMetadata() {
		System.out.println("### Metadata Parser is testing with network: "
				+ network.getTitle() + " ###");

		mdp.setMetadata(MetadataEntries.SOURCE, "Gene Ontology");
		mdp.setMetadata(MetadataEntries.DESCRIPTION,
				"DAG created form OBO file.");

		Map metadata = Cytoscape.getNetworkAttributes().getAttributeMap(
				network.getIdentifier(), mdp.DEFAULT_NETWORK_METADATA_LABEL);
		assertNotNull(metadata);
		assertEquals("Gene Ontology", metadata.get(MetadataEntries.SOURCE
				.toString()));
		assertEquals("DAG created form OBO file.", metadata
				.get(MetadataEntries.DESCRIPTION.toString()));

	}

}
