package edu.ucsd.bioeng.idekerlab.biomartclient.tests;

import java.io.File;
import java.util.Map;

import junit.framework.TestCase;
import edu.ucsd.bioeng.idekerlab.biomartclient.BiomartStub;

public class BiomartStubTest extends TestCase {

	BiomartStub stub;
	
	protected void setUp() throws Exception {
		super.setUp();
		File f = new File("src/test/resources/mart.registry.xml");
		stub = new BiomartStub(f.toURI().toURL().toString());
		stub.getRegistry();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetRegistry() throws Exception {
		Map<String, Map<String, String>> reg = stub.getRegistry();
		
		assertEquals(54, reg.keySet().size());
	}
	
	public void testGetAvailableDatasets() throws Exception {
		Map<String, String> result = stub.getAvailableDatasets("ensembl");
		
		assertTrue(result.keySet().contains("oprinceps_gene_ensembl"));
		assertTrue(result.keySet().contains("etelfairi_gene_ensembl"));
		assertTrue(result.keySet().contains("drerio_gene_ensembl"));
		assertTrue(result.keySet().contains("cjacchus_gene_ensembl"));
		assertTrue(result.keySet().contains("ocuniculus_gene_ensembl"));

		assertEquals(51, result.keySet().size());
	}
	

/* getAllGOAnnotations requires and actual web service to work
	public void testGetAllGOAnnotations() throws Exception {
		// Get Human annotation.
		List<String[]> res = stub.getAllGOAnnotations("10090");
		Set<String> uniqueID = new HashSet<String>();
		
		for(String[] line: res) {
			uniqueID.add(line[0]);
			for(String entry: line) {
				
				System.out.print(entry + "\t");
			}
			System.out.println("");
		}
		System.out.println("======== Total " + uniqueID.size() + " annotated genes ==========");
		res.clear();
		uniqueID.clear();
		uniqueID = null;
		res = null;	
	}
*/

/* sendQuery requires an actual webservice to work
	public void testSendQuery() throws Exception {
		
		Dataset dataset;
		Attribute[] attrs;
		Filter[] filters;
		
		dataset = new Dataset("hsapiens_gene_ensembl");
		attrs = new Attribute[3];
		attrs[0] = new Attribute("ensembl_gene_id");
		attrs[1] = new Attribute("go");
		attrs[2] = new Attribute("evidence_code");
	
		filters = new Filter[1];
		filters[0] = new Filter("with_go", null);
		
		String query2 = XMLQueryBuilder.getQueryString(dataset, attrs, filters);
		
		List<String[]> res = stub.sendQuery(query2);
		
		
		Set<String> uniqueID = new HashSet<String>();
		
		for(String[] line: res) {
			uniqueID.add(line[0]);
			for(String entry: line) {
				
			System.out.print(entry + "\t");
			}
			System.out.println("");
		}
	
		System.out.println("======== Total " + uniqueID.size() + " annotated genes ==========");
		
	}
	*/

}
