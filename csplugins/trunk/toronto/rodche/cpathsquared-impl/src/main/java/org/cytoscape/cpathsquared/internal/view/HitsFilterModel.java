package org.cytoscape.cpathsquared.internal.view;

import java.util.Map;
import java.util.TreeMap;

import cpath.service.jaxb.SearchHit;
import cpath.service.jaxb.SearchResponse;

/**
 * Contains information regarding the currently selected set of interaction bundles.
 *
 */
final class HitsFilterModel {

    private SearchResponse response;   
    
    final Map<String, Integer> numHitsByTypeMap = new TreeMap<String, Integer>();
    final Map<String, Integer> numHitsByOrganismMap = new TreeMap<String, Integer>();
    final Map<String, Integer> numHitsByDatasourceMap = new TreeMap<String, Integer>();

    public int getNumRecords() {
        if (response != null && !response.isEmpty()) {
            return response.getSearchHit().size();
        } else {
            return -1;
        }
    }
  
    
    private void catalog(SearchHit record) {
        String type = record.getBiopaxClass();
        Integer count = numHitsByTypeMap.get(type);
        if (count != null) {
        	numHitsByTypeMap.put(type, count + 1);
        } else {
        	numHitsByTypeMap.put(type, 1);
        }
        
        for(String org : record.getOrganism()) {
        	Integer i = numHitsByOrganismMap.get(org);
            if (i != null) {
                numHitsByOrganismMap.put(org, i + 1);
            } else {
            	numHitsByOrganismMap.put(org, 1);
            }
        }
        
        for(String ds : record.getDataSource()) {
        	Integer i = numHitsByDatasourceMap.get(ds);
            if (i != null) {
                numHitsByDatasourceMap.put(ds, i + 1);
            } else {
            	numHitsByDatasourceMap.put(ds, 1);
            }
        }
    }
    
    /**
     * Refresh the model and notify all observers about it's changed.
     * 
     * @param response
     */
	public void setSearchResponse(SearchResponse response) {
		this.response = response;
		numHitsByTypeMap.clear();
		numHitsByOrganismMap.clear();
		numHitsByDatasourceMap.clear();

		if(response != null)
			for (SearchHit record : response.getSearchHit()) {
				catalog(record);
			}
	}

    
    public SearchResponse getSearchResponse() {
        return response;
    }
        
}