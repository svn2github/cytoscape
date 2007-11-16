package edu.ucsd.bioeng.idekerlab.biomartclient.tests;

import java.util.List;

import edu.ucsd.bioeng.idekerlab.biomartclient.BiomartStub;
import edu.ucsd.bioeng.idekerlab.biomartclient.utils.QueryBuilderUtil;
import junit.framework.TestCase;

public class QueryBuilderUtilTest extends TestCase {

	BiomartStub stub;
	
	protected void setUp() throws Exception {
		super.setUp();
		stub = new BiomartStub();
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	
	public void testGetAllAliases() throws Exception {
		List<String[]> res = stub.sendQuery(QueryBuilderUtil.getAllAliases(null));
		for(String[] r: res) {
			for(String c:r) {
				System.out.print("===" + c);
			}
			System.out.println("");
		}
		
		List<String> ds = stub.getAvailableDatasets("ensembl");
		int count = 1;
		for(String e: ds) {
			if(e.endsWith("gene_ensembl")) {
				System.out.println("Datasource " + count + " = " + e);
				count++;
			}
			
		}
	}
}
