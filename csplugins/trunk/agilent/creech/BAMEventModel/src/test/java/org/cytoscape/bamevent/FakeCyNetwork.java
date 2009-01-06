package org.cytoscape.bamevent;

import java.util.ArrayList;
import java.util.List;

public class FakeCyNetwork implements Notifier {
    private static EventFactory factory = ConcreteEventFactory.getInstance();
    private static EventManager manager = factory.getEventManager();
    static private int netGlobalID = 1;
    private int id;

    private List<FakeCyNode> nodes = new ArrayList<FakeCyNode>();
    FakeCyNetwork () {	
	id = netGlobalID++;
	EventNotification<FakeCyNetwork> note =
	    factory.createEventNotification (this, // notifier
		    this, // context
		    factory.createEventAction("NETWORK.CREATED"), // EventAction
		    null // supportingInfo
	    );
	manager.dispatch (this, note);	    
    }
    
    // Follow C3 API:
    public FakeCyNode addNode () {
	FakeCyNode node = new FakeCyNode();
	nodes.add (node);
	// Perform Event dispatching, we added a node:
	EventNotification<FakeCyNetwork> note =
	    factory.createEventNotification (this, // notifier
					     this, // context
					     factory.createEventAction("NODE.ADDED"), // EventAction
					     node // supportingInfo
					     );
	manager.dispatch (this, note);
	return node;
    }
    
    public void removeNode (FakeCyNode node) {
	// Perform Event dispatching, we are about to remove a node:
	EventNotification<FakeCyNetwork> note =
	    factory.createEventNotification(this, // notifier
					    this, // context
					    factory.createEventAction("NODE.REMOVING"), // EventAction
					    node // supportingInfo
					    );
	manager.dispatch (this, note);	    
	nodes.remove (node);
    }

    public List<FakeCyNode> getNodes () {
	return nodes;
    }

    public String toString() {
	return "FakeCyNetwork: " + id;
    }
}
