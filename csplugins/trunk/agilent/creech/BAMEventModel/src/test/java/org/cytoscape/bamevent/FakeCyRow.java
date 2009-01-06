package org.cytoscape.bamevent;

import java.util.HashMap;
import java.util.Map;

/**
 * Roughly copy Core3 CyRow structure for simple tests.
 */

public class FakeCyRow implements Notifier {
    private static EventFactory factory = ConcreteEventFactory.getInstance();
    private static EventManager manager = factory.getEventManager();
    static private int globalID = 1;
    private int id;
    private Map<String,String> fakeRow = new HashMap<String,String>();

    public FakeCyRow () {
	id = globalID++;
	EventNotification<FakeCyRow> note =
	    factory.createEventNotification (this, // notifier
					     this, // context
					     factory.createEventAction("CYROW.CREATED"), // EventAction
					     null // supportingInfo
					     );
	manager.dispatch (this, note);	    
    }


    public String get (String attribute) {
	return fakeRow.get(attribute);
    }

    public void set (String attribute, String value) {
	String oldVal = fakeRow.get(attribute);
	fakeRow.put (attribute,value);
	EventNotification<FakeCyRow> note =
	    factory.createEventNotification(this, // notifier
					    this, // context
					    factory.createEventAction("ATTRIBUTE.SET"), // EventAction
					    attribute, // supporting arg 1
					    oldVal, // supporting arg 2
					    value // supporting arg 3
					    );
	manager.dispatch (this, note);	    
    }

    public String toString() {
	return "FakeCyRow " + id;
    }
}
