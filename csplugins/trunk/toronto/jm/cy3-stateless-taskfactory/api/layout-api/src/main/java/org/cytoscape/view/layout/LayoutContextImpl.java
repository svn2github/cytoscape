package org.cytoscape.view.layout;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.task.NetworkViewTaskContext;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

public class LayoutContextImpl implements NetworkViewTaskContext, LayoutContext {

	private static final String ALL_NODES = " All Nodes";
	private static final String SELECTED_NODES_ONLY = " Selected Nodes Only";
	private static final String NODE_PREFIX = "(Node) "; 
	private static final String EDGE_PREFIX = "(Edge) "; 
	
	private ListSingleSelection<String> submenuDef;

	/**
	 * The network view that the layout will be applied to.
	 */
	protected CyNetworkView networkView;
	
	private String edgeAttribute = null;
	private String nodeAttribute = null;
	private Dimension currentSize = new Dimension(20, 20);

	Set<Class<?>> nodeAttrTypes;
	Set<Class<?>> edgeAttrTypes;

	protected final boolean supportsSelectedOnly;

	/**
	 * The network model underlying the networkView.  This shouldn't be set directly
	 * by extending classes.
	 */
	protected CyNetwork network;

	/**
	 * The set of nodes that are  
	 */
	protected Set<View<CyNode>> staticNodes = new HashSet<View<CyNode>>();
	
	/**
	 * Indicates that only selected nodes should be laid out.
	 */
	protected boolean selectedOnly;
	
	public LayoutContextImpl(boolean supportsSelectedOnly, Set<Class<?>> supportedNodeAttributeTypes, Set<Class<?>> supportedEdgeAttributeTypes) {
		this.supportsSelectedOnly = supportsSelectedOnly;
		this.nodeAttrTypes = supportedNodeAttributeTypes;
		this.edgeAttrTypes = supportedEdgeAttributeTypes;
	}
	
	/**
	 * Never use this method from within a layout to access the submenu options,
     * instead call the configureLayoutFromSubmenuSelection() method to configure
	 * the layout based on menu selection. 
	 * @return The list single selection object that specifies the submenu
	 * names to be used for generating selection submenus. 
	 */
	@Tunable(description="Apply to")
	public ListSingleSelection<String> getSubmenuOptions() {

		List<String> possibleValues = new ArrayList<String>();

		if ( nodeAttrTypes != null && !nodeAttrTypes.isEmpty() ) {
	        for (final CyColumn column : network.getDefaultNodeTable().getColumns()) 
	            if (nodeAttrTypes.contains(column.getType()))
					possibleValues.add(NODE_PREFIX + column.getName());
		} else if ( edgeAttrTypes != null && !edgeAttrTypes.isEmpty() ) {
	        for (final CyColumn column : network.getDefaultEdgeTable().getColumns()) 
	            if (edgeAttrTypes.contains(column.getType()))
					possibleValues.add(EDGE_PREFIX + column.getName());
		}
	
		int numSelected = CyTableUtil.getNodesInState(network, CyNetwork.SELECTED, true).size();
		if (supportsSelectedOnly() && numSelected > 0) {

			if ( possibleValues.isEmpty() ) {
				possibleValues.add(ALL_NODES);
				possibleValues.add(SELECTED_NODES_ONLY);
			} else {
				List<String> newPossibleValues = new ArrayList<String>();
				for ( String pv : possibleValues ) {
					newPossibleValues.add(pv + ALL_NODES);
					newPossibleValues.add(pv + SELECTED_NODES_ONLY);
				}		
				possibleValues = newPossibleValues;
			}
		} 

//		if ( possibleValues.isEmpty() )
//			possibleValues.add( humanName );
		
		submenuDef = new ListSingleSelection<String>( possibleValues );
		if (possibleValues.size() > 0) {
			submenuDef.setSelectedValue(possibleValues.get(0));
		}
		return submenuDef;
	}

	/**
	 * This method is a no-op.  Don't use it.
	 */	
	public void setSubmenuOptions(ListSingleSelection<String> opts) {
		// no-op
	}

	/**
	 *
	 */
	protected void configureLayoutFromSubmenuSelection() {
		String selectedMenu = submenuDef.getSelectedValue(); 

		if ( selectedMenu == null || selectedMenu == "" )
			return;

		setSelectedOnly( selectedMenu.endsWith(SELECTED_NODES_ONLY) );

		if ( selectedMenu.endsWith( ALL_NODES ) ) 
			selectedMenu = selectedMenu.replaceFirst( ALL_NODES, "" );
		if ( selectedMenu.endsWith( SELECTED_NODES_ONLY ) ) 
			selectedMenu = selectedMenu.replaceFirst( SELECTED_NODES_ONLY, "" );


		if ( selectedMenu.startsWith( NODE_PREFIX ) ) 
			selectedMenu = selectedMenu.replaceFirst( NODE_PREFIX, "" );
		if ( selectedMenu.startsWith( EDGE_PREFIX ) ) 
			selectedMenu = selectedMenu.replaceFirst( EDGE_PREFIX, "" );

		if ( selectedMenu.length() > 0 )
			setLayoutAttribute(selectedMenu);	
	}
	
	/**
	 * Sets the network view to be laid out.
	 * @param networkView the network view to be laid out.
	 */
	@Override
	public void setNetworkView(final CyNetworkView networkView) {
		this.networkView = networkView;
		this.network = networkView.getModel();
		double node_count = (double) network.getNodeCount();
		node_count = Math.sqrt(node_count);
		node_count *= 100;
		currentSize = new Dimension((int) node_count, (int) node_count);
	}
	
	/**
	 * Set the name of the attribute to use for attribute
	 * dependent layout algorithms.
	 *
	 * @param attributeName The name of the attribute
	 */
	public void setLayoutAttribute(String attributeName) {
		if (nodeAttrTypes.size() > 0) {
			nodeAttribute = attributeName;
		} else if (edgeAttrTypes.size() > 0) {
			edgeAttribute = attributeName;
		}
	}
	
	/**
	 * Set the flag that indicates that this algorithm
	 * should only operate on the currently selected nodes.
	 *
	 * @param selectedOnly set to "true" if the algorithm should
	 * only apply to selected nodes only
	 */
	public void setSelectedOnly(boolean selectedOnly) {
		this.selectedOnly = selectedOnly;
	}

	/** 
	 * Descendants need to call this if they intend to use the "staticNodes" field.
	 */
	public final void initStaticNodes() {
		staticNodes.clear();
		final Set<CyNode> selectedNodes =
			new HashSet<CyNode>(CyTableUtil.getNodesInState(networkView.getModel(),
									CyNetwork.SELECTED, true));
		for (final View<CyNode> nodeView : networkView.getNodeViews()) {
			if (!selectedNodes.contains(nodeView.getModel()))
				staticNodes.add(nodeView);
		}
	}
	
	/**
	 * Indicates whether this algorithm supports applying the layout 
	 * only to selected nodes.
	 */
	public final boolean supportsSelectedOnly() {
		return supportsSelectedOnly;
	}
	
	@Override
	public CyNetworkView getNetworkView() {
		return networkView;
	}
	
	@Override
	public List<String> getInitialAttributeList() {
		return new ArrayList<String>();
	}
	
	public boolean getSelectedOnly() {
		return selectedOnly;
	}

	public Set<View<CyNode>> getStaticNodes() {
		return staticNodes;
	}
}
