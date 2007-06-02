/* vim: set ts=2: */
package csplugins.layout.algorithms.graphPartition;

import cern.colt.list.*;

import cern.colt.map.*;

import csplugins.layout.LayoutNode;
import csplugins.layout.LayoutPartition;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.layout.AbstractLayout;

import cytoscape.task.*;

import cytoscape.view.CyNetworkView;

import giny.model.*;

import java.lang.Throwable;

import java.util.*;

import javax.swing.JOptionPane;


/**
 * An abstract class that handles the partitioning of graphs so that
 * the partitions will be laid out individually.
 */
public abstract class AbstractGraphPartition extends AbstractLayout {
	double incr = 100;

	/**
	 * Creates a new AbstractGraphPartition object.
	 */
	public AbstractGraphPartition() {
		super();
	}

	/**
	 * Override this method and layout the LayoutPartion just
	 * like you would a NetworkView.
	 *
	 * @param partition The LayoutPartion to be laid out. 
	 */
	public abstract void layoutPartion(LayoutPartition partition);

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean supportsSelectedOnly() {
		return true;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param v DOCUMENT ME!
	 */
	public void setSelectedOnly(boolean v) {
		selectedOnly = v;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param v DOCUMENT ME!
	 */
	protected void setTaskStatus(int percent) {
		if (taskMonitor != null) {
			taskMonitor.setPercentCompleted(percent);
			taskMonitor.setStatus("Completed " + percent + "%");
		}
	}


	/**
	 * AbstractGraphPartitionLayout implements the constuct method
	 * and calls layoutPartion for each partition.
	 */
	public void construct() {
		initialize();

		List partitions = LayoutPartition.partition(network, networkView, selectedOnly, null);

		int numNodes = network.getNodeCount();
		int numComplete = 0;

		// Set up offsets -- we start with the overall min and max
		double xStart = ((LayoutPartition) partitions.get(0)).getMinX();
		double yStart = ((LayoutPartition) partitions.get(0)).getMinY();

		Iterator partIter = partitions.iterator();

		while (partIter.hasNext()) {
			LayoutPartition part = (LayoutPartition) partIter.next();
			xStart = Math.min(xStart, part.getMinX());
			yStart = Math.min(yStart, part.getMinY());
		}

		double next_x_start = xStart;
		double next_y_start = yStart;
		double current_max_y = 0;

		double max_dimensions = Math.sqrt((double) network.getNodeCount());
		// give each node room
		max_dimensions *= incr;
		max_dimensions += xStart;

		setTaskStatus(1);

		Iterator p = partitions.iterator();

		while (p.hasNext() && !canceled) {
			// get the partition
			LayoutPartition partition = (LayoutPartition) p.next();

			// Partitions Requiring Layout
			if (partition.nodeCount() > 1) {
				try {
					layoutPartion(partition);
				} catch (Throwable _e) {
					_e.printStackTrace();
					return;
				}

				// OFFSET
				partition.offset(next_x_start, next_y_start);

			// single nodes
			} else if ( partition.nodeCount() == 1 ) {
				// Reset our bounds
				partition.resetNodes();

				// Single node -- get it
				LayoutNode node = (LayoutNode) partition.getNodeList().get(0);
				node.setLocation(next_x_start, next_y_start);
				partition.moveNodeToLocation(node);
			} else {
				continue;
			}

			double last_max_x = partition.getMaxX();
			double last_max_y = partition.getMaxY();

			if (last_max_y > current_max_y) {
				current_max_y = last_max_y;
			}

			if (last_max_x > max_dimensions) {
				next_x_start = xStart;
				next_y_start = current_max_y;
				next_y_start += incr;
			} else {
				next_x_start = last_max_x;
				next_x_start += incr;
			}
		
			numComplete += partition.size();
			setTaskStatus( (int)(Math.rint(100*numComplete/(double)numNodes)));
		} 
	}
}
