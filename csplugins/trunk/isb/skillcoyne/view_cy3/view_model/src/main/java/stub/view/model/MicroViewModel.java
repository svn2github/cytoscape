/**
 * 
 */
package stub.view.model;

import stub.graph.*;
/**
 * @author skillcoy
 *
 */

public class MicroViewModel {
// imagine this view model is for only a single node
	private Node vNode;
	private boolean selected = false;
	private java.awt.Color nodeColor = java.awt.Color.white;
	
	public MicroViewModel(Node node) {
		vNode = node;
	}

	public Node getNode() {
		return vNode;
	}
	
	public void setSelected(boolean select) {
		selected = select;
	}
	
	public void setColor(java.awt.Color color) {
		nodeColor = color;
	}
	 
}
