package edu.ucsd.bioeng.idekerlab.biomartclient.tests;

import java.util.List;
import java.util.Map;

import edu.ucsd.bioeng.idekerlab.biomartclient.BiomartStub;
import edu.ucsd.bioeng.idekerlab.biomartclient.utils.Attribute;
import edu.ucsd.bioeng.idekerlab.biomartclient.utils.Dataset;
import edu.ucsd.bioeng.idekerlab.biomartclient.utils.Filter;
import edu.ucsd.bioeng.idekerlab.biomartclient.utils.XMLQueryBuilder;
import junit.framework.TestCase;

public class BiomartStubTest extends TestCase {

	BiomartStub stub;
	
	protected void setUp() throws Exception {
		super.setUp();
		stub = new BiomartStub();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetRegistry() throws Exception {
		System.out.println("============ Test biomart registry ============");
		Map<String, Map<String, String>> reg = stub.getRegistry();
		
		// Number of datasources.  This number can be changed!
		assertEquals(reg.keySet().size(), 22);

		System.out.println("============ Test biomart registry Done! ============");
	}
	
	public void testGetAvailableDatasets() throws Exception {
		//stub.getAvailableDatasets("ensembl");
	}
	
	public void testGetAvailableAttributes() throws Exception {
		//stub.getAvailableAttributes("oanatinus_gene_ensembl");
	}
	
	

	public void testSendQuery() throws Exception {
		
		Dataset dataset;
		Attribute[] attrs;
		Filter[] filters;
		
		dataset = new Dataset("scerevisiae_gene_ensembl");
		attrs = new Attribute[2];
		attrs[0] = new Attribute("entrezgene");
		attrs[1] = new Attribute("uniprot_swissprot");
	
		filters = new Filter[1];
		filters[0] = new Filter("entrezgene", "852394");
		String query2 = XMLQueryBuilder.getQueryString(dataset, attrs, filters);
		
//		List<String[]> res = stub.sendQuery(query2);
//		
//		for(String[] line: res) {
//			for(String entry: line) {
//				System.out.print(entry + "\t");
//			}
//			System.out.println("");
//		}
	
	}

}
