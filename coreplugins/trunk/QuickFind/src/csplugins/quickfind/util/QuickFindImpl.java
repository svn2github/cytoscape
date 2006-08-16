package csplugins.quickfind.util;

import csplugins.widgets.autocomplete.index.TextIndex;
import csplugins.widgets.autocomplete.index.TextIndexFactory;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.task.TaskMonitor;
import giny.model.GraphObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;

/**
 * Default implementation of the QuickFind interface.  For details, see
 * {@link QuickFind}.
 *
 * @author Ethan Cerami.
 */
class QuickFindImpl implements QuickFind {
    private ArrayList listenerList = new ArrayList();
    private HashMap networkMap = new HashMap();
    private String attributeKey = QuickFind.UNIQUE_IDENTIFIER;
    private IndexType indexType = IndexType.NODE_INDEX;
    private CyAttributes nodeAttributes, edgeAttributes;
    private int maxProgress;
    private int currentProgress;
    private static final boolean OUTPUT_PERFORMANCE_STATS = false;

    public QuickFindImpl(CyAttributes nodeAttributes,
            CyAttributes edgeAttributes) {
        this.nodeAttributes = nodeAttributes;
        this.edgeAttributes = edgeAttributes;
    }

    public void addNetwork(CyNetwork network, TaskMonitor taskMonitor) {
        //  Create new text index
        TextIndex textIndex = TextIndexFactory.createDefaultTextIndex();
        networkMap.put(network, textIndex);

        //  Determine maxProgress
        currentProgress = 0;
        maxProgress = getGraphObjectCount(network);

        // Notify all listeners of add event
        for (int i = 0; i < listenerList.size(); i++) {
            QuickFindListener listener = (QuickFindListener)
                    listenerList.get(i);
            listener.networkAddedToIndex(network);
        }

        // Notify all listeners of index start event
        for (int i = 0; i < listenerList.size(); i++) {
            QuickFindListener listener = (QuickFindListener)
                    listenerList.get(i);
            listener.indexingStarted();
        }

        //  Index network
        indexNetwork(network, taskMonitor);

        // Notify all listeners of index end event
        for (int i = 0; i < listenerList.size(); i++) {
            QuickFindListener listener = (QuickFindListener)
                    listenerList.get(i);
            listener.indexingEnded();
        }

    }

    public void removeNetwork(CyNetwork network) {
        networkMap.remove(networkMap);

        // Notify all listeners of remove event
        for (int i = 0; i < listenerList.size(); i++) {
            QuickFindListener listener = (QuickFindListener)
                    listenerList.get(i);
            listener.networkRemovedfromIndex(network);
        }
    }

    public TextIndex getTextIndex(CyNetwork network) {
        return (TextIndex) networkMap.get(network);
    }

    public String getCurrentAttributeKey() {
        return attributeKey;
    }

    public IndexType getCurrentIndexType() {
        return indexType;
    }

    public void reindexAllNetworks(IndexType type, String attributeKey,
            TaskMonitor taskMonitor) {

        // Notify all listeners of index start event
        for (int i = 0; i < listenerList.size(); i++) {
            QuickFindListener listener = (QuickFindListener)
                    listenerList.get(i);
            listener.indexingStarted();
        }

        this.indexType = type;
        this.attributeKey = attributeKey;
        Iterator iterator = networkMap.keySet().iterator();

        //  Determine maxProgress
        currentProgress = 0;
        maxProgress = 0;
        while (iterator.hasNext()) {
            CyNetwork cyNetwork = (CyNetwork) iterator.next();
            maxProgress += getGraphObjectCount(cyNetwork);
        }

        iterator = networkMap.keySet().iterator();
        while (iterator.hasNext()) {
            CyNetwork cyNetwork = (CyNetwork) iterator.next();
            TextIndex textIndex = (TextIndex) networkMap.get(cyNetwork);
            textIndex.resetIndex();
            indexNetwork(cyNetwork, taskMonitor);
        }

        // Notify all listeners of index start event
        for (int i = 0; i < listenerList.size(); i++) {
            QuickFindListener listener = (QuickFindListener)
                    listenerList.get(i);
            listener.indexingEnded();
        }
    }

    public void addQuickFindListener(QuickFindListener listener) {
        this.listenerList.add(listener);
    }

    public void removeQuickFindListener(QuickFindListener listener) {
        this.listenerList.remove(listener);
    }

    public QuickFindListener[] getQuickFindListeners() {
        return (QuickFindListener[]) listenerList.toArray(
                new QuickFindListener[listenerList.size()]);
    }

    private int getGraphObjectCount(CyNetwork network) {
        if (indexType == IndexType.NODE_INDEX) {
            return network.getNodeCount();
        } else {
            return network.getEdgeCount();
        }
    }

    private void indexNetwork(CyNetwork network, TaskMonitor taskMonitor) {
        TextIndex textIndex = (TextIndex) networkMap.get(network);
        Iterator iterator = null;
        CyAttributes attributes = null;
        Date start = new Date();

        //  Determine node / edge type
        if (indexType == IndexType.NODE_INDEX) {
            iterator = network.nodesIterator();
            attributes = this.nodeAttributes;
            taskMonitor.setStatus("Indexing node attributes");
        } else {
            iterator = network.edgesIterator();
            attributes = this.edgeAttributes;
            taskMonitor.setStatus("Indexing edge attributes");
        }

        //  Iterate through all nodes or edges
        while (iterator.hasNext()) {
            currentProgress++;
            GraphObject graphObject = (GraphObject) iterator.next();

            //  Get all attribute values, and index
            String values[] = CyAttributesUtil.getAttributeValues(attributes,
                    graphObject.getIdentifier(), attributeKey);
            if (values != null) {
                addToIndex(values, graphObject, textIndex);
            }

            //  Determine percent complete
            int percentComplete = 100 * (int) (currentProgress
                    / (double) maxProgress);
            taskMonitor.setPercentCompleted(percentComplete);
        }
        Date stop = new Date();
        long interval = stop.getTime() - start.getTime();
        if (OUTPUT_PERFORMANCE_STATS) {
            System.out.println("Time to index network:  " + interval + " ms");
        }
    }

    private void addToIndex(String value[], GraphObject graphObject,
            TextIndex textIndex) {
        //  Add to index
        for (int i = 0; i < value.length; i++) {
            textIndex.addToIndex(value[i], graphObject);
        }
    }
}