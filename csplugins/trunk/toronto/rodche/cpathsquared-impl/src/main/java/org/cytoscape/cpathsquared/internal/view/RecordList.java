package org.cytoscape.cpathsquared.internal.view;

import java.util.List;
import java.util.TreeMap;

import cpath.service.jaxb.SearchHit;
import cpath.service.jaxb.SearchResponse;


/**
 * List of BioPAX Records.
 *
 * @author Ethan Cerami.
 */
public class RecordList {
    private SearchResponse response;
    TreeMap<String, Integer> interactionTypeMap = new TreeMap<String, Integer>();

    /**
     * Constructor.
     * @param response 
     */
    public RecordList (SearchResponse response) {
        this.response = response;
        catalogInteractions();
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
     * Gets the Summary Response XML.
     * @return 
     */
    public SearchResponse getSummaryResponse() {
        return response;
    }


    /**
     * Gets catalog of entity sources.
     * @return Map<Entity Type, # Records>
     */
    public TreeMap<String, Integer> getEntityTypeMap() {
        return interactionTypeMap;
    }

    private void catalogInteractions() {
        List<SearchHit> recordList = response.getSearchHit();
        if (recordList != null) {
            for (SearchHit record:  recordList) {
                catalogInteractionType(record);
                //  TODO:  additional catalogs, as needed.
            }
        }
    }

    private void catalogInteractionType(SearchHit record) {
        String type = record.getBiopaxClass();
        Integer count = interactionTypeMap.get(type);
        if (count != null) {
            count = count + 1;
        } else {
            count = 1;
        }
        interactionTypeMap.put(type, count);
    }
}