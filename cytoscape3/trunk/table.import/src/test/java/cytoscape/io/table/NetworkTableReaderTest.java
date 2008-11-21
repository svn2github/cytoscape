
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
import cytoscape.io.table.reader.NetworkTableMappingParameters;
import cytoscape.io.table.reader.NetworkTableReader;
import cytoscape.io.table.reader.TextFileDelimiters;
import junit.framework.TestCase;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Test cases for network & edge attributes table reader.
 *
 * @since Cytoscape 2.4
 * @version 0.6
 *
 * @author kono
 *
 */
public class NetworkTableReaderTest extends TestCase {
	private NetworkTableReader reader;

	/*
	 * Test file: galFiltered.sif + some edge attributes.
	 */
	private static final String TEST_TABLE = "src/test/resources/testData/galFiltered.txt";

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
		File network = new File(TEST_TABLE);

		List<String> delimiters = new ArrayList<String>();
		delimiters.add(TextFileDelimiters.TAB.toString());

		String[] galAttrName = {
		                           "Source", "Target", "Interaction", "edge bool attr",
		                           "edge string attr", "edge float attr"
		                       };
		Byte[] galAttrTypes = {
		                          CyAttributes.TYPE_STRING, CyAttributes.TYPE_STRING,
		                          CyAttributes.TYPE_STRING, CyAttributes.TYPE_BOOLEAN,
		                          CyAttributes.TYPE_STRING, CyAttributes.TYPE_FLOATING
		                      };
		NetworkTableMappingParameters mapping = new NetworkTableMappingParameters(delimiters,
		                                                                          TextFileDelimiters.PIPE
		                                                                          .toString(),
		                                                                          galAttrName,
		                                                                          galAttrTypes,
		                                                                          null, null, 0, 1,
		                                                                          2, null);

		reader = new NetworkTableReader(network.getName(), network.toURL(), mapping, 0, null);

		CyNetwork net = Cytoscape.createNetwork(reader, false, null);

		/*
		 * test cases
		 */
		assertEquals(331, net.getNodeCount());
		assertEquals(362, net.getEdgeCount());

		CyAttributes attr = Cytoscape.getEdgeAttributes();
		assertTrue(attr.getBooleanAttribute("YGL122C (pp) YOL123W", "edge bool attr"));
		assertFalse(attr.getBooleanAttribute("YKR026C (pp) YGL122C", "edge bool attr"));

		assertEquals(1.2344543, attr.getDoubleAttribute("YBL026W (pp) YOR167C", "edge float attr"));
		assertEquals("abcd12706",
		             attr.getStringAttribute("YBL026W (pp) YOR167C", "edge string attr"));
		assertEquals("abcd12584", attr.getStringAttribute("YPL248C (pd) ?", "edge string attr"));

		Cytoscape.destroyNetwork(net);
	}
}
