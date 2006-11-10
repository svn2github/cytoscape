package cytoscape.data.readers;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;


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
	
	private static final String NETWORK_FILE = "testData/galFiltered.xls";
	private NetworkTableReader reader;
	

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testReadTable() throws Exception {
		
		File network = new File(NETWORK_FILE);

		POIFSFileSystem excelIn = new POIFSFileSystem(new FileInputStream(
				network));
		HSSFWorkbook wb = new HSSFWorkbook(excelIn);

		HSSFSheet sheet = wb.getSheetAt(0);
		
		
		List<String> delimiters = new ArrayList<String>();
		delimiters.add(TextFileDelimiters.TAB.toString());

		String[] galAttrName = { "Source", "Target", "Interaction",
				"edge bool attr", "edge string attr", "edge float attr" };
		byte[] galAttrTypes = { CyAttributes.TYPE_STRING,
				CyAttributes.TYPE_STRING, CyAttributes.TYPE_STRING,
				CyAttributes.TYPE_BOOLEAN, CyAttributes.TYPE_STRING,
				CyAttributes.TYPE_FLOATING };
		NetworkTableMappingParameters mapping = new NetworkTableMappingParameters(
				delimiters, TextFileDelimiters.PIPE.toString(), galAttrName,
				galAttrTypes, null, 0, 1, 2, null);

		reader = new ExcelNetworkSheetReader(wb.getSheetName(0), sheet, mapping);

		CyNetwork net = Cytoscape.createNetwork(reader, false, null);
		
		/*
		 * test cases
		 */
		assertEquals("Yeast Network Sheet 1", net.getTitle());
		assertEquals(331, net.getNodeCount());
		assertEquals(362, net.getEdgeCount());
		
		CyAttributes attr = Cytoscape.getEdgeAttributes();
		assertTrue(attr.getBooleanAttribute("YGL122C (pp) YOL123W",
				"edge bool attr"));
		assertFalse(attr.getBooleanAttribute("YKR026C (pp) YGL122C",
				"edge bool attr"));

		assertEquals(1.2344543, attr.getDoubleAttribute("YBL026W (pp) YOR167C",
				"edge float attr"));
		assertEquals("abcd12706", attr.getStringAttribute(
				"YBL026W (pp) YOR167C", "edge string attr"));
		assertEquals("abcd12584", attr.getStringAttribute("YPL248C (pd) ?",
				"edge string attr"));
		
		Cytoscape.destroyNetwork(net);
		
	}

}
