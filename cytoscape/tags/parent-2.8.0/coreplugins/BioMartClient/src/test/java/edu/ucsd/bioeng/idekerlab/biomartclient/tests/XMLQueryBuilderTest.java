package edu.ucsd.bioeng.idekerlab.biomartclient.tests;

import edu.ucsd.bioeng.idekerlab.biomartclient.utils.Attribute;
import edu.ucsd.bioeng.idekerlab.biomartclient.utils.Dataset;
import edu.ucsd.bioeng.idekerlab.biomartclient.utils.Filter;
import edu.ucsd.bioeng.idekerlab.biomartclient.utils.XMLQueryBuilder;
import junit.framework.TestCase;

public class XMLQueryBuilderTest extends TestCase {

	private Dataset dataset;
	private Attribute[] attrs;
	private Filter[] filters;
	
	protected void setUp() throws Exception {
	
		dataset = new Dataset("hsapiens_gene_ensembl");
		attrs = new Attribute[3];
		attrs[0] = new Attribute("hgnc_symbol");
		attrs[1] = new Attribute("entrezgene");
		attrs[2] = new Attribute("uniprot_swissprot");
	
		filters = new Filter[1];
		filters[0] = new Filter("entrezgene", "6736,100,662");
		
	}

	protected void tearDown() throws Exception {
	}

	public void testGetQueryString() {
		System.out.println("=================");
		
		assertNotNull(XMLQueryBuilder.getQueryString(dataset, attrs, filters));
	}

}
