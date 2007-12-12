/**
 * 
 */
package stub.view.model;

import stub.graph.*;

import java.util.*;
import java.awt.Color;

public class MainViewModel {

	private HashMap<Node, Color> nodeColorMap = new HashMap<Node, Color>();
	private HashMap<Edge, Color> edgeColorMap = new HashMap<Edge, Color>();;
	
	private HashMap<Node, Boolean> nodeSelectionMap = new HashMap<Node, Boolean>();
	private HashMap<Edge, Boolean> edgeSelectionMap = new HashMap<Edge, Boolean>();;

	
	public void setColor(Node node, Color color) {
			nodeColorMap.put(node, color);
		
	}
	public void setColor(Edge edge, Color color) {
			edgeColorMap.put(edge, color);
	}
	
	public void setSelected(Node node, boolean selected) {
			nodeSelectionMap.put(node, selected);
	}
		
	public void setSelected(Edge edge, boolean selected) {
			edgeSelectionMap.put(edge, selected);
	}

	
	
}
