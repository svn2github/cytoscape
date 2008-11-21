
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.io.table;

import cytoscape.Cytoscape;
import cytoscape.io.table.reader.AttributeMappingParameters;
import cytoscape.io.table.reader.DefaultAttributeTableReader;
import cytoscape.io.table.reader.TextFileDelimiters;
import static cytoscape.io.table.reader.TextFileDelimiters.COMMA;
import static cytoscape.io.table.reader.TextFileDelimiters.PIPE;
import cytoscape.io.table.reader.TextTableReader;
import junit.framework.TestCase;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DefaultAttributeTableReaderTest extends TestCase {
	private DefaultAttributeTableReader tableReader;

	/*
	 * Toy example created from galFiltered.sif and its attribute files.
	 */
	private static final String DATA_FILE = "src/test/resources/testData/annotation/galSubnetworkAnnotation2.txt";
	private static final String NETWORK_FILE = "src/test/resources/testData/galSubnetwork.sif";

	private static final String DATA_FILE2 = "src/test/resources/testData/annotation/annotationSampleForYeast.txt";
	private static final String NETWORK_FILE2 = "src/test/resources/testData/galFiltered.sif";

	protected void setUp() throws Exception {
		super.setUp();
		Cytoscape.buildOntologyServer();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testReadTable() throws Exception {
		File network = new File(NETWORK_FILE);
		File galNetwork = new File(NETWORK_FILE2);

		//
		CyNetwork net = Cytoscape.createNetworkFromFile(network.getAbsolutePath());
		File source = new File(DATA_FILE);

		CyNetwork galNet = Cytoscape.createNetworkFromFile(galNetwork.getAbsolutePath());

		File galSource = new File(DATA_FILE2);

		/*
		 * Test1
		 */
		List<String> delimiters = new ArrayList<String>();
		delimiters.add(TextFileDelimiters.TAB.toString());

		List<Integer> aliasList = new ArrayList<Integer>();
		aliasList.add(2);

		String[] galAttrName = {
		                           "ID", "ID in SGD", "Synonyms", "Description of Genes", "Date",
		                           "Sample Boolean Attr2", "gal1RGexp", "gal1RGsig", "String List"
		                       };
		Byte[] galAttrTypes = {
		                          CyAttributes.TYPE_STRING, CyAttributes.TYPE_STRING,
		                          CyAttributes.TYPE_STRING, CyAttributes.TYPE_STRING,
		                          CyAttributes.TYPE_INTEGER, CyAttributes.TYPE_BOOLEAN,
		                          CyAttributes.TYPE_FLOATING, CyAttributes.TYPE_FLOATING,
		                          CyAttributes.TYPE_SIMPLE_LIST
		                      };

		for (int i = 0; i < galAttrTypes.length; i++) {
			System.out.println("GAL Data Type " + i + " = " + galAttrTypes[i]);
		}

		AttributeMappingParameters mapping = new AttributeMappingParameters(TextTableReader.ObjectType.NODE,
		                                                                    null, COMMA.toString(),
		                                                                    0, "ID", aliasList,
		                                                                    galAttrName,
		                                                                    galAttrTypes, null, null);
		tableReader = new DefaultAttributeTableReader(source.toURL(), mapping, 0, null);
		tableReader.readTable();

		assertEquals("ribosomal protein S28A (S33A) (YS27)",
		             Cytoscape.getNodeAttributes()
		                      .getStringAttribute("YOR167C", "Description of Genes"));
		assertEquals(Integer.valueOf(20010118),
		             Cytoscape.getNodeAttributes().getIntegerAttribute("YHR141C", "Date"));
		//assertEquals(4, Cytoscape.getNodeAttributes().getListAttribute("YER112W", "alias").size());
//		assertEquals(7,
//		             Cytoscape.getNodeAttributes().getListAttribute("YDR277C", "String List").size());

		assertEquals("List",
		             Cytoscape.getNodeAttributes().getListAttribute("YDR277C", "String List").get(5));

		/*
		 * Test2
		 */
		List<Integer> aliases = new ArrayList<Integer>();
		aliases.add(2);

		String[] cols = { "Object Name in SGD", "key", "alias", "Taxon ID" };

		tableReader = new DefaultAttributeTableReader(galSource.toURL(),
		                                              TextTableReader.ObjectType.NODE, delimiters,
		                                              PIPE.toString(), 1, "ID", aliases, cols,
		                                              null, null, 0);
		tableReader.readTable();

		System.out.println("* YOL123W Object Name in SGD = "
		                   + Cytoscape.getNodeAttributes()
		                              .getStringAttribute("YOL123W", "Object Name in SGD"));
		assertEquals("S000005483",
		             Cytoscape.getNodeAttributes()
		                      .getStringAttribute("YOL123W", "Object Name in SGD"));
		assertEquals("S000006010",
		             Cytoscape.getNodeAttributes()
		                      .getStringAttribute("YPL089C", "Object Name in SGD"));
		assertEquals("taxon:4932",
		             Cytoscape.getNodeAttributes().getStringAttribute("YDR009W", "Taxon ID"));
		
//		assertTrue(Cytoscape.getNodeAttributes().getListAttribute("YOR315W", "alias")
//		                    .contains("SFG1"));

//		assertTrue(Cytoscape.getOntologyServer().getNodeAliases().getAliases("YLR319C")
//		                    .contains("AIP3"));

		Cytoscape.destroyNetwork(galNet);
		Cytoscape.destroyNetwork(net);
	}
}
