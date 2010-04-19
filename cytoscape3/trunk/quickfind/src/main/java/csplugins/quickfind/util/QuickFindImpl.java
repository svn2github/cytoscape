
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package csplugins.quickfind.util;

import csplugins.widgets.autocomplete.index.GenericIndex;
import csplugins.widgets.autocomplete.index.Hit;
import csplugins.widgets.autocomplete.index.IndexFactory;
import cytoscape.Cytoscape;
import org.cytoscape.work.TaskMonitor;//cytoscape.task.TaskMonitor;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.GraphObject;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyDataTable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Default implementation of the QuickFind interface.  For details, see
 * {@link QuickFind}.
 *
 * @author Ethan Cerami.
 */
class QuickFindImpl implements QuickFind {
	private ArrayList listenerList = new ArrayList();
	private HashMap networkMap = new HashMap();
	private int maxProgress;
	private int currentProgress;
	private static final boolean OUTPUT_PERFORMANCE_STATS = false;

	/**
	 * Creates a new QuickFindImpl object.
	 *
	 * @param nodeAttributes  DOCUMENT ME!
	 * @param edgeAttributes  DOCUMENT ME!
	 */
	public QuickFindImpl() {
	} 
	/**
	 *  DOCUMENT ME!
	 *
	 * @param network DOCUMENT ME!
	 * @param taskMonitor DOCUMENT ME!
	 */
	public synchronized void addNetwork(CyNetwork network, TaskMonitor taskMonitor) {

		// check args - short circuit if necessary
		if (network.getNodeCount() == 0) {
			return;
		}

		//  Use default index specified by network, if available.
		//  Otherwise, index by UNIQUE_IDENTIFIER.
		String controllingAttribute = null;
		//CyDataTable networkAttributes = network.getNetworkCyDataTables().get(CyNetwork.DEFAULT_ATTRS);

		//if (networkAttributes != null) {
			//controllingAttribute = networkAttributes.get(QuickFind.DEFAULT_INDEX,String.class);
			controllingAttribute = network.attrs().get(QuickFind.DEFAULT_INDEX,String.class);//networkAttributes.get(QuickFind.DEFAULT_INDEX,String.class);
		//}

		if (controllingAttribute == null) {
            //  Small hack to index BioPAX Networks by default with node_label.
		
			for ( CyNode node : network.getNodeList() ) {
                String bioPaxFlag = node.attrs().get("biopax.node_label", String.class);
                if (bioPaxFlag != null) {
                    controllingAttribute = "biopax.node_label";
                } else {
                    controllingAttribute = QuickFind.UNIQUE_IDENTIFIER;
                }           	
            }
        }

		//  Determine maxProgress
		currentProgress = 0;
		maxProgress = getGraphObjectCount(network, QuickFind.INDEX_NODES);

		// Notify all listeners of add event
		for (int i = 0; i < listenerList.size(); i++) {
			QuickFindListener listener = (QuickFindListener) listenerList.get(i);
			listener.networkAddedToIndex(network);
		}

		// Notify all listeners of index start event
		for (int i = 0; i < listenerList.size(); i++) {
			QuickFindListener listener = (QuickFindListener) listenerList.get(i);
			listener.indexingStarted(network, QuickFind.INDEX_NODES, controllingAttribute);
		}

		//  Create Appropriate Index Type, based on attribute type.
		CyDataTable nodeAttrs = network.getNodeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
		Class<?> attributeType = nodeAttrs.getColumnTypeMap().get(controllingAttribute);

		GenericIndex index = createIndex(QuickFind.INDEX_NODES, attributeType, controllingAttribute);
		indexNetwork(network, QuickFind.INDEX_NODES, nodeAttrs, attributeType,
		             controllingAttribute, index, taskMonitor);
		networkMap.put(network, index);

		// Notify all listeners of end index event
		for (int i = 0; i < listenerList.size(); i++) {
			QuickFindListener listener = (QuickFindListener) listenerList.get(i);
			listener.indexingEnded();
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param network DOCUMENT ME!
	 */
	public synchronized void removeNetwork(CyNetwork network) {
		networkMap.remove(networkMap);

		// Notify all listeners of remove event
		for (int i = 0; i < listenerList.size(); i++) {
			QuickFindListener listener = (QuickFindListener) listenerList.get(i);
			listener.networkRemovedfromIndex(network);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param network DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public synchronized GenericIndex getIndex(CyNetwork network) {
		return (GenericIndex) networkMap.get(network);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param cyNetwork DOCUMENT ME!
	 * @param indexType DOCUMENT ME!
	 * @param controllingAttribute DOCUMENT ME!
	 * @param taskMonitor DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public synchronized GenericIndex reindexNetwork(CyNetwork cyNetwork, int indexType,
	                                                String controllingAttribute,
	                                                TaskMonitor taskMonitor) {
        Date start = new Date();
        if ((indexType != QuickFind.INDEX_NODES) && (indexType != QuickFind.INDEX_EDGES)) {
			throw new IllegalArgumentException("indexType must be set to: "
			                                   + "QuickFind.INDEX_NODES or QuickFind.INDEX_EDGES");
		}

		// Notify all listeners of index start event
		for (int i = 0; i < listenerList.size(); i++) {
			QuickFindListener listener = (QuickFindListener) listenerList.get(i);
			listener.indexingStarted(cyNetwork, indexType, controllingAttribute);
		}

		//  Determine maxProgress
		currentProgress = 0;
		maxProgress = 0;

		CyDataTable attributes;

		if (indexType == QuickFind.INDEX_NODES) {
			attributes = cyNetwork.getNodeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
		} else {
			attributes = cyNetwork.getEdgeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
		}

		if (controllingAttribute.equals(QuickFind.INDEX_ALL_ATTRIBUTES)) {
			//String[] attributeNames = attributes.getAttributeNames();
			//String[] attributeNames = attributes.get;

			//for (int i = 0; i < attributeNames.length; i++) {
			for (int i = 0; i < attributes.getColumnTypeMap().size(); i++) {
				if (!attributes.getColumnTypeMap().isEmpty()) {
					maxProgress += getGraphObjectCount(cyNetwork, indexType);
				}
			}
		} else {
			maxProgress = getGraphObjectCount(cyNetwork, indexType);
		}

		GenericIndex index = null;

		if (controllingAttribute.equals(QuickFind.INDEX_ALL_ATTRIBUTES)) {
			//  Option 1:  Index all attributes
			//index = createIndex(indexType, CyAttributes.TYPE_STRING, controllingAttribute);
			index = createIndex(indexType, String.class, controllingAttribute);
			
			//String[] attributeNames = attributes.getAttributeNames();

			Map<String,Class<?>> typeMap = attributes.getColumnTypeMap();
			for (String key: typeMap.keySet()) { // int i = 0; i < keys.size(); i++) {
				//if (attributes.getUserVisible(attributeNames[i])) {
					
				indexNetwork(cyNetwork, indexType, attributes, typeMap.get(key),
						key, index, taskMonitor);
				//}
			}
		} else {
			//  Option 2:  Index single attribute.
			//  Create appropriate index type, based on attribute type.
			//int attributeType = attributes.getType(controllingAttribute);
			Class<?> attributeType = attributes.getColumnTypeMap().get(controllingAttribute);
			if ( attributeType != null ) {
			
				index = createIndex(indexType, attributeType, controllingAttribute);
				indexNetwork(cyNetwork, indexType, attributes, attributeType, controllingAttribute,
				             index, taskMonitor);
			}
		}

		networkMap.put(cyNetwork, index);

		// Notify all listeners of index end event
		for (int i = 0; i < listenerList.size(); i++) {
			QuickFindListener listener = (QuickFindListener) listenerList.get(i);
			listener.indexingEnded();
		}

        Date stop = new Date();
        long duration = stop.getTime() - start.getTime();
		if (OUTPUT_PERFORMANCE_STATS) {
        	System.out.println("Time to re-index:  " + duration + " ms");
		}
        return index;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param network DOCUMENT ME!
	 * @param hit DOCUMENT ME!
	 */
	public synchronized void selectHit(CyNetwork network, Hit hit) {
		// Notify all listeners of event
		for (int i = 0; i < listenerList.size(); i++) {
			QuickFindListener listener = (QuickFindListener) listenerList.get(i);
			listener.onUserSelection(network, hit);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param network DOCUMENT ME!
	 * @param low DOCUMENT ME!
	 * @param high DOCUMENT ME!
	 */
	public synchronized void selectRange(CyNetwork network, Number low, Number high) {
		// Notify all listeners of event
		for (int i = 0; i < listenerList.size(); i++) {
			QuickFindListener listener = (QuickFindListener) listenerList.get(i);
			listener.onUserRangeSelection(network, low, high);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param listener DOCUMENT ME!
	 */
	public synchronized void addQuickFindListener(QuickFindListener listener) {
		this.listenerList.add(listener);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param listener DOCUMENT ME!
	 */
	public synchronized void removeQuickFindListener(QuickFindListener listener) {
		this.listenerList.remove(listener);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public synchronized QuickFindListener[] getQuickFindListeners() {
		return (QuickFindListener[]) listenerList.toArray(new QuickFindListener[listenerList.size()]);
	}

	private synchronized int getGraphObjectCount(CyNetwork network, int indexType) {
		if (indexType == QuickFind.INDEX_NODES) {
			return network.getNodeCount();
		} else {
			return network.getEdgeCount();
		}
	}

	private void indexNetwork(CyNetwork network, int indexType, CyDataTable attributes,
	                          Class<?> attributeType, String controllingAttribute, 
							  GenericIndex index,
	                          TaskMonitor taskMonitor) {
		Date start = new Date();
		//Iterator<GraphObject> iterator;
		Iterator iterator;
		
		if (indexType == QuickFind.INDEX_NODES) {
			//taskMonitor.setStatus("Indexing node attributes");
			taskMonitor.setStatusMessage("Indexing node attributes");
			//iterator = network.getNodeList().iterator();
			iterator = network.getNodeList().iterator();
		} else if (indexType == QuickFind.INDEX_EDGES) {
			taskMonitor.setStatusMessage("Indexing edge attributes");
			iterator = network.getEdgeList().iterator();
		} else {
			throw new IllegalArgumentException("indexType must be set to: "
			                                   + "QuickFind.INDEX_NODES or QuickFind.INDEX_EDGES");
		}

		//  Iterate through all nodes or edges
		while (iterator.hasNext()) {
			currentProgress++;

			GraphObject graphObject = (GraphObject)iterator.next();
			final Object value = graphObject.attrs().getRaw(controllingAttribute);
			if (value != null) 
				index.addToIndex(value, graphObject);

			//  Determine percent complete
			//int percentComplete = 100 * (int) (currentProgress / (double) maxProgress);
			double percentComplete =  (currentProgress / (double) maxProgress);
			taskMonitor.setProgress(percentComplete);
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
	 * @param indexType             QuickFind.INDEX_NODES or QuickFind.INDEX_EDGES
	 * @param attributeType         CyAttributes type.
	 * @param controllingAttribute  Controlling attribute.
	 * @return GenericIndex Object.
	 */
	private GenericIndex createIndex(int indexType, Class<?> attributeType, String controllingAttribute) {
		GenericIndex index;

		if (attributeType == Integer.class || attributeType == int.class
		    || attributeType == Double.class || attributeType == double.class ) {
			index = IndexFactory.createDefaultNumberIndex(indexType);
		} else {
			index = IndexFactory.createDefaultTextIndex(indexType);
		}

		index.setControllingAttribute(controllingAttribute);

		return index;
	}
}
