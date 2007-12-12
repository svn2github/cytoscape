/**
 * 
 */
package stub.controller;

import stub.controller.event.Event;
import stub.controller.event.EventHandler;
import stub.controller.event.EventPublisher;
import stub.view.model.MainViewModel;

/**
 * @author skillcoy
 * 
 */
public class MicroViewController implements Controller{

	private stub.view.model.MicroViewModel viewDataModel;


	public MicroViewController(stub.view.model.MicroViewModel vdm) {
		this.viewDataModel = vdm;
	}

	public void setEventPublisher(EventPublisher ep) {
		EventPublisher evPub = ep;
		// not quite understanding what the type is I'm using for the generic...
		evPub.registerInterest(MainViewController.ColorEvent.class,
				new EventHandler<MainViewController.ColorEvent>() {

					public void handle(MainViewController.ColorEvent evt) {
						System.out.println(MicroViewController.class + " EventHandler.handle() " + evt.toString());
						if (evt.getGraphObject().equals(viewDataModel.getNode())) 
							changeColor(viewDataModel.getNode(), evt.getColor());
					}
				});

	}

	// I'm sure this can be done with generics instead of overloading...but I
	// don't quite know how
	public void select(boolean select) {
		this.viewDataModel.setSelected(select);
	}

	public void changeColor(stub.graph.Node node, java.awt.Color color) {
		this.viewDataModel.setColor(color);
	}

}
