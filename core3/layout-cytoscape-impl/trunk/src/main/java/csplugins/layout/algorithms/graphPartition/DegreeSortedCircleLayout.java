/* vim: set ts=2: */
package csplugins.layout.algorithms.graphPartition;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.view.layout.AbstractGraphPartition;
import org.cytoscape.view.layout.AbstractLayout;
import org.cytoscape.view.layout.LayoutNode;
import org.cytoscape.view.layout.LayoutPartition;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.undo.UndoSupport;


/**
 *
 */
public class DegreeSortedCircleLayout extends AbstractLayout implements TunableValidator {
	
	private static final String DEGREE_ATTR_NAME = "degree";
	private CyTableManager tableMgr;


	/**
	 * Creates a new DegreeSortedCircleLayout object.
	 */
	public DegreeSortedCircleLayout(UndoSupport undoSupport, CyTableManager tableMgr) {
		super(undoSupport);
		this.tableMgr = tableMgr;
	}

	// TODO
	public boolean tunablesAreValid(final Appendable errMsg) {
		return true;
	}
	
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new DegreeSortedCircleLayoutTask(networkView, getName(), selectedOnly, staticNodes,
				DEGREE_ATTR_NAME, tableMgr));
	}
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String toString() {
		return "Degree Sorted Circle Layout";
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getName() {
		return "degree-circle";
	}
}
