package stub;
/**
 * 
 */

import stub.controller.*;
import stub.controller.event.*;
import stub.graph.*;
import stub.view.*;
import stub.view.model.*;


/**
 * @author skillcoy
 *
 */
public class MVC_Main {

	private static Node nA;
	private static Node nC;
	private static Edge eAB;
	private static Graph graph;
	 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		setupGraph();
		
		EventPublisher ep = new EventPublisher();
		
		// MainView
		MainViewModel mvm = new MainViewModel();

		MainViewController mvController = new MainViewController(mvm);
		mvController.setEventPublisher(ep);
		
		// presentation
		MainView mainView = new MainView(graph, mvm);
		mainView.setController(mvController);
		
		
		// MicroView
		MicroViewModel microModel = new MicroViewModel(nA);
		
		MicroViewController microC = new MicroViewController(microModel);
		microC.setEventPublisher(ep);

		// presentation
		MicroView microView = new MicroView(graph, microModel);
		microView.setController(microC);
		
		
		mainView.onMouseEvent(nA, java.awt.Color.red);
	}
	
	private static void setupGraph() {
		graph = new Graph();
		
		nA = new Node("A");
		graph.addNode(nA);
		Node nB = new Node("B");
		graph.addNode(nB);
		nC = new Node("C");
		graph.addNode(nC);
		Node nD = new Node("D");
		graph.addNode(nD);
		
		eAB = new Edge("AB", nA, nB);
		graph.addEdge(eAB);
		Edge eBC = new Edge("BC", nB, nC);
		graph.addEdge(eBC);
		Edge eCD = new Edge("CD", nC, nD);
		graph.addEdge(eCD);
	}

}
