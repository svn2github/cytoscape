package edu.ucsd.bioeng.idekerlab.biomartclient.tests;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;

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
		BufferedReader reader = stub.sendQuery(QueryBuilderUtil.getAllAliases(null));
		String line;
		while ((line = reader.readLine()) != null)
			System.out.println(line);
		
		Map<String, String> ds = stub.getAvailableDatasets("ensembl");
		int count = 1;
		for(String e: ds.keySet()) {
			if(e.endsWith("gene_ensembl")) {
				System.out.println("Datasource " + count + " = " + e + "-" + ds.get(e));
				count++;
			}
			
		}
	}
}
