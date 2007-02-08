package csplugins.brquickfind.util;

import giny.model.GraphObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import csplugins.brwidgets.autocomplete.index.BRGenericIndex;
import csplugins.brwidgets.autocomplete.index.BRIndexFactory;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.task.TaskMonitor;

/**
 * Default implementation of the QuickFind interface.  For details, see
 * {@link BRQuickFind}.
 *
 * @author Ethan Cerami.
 */
class BRQuickFindImpl implements BRQuickFind {
    private ArrayList listenerList = new ArrayList();
    private HashMap networkMap = new HashMap();
    private CyAttributes nodeAttributes;
    private int maxProgress;
    private int currentProgress;
    private static final boolean OUTPUT_PERFORMANCE_STATS = false;

    public BRQuickFindImpl(CyAttributes nodeAttributes) {
        this.nodeAttributes = nodeAttributes;
    }

    public void addNetwork(CyNetwork network, TaskMonitor taskMonitor) {

        //  Use default index specified by network, if available.
        //  Otherwise, index by UNIQUE_IDENTIFIER.
        String controllingAttribute = null;
        CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
        if (networkAttributes != null) {
            controllingAttribute = networkAttributes.getStringAttribute
                    (network.getIdentifier(), BRQuickFind.DEFAULT_INDEX);
        }
        if (controllingAttribute == null) {
            controllingAttribute = BRQuickFind.UNIQUE_IDENTIFIER;
        }

        //  Determine maxProgress
        currentProgress = 0;
        maxProgress = getGraphObjectCount(network);

        // Notify all listeners of add event
        for (int i = 0; i < listenerList.size(); i++) {
            BRQuickFindListener listener = (BRQuickFindListener)
                    listenerList.get(i);
            listener.networkAddedToIndex(network);
        }

        // Notify all listeners of index start event
        for (int i = 0; i < listenerList.size(); i++) {
            BRQuickFindListener listener = (BRQuickFindListener)
                    listenerList.get(i);
            listener.indexingStarted();
        }

        //  Create Appropriate Index Type, based on attribute type.
        int attributeType = nodeAttributes.getType(controllingAttribute);
        BRGenericIndex index = createIndex(attributeType, controllingAttribute);
        indexNetwork(network, attributeType, controllingAttribute, index,
                taskMonitor);
        networkMap.put(network, index);

        // Notify all listeners of end index event
        for (int i = 0; i < listenerList.size(); i++) {
            BRQuickFindListener listener = (BRQuickFindListener)
                    listenerList.get(i);
            listener.indexingEnded();
        }
    }

    public void removeNetwork(CyNetwork network) {
        networkMap.remove(networkMap);

        // Notify all listeners of remove event
        for (int i = 0; i < listenerList.size(); i++) {
            BRQuickFindListener listener = (BRQuickFindListener)
                    listenerList.get(i);
            listener.networkRemovedfromIndex(network);
        }
    }

    public BRGenericIndex getIndex(CyNetwork network) {
        return (BRGenericIndex) networkMap.get(network);
    }

    public BRGenericIndex reindexNetwork(CyNetwork cyNetwork,
            String controllingAttribute, TaskMonitor taskMonitor) {

        // Notify all listeners of index start event
        for (int i = 0; i < listenerList.size(); i++) {
            BRQuickFindListener listener = (BRQuickFindListener)
                    listenerList.get(i);
            listener.indexingStarted();
        }

        //  Determine maxProgress
        currentProgress = 0;
        maxProgress = 0;        
//APico 10.7.06
//        if (controllingAttribute.equals(QuickFind.INDEX_ALL_ATTRIBUTES)) {
//            String attributeNames[] = nodeAttributes.getAttributeNames();
//            for (int i = 0; i < attributeNames.length; i++) {
//                if (nodeAttributes.getUserVisible(attributeNames[i])) {
//                    maxProgress += getGraphObjectCount(cyNetwork);
//                }
//            }
//        } else {
            maxProgress = getGraphObjectCount(cyNetwork);
//        }

        BRGenericIndex index = null;

//APico 10.7.06
//        if (controllingAttribute.equals(QuickFind.INDEX_ALL_ATTRIBUTES)) {
//            //  Option 1:  Index all attributes
//            index = createIndex(CyAttributes.TYPE_STRING, controllingAttribute);
//            String attributeNames[] = nodeAttributes.getAttributeNames();
//            for (int i = 0; i < attributeNames.length; i++) {
//                if (nodeAttributes.getUserVisible(attributeNames[i])) {
//                    indexNetwork(cyNetwork, CyAttributes.TYPE_STRING,
//                            attributeNames[i], index, taskMonitor);
//                }
//            }
//        } else {
            //  Option 2:  Index single attribute.
            //  Create appropriate index type, based on attribute type.
            int attributeType = nodeAttributes.getType(controllingAttribute);
            index = createIndex(attributeType, controllingAttribute);
            indexNetwork(cyNetwork, attributeType, controllingAttribute,
                    index, taskMonitor);
//        }
        networkMap.put(cyNetwork, index);

        // Notify all listeners of index start event
        for (int i = 0; i < listenerList.size(); i++) {
            BRQuickFindListener listener = (BRQuickFindListener)
                    listenerList.get(i);
            listener.indexingEnded();
        }
        return index;
    }

    public void addQuickFindListener(BRQuickFindListener listener) {
        this.listenerList.add(listener);
    }

    public void removeQuickFindListener(BRQuickFindListener listener) {
        this.listenerList.remove(listener);
    }

    public BRQuickFindListener[] getQuickFindListeners() {
        return (BRQuickFindListener[]) listenerList.toArray(
                new BRQuickFindListener[listenerList.size()]);
    }

    private int getGraphObjectCount(CyNetwork network) {
        return network.getNodeCount();
    }

    private void indexNetwork(CyNetwork network, int attributeType,
            String controllingAttribute, BRGenericIndex index,
            TaskMonitor taskMonitor) {
        Date start = new Date();
        Iterator iterator = network.nodesIterator();
        taskMonitor.setStatus("Indexing node attributes");

        //  Iterate through all nodes or edges
        while (iterator.hasNext()) {
            currentProgress++;
            GraphObject graphObject = (GraphObject) iterator.next();
            addToIndex(attributeType, graphObject, controllingAttribute, index);
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
        networkMap.put(network, index);
    }

    /**
     * Creates appropriate index, based on attribute type.
     * @param attributeType         CyAttributes type.
     * @param controllingAttribute  Controlling attribute.
     * @return GenericIndex Object.
     */
    private BRGenericIndex createIndex (int attributeType,
            String controllingAttribute) {
        BRGenericIndex index;
        if (attributeType == CyAttributes.TYPE_INTEGER
            || attributeType == CyAttributes.TYPE_FLOATING) {
            index = BRIndexFactory.createDefaultNumberIndex();
        } else {
            index = BRIndexFactory.createDefaultTextIndex();
        }
        index.setControllingAttribute(controllingAttribute);
        return index;
    }

    /**
     * Adds new items to index.
     * @param attributeType         CyAttributes type.
     * @param graphObject           Graph Object.
     * @param controllingAttribute  Controlling attribute.
     * @param index                 Index to add to.
     */
    private void addToIndex(int attributeType, GraphObject graphObject,
            String controllingAttribute, BRGenericIndex index) {
        //  Get attribute values, and index
        if (attributeType == CyAttributes.TYPE_INTEGER) {
            Integer value = nodeAttributes.getIntegerAttribute
                    (graphObject.getIdentifier(), controllingAttribute);
            if (value != null) {
                index.addToIndex(value, graphObject);
            }
        } else if (attributeType == CyAttributes.TYPE_FLOATING) {
            Double value = nodeAttributes.getDoubleAttribute
                    (graphObject.getIdentifier(), controllingAttribute);
            if (value != null) {
                index.addToIndex(value, graphObject);
            }
        } else {
            String values[] = CyAttributesUtil.getAttributeValues
                (nodeAttributes, graphObject.getIdentifier(),
                controllingAttribute);
            if (values != null) {
                addStringsToIndex(values, graphObject, index);
            }
        }
    }

    /**
     * Adds multiple strings to an index.
     * @param value         Array of Strings.
     * @param graphObject   Graph Object.
     * @param index         Index to add to.
     */
    private void addStringsToIndex(String value[], GraphObject graphObject,
            BRGenericIndex index) {
        //  Add to index
        for (int i = 0; i < value.length; i++) {
            index.addToIndex(value[i], graphObject);
        }
    }
}