package org.cytoscape.cpathsquared.internal.view;

import java.util.List;
import java.util.TreeMap;

import cpath.service.jaxb.SearchHit;
import cpath.service.jaxb.SearchResponse;


public final class RecordList {
    private SearchResponse response;
    TreeMap<String, Integer> typeMap = new TreeMap<String, Integer>();

    /**
     * Constructor.
     * @param response 
     */
    public RecordList (SearchResponse response) {
        this.response = response;
        catalogByType();
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
    public TreeMap<String, Integer> getEntityTypeMap() {
        return typeMap;
    }

    private void catalogByType() {
        List<SearchHit> recordList = response.getSearchHit();
        if (recordList != null) {
            for (SearchHit record:  recordList) {
                catalogByType(record);
                //  TODO:  additional catalogs, as needed.
            }
        }
    }

    private void catalogByType(SearchHit record) {
        String type = record.getBiopaxClass();
        Integer count = typeMap.get(type);
        if (count != null) {
            count = count + 1;
        } else {
            count = 1;
        }
        typeMap.put(type, count);
    }
}