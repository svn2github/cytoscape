package giny.model;

import java.util.EventObject;

import giny.view.GraphView;

/**
 * This event will be fired when nested network is created/removed.
 * 
 * @author kono
 *
 */
public class NestedNetworkChangeEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4279473805735358503L;
	
	private final Node parentNode;
	private final GraphView graphView;

	public NestedNetworkChangeEvent(Object source, Node parentNode, GraphView nestedGraphView) {
		super(source);
		this.parentNode = parentNode;
		this.graphView = nestedGraphView;
	}

	
	/**
	 * 
	 * @return Node points to the nested network.
	 */
	public Node getNestedNode() {
		return parentNode;
	}
	
	
	/**
	 * Nested network view pointed by a node in other network.
	 * This can be null because network view may not be available at this point.
	 * 
	 * @return nested network.
	 */
	public GraphView getNestedNetworkView() {
		return this.graphView;
	}
}
