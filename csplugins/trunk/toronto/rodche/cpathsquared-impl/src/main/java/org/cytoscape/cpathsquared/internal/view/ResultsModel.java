package org.cytoscape.cpathsquared.internal.view;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.TreeMap;

import cpath.service.jaxb.SearchHit;
import cpath.service.jaxb.SearchResponse;

/**
 * Contains information regarding the currently selected set of interaction bundles.
 *
 */
public final class ResultsModel extends Observable {

    private SearchResponse response;   
    
    Map<String, Integer> numHitsByTypeMap = new TreeMap<String, Integer>();
    Map<String, Integer> numHitsByOrganismMap = new TreeMap<String, Integer>();
    Map<String, Integer> numHitsByDatasourceMap = new TreeMap<String, Integer>();
    
    // URI-to-HTML summary text map
    Map<String, String> summaryMap = new HashMap<String, String>();
    Map<String, Collection<NameValuePairListItem>> pathwaysMap = new HashMap<String, Collection<NameValuePairListItem>>();    
    Map<String, Collection<NameValuePairListItem>> moleculesMap = new HashMap<String, Collection<NameValuePairListItem>>();

    public int getNumRecords() {
        if (response != null && !response.isEmpty()) {
            return response.getSearchHit().size();
        } else {
            return -1;
        }
    }
  
    
    /**
     * Re-builds hit-to-type, hit-to-organism, hit-to-datasource, 
     * etc. internal maps.
     */
	private void init() {
		if (response != null && !response.isEmpty()) {
			numHitsByTypeMap.clear();
			numHitsByOrganismMap.clear();
			numHitsByDatasourceMap.clear();
			pathwaysMap.clear();
			moleculesMap.clear();
			summaryMap.clear();

			for (SearchHit record : response.getSearchHit()) {
				catalog(record);
			}
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
    
    
    public void setSearchResponse (SearchResponse response) {
        this.response = response;
        init();
        
        this.setChanged();
        this.notifyObservers();
    }

    
    public SearchResponse getSearchResponse() {
        return response;
    }

}