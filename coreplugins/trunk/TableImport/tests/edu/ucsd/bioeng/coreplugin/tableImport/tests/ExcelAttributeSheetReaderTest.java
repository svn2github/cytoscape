
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

package edu.ucsd.bioeng.coreplugin.tableImport.tests;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import edu.ucsd.bioeng.coreplugin.tableImport.reader.AttributeMappingParameters;
import edu.ucsd.bioeng.coreplugin.tableImport.reader.ExcelAttributeSheetReader;
import edu.ucsd.bioeng.coreplugin.tableImport.reader.TextFileDelimiters;
import static edu.ucsd.bioeng.coreplugin.tableImport.reader.TextFileDelimiters.*;
import edu.ucsd.bioeng.coreplugin.tableImport.reader.TextTableReader;

import junit.framework.TestCase;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ExcelAttributeSheetReaderTest extends TestCase {
	private static final String WORKBOOK1 = "testData/annotation/galSubnetworkAnnotation3.xls";
	private static final String NETWORK_FILE = "testData/galSubnetwork.sif";

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
		/*
		 * Load test network
		 */
		File network = new File(NETWORK_FILE);
		CyNetwork net = Cytoscape.createNetworkFromFile(network.getAbsolutePath());

		/*
		 * Single Sheet Test
		 */
		InputStream is = null;
		POIFSFileSystem excelIn;
		try {
			is = new FileInputStream(WORKBOOK1);
			excelIn = new POIFSFileSystem(is);
		}
		finally {
			if (is != null) {
				is.close();
			}
		}
		
		HSSFWorkbook wb = new HSSFWorkbook(excelIn);

		HSSFSheet sheet = wb.getSheetAt(0);

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

		TextTableReader rd = new ExcelAttributeSheetReader(sheet, mapping, 0);
		rd.readTable();

		assertEquals("ribosomal protein S28A (S33A) (YS27)",
		             Cytoscape.getNodeAttributes()
		                      .getStringAttribute("YOR167C", "Description of Genes"));
		assertEquals(new Integer(20010118),
		             Cytoscape.getNodeAttributes().getIntegerAttribute("YHR141C", "Date"));
		//assertEquals(4, Cytoscape.getNodeAttributes().getListAttribute("YER112W", "alias").size());

		/*
		 * Multiple sheet test (not yet supported)
		 */
		Cytoscape.destroyNetwork(net);
	}
}
