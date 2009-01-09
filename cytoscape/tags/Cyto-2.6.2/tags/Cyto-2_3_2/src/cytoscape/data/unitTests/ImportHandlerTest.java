
package cytoscape.data.unitTests;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import cytoscape.util.CyFileFilter;
import cytoscape.util.SIFFileFilter;
import cytoscape.util.XGMMLFileFilter;
import cytoscape.util.GMLFileFilter;
import cytoscape.data.readers.GraphReader;
import cytoscape.data.readers.InteractionsReader;
import cytoscape.data.ImportHandler;

import java.util.*;
import java.io.*;
import java.lang.String;

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
	private Collection DUMMY_COLLECTION;
	private List DUMMY_LIST;
	private CyFileFilter DUMMY_DOC_FILTER;
	private CyFileFilter DUMMY_SIF_FILTER;
	private CyFileFilter DUMMY_XML_FILTER;
	private CyFileFilter DUMMY_XLS_FILTER;
	private static String DUMMY_GRAPH_NATURE = "NETWORK";
	private static String DUMMY_NODE_NATURE = "NODE";
	private static String DUMMY_EDGE_NATURE = "EDGE";
	private static String DUMMY_PROPERTIES_NATURE = "PROPERTIES";
	private static String DUMMY_NATURE = "dummy";
	private ImportHandler importHandler;
	

    /**
     * Set things up.
     * @throws Exception All Exceptions.
     */
    public void setUp() throws Exception {
    	importHandler = new ImportHandler();
    	DUMMY_DOC_FILTER = new CyFileFilter(".doc", "Documents", "dummy");
    	DUMMY_XLS_FILTER = new CyFileFilter(".xls", "Excel", "dummy");
    	DUMMY_SIF_FILTER = new CyFileFilter(".sif",
    			"Another Sif Filter", DUMMY_GRAPH_NATURE);
    	DUMMY_XML_FILTER = new CyFileFilter(".xml",
    			"Another Xml Filter", DUMMY_GRAPH_NATURE);
    	DUMMY_SIF_FILE = File.createTempFile("inputSifTest", ".sif");
    	DUMMY_XGMML_FILE = File.createTempFile("inputXgmmlTest", ".xgmml");
    	DUMMY_GML_FILE = File.createTempFile("inputGmlTest", ".gml");
    	DUMMY_XML_FILE = File.createTempFile("inputXmlTest", ".xml");
    	DUMMY_DOC_FILE = File.createTempFile("inputDocTest", ".doc");
    	DUMMY_GRAPH_READER = new InteractionsReader(DUMMY_SIF_FILE.toString());
    }
    
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
     * Tests Boolean Values.
     */
    public void testConstructor() {
    	//should contain three filters
    	DUMMY_LIST = importHandler.getAllFilters();
    	boolean exists = (DUMMY_LIST == null);
    	assertEquals(false, exists);
    	
    	//test getSize
    	int value = DUMMY_LIST.size(); //with the ALL Files filter
    	assertEquals(4, value);
    }
    
    public void testGetAlls() {
    	
    	//Try getting descriptions w/ out extensions with bad nature
    	DUMMY_COLLECTION = importHandler.getAllTypes(DUMMY_NATURE);
    	boolean exists = (DUMMY_COLLECTION == null);
    	assertEquals(false, exists);
    	int value = DUMMY_COLLECTION.size();
    	assertEquals(0, value);
    	
    	//Try getting descriptions w/ out extensions with good nature
    	DUMMY_COLLECTION = importHandler.getAllTypes(DUMMY_GRAPH_NATURE);
    	exists = (DUMMY_COLLECTION == null);
    	assertEquals(false, exists);
    	value = DUMMY_COLLECTION.size();
    	assertEquals(3, value);
    	
    	//Try getting just extensions
    	DUMMY_COLLECTION = importHandler.getAllExtensions();
    	exists = (DUMMY_COLLECTION == null);
    	assertEquals(false, exists);
    	assertEquals(3, value);
    	
    	//Try getting descriptions
    	DUMMY_COLLECTION = importHandler.getAllDescriptions();
    	exists = (DUMMY_COLLECTION == null);
    	assertEquals(false, exists);
    	value = DUMMY_COLLECTION.size();
    	assertEquals(3, value);
    	
    	//Try getting filters
    	DUMMY_LIST = importHandler.getAllFilters();
    	exists = (DUMMY_LIST == null);
    	assertEquals(false, exists);
    	value = DUMMY_LIST.size();
    	assertEquals(4, value);
    	
    	//Try getting only filters of a bad nature
    	DUMMY_LIST = importHandler.getAllFilters(DUMMY_NATURE);
    	exists = (DUMMY_LIST == null);
    	assertEquals(false, exists);
    	value = DUMMY_LIST.size();
    	assertEquals(0, value);
    	
    	//Try getting only filters of a good nature
    	DUMMY_LIST = importHandler.getAllFilters(DUMMY_GRAPH_NATURE);
    	exists = (DUMMY_LIST == null);
    	assertEquals(false, exists);
    	value = DUMMY_LIST.size();
    	assertEquals(4, value);
    }
    
    public void testAddFilter() {
    
    	//try adding the same filter
    	boolean exists = importHandler.addFilter(DUMMY_SIF_FILTER);
    	assertEquals(true, exists);
    	
    	DUMMY_LIST = importHandler.getAllFilters();
    	int value = DUMMY_LIST.size();
    	assertEquals(5, value);
    	
    	//try adding a new filter
    	exists = importHandler.addFilter(DUMMY_DOC_FILTER);
    	assertEquals(true, exists);
    	
    	DUMMY_LIST = importHandler.getAllFilters();
    	value = DUMMY_LIST.size();
    	assertEquals(6, value);
    }
    
    public void testAddFilters(){
    	CyFileFilter[] cff1 = {DUMMY_SIF_FILTER, DUMMY_XML_FILTER};
    	CyFileFilter[] cff2 = {DUMMY_DOC_FILTER, DUMMY_XLS_FILTER};
    	CyFileFilter[] cff3 = {DUMMY_SIF_FILTER, DUMMY_DOC_FILTER};
    	
    	//try an array it already has
    	boolean exists = importHandler.addFilter(cff1);
    	assertEquals(false, exists);
    	
    	DUMMY_LIST = importHandler.getAllFilters();
    	int value = DUMMY_LIST.size();
    	assertEquals(6, value);
    	
    	//try an array it partly has
    	exists = importHandler.addFilter(cff3);
    	assertEquals(false, exists);
    	
    	DUMMY_LIST = importHandler.getAllFilters();
    	value = DUMMY_LIST.size();
    	assertEquals(6, value);
    	
    	//try an array it doesn't have
    	exists = importHandler.addFilter(cff2);
    	assertEquals(false, exists);
    	
    	DUMMY_LIST = importHandler.getAllFilters();
    	value = DUMMY_LIST.size();
    	assertEquals(6, value);
    }
    
    public void testGetFileAttributes() {
    	//check description
    	String value = importHandler.getFileType(DUMMY_SIF_FILE.toString());
    	assertEquals ("SIF files" , value);
    	
    	//check extension
    	Collection extensions = importHandler.getAllExtensions();
    	boolean exists = extensions.contains(".sif");
    	assertEquals (true, exists);
    	
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
    