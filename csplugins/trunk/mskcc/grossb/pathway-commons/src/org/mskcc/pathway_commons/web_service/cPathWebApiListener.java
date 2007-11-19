package org.mskcc.pathway_commons.web_service;

import org.mskcc.pathway_commons.schemas.search_response.SearchResponseType;
import org.mskcc.pathway_commons.schemas.summary_response.SummaryResponseType;

/**
 * Listener for listener to Requests made to the cPath Web API.
 *
 * @author Ethan Cerami
 */
public interface cPathWebApiListener {

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

    /**
     * Indicates that someone has initiated a request for parent summaries.
     *
     * @param primaryId     Primary ID of Child.
     */
    public void requestInitiatedForParentSummaries (long primaryId);

    /**
     * Indicates that a request for parent summaries has completed.
     *
     * @param primaryId         Primary ID of Child.
     * @param summaryResponse   Summary Response Object.
     */
    public void requestCompletedForParentSummaries (long primaryId,
            SummaryResponseType summaryResponse);
}
