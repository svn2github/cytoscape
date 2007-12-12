/**
 * 
 */
package stub.controller;

import stub.controller.event.*;
import stub.graph.GraphObject;

/**
 * @author skillcoy
 *
 */
public class MainViewController implements Controller {
	/* um, should the controller be created this way?
	 * Since it is supposed to be the public API of the view model, maybe
	 * the view model instantiates it??  
	 */
	private stub.view.model.MainViewModel viewDataModel;
	private EventPublisher evPub;
	
	public MainViewController(stub.view.model.MainViewModel vdm) {
		this.viewDataModel = vdm;
	}
	
	public void setEventPublisher(EventPublisher ep) {
		evPub = ep;
	}
	
	// I'm sure this can be done with generics instead of overloading...but I don't quite know how
	public void select(stub.graph.Node node, boolean select) {
		this.viewDataModel.setSelected(node, select);
		evPub.publishEvent( new SelectEvent(node, select) );

	}
	public void select(stub.graph.Edge edge, boolean select) {
		this.viewDataModel.setSelected(edge, select);
		evPub.publishEvent( new SelectEvent(edge, select) );
	}
	
	public void changeColor(stub.graph.Node node, java.awt.Color color) {
		this.viewDataModel.setColor(node, color);
		System.out.println("Setting " + node.getName() + " to color " + color.toString());
		evPub.publishEvent( new ColorEvent(node, color) );

	}
	public void changeColor(stub.graph.Edge edge, java.awt.Color color) {
		this.viewDataModel.setColor(edge, color);
		evPub.publishEvent( new ColorEvent(edge, color) );
	}
	
	
	public class SelectEvent {

		private GraphObject graphObj;
		private boolean selected;
		// not sure this is the right way to do this
		public SelectEvent(GraphObject go, boolean s) {
			graphObj = go;
			selected = s;
		}
		
		public GraphObject getGraphObject() {
			return graphObj;
		}
		
		public boolean getSelectionState() {
			return selected;
		}
	}
	
	public class ColorEvent<G> {
		private G graphObj;
		private java.awt.Color color;
		
		public ColorEvent(G go, java.awt.Color c) {
			graphObj = go;
			color = c;
		}
		
		public G getGraphObject() {
			return graphObj;
		}
		
		public java.awt.Color getColor() {
			return color;
		}
	}

	
	
}
