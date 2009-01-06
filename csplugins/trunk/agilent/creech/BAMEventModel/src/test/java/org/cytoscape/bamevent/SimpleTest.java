package org.cytoscape.bamevent;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Various simple tests of the BAM event model.
 */
public class SimpleTest extends TestCase {
    // create two fake networks with 3 fake genes in each:
    private FakeCyNetwork net1;
    private FakeCyNetwork net2;
    private FakeCyNode net1Gene3;
    private EventFactory factory = ConcreteEventFactory.getInstance();
    private EventManager manager = factory.getEventManager();

    public static Test suite() {
        // Will dynamically add all methods as tests that begin with 'test'
        // and have no arguments:
        return new TestSuite(SimpleTest.class);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public void setUp() throws Exception {
    }

    public void tearDown() throws Exception {
    }


    public void testBAMEventModel () {
	
	// interested in any node event on any network:
	EventConditions ec = factory.createEventConditions (EventAction.ANY);
	manager.registerInterest (FakeCyNetwork.class, new AnyNetOpListener(), ec);	
	ec = factory.createEventConditions (EventAction.ANY, // EventAction
					    factory.createDeliveryOptions(), // DeliveryOptions
					    new Gene3RemovedFilter() // filter
					    );
	manager.registerInterest (FakeCyNetwork.class, new Gene3RemovedListener(), ec);

	// interested in any CyRow change events:
	ec = factory.createEventConditions (factory.createEventAction("ATTRIBUTE.SET"));
	manager.registerInterest (FakeCyRow.class, new Net1Gene1CyRowChangeListener(), ec);


	net1 = new FakeCyNetwork ();
	net2 = new FakeCyNetwork ();

	// Register interest in some events:

	ec = factory.createEventConditions (factory.createEventAction("NODE.ADDED"));
	// interested in node added events to net1:
	manager.registerInterest (net1, new Net1NodeAddedListener(), ec);

	ec = factory.createEventConditions (factory.createEventAction("NODE.REMOVING"));
	// interested in node removed events on net2:
	manager.registerInterest (net2, new Net2NodeRemovedListener(), ec);


	FakeCyNode node = net1.addNode ();
	node.getAttrs().set ("canonicalName", "gene1");
	ec = factory.createEventConditions (factory.createEventAction("ATTRIBUTE.SET"));
	// associate node's FakeCyRow with the listener:
	manager.registerInterest (node.getAttrs(), new Net1Gene1CyRowChangeListener(), ec);

	node = net1.addNode ();
	node.getAttrs().set ("canonicalName", "gene2");
	net1Gene3 = net1.addNode ();
	net1Gene3.getAttrs().set ("canonicalName", "gene3");
	node = net2.addNode ();
	node.getAttrs().set ("canonicalName", "gene1");
	node = net2.addNode ();
	node.getAttrs().set ("canonicalName", "gene2");
	FakeCyNode net2Gene3 = net2.addNode ();
	net2Gene3.getAttrs().set ("canonicalName", "gene3");	
	net1.removeNode (net1Gene3);
	net2.removeNode (net2Gene3);
    }

    ////// Event Receivers ////////

    // only called when net1 has a node added:
    private class Net1NodeAddedListener implements Listener<FakeCyNetwork,EventNotification<FakeCyNetwork>> {
	public void receiveNotification (EventNotification<FakeCyNetwork> note) {
	    System.out.println ("Called Net1NodeAddedListener.receiveNotification, notification= " +
				note);
	    Assert.assertTrue (note.getNotifier() == net1);
	    Assert.assertTrue (note.getEventAction().isEventAction("NODE.ADDED"));
	}
    }

    // only called when net2 has a node removed:
    private class Net2NodeRemovedListener implements Listener<FakeCyNetwork,EventNotification<FakeCyNetwork>> {
	public void receiveNotification (EventNotification<FakeCyNetwork> note) {
	    System.out.println ("Called Net2NodeRemovedListener.receiveNotification, notification= " +
				note);
	    Assert.assertTrue (note.getNotifier() == net2);
	    Assert.assertTrue (note.getEventAction().isEventAction("NODE.REMOVING"));
	}
    }

    // respond to any network operation
    private class AnyNetOpListener implements Listener<FakeCyNetwork,EventNotification<FakeCyNetwork>> {
	public void receiveNotification (EventNotification<FakeCyNetwork> note) {
	    System.out.println ("Called AnyNetOpListener.receiveNotification, notification= " +
				note);
	}
    }

    // respond to any attribute change to any FakeCyRow:
    private class AnyCyRowChangeListener implements Listener<FakeCyRow,EventNotification<FakeCyRow>> {    
	public void receiveNotification (EventNotification<FakeCyRow> note) {
	    String attributeChanged = (String)note.getSupportingInfo().get(0);
	    String attributeOldValue = (String)note.getSupportingInfo().get(1);
	    String attributeNewValue = (String)note.getSupportingInfo().get(2);
	    System.out.println ("Called AnyCyRowChangeListener.receiveNotification, CyRow= " + note.getContext() +
				" attributeChanged = " + attributeChanged + 
				" attributeOldValue = " + attributeOldValue + 
				" attributeNewValue = " + attributeNewValue);
	}
    }

    // respond to a change to an attribute of gene1 in net1:
    private class Net1Gene1CyRowChangeListener implements Listener<FakeCyRow,EventNotification<FakeCyRow>> {
	public void receiveNotification (EventNotification<FakeCyRow> note) {
	    String attributeChanged = (String)note.getSupportingInfo().get(0);
	    String attributeOldValue = (String)note.getSupportingInfo().get(1);
	    String attributeNewValue = (String)note.getSupportingInfo().get(2);
	    System.out.println ("Called Net1Gene1CyRowChangeListener.receiveNotification, CyRow= " +
				note.getContext() +
				" belongs to net1 gene1 " + 
				" attributeChanged = " + attributeChanged + 
				" attributeOldValue = " + attributeOldValue + 
				" attributeNewValue = " + attributeNewValue);
	}
    }
    

    // filtered version of any network operation that is called when
    // Gene3 is removed from any network:
    private class Gene3RemovedListener implements Listener<FakeCyNetwork,EventNotification<FakeCyNetwork>> {
	public void receiveNotification (EventNotification<FakeCyNetwork> note) {
	    System.out.println ("Called Gene3RemovedListener.receiveNotification, notification= " +
				note);
	    Assert.assertTrue (note.getEventAction().isEventAction ("NODE.REMOVING"));
	    FakeCyNode node = (FakeCyNode)(note.getSupportingInfo().get(0));
	    Assert.assertTrue (node == net1Gene3);
	}
    }
    private class Gene3RemovedFilter implements EventFilter {
	// only allow NODE.REMOVING events where Gene3 was removed:
	public boolean includeEvent (EventNotification<?> note) {
	    if (!note.getEventAction().isEventAction ("NODE.REMOVING")) {
		return false;
	    }
	    FakeCyNode node = (FakeCyNode)(note.getSupportingInfo().get(0));	    
	    return (node == net1Gene3);
	}
    }
}


