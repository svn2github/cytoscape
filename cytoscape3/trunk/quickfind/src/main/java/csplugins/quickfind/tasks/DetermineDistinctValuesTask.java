package csplugins.quickfind.tasks;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.GraphObject;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.ValuedTask;

import csplugins.quickfind.util.CyAttributesUtil;
import csplugins.quickfind.util.QuickFind;

public class DetermineDistinctValuesTask implements ValuedTask<List<String>> {

    private CyNetwork network;
    private int indexType;
    private String attributeKey;
    private int maxValues;

    /**
     * Creates a new DetermineDistinctValuesTask object.
     *
     * @param network 
     * @param attributeKey
     * @param indexType
     * @param maxValues
     */
    public DetermineDistinctValuesTask(CyNetwork network, 
                                       String attributeKey,
                                       int indexType,
                                       int maxValues) {
        
        if (attributeKey.equals(QuickFind.INDEX_ALL_ATTRIBUTES)) {
            attributeKey = QuickFind.UNIQUE_IDENTIFIER;
        }

        this.network = network;
        this.attributeKey = attributeKey;
        this.maxValues = maxValues;
    }

    public List<String> run(TaskMonitor taskMonitor) {
        taskMonitor.setProgress(0);

        CyDataTable attributes;
        if (indexType == QuickFind.INDEX_NODES) {
            // FIXME: is this correct?
            attributes = network.getCyDataTables("node").get(CyNetwork.DEFAULT_ATTRS);
        } else {
            // FIXME: is this correct?
            attributes = network.getCyDataTables("edge").get(CyNetwork.DEFAULT_ATTRS);
        }

        Iterator<? extends GraphObject> iterator;
        if (indexType == QuickFind.INDEX_NODES) {
            iterator = network.getNodeList().iterator();
        } else {
            iterator = network.getEdgeList().iterator();
        }

        String[] values = CyAttributesUtil.getDistinctAttributeValues(iterator, network, attributes,
                                                                      attributeKey, maxValues);
        return Arrays.asList(values);
    }

    @Override
    public void cancel() {}

    
}
