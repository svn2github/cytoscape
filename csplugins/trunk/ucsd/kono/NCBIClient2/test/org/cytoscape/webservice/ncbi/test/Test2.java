package org.cytoscape.webservice.ncbi.test;



import gov.nih.ncbi.soap.eutils.gene.EFetchGeneService;
import gov.nih.ncbi.soap.eutils.gene.EFetchRequest;
import gov.nih.ncbi.soap.eutils.gene.EFetchResult;
import gov.nih.nlm.ncbi.soap.eutils.EUtilsService;
import gov.nih.nlm.ncbi.soap.eutils.EUtilsServiceSoap;
import gov.nih.nlm.ncbi.soap.eutils.esearch.ESearchRequest;
import gov.nih.nlm.ncbi.soap.eutils.esearch.ESearchResult;

import org.junit.Test;

public class Test2 {



	@Test
	public void TestNCBIService() {
		System.out.println("========= Testing NCBI 2 ==============");

		try {
			EUtilsService service = new EUtilsService();
			EUtilsServiceSoap ss = service.getEUtilsServiceSoap();
			ESearchRequest req = new ESearchRequest();
			req.setDb("gene");
			req.setTerm("tp53");
			req.setRetMax("15");

			ESearchResult res = ss.runESearch(req);

			
			System.out.println("Found ids: " + res.getCount());
			System.out.print("First " + res.getRetMax() + " ids: ");

			for (int i = 0; i < res.getIdList().getId().size(); i++) {
				System.out.print(res.getIdList().getId().get(i) + " ");
			}
			System.out.println();
			
			EFetchRequest req2 = new EFetchRequest();
			req2.setId("7157,24842");
			
			EFetchGeneService geneService = new EFetchGeneService();
			gov.nih.ncbi.soap.eutils.gene.EUtilsServiceSoap service2 = geneService.getEUtilsServiceSoap();
			
			EFetchResult res2 = service2.runEFetch(req2);
			System.out.println("========> res = " + res2.getEntrezgeneSet().getEntrezgene().get(0).getEntrezgeneSummary());
			
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

}
