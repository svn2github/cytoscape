/* vim: set ts=2: */
package org.cytoscape.view.layout;

import org.cytoscape.model.CyNode;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.UndoSupport;

import java.util.ArrayList;
import java.util.List;


/**
 * An abstract class that handles the partitioning of graphs so that
 * the partitions will be laid out individually.
 */
public abstract class AbstractGraphPartition extends AbstractLayout {
	double incr = 100;
	protected List <LayoutPartition> partitionList = null;
	protected EdgeWeighter edgeWeighter = null;
	@Tunable(description="Don't partition graph before layout", group="Standard settings")
	public boolean singlePartition = false;

	// Information for taskMonitor
	double current_start = 0;	// Starting node number
	double	current_size = 0;		// Partition size
	double	total_nodes = 0;		// Total number of nodes

	/**
	 * Creates a new AbstractGraphPartition object.
	 */
	public AbstractGraphPartition(UndoSupport undo) {
		super(undo);
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
	 * Sets the singlePartition flag, which disables partitioning. This
	 * can be used by users who do not want to partition their graph for
	 * some reason.
	 *
	 * @param flag if false, no paritioning will be done
	 */
	public void setPartition(boolean flag) {
		if (flag)
			this.singlePartition = false;
		else
			this.singlePartition = true;
	}

	/**
	 * Sets the singlePartition flag, which disables partitioning. This
	 * can be used by users who do not want to partition their graph for
	 * some reason.
	 *
	 * @param value if "false", no paritioning will be done
	 */
	public void setPartition(String value) {
		Boolean val = new Boolean(value);
		setPartition(val.booleanValue());
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param percent The percentage of completion for this partition
	 */
	protected void setTaskStatus(int percent) {
		if (taskMonitor != null) {
			// Calculate the nodes done for this partition
			double nodesDone = current_size*(double)percent/100.;
			// Calculate the percent done overall
			double pDone = (nodesDone+current_start)/total_nodes;
			taskMonitor.setProgress(pDone);
			taskMonitor.setStatusMessage("Completed " + (int)pDone + "%");
		}
	}

	/**
	 * AbstractGraphPartitionLayout implements the constuct method
	 * and calls layoutPartion for each partition.
	 */
	public void construct() {
		initialize();

		if (edgeWeighter != null)
			edgeWeighter.reset();

		// Depending on whether we are partitioned or not,
		// we use different initialization.  Note that if the user only wants
		// to lay out selected nodes, partitioning becomes a very bad idea!
		if (selectedOnly || singlePartition) {
			// We still use the partition abstraction, even if we're
			// not partitioning.  This makes the code further down
			// much cleaner
			LayoutPartition partition = new LayoutPartition(network, networkView, selectedOnly, edgeWeighter);
			partitionList = new ArrayList(1);
			partitionList.add(partition);
		} else if (staticNodes != null && staticNodes.size() > 0) {
			// Someone has programmatically locked a set of nodes -- construct
			// the list of unlocked nodes
			List<CyNode> unlockedNodes = new ArrayList();
			for (CyNode node: network.getNodeList()) {
				if (!isLocked(networkView.getNodeView(node))) {
					unlockedNodes.add(node);
				}
			}
			LayoutPartition partition = new LayoutPartition(network, networkView, unlockedNodes, edgeWeighter);
			partitionList = new ArrayList(1);
			partitionList.add(partition);
		} else {
			partitionList = LayoutPartition.partition(network, networkView, false, edgeWeighter);
		}

		total_nodes = network.getNodeCount();
		current_start = 0;

		// Set up offsets -- we start with the overall min and max
		double xStart = (partitionList.get(0)).getMinX();
		double yStart = (partitionList.get(0)).getMinY();

		for (LayoutPartition part: partitionList) {
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


		for (LayoutPartition partition: partitionList) {
			if (canceled) break;
			// get the partition
			current_size = (double)partition.size();
			// System.out.println("Partition #"+partition.getPartitionNumber()+" has "+current_size+" nodes");
			setTaskStatus(1);

			// Partitions Requiring Layout
			if (partition.nodeCount() > 1) {
				try {
					layoutPartion(partition);
				} catch (Throwable _e) {
					_e.printStackTrace();
					return;
				}

			if (!selectedOnly && !singlePartition) {
				// System.out.println("Offsetting partition #"+partition.getPartitionNumber()+" to "+next_x_start+", "+next_y_start);
				// OFFSET
				partition.offset(next_x_start, next_y_start);
			}

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

			setTaskStatus( 100 );
			current_start += current_size;
		} 
	}
}
