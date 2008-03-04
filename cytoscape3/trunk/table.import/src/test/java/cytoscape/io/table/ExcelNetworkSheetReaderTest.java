
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

import org.cytoscape.GraphPerspective;
import cytoscape.Cytoscape;

import org.cytoscape.attributes.CyAttributes;

import cytoscape.io.table.reader.ExcelNetworkSheetReader;
import cytoscape.io.table.reader.NetworkTableMappingParameters;
import cytoscape.io.table.reader.NetworkTableReader;
import cytoscape.io.table.reader.TextFileDelimiters;

import junit.framework.TestCase;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.List;


/**
 * Test cases for Excel network file import.<br>
 *
 * @since Cytoscape 2.4
 *
 * @version 0.6
 * @author Keiichiro Ono
 *
 */
public class ExcelNetworkSheetReaderTest extends TestCase {
	private static final String NETWORK_FILE = "src/test/resources/testData/galFiltered.xls";
	private NetworkTableReader reader;

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

		POIFSFileSystem excelIn = new POIFSFileSystem(new FileInputStream(network));
		HSSFWorkbook wb = new HSSFWorkbook(excelIn);

		HSSFSheet sheet = wb.getSheetAt(0);

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

		reader = new ExcelNetworkSheetReader(wb.getSheetName(0), sheet, mapping);

		GraphPerspective net = Cytoscape.createNetwork(reader, false, null);

		/*
		 * test cases
		 */
		assertEquals("Yeast Network Sheet 1", net.getTitle());
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
