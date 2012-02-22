package org.cytoscape.cpathsquared.internal.task;

import java.util.List;
import java.util.Set;

import org.cytoscape.cpathsquared.internal.CPath2Exception;
import org.cytoscape.cpathsquared.internal.CPath2Properties;
import org.cytoscape.cpathsquared.internal.CPath2WebService;
import org.cytoscape.cpathsquared.internal.task.ExecuteSearchTaskFactory.ResultHandler;
import org.cytoscape.cpathsquared.internal.util.EmptySetException;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import cpath.service.jaxb.SearchHit;
import cpath.service.jaxb.SearchResponse;

/**
 * Controller for Executing a Search.
 *
 * @author Ethan Cerami, Igor Rodchenkov
 */
public class ExecuteSearchTask implements Task {
    private CPath2WebService webApi;
    private String keyword;
    private Set<String> organism;
    private Set<String> datasource;
	private ResultHandler result;

    /**
     * Constructor.
     *
     * @param webApi         cPath Web Api.
     * @param keyword        Keyword
     * @param organism TODO
     * @param datasource TODO
     * @param result 
     */
    public ExecuteSearchTask(CPath2WebService webApi, String keyword,
            Set<String> organism, Set<String> datasource, ResultHandler result) {
        this.webApi = webApi;
        this.keyword = keyword;
        this.organism = organism;
        this.datasource = datasource;
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
        return "Searching " + CPath2Properties.serverName + "...";
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
            SearchResponse searchResponse = webApi.search(keyword,
                    organism, datasource);
            List<SearchHit> searchHits = searchResponse.getSearchHit();
            numHits = searchHits.size();
            
            taskMonitor.setProgress(0.1);
            
// TODO using sub-queries, we could also retrieve more info for each hit (participants, entity refs, parent interactions, etc..)            
//            int numRetrieved = 1;
//            for (SearchHit hit:  searchHits) {
//                taskMonitor.setStatusMessage("Retrieving details for:  " + hit.getName());
//                try {
//                    webApi.getParentSummaries(hit.getUri(), taskMonitor);
//                } catch (EmptySetException e) {
//                }
//                double progress = numRetrieved++ / (double) numHits;
//                taskMonitor.setProgress(progress);
//            }
        } catch (EmptySetException e) {
        } catch (CPath2Exception e) {
            if (e.getErrorCode() != CPath2Exception.ERROR_CANCELED_BY_USER) {
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