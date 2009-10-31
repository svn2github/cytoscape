package csplugins.quickfind.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import csplugins.quickfind.util.QuickFind;
import csplugins.quickfind.util.QuickFindFactory;

public class ReindexQuickFindTask implements Task {
    
    private String newAttributeKey;
    private CyNetwork cyNetwork;
    private int indexType;
    private TaskMonitor taskMonitor;

    /**
     * Constructor.
     *
     * @param indexType       Index Type.
     * @param newAttributeKey New Attribute Key for Indexing.
     */
    public ReindexQuickFindTask(CyNetwork cyNetwork,
                                int indexType,
                                String newAttributeKey) {
        this.cyNetwork = cyNetwork;
        this.indexType = indexType;
        this.newAttributeKey = newAttributeKey;
    }

    /**
     * Executes Task:  Reindex.
     */
    @Override
    public void run(TaskMonitor monitor) {
        QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
        quickFind.reindexNetwork(cyNetwork, indexType, newAttributeKey, taskMonitor);
    }

    @Override
    public void cancel() {}

}
