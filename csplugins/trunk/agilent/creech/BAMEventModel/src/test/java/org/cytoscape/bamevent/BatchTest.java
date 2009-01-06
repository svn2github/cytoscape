package org.cytoscape.bamevent;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Show how a mixture of high-level actions that want to batch together events and
 * also receive events immediately. Note this this does not address the deletion 
 * problem with batching.
 */
public class BatchTest extends TestCase {
    // Just used to create nodes and nets. Should be placed in the
    // private classes, but statics can't be found in non-public
    // classes:
    static int numCopyCalls;
    private EventFactory factory = ConcreteEventFactory.getInstance();
    private EventManager manager = factory.getEventManager();

    public static Test suite() {
        // Will dynamically add all methods as tests that begin with 'test'
        // and have no arguments:
        return new TestSuite(BatchTest.class);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public void setUp() throws Exception {
	numCopyCalls = 0;
    }

    public void tearDown() throws Exception {
    }

    public void testBatchEvents () {
	

	// interest in updating the display when a node is
	// added. Allow batches and collapsing of notifications:
	EventConditions ec = factory.createEventConditions (factory.createEventAction("NODE.ADDED"),
					    DeliveryTiming.CAN_BE_DELAYED, // DeliverTiming
					    true,                         // collapse notifications
					    DeliveryMethod.SYNCHRONOUS,   // DeliveryMethod
					    null                          // no event filter
					    );
	manager.registerInterest (NetworkUtilities.class, new AddNodeDisplayUpdateListener () , ec);
	
	// register interest in an immediate delivery event about the progress in copying networks:
	ec = factory.createEventConditions (factory.createEventAction("NODE.ADDED"),
					    DeliveryTiming.DELIVER_IMMEDIATELY, // DeliverTiming
					    false,                         // collapse notifications
					    DeliveryMethod.SYNCHRONOUS,   // DeliveryMethod
					    null                          // no event filter
					    );
	manager.registerInterest (NetworkUtilities.class, new NetworkCopyProgressListener () , ec);

	// setup 2 networks with 3 nodes each:
	List<FakeCyNetwork> allNets = new ArrayList<FakeCyNetwork>(2);
	FakeCyNetwork net1 = new FakeCyNetwork();
	allNets.add(net1);
	net1.addNode();
	net1.addNode();
	net1.addNode();
	FakeCyNetwork net2 = new FakeCyNetwork();
	allNets.add(net2);
	net2.addNode();
	net2.addNode();
	net2.addNode();
	NetworkUtilities nu = new NetworkUtilities();
	nu.copyNetwork (net1);
	nu.copyNetworks (allNets);
    }

    ////// Event Receivers ////////

    // Simulate a listener that would do a display update when a Node is added to a network:
    // This listener allows the EventManager to collapse matching notifications:
    private class AddNodeDisplayUpdateListener implements Listener<FakeCyNetwork,EventNotification<FakeCyNetwork>> {
	/**
	 * If 10K Node additions occur in a accumulation and collapseNotifications=true, then
	 * only once notification will take place where this notification will chain to the
	 * other notifications (10K long chain). In this case, since all we would want to do
	 * is update the display, we wouldn't use any of the items in the chain. But, if
	 * a Listener needed to look at the different notifications, it could.
	 */
	public void receiveNotification (EventNotification<FakeCyNetwork> note) {	
	    Assert.assertTrue (note.getEventAction().isEventAction("NODE.ADDED"));	
	    // This should only be called once via copyNetwork(), not once per copied node:
	    numCopyCalls++;
	    Assert.assertTrue (numCopyCalls == 1);
	}
    }

    // Simulate a progress bar listener that immediately receives updates about the progress of copying a set of
    // networks:
    private class NetworkCopyProgressListener implements Listener<FakeCyNetwork,EventNotification<FakeCyNetwork>> {
	public void receiveNotification (EventNotification<FakeCyNetwork> note) {
	    List<Integer> percentList = (List<Integer>)note.getSupportingInfo();
	     int percentComplete = percentList.get(0);
	     // How progress bar here:
	}

    }
    ////// Event Notifiers ////////

    private class NetworkUtilities implements Notifier {
	/**
	 * Simulate copying a network.
	 * Test postponing normal events until after the copy operation.
	 * This points out a problem where you would really want to not fire
	 * any events until the new copy is completed since the network
	 * might be in a half-baked state until the copy is completed.
	 */
	public FakeCyNetwork copyNetwork (FakeCyNetwork net) {
	    // Hold off events until the end of the copy:
	    manager.startAccumulating ();
	    FakeCyNetwork newNet = new FakeCyNetwork();
	    // copyAttributes (this,newNet);
	    List<FakeCyNode> nodes = net.getNodes();
	    for (FakeCyNode node : nodes) {
		newNet.addNode();
	    }
	    // The copy is now complete, fire notifications:
	    manager.dispatchAccumulated ();
	    
	    return newNet;
	}

	/**
	 * Show a higher-level method accumulating what is done by a lower-level method
	 * that also accumulates. We want to show we can delay accumulation until all the networks
	 * have been copied. Yes, this is not a very realistic example...
	 */
	public void copyNetworks (List<FakeCyNetwork> nets) {
	    // Hold off events until the end of the copy:
	    manager.startAccumulating ();	    
	    List<FakeCyNetwork> newNets = new ArrayList<FakeCyNetwork>(nets.size());
	    int netCount = 0;
	    for (FakeCyNetwork net : nets) {
		FakeCyNetwork copyNet = copyNetwork(net);
		newNets.add (copyNetwork(net));
    		// compute how far we are along in copying networks--never have this delayed:
		netCount++;
    		Integer progressPercent = (netCount / nets.size()) * 100;
    		EventNotification<FakeCyNetwork> progressNote =
    		    factory.createEventNotification(this, // notifier
    						    copyNet, // context
    						    factory.createEventAction("NETWORK.COPY.PROGRESS"), // EventAction
    						    progressPercent // supportingInfo
    						    );	    
    		// NOT delayed dispatch:
    		manager.dispatch (this, progressNote);
	    }
	    // The copy is now complete, fire notifications:
	    manager.dispatchAccumulated ();
	}
    }
}
