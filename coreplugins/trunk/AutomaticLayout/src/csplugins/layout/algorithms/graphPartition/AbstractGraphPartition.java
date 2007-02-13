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


/* NOTE: The AbstractGraphPartition class uses SGraphPartition to generate
   the partitions in the graph. Originally this class used GraphPartition,
   but it is broken. */
/**
 *
 */
public abstract class AbstractGraphPartition extends AbstractLayout {
	protected TaskMonitor taskMonitor = null;
	double incr = 100;

	/**
	 * Creates a new AbstractGraphPartition object.
	 */
	public AbstractGraphPartition() {
		super();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param partition DOCUMENT ME!
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

	/* AbstractGraphPartitionLayout implements the constuct method
	 * and calls layoutPartion for each partition.
	 */
	/**
	 *  DOCUMENT ME!
	 */
	public void construct() {
		initialize();

		List partitions = LayoutPartition.partition(network, networkView, selectedOnly, null);

		// monitor
		int percent = 0;
		double currentProgress = 0;
		double lengthOfTask = partitions.size();
		String statMessage = "Layout";

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

		currentProgress++;
		percent = (int) ((currentProgress * 100) / lengthOfTask);
		statMessage = "Completed " + percent + "%";

		if (taskMonitor != null) {
			taskMonitor.setPercentCompleted(percent);
			taskMonitor.setStatus(statMessage);
		}

		//System.out.println( "AbstractLayout::There are "+partitions.size()+" Partitions!!");
		int part = 0;
		Iterator p = partitions.iterator();

		while (p.hasNext() && !canceled) {
			part++;

			// get the partition
			LayoutPartition partition = (LayoutPartition) p.next();

			if (partition.nodeCount() == 0) {
				continue;
			}

			// Partitions Requiring Layout
			if (partition.nodeCount() != 1) {
				try {
					layoutPartion(partition);
				} catch (Throwable _e) {
					_e.printStackTrace();

					return;
				}

				// OFFSET
				partition.offset(next_x_start, next_y_start);
			} // end >1 node partitions  

			// Single Nodes
			else {
				// Reset our bounds
				partition.resetNodes();

				// Single node -- get it
				LayoutNode node = (LayoutNode) partition.getNodeList().get(0);
				node.setLocation(next_x_start, next_y_start);
				partition.moveNodeToLocation(node);
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
		} // end iterate through partitions
	}
}
