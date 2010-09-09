package org.cytoscape.view.model.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.model.events.AboutToRemoveEdgeEvent;
import org.cytoscape.model.events.AboutToRemoveEdgeListener;
import org.cytoscape.model.events.AboutToRemoveNodeEvent;
import org.cytoscape.model.events.AboutToRemoveNodeListener;
import org.cytoscape.model.events.AddedEdgeEvent;
import org.cytoscape.model.events.AddedEdgeListener;
import org.cytoscape.model.events.AddedNodeEvent;
import org.cytoscape.model.events.AddedNodeListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.NetworkViewChangeMicroListener;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.events.AddedEdgeViewEvent;
import org.cytoscape.view.model.events.AddedNodeViewEvent;
import org.cytoscape.view.model.events.FitContentEvent;
import org.cytoscape.view.model.events.FitSelectedEvent;
import org.cytoscape.view.model.events.NetworkViewChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Row-oriented implementation of CyNetworkView model. This is a consolidated
 * view model representing a network.
 * 
 * @author kono
 * 
 */
public class NetworkViewImpl extends ViewImpl<CyNetwork> implements CyNetworkView, AddedEdgeListener,
		AddedNodeListener, AboutToRemoveEdgeListener, AboutToRemoveNodeListener {
	
	private static final Logger logger = LoggerFactory.getLogger(NetworkViewImpl.class);

	private Map<CyNode, View<CyNode>> nodeViews;
	private Map<CyEdge, View<CyEdge>> edgeViews;

	
	/**
	 * Create a new instance of a network view model.
	 * This constructor do NOT fire event for presentation layer.
	 * 
	 * @param network
	 * @param cyEventHelper
	 */
	public NetworkViewImpl(final CyNetwork network, final CyEventHelper cyEventHelper) {
		super(network, cyEventHelper);

		nodeViews = new HashMap<CyNode, View<CyNode>>();
		edgeViews = new HashMap<CyEdge, View<CyEdge>>();

		for (final CyNode node : network.getNodeList())
			nodeViews.put(node, new NodeViewImpl(node, cyEventHelper, this));

		for (CyEdge edge : network.getEdgeList())
			edgeViews.put(edge, new EdgeViewImpl(edge, cyEventHelper, this));
		
		logger.info("Network View Model Created.  Model ID = " + this.getModel().getSUID() + ", View Model ID = " + suid + " First phase of network creation process (model creation) is done. \n\n");
	}


	@Override
	public View<CyNode> getNodeView(CyNode node) {
		return this.nodeViews.get(node);
	}

	@Override
	public Collection<View<CyNode>> getNodeViews() {
		return this.nodeViews.values();
	}

	@Override
	public View<CyEdge> getEdgeView(final CyEdge edge) {
		return this.edgeViews.get(edge);
	}

	@Override
	public Collection<View<CyEdge>> getEdgeViews() {
		return this.edgeViews.values();
	}

	@Override
	public Collection<View<? extends CyTableEntry>> getAllViews() {
		final Set<View<? extends CyTableEntry>> views = new HashSet<View<? extends CyTableEntry>>();

		views.addAll(nodeViews.values());
		views.addAll(edgeViews.values());
		views.add(this);

		return views;
	}

	
	// /// Event Handlers //////

	@Override
	public void handleEvent(AboutToRemoveNodeEvent e) {
		if (model != e.getSource())
			return;

		nodeViews.remove(e.getNode());
	}

	@Override
	public void handleEvent(AboutToRemoveEdgeEvent e) {
		if (model != e.getSource())
			return;

		edgeViews.remove(e.getEdge());
	}

	
	@Override
	public void handleEvent(final AddedNodeEvent e) {
		// Respond to the event only if the source is equal to the network model associated with this view.
		if (model != e.getSource())
			return;

		final CyNode node = e.getNode();
		logger.debug("Creating new node view model: " + node.toString());
		final View<CyNode> nv = new NodeViewImpl(node, cyEventHelper, this);
		nodeViews.put(node, nv);
		
		// Cascading event.
		cyEventHelper.fireSynchronousEvent(new AddedNodeViewEvent(this, nv));
	}

	
	@Override
	public void handleEvent(final AddedEdgeEvent e) {
		if (model != e.getSource())
			return;

		final CyEdge edge = e.getEdge();
		final View<CyEdge> ev = new EdgeViewImpl(edge, cyEventHelper, this);
		edgeViews.put(edge, ev); // FIXME: View creation here and in
									// initializer: should be in one place

		cyEventHelper.fireSynchronousEvent(new AddedEdgeViewEvent(this, ev));
	}
	
	// The following methods are utilities for calling methods in upper layer (presentation)
	
	public void fitContent() {
		logger.debug("Firing fitContent event from: View ID = " + this.suid);
		cyEventHelper.fireAsynchronousEvent( new FitContentEvent(this));
	}
	
	public void fitSelected() {
		logger.debug("Firing fitSelected event from: View ID = " + this.suid);
		cyEventHelper.fireAsynchronousEvent( new FitSelectedEvent(this));
	}
	
	public void updateView() {
		logger.debug("Firing update view event from: View ID = " + this.suid);
		cyEventHelper.fireSynchronousEvent( new NetworkViewChangedEvent(this));
	}


	@Override
	public <T, V extends T> void setVisualProperty(
			VisualProperty<? extends T> vp, V value) {
		if(value == null)
			this.visualProperties.remove(vp);
		else
			this.visualProperties.put(vp, value);
		
		cyEventHelper.getMicroListener(NetworkViewChangeMicroListener.class, this).networkVisualPropertySet(this, vp, value);	
	}
	
}
