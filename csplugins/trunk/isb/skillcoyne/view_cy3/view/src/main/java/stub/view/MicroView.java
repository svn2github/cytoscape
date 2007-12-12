/**
 * 
 */
package stub.view;

/**
 * @author skillcoy
 *
 */
public class MicroView {

	private stub.controller.MicroViewController mvController;
	
	private stub.graph.Graph graph;
	private stub.view.model.MicroViewModel viewModel;

	public MicroView(stub.graph.Graph g, stub.view.model.MicroViewModel mvm) {
		this.graph = g;
		this.viewModel = mvm;
	}

	public void setController(stub.controller.MicroViewController c) {
		mvController = c;
	}

}
