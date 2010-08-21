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
import org.cytoscape.model.GraphObject;
import org.cytoscape.model.events.AboutToRemoveEdgeEvent;
import org.cytoscape.model.events.AboutToRemoveEdgeListener;
import org.cytoscape.model.events.AboutToRemoveNodeEvent;
import org.cytoscape.model.events.AboutToRemoveNodeListener;
import org.cytoscape.model.events.AddedEdgeEvent;
import org.cytoscape.model.events.AddedEdgeListener;
import org.cytoscape.model.events.AddedNodeEvent;
import org.cytoscape.model.events.AddedNodeListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.events.AddedEdgeViewEvent;
import org.cytoscape.view.model.events.AddedNodeViewEvent;
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

	
	public NetworkViewImpl(final CyNetwork network, final CyEventHelper cyEventHelper) {
		super(network, cyEventHelper);

		nodeViews = new HashMap<CyNode, View<CyNode>>();
		edgeViews = new HashMap<CyEdge, View<CyEdge>>();

		for (final CyNode node : network.getNodeList())
			nodeViews.put(node, new ViewImpl<CyNode>(node, cyEventHelper));

		for (CyEdge edge : network.getEdgeList())
			edgeViews.put(edge, new ViewImpl<CyEdge>(edge, cyEventHelper));
		
		logger.info("* Network View Created.  SUID = " + suid);
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
	public Collection<View<? extends GraphObject>> getAllViews() {
		final Set<View<? extends GraphObject>> views = new HashSet<View<? extends GraphObject>>();

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
		if (model != e.getSource()) {
			logger.error("Error adding node: wrong network! " + model.toString()
					+ " ~~ " + e.getSource().toString());
			return;
		}

		final CyNode node = e.getNode();
		System.out.println(" Adding node to view: " + node.toString());
		final View<CyNode> nv = new ViewImpl<CyNode>(node, cyEventHelper);
		nodeViews.put(node, nv);
		cyEventHelper.fireSynchronousEvent(new AddedNodeViewEvent(this, nv));
	}

	
	@Override
	public void handleEvent(final AddedEdgeEvent e) {
		if (model != e.getSource()) {
			logger.error("Error adding edge: wrong network! " + model.toString()
					+ " ~~ " + e.getSource().toString());
			return;
		}

		final CyEdge edge = e.getEdge();
		System.out.println(" Adding edge to view! " + edge.toString());
		final View<CyEdge> ev = new ViewImpl<CyEdge>(edge, cyEventHelper);
		edgeViews.put(edge, ev); // FIXME: View creation here and in
									// initializer: should be in one place

		cyEventHelper.fireSynchronousEvent(new AddedEdgeViewEvent(this, ev));
	}
	
	
	//// TODO: FIXME: The following methods will be removed!
	public void fitContent() {
		System.out.println("running dummy fitContent");
	}
	public void fitSelected() {
		System.out.println("running dummy fitSelected");
	}
	public void updateView() {
		cyEventHelper.fireAsynchronousEvent( new NetworkViewChangedEvent(NetworkViewImpl.this));
	}

}
