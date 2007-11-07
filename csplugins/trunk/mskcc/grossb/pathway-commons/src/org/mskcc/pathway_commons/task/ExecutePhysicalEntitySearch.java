package org.mskcc.pathway_commons.task;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import org.mskcc.pathway_commons.web_service.PathwayCommonsWebApi;
import org.mskcc.pathway_commons.web_service.CPathException;
import org.mskcc.pathway_commons.web_service.EmptySetException;
import org.mskcc.pathway_commons.schemas.search_response.SearchResponseType;
import org.mskcc.pathway_commons.schemas.search_response.SearchHitType;

import java.util.List;

/**
 * Controller for Executing a Physical Entity Search.
 *
 * @author Ethan Cerami.
 */
public class ExecutePhysicalEntitySearch implements Task {
    private PathwayCommonsWebApi webApi;
    private String keyword;
    private int ncbiTaxonomyId;
    private int startIndex;
    private TaskMonitor taskMonitor;

    /**
     * Constructor.
     *
     * @param webApi         Pathway Commons Web Api.
     * @param keyword        Keyword
     * @param ncbiTaxonomyId NCBI Taxonomy ID.
     * @param startIndex     Start Index.
     */
    public ExecutePhysicalEntitySearch(PathwayCommonsWebApi webApi, String keyword,
            int ncbiTaxonomyId, int startIndex) {
        this.webApi = webApi;
        this.keyword = keyword;
        this.ncbiTaxonomyId = ncbiTaxonomyId;
        this.startIndex = startIndex;
    }

    /**
     * Our implementation of Task.abort()
     */
    public void halt() {
        webApi.abort();
    }

    /**
     * Our implementation of Task.setTaskMonitor().
     *
     * @param taskMonitor TaskMonitor
     */
    public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    /**
     * Our implementation of Task.getTitle.
     *
     * @return Task Title.
     */
    public String getTitle() {
        return "Searching Pathway Commons...";
    }

    /**
     * Our implementation of Task.run().
     */
    public void run() {
        try {
            // read the network from pathway commons
            taskMonitor.setPercentCompleted(-1);
            taskMonitor.setStatus("Executing Search");

            //  Execute the Search
            SearchResponseType searchResponse = webApi.searchPhysicalEntities(keyword,
                    ncbiTaxonomyId, startIndex, taskMonitor);
            List<SearchHitType> searchHits = searchResponse.getSearchHit();

            int numHits = searchHits.size();
            int numRetrieved = 1;
            taskMonitor.setPercentCompleted(1);
            for (SearchHitType hit:  searchHits) {
                taskMonitor.setStatus("Retrieving interaction details for:  " +
                    hit.getName());
                webApi.getParentSummaries(hit.getPrimaryId(), taskMonitor);
                int percentComplete = (int) (100 * (numRetrieved++ / (float) numHits));
                taskMonitor.setPercentCompleted(percentComplete);
            }

            // update the task monitor
            taskMonitor.setStatus("Done");
            taskMonitor.setPercentCompleted(100);
        } catch (EmptySetException e) {
            taskMonitor.setException(e, "No matches found for:  " + keyword + ".",
                    "Please try a different search term and try again.");
        } catch (CPathException e) {
            if (e.getErrorCode() != CPathException.ERROR_CANCELED_BY_USER) {
                taskMonitor.setException(e, e.getMessage(), e.getRecoveryTip());
            }
        }
    }
}