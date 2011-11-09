package org.cytoscape.cpathsquared.internal.task;

import java.util.List;

import org.cytoscape.cpathsquared.internal.CPathException;
import org.cytoscape.cpathsquared.internal.CPathProperties;
import org.cytoscape.cpathsquared.internal.CPathWebService;
import org.cytoscape.cpathsquared.internal.task.ExecutePhysicalEntitySearchTaskFactory.ResultHandler;
import org.cytoscape.cpathsquared.internal.util.EmptySetException;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import cpath.service.jaxb.SearchHit;
import cpath.service.jaxb.SearchResponse;

/**
 * Controller for Executing a Physical Entity Search.
 *
 * @author Ethan Cerami, Igor Rodchenkov
 */
public class ExecutePhysicalEntitySearchTask implements Task {
    private CPathWebService webApi;
    private String keyword;
    private int ncbiTaxonomyId;
	private ResultHandler result;

    /**
     * Constructor.
     *
     * @param webApi         cPath Web Api.
     * @param keyword        Keyword
     * @param ncbiTaxonomyId NCBI Taxonomy ID.
     * @param result 
     */
    public ExecutePhysicalEntitySearchTask(CPathWebService webApi, String keyword,
            int ncbiTaxonomyId, ResultHandler result) {
        this.webApi = webApi;
        this.keyword = keyword;
        this.ncbiTaxonomyId = ncbiTaxonomyId;
        this.result = result;
    }

    /**
     * Our implementation of Task.abort()
     */
    public void cancel() {
        webApi.abort();
    }

    /**
     * Our implementation of Task.getTitle.
     *
     * @return Task Title.
     */
    public String getTitle() {
        return "Searching " + CPathProperties.serverName + "...";
    }

    /**
     * Our implementation of Task.run().
     */
    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
    	int numHits = 0;
        try {
            // read the network from cpath instance
            taskMonitor.setProgress(0);
            taskMonitor.setStatusMessage("Executing Search");

            //  Execute the Search
            SearchResponse searchResponse = webApi.searchPhysicalEntities(keyword,
                    ncbiTaxonomyId, taskMonitor);
            List<SearchHit> searchHits = searchResponse.getSearchHit();

            numHits = searchHits.size();
            int numRetrieved = 1;
            taskMonitor.setProgress(0.01);
            for (SearchHit hit:  searchHits) {
                taskMonitor.setStatusMessage("Retrieving interaction details for:  " +
                    hit.getName());
                try {
                    webApi.getParentSummaries(hit.getUri(), taskMonitor);
                } catch (EmptySetException e) {
                }
                double progress = numRetrieved++ / (double) numHits;
                taskMonitor.setProgress(progress);
            }
        } catch (EmptySetException e) {
        } catch (CPathException e) {
            if (e.getErrorCode() != CPathException.ERROR_CANCELED_BY_USER) {
            	throw e;
            }
        } catch (Throwable e) { // - helps with optional/unresolved runtime dependencies!
            	throw new RuntimeException(e);
        } finally {
            taskMonitor.setStatusMessage("Done");
            taskMonitor.setProgress(1);
            result.finished(numHits);
        }
    }
}