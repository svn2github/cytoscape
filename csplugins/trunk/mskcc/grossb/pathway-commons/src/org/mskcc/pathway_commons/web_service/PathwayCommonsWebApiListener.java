package org.mskcc.pathway_commons.web_service;

import org.mskcc.pathway_commons.schemas.search_response.SearchResponseType;

/**
 * Listener for listener to Requests made to the Pathway Commons API.
 *
 * @author Ethan Cerami
 */
public interface PathwayCommonsWebApiListener {

    /**
     * Indicates that someone has initiated a search for physical entities.
     *
     * @param keyword        Keyword Term(s)
     * @param ncbiTaxonomyId NCBI Texonomy ID.
     * @param startIndex     Start Index Value.
     */
    public void searchInitiatedForPhysicalEntities(String keyword, int ncbiTaxonomyId,
            int startIndex);

    /**
     * Indicates that a search for physical entities has completed.
     *
     * @param peSearchResponse Search Response Object.
     */
    public void searchCompletedForPhysicalEntities(SearchResponseType peSearchResponse);
}
