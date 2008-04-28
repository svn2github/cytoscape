package org.cytoscape.coreplugin.cpath2.task;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.Cytoscape;
import org.cytoscape.coreplugin.cpath2.web_service.CPathWebService;
import org.cytoscape.coreplugin.cpath2.web_service.EmptySetException;
import org.cytoscape.coreplugin.cpath2.web_service.CPathException;
import org.cytoscape.coreplugin.cpath2.web_service.CPathProperties;
import org.cytoscape.coreplugin.cpath2.schemas.search_response.SearchResponseType;
import org.cytoscape.coreplugin.cpath2.schemas.search_response.ExtendedRecordType;

import javax.swing.*;
import java.util.List;

/**
 * Controller for Executing a Physical Entity Search.
 *
 * @author Ethan Cerami.
 */
public class ExecutePhysicalEntitySearch implements Task {
    private CPathWebService webApi;
    private String keyword;
    private int ncbiTaxonomyId;
    private TaskMonitor taskMonitor;
    private int numMatchesFound = 0;

    /**
     * Constructor.
     *
     * @param webApi         cPath Web Api.
     * @param keyword        Keyword
     * @param ncbiTaxonomyId NCBI Taxonomy ID.
     */
    public ExecutePhysicalEntitySearch(CPathWebService webApi, String keyword,
            int ncbiTaxonomyId) {
        this.webApi = webApi;
        this.keyword = keyword;
        this.ncbiTaxonomyId = ncbiTaxonomyId;
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
     * Gets Number of Matches Found.
     * @return Number of Matches Found.
     */
    public int getNumMatchesFound() {
        return this.numMatchesFound;
    }

    /**
     * Our implementation of Task.getTitle.
     *
     * @return Task Title.
     */
    public String getTitle() {
        return "Searching " + CPathProperties.getInstance().getCPathServerName() + "...";
    }

    /**
     * Our implementation of Task.run().
     */
    public void run() {
        try {
            // read the network from cpath instance
            taskMonitor.setPercentCompleted(-1);
            taskMonitor.setStatus("Executing Search");

            //  Execute the Search
            SearchResponseType searchResponse = webApi.searchPhysicalEntities(keyword,
                    ncbiTaxonomyId, taskMonitor);
            List<ExtendedRecordType> searchHits = searchResponse.getSearchHit();

            int numHits = searchHits.size();
            int numRetrieved = 1;
            taskMonitor.setPercentCompleted(1);
            for (ExtendedRecordType hit:  searchHits) {
                taskMonitor.setStatus("Retrieving interaction details for:  " +
                    hit.getName());
                try {
                    webApi.getParentSummaries(hit.getPrimaryId(), taskMonitor);
                } catch (EmptySetException e) {
                }
                int percentComplete = (int) (100 * (numRetrieved++ / (float) numHits));
                taskMonitor.setPercentCompleted(percentComplete);
            }
            this.numMatchesFound = numHits;
        } catch (EmptySetException e) {
        } catch (CPathException e) {
            if (e.getErrorCode() != CPathException.ERROR_CANCELED_BY_USER) {
                taskMonitor.setException(e, e.getMessage(), e.getRecoveryTip());
            }
        } finally {
            taskMonitor.setStatus("Done");
            taskMonitor.setPercentCompleted(100);
        }
    }
}