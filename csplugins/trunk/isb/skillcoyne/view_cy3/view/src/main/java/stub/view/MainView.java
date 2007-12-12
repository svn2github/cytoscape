/**
 * 
 */
package stub.view;

import stub.graph.*;
import stub.view.model.*;

/**
 * Also implements a listener for things like mouse events
 *
 */
public class MainView {
	
	private stub.controller.MainViewController mvController;
	private stub.graph.Graph graph;
	private stub.view.model.MainViewModel viewModel;

	public MainView(stub.graph.Graph g, stub.view.model.MainViewModel mvm) {
		this.graph = g;
		this.viewModel = mvm;
	}
	
	public void setController(stub.controller.MainViewController c) {
		this.mvController = c;
	}
	
	// just an example, not actual implementation
	public void onMouseEvent(Node node, java.awt.Color color) {
		// Get the node or edge from the mia then change it's color
		this.mvController.changeColor(node, color);
	}
	
	

	
}
