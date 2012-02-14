package org.cytoscape.view.layout;

import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

public class EdgeWeightedLayoutTaskContext extends LayoutContextImpl {
	private static final String groupName = "Edge Weight Settings";

	/** A tunable for determining the edge attribute that contains the weights. */	
	@Tunable(description="The edge attribute that contains the weights",groups=groupName)
	public ListSingleSelection<String> edgeWeightColumn;

	/** A tunable for determining how to interpret weight values. */	
	@Tunable(description="How to interpret weight values",groups=groupName)
	public ListSingleSelection<WeightTypes> weightChoices;

	/** A tunable for determining the minimum edge weight to consider. */
	@Tunable(description="The minimum edge weight to consider",groups=groupName)
	public double minWeight = 0;	

	/** A tunable for determining the maximum edge weight to consider. */
	@Tunable(description="The maximum edge weight to consider",groups=groupName)
	public double maxWeight = Double.MAX_VALUE;	

	public EdgeWeightedLayoutTaskContext(boolean supportsSelectedOnly, Set<Class<?>> supportedNodeAttrTypes, Set<Class<?>> supportedEdgeAttrTypes) {
		super(supportsSelectedOnly, supportedNodeAttrTypes, supportedEdgeAttrTypes);
	}
	
    /**
	 * {@inheritDoc}
	 */
	@Override
	public void setNetworkView(CyNetworkView view) {
		super.setNetworkView(view);
		initSelectionTunables();	
	}

	private void initSelectionTunables() {
		weightChoices = new ListSingleSelection<WeightTypes>( WeightTypes.values() );	

		CyTable edgeTable = network.getDefaultEdgeTable();
		SortedSet<String> goodColumns = new TreeSet<String>();
		for ( CyColumn col : edgeTable.getColumns() ) {
			if ( col.getType() == Integer.class || col.getType() == Double.class )
				goodColumns.add(col.getName());
		}
		edgeWeightColumn = new ListSingleSelection<String>( new ArrayList<String>( goodColumns ) );
	}
}
