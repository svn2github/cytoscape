package org.cytoscape.cpathsquared.internal;


import java.util.Set;

import cpath.service.jaxb.SearchResponse;

/**
 * Listener for listener to Requests made to the cPath Web API.
 *
 * @author Ethan Cerami
 */
public interface CPath2WebServiceListener {

    /**
     * Indicates that someone has initiated a search for physical entities.
     *
     * @param keyword        Keyword Term(s)
     * @param organism TODO
     * @param datasource TODO
     */
    public void searchInitiated(String keyword, Set<String> organism, Set<String> datasource);

    /**
     * Indicates that a search for physical entities has completed.
     *
     * @param peSearchResponse Search Response Object.
     */
    public void searchCompleted(SearchResponse peSearchResponse);

}
