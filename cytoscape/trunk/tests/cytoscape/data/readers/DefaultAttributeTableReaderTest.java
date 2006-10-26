package cytoscape.data.readers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

public class DefaultAttributeTableReaderTest extends TestCase {

	private DefaultAttributeTableReader tableReader;

	private static final String DATA_FILE = "testData/goslim_annotation.txt";
	private static final String NETWORK_FILE = "testData/go_ccNetwork.sif";
	
	private static final String DATA_FILE2 = "testData/annotation/annotationSampleForYeast.txt";
	private static final String NETWORK_FILE2 = "testData/galFiltered.sif";

	protected void setUp() throws Exception {
		super.setUp();

	}

	protected void tearDown() throws Exception {
		super.tearDown();
		
	}

	public void testReadTable() throws IOException {
		File network = new File(NETWORK_FILE);
		File galNetwork = new File(NETWORK_FILE2);

		CyNetwork net = Cytoscape.createNetworkFromFile(network.getAbsolutePath());
		File source = new File(DATA_FILE);

		CyNetwork galNet = Cytoscape.createNetworkFromFile(galNetwork.getAbsolutePath());
		
		
		
		File galSource = new File(DATA_FILE2);
		
		List<String> delimiters = new ArrayList<String>();
		delimiters.add(TextFileDelimiters.TAB.toString());
		tableReader = new DefaultAttributeTableReader(source.toURL(),
				TextTableReader.ObjectType.NODE, delimiters);
		tableReader.readTable();
		
		List<Integer> aliases = new ArrayList<Integer>();
		aliases.add(2);
		String[] cols = {"Object Name in SGD", "key", "ali", "Taxon ID"};
		
		tableReader = new DefaultAttributeTableReader(galSource.toURL(),
				TextTableReader.ObjectType.NODE, delimiters, "\\|", 1, "ID", aliases, cols, null, null);
		tableReader.readTable();

		assertEquals("plasma membrane", Cytoscape.getNodeAttributes()
				.getStringAttribute("GO:0005886", "Ontology Term Name"));
		assertEquals("protein complex", Cytoscape.getNodeAttributes()
				.getStringAttribute("GO:0043234", "Ontology Term Name"));
		assertEquals("goslim_yeast", Cytoscape.getNodeAttributes()
				.getStringAttribute("GO:0005815", "Subset"));
		
		System.out.println("* YOL123W Object Name in SGD = " + Cytoscape.getNodeAttributes()
				.getStringAttribute("YOL123W", "Object Name in SGD"));
		assertEquals("S000005483", Cytoscape.getNodeAttributes()
				.getStringAttribute("YOL123W", "Object Name in SGD"));
		assertEquals("S000006010", Cytoscape.getNodeAttributes()
				.getStringAttribute("YPL089C", "Object Name in SGD"));
		assertEquals("taxon:4932", Cytoscape.getNodeAttributes()
				.getStringAttribute("YDR009W", "Taxon ID"));
		assertTrue(Cytoscape.getNodeAttributes().getAttributeList("YOR315W", "alias").contains("SFG1"));
		
		assertTrue(Cytoscape.getOntologyServer().getNodeAliases().getAliases("YLR319C").contains("AIP3"));
		
		Cytoscape.destroyNetwork(galNet);
		Cytoscape.destroyNetwork(net);
	}

}
