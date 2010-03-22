package edu.ucsd.bioeng.idekerlab.biomartclient.tests;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import edu.ucsd.bioeng.idekerlab.biomartclient.BiomartStub;
import edu.ucsd.bioeng.idekerlab.biomartclient.utils.Attribute;
import edu.ucsd.bioeng.idekerlab.biomartclient.utils.Dataset;
import edu.ucsd.bioeng.idekerlab.biomartclient.utils.Filter;
import edu.ucsd.bioeng.idekerlab.biomartclient.utils.XMLQueryBuilder;

public class BiomartStubTest extends TestCase {

	BiomartStub stub;
	
	protected void setUp() throws Exception {
		super.setUp();
		stub = new BiomartStub();
		stub.getRegistry();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetRegistry() throws Exception {
//		System.out.println("============ Test biomart registry ============");
//		Map<String, Map<String, String>> reg = stub.getRegistry();
//		
//		// Number of datasources.  This number can be changed!
//		assertEquals(reg.keySet().size(), 22);

		System.out.println("============ Test biomart registry Done! ============");
	}
	
	public void testGetAvailableDatasets() throws Exception {
		System.out.println("============ Test biomart Available Datasets ============");
		
		Map<String, String> result = stub.getAvailableDatasets("ensembl");
		
		for(String key: result.keySet()) {
			System.out.println( key + " =====> " + result.get(key));
		}
		
		System.out.println("============ Test biomart Available Datasets DONE. ============");
	}
	
	public void testGetAvailableAttributes() throws Exception {
//		stub.getAvailableAttributes("oanatinus_gene_ensembl");
	}
	
	
	public void testGetAllGOAnnotations() throws Exception {
		// Get Human annotation.
//		List<String[]> res = stub.getAllGOAnnotations("10090");
//		Set<String> uniqueID = new HashSet<String>();
//		
//		for(String[] line: res) {
//			uniqueID.add(line[0]);
//			for(String entry: line) {
//				
//				System.out.print(entry + "\t");
//			}
//			System.out.println("");
//		}
//		System.out.println("======== Total " + uniqueID.size() + " annotated genes ==========");
//		res.clear();
//		uniqueID.clear();
//		uniqueID = null;
//		res = null;	
	}

	public void testSendQuery() throws Exception {
		
//		Dataset dataset;
//		Attribute[] attrs;
//		Filter[] filters;
//		
//		dataset = new Dataset("hsapiens_gene_ensembl");
//		attrs = new Attribute[3];
//		attrs[0] = new Attribute("ensembl_gene_id");
//		attrs[1] = new Attribute("go");
//		attrs[2] = new Attribute("evidence_code");
//	
//		filters = new Filter[1];
//		filters[0] = new Filter("with_go", null);
//		
//		String query2 = XMLQueryBuilder.getQueryString(dataset, attrs, filters);
//		
//		List<String[]> res = stub.sendQuery(query2);
//		
//		
//		Set<String> uniqueID = new HashSet<String>();
//		
//		for(String[] line: res) {
//			uniqueID.add(line[0]);
//			for(String entry: line) {
//				
//				System.out.print(entry + "\t");
//			}
//			System.out.println("");
//		}
//	
//		System.out.println("======== Total " + uniqueID.size() + " annotated genes ==========");
		
	}

}
