package edu.ucsd.bioeng.idekerlab.biomartclient.tests;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;
import java.io.File;

import edu.ucsd.bioeng.idekerlab.biomartclient.BiomartStub;
import edu.ucsd.bioeng.idekerlab.biomartclient.utils.QueryBuilderUtil;
import junit.framework.TestCase;

public class QueryBuilderUtilTest extends TestCase {

	BiomartStub stub;
	private final String queryString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Query count=\"\" datasetConfigVersion=\"0.6\" formatter=\"TSV\" header=\"1\" uniqueRows=\"1\" virtualSchemaName=\"default\"><Dataset name=\"hsapiens_gene_ensembl\"><Attribute name=\"ensembl_gene_id\"/></Dataset></Query>";
	
	protected void setUp() throws Exception {
		super.setUp();
        File f = new File("./src/test/resources/mart.registry.xml");
        stub = new BiomartStub(f.toURL().toString());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	
	public void testGetAllAliases() throws Exception {
		String query= QueryBuilderUtil.getAllAliases(null);	
		assertEquals( queryString, query );
	}
}
