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
package cytoscape.data;

import cytoscape.data.ImportHandler;

import cytoscape.data.readers.GraphReader;
import cytoscape.data.readers.InteractionsReader;

import cytoscape.util.CyFileFilter;
import cytoscape.util.SIFFileFilter;

import junit.framework.TestCase;

import java.io.*;

import java.lang.String;

import java.util.*;


/**
 * Tests Implementation of ImportHandler.
 *
 * TODO:  Add to DataSuite
 */
public class ImportHandlerTest extends TestCase {
	private File DUMMY_SIF_FILE;
	private File DUMMY_XGMML_FILE;
	private File DUMMY_GML_FILE;
	private File DUMMY_XML_FILE;
	private File DUMMY_DOC_FILE;
	private GraphReader graphReader;
	private InteractionsReader DUMMY_GRAPH_READER;
	private Collection dummyCollection;
	private List DUMMY_LIST;
	private CyFileFilter DUMMY_DOC_FILTER;
	private CyFileFilter DUMMY_SIF_FILTER;
	private CyFileFilter DUMMY_XML_FILTER;
	private CyFileFilter DUMMY_XLS_FILTER;
	private static String DUMMY_GRAPH_NATURE = "NETWORK";
	private static String DUMMY_NATURE = "xxxx";
	private ImportHandler importHandler;

	/**
	 * Set things up.
	 * @throws Exception All Exceptions.
	 */
	public void setUp() throws Exception {
		importHandler = new ImportHandler();
		DUMMY_DOC_FILTER = new CyFileFilter("doc", "Documents", "dummy");
		DUMMY_XLS_FILTER = new CyFileFilter("xls", "Excel", "dummy");
		DUMMY_SIF_FILTER = new CyFileFilter("sif", "Another Sif Filter", DUMMY_GRAPH_NATURE);
		DUMMY_XML_FILTER = new CyFileFilter("xml", "Another Xml Filter", DUMMY_GRAPH_NATURE);
		DUMMY_SIF_FILE = File.createTempFile("inputSifTest", ".sif");
		DUMMY_XGMML_FILE = File.createTempFile("inputXgmmlTest", ".xgmml");
		DUMMY_GML_FILE = File.createTempFile("inputGmlTest", ".gml");
		DUMMY_XML_FILE = File.createTempFile("inputXmlTest", ".xml");
		DUMMY_DOC_FILE = File.createTempFile("inputDocTest", ".doc");
		DUMMY_GRAPH_READER = new InteractionsReader(DUMMY_SIF_FILE.toString());
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void tearDown() throws Exception {
		importHandler = null;
		DUMMY_DOC_FILTER = null;
		DUMMY_XLS_FILTER = null;
		DUMMY_SIF_FILTER = null;
		DUMMY_XML_FILTER = null;
		DUMMY_SIF_FILE.delete();
		DUMMY_XGMML_FILE.delete();
		DUMMY_GML_FILE.delete();
		DUMMY_XML_FILE.delete();
		DUMMY_DOC_FILE.delete();
		graphReader = null;
	}

	/**
	 * Tests ImportHandler Default Constructor.
	 */
	public void testConstructor() {
		// should contain three filters + the ALL File filter
		DUMMY_LIST = importHandler.getAllFilters();
		assertTrue(DUMMY_LIST != null);

		// test getSize
		int value = DUMMY_LIST.size();
		assertEquals(4, value);
	}

	/**
	 * Tests the initial set up ImportHandler with no extra filters registered.
	 */
	public void testGetAlls() {
		// Try getting descriptions for all files of type:  GRAPH_NATURE.
		dummyCollection = importHandler.getAllTypes(ImportHandler.GRAPH_NATURE);
		assertTrue(dummyCollection != null);
		assertEquals(3, dummyCollection.size());

		//  Validate one filter description
		boolean xgmmlFlag = false;
		Iterator iterator = dummyCollection.iterator();

		while (iterator.hasNext()) {
			String description = (String) iterator.next();

			if (description.equals("XGMML files")) {
				xgmmlFlag = true;
			}
		}

		assertTrue(xgmmlFlag);

		// Try getting a list of all registered file extensions.
		dummyCollection = importHandler.getAllExtensions();
		assertTrue(dummyCollection != null);
		assertEquals(4, dummyCollection.size());

		//  Validate one file extension.
		boolean sifFlag = false;
		iterator = dummyCollection.iterator();

		while (iterator.hasNext()) {
			String fileExtension = (String) iterator.next();

			if (fileExtension.equals("sif")) {
				sifFlag = true;
			}
		}

		assertTrue(sifFlag);

		// Try getting a list of all filter descriptions  (w/ file extensions).
		dummyCollection = importHandler.getAllDescriptions();
		assertTrue(dummyCollection != null);
		assertEquals(3, dummyCollection.size());

		//  Validate one filter description
		boolean gmlFlag = false;
		iterator = dummyCollection.iterator();

		while (iterator.hasNext()) {
			String description = (String) iterator.next();

			if (description.equals("GML files (*.gml)")) {
				gmlFlag = true;
			}
		}

		assertTrue(gmlFlag);

		//  Try getting descriptions w/ out extensions for filters of type: XXX,
		//  when no filters of type XXX exist.
		dummyCollection = importHandler.getAllTypes(DUMMY_NATURE);
		assertTrue(dummyCollection != null);

		//  Should be equal to 0, since no such filters exist.
		assertEquals(0, dummyCollection.size());

		//  Try getting filters of type:  XXX, when no filters of type XXX exist.
		DUMMY_LIST = importHandler.getAllFilters(DUMMY_NATURE);
		assertTrue(dummyCollection != null);

		//  Should be equal to 0, since no such filters exist.
		assertEquals(0, dummyCollection.size());

		// Try getting all filters
		dummyCollection = importHandler.getAllFilters();
		assertTrue(dummyCollection != null);
		assertEquals(4, dummyCollection.size());

		//  Validate one filter
		sifFlag = false;
		iterator = dummyCollection.iterator();

		while (iterator.hasNext()) {
			Object o = iterator.next();

			if (o instanceof SIFFileFilter) {
				sifFlag = true;
			}
		}

		assertTrue(sifFlag);

		//  Try getting only filters of a good nature
		DUMMY_LIST = importHandler.getAllFilters(DUMMY_GRAPH_NATURE);
		assertTrue(dummyCollection != null);
		assertEquals(4, dummyCollection.size());

		//  Validate one filter
		sifFlag = false;
		iterator = dummyCollection.iterator();

		while (iterator.hasNext()) {
			Object o = iterator.next();

			if (o instanceof SIFFileFilter) {
				sifFlag = true;
			}
		}

		assertTrue(sifFlag);
	}

	/**
	 * Tests the registering of new filters.
	 */
	public void testAddFilter() {
		importHandler.resetImportHandler();

		// try adding a duplicate filter;  this should fail
		boolean success = importHandler.addFilter(DUMMY_SIF_FILTER);
		assertEquals(false, success);

		// try adding a new filter; this should succeed
		success = importHandler.addFilter(DUMMY_DOC_FILTER);
		assertEquals(true, success);

		//  Should now have 5 filters.
		DUMMY_LIST = importHandler.getAllFilters();
		assertEquals(5, DUMMY_LIST.size());
	}

	/**
	 * Tests the registering of multiple filters.
	 */
	public void testAddFilters() {
		CyFileFilter[] cff1 = { DUMMY_SIF_FILTER, DUMMY_XML_FILTER };
		CyFileFilter[] cff2 = { DUMMY_DOC_FILTER, DUMMY_XLS_FILTER };

		//  reset, should have 4 filters
		importHandler.resetImportHandler();
		DUMMY_LIST = importHandler.getAllFilters();
		assertEquals(4, DUMMY_LIST.size());

		// try adding two duplicate filters;  this should fail
		boolean success = importHandler.addFilter(cff1);
		assertEquals(false, success);

		//  should still have 4 filters
		DUMMY_LIST = importHandler.getAllFilters();
		assertEquals(4, DUMMY_LIST.size());

		// try adding two new filters
		success = importHandler.addFilter(cff2);
		assertEquals(true, success);

		// should now have six filers
		DUMMY_LIST = importHandler.getAllFilters();
		assertEquals(6, DUMMY_LIST.size());
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testGetFileAttributes() {
		//check description
		String value = importHandler.getFileType(DUMMY_SIF_FILE.toString());
		assertEquals("SIF files", value);

		//check extension
		Collection extensions = importHandler.getAllExtensions();
		boolean exists = extensions.contains("sif");
		assertEquals(true, exists);

		//check reader
		//An arbitrary string call (locationless) should return null
		graphReader = importHandler.getReader(DUMMY_GRAPH_NATURE);
		assertEquals(null, graphReader);

		//a real file should return a real reader
		//test to make sure it's not null
		//error ouput showed that this does get an interactions reader
		graphReader = importHandler.getReader(DUMMY_SIF_FILE.toString());
		exists = (graphReader == null);
		assertFalse(exists);
	}

	//not sure if I should test a private method 
	//technically by testing all the public methods all the 
	//private ones are indirectly tested
	/*  public void testConcatAllExtensions()
	  {
	      DUMMY_LIST = importHandler.getAllFilters();
	      String[] Str1 = importHandler.concatAllExtensions();
	      DUMMY_LIST = importHandler.getAllFilters(DUMMY_GRAPH_FILTER);
	      String[] Str2 = importHandler.concatAllExtensions();
	      assertEquals (Str1, Str2);

	      //check that it added an "All filters" filter to the list
	      int value = List.size();
	      assertEquals(4, value);

	  }*/

	/**
	 * Runs just this one unit test.
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(ImportHandlerTest.class);
	}
}
