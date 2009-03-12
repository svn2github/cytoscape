package org.cytoscape.webservice.psicquic.ontology;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.ebisearchservice.EBISearchService;
import uk.ac.ebi.ebisearchservice.EBISearchService_Service;

public class UniprotSearchUtil {

	private static EBISearchService eport;

	static {
		EBISearchService_Service ebeye = new EBISearchService_Service();
		eport = ebeye.getEBISearchServiceHttpPort();
	}

	public static List<String> getInteractorByKeyword(String key) {
		int count = eport.getNumberOfResults("uniprot", key);
		System.out.println("Num candidates ====> " + count);
		int i = 1;
		
		List<String> firstResult = eport.getResultsIds("uniprot", key, 0, 100).getString();
		if(count <= 100) {
			System.out.println("return = " + firstResult.size());
			return firstResult; 
		} else {
			List<String> result = new ArrayList<String>(firstResult);
			int index = 100;
			while(index<count) {
				result.addAll(eport.getResultsIds("uniprot", key, index, 100).getString());
				index = index + 100;
			}
			System.out.println("return = " + result.size());
			return result;
		}
	}
	
	
	
}
