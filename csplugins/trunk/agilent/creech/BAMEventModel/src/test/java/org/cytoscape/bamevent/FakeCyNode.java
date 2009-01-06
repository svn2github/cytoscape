package org.cytoscape.bamevent;

public class FakeCyNode implements Notifier {
    private static EventFactory factory = ConcreteEventFactory.getInstance();
    private static EventManager manager = factory.getEventManager();
    static private int nodeGlobalID = 1;
    private int id;
    private  FakeCyRow cyRow = new FakeCyRow();

    public FakeCyNode () {
	id = nodeGlobalID++;
	EventNotification<FakeCyNode> note =
	    factory.createEventNotification (this, // notifier
					     this, // context
					     factory.createEventAction("NODE.CREATED"), // EventAction
					     null // supportingInfo
					     );
	manager.dispatch (this, note);	    
    }
    public String toString() {
	return "FakeCyNode " + id;
    }
    public FakeCyRow getAttrs() {
	return cyRow;
    }
}
