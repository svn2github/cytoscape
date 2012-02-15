package org.cytoscape.cpathsquared.internal;


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
     * @param ncbiTaxonomyId NCBI Texonomy ID.
     */
    public void searchInitiatedForPhysicalEntities(String keyword, int ncbiTaxonomyId);

    /**
     * Indicates that a search for physical entities has completed.
     *
     * @param peSearchResponse Search Response Object.
     */
    public void searchCompletedForPhysicalEntities(SearchResponse peSearchResponse);

    /**
     * Indicates that someone has initiated a request for parent summaries.
     *
     * @param primaryId     Primary ID of Child.
     */
    public void requestInitiatedForParentSummaries (String primaryId);

    /**
     * Indicates that a request for parent summaries has completed.
     *
     * @param primaryId         Primary ID of Child.
     * @param parents
     */
    public void requestCompletedForParentSummaries (String primaryId,
            SearchResponse parents);
}
