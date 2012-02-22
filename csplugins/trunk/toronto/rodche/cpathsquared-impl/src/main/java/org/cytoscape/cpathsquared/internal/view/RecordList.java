package org.cytoscape.cpathsquared.internal.view;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cpath.service.jaxb.SearchHit;
import cpath.service.jaxb.SearchResponse;


final class RecordList {
    private SearchResponse response;   
    private Map<String, Integer> typeMap = new TreeMap<String, Integer>();
    private Map<String, Integer> organismMap = new TreeMap<String, Integer>();
    private Map<String, Integer> dataSourceMap = new TreeMap<String, Integer>();

    /**
     * Constructor.
     * @param response 
     */
    public RecordList (SearchResponse response) {
        this.response = response;
        catalog();
    }

    /**
     * Gets number of records.
     * @return number of records.
     */
    public int getNumRecords() {
        if (response != null && !response.isEmpty()) {
            return response.getSearchHit().size();
        } else {
            return -1;
        }
    }

    /**
     * Gets hits
     * @return 
     */
    public List<SearchHit> getHits() {
        return response.getSearchHit();
    }


    /**
     * Gets catalog of entity types.
     * @return Map<Entity Type, # Records>
     */
    public Map<String, Integer> getTypeMap() {
        return typeMap;
    }

    public Map<String, Integer> getOrganismMap() {
        return organismMap;
    }
    
    public Map<String, Integer> getDataSourceMap() {
        return dataSourceMap;
    }
    
    
    private void catalog() {
        List<SearchHit> recordList = response.getSearchHit();
        if (recordList != null) {
            for (SearchHit record:  recordList) {
                catalog(record);
            }
        }
    }

    private void catalog(SearchHit record) {
        String type = record.getBiopaxClass();
        Integer count = typeMap.get(type);
        if (count != null) {
        	typeMap.put(type, count + 1);
        } else {
        	typeMap.put(type, 1);
        }
        
        
        for(String org : record.getOrganism()) {
        	Integer i = organismMap.get(org);
            if (i != null) {
                organismMap.put(org, i + 1);
            } else {
            	organismMap.put(org, 1);
            }
        }
        
        for(String ds : record.getDataSource()) {
        	Integer i = dataSourceMap.get(ds);
            if (i != null) {
                dataSourceMap.put(ds, i + 1);
            } else {
            	dataSourceMap.put(ds, 1);
            }
        }
    }
}