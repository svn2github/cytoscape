package org.cytoscape.bamevent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.cytoscape.event.CyEvent;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.CyListener;
import org.cytoscape.event.internal.CyEventHelperImpl;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.mock.MockServiceReference;
import org.springframework.osgi.mock.MockBundleContext;
import org.osgi.framework.InvalidSyntaxException;

/**
 * Show the features of instance-based listening and passing multiple
 * arguments to a listener for various event models.
 */
public class InstanceAndArgFeatureTest extends TestCase {
    static private int netGlobalID = 1;
    static private int nodeGlobalID = 1;
    static private int rowGlobalID = 1;

    private FakeCyNetwork net1;
    private FakeCyNetwork net2;
    // BAM Event Model setup:
    private EventFactory bamFactory = ConcreteEventFactory.getInstance();
    private EventManager bamManager = bamFactory.getEventManager();
    // Core3 Event Model setup:
    private CyEventHelper eventHelper;

    public static Test suite() {
        // Will dynamically add all methods as tests that begin with 'test'
        // and have no arguments:
        return new TestSuite(InstanceAndArgFeatureTest.class);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public void setUp() throws Exception {
    }

    public void tearDown() throws Exception {
    }

    public void testEventModels () {
	net1 = new FakeCyNetwork ();
	net2 = new FakeCyNetwork ();

	FakeCyNode net1Node1 = net1.addNode ();
	FakeCyNode net1Node2 = net1.addNode ();
	FakeCyNode net2Node1 = net2.addNode ();
	FakeCyNode net2Node2 = net2.addNode ();
	// only track net1Node1's attribute changes:
	// BAM MODEL SPECIFIC BEGIN:
	EventConditions ec = bamFactory.createEventConditions (bamFactory.createEventAction("ATTRIBUTE.SET"));
	bamManager.registerInterest (net1Node1.getAttrs(), new BAMAttributeSetListener(), ec);
	// BAM MODEL SPECIFIC END.
	// CORE3 MODEL SPECIFIC BEGIN:
	final CORE3AttributeSetListener service = new CORE3AttributeSetListenerImpl (net1Node1.getAttrs());
	final ServiceReference reference = new MockServiceReference();
	BundleContext bc = new MockBundleContext() {
		public ServiceReference getServiceReference(String clazz) {
		    return reference;
		}
		public ServiceReference[] getServiceReferences(String clazz, String filter)
		    throws InvalidSyntaxException {
		    return new ServiceReference[] { reference };
		}
		public Object getService(ServiceReference ref) {
		    if (reference == ref)
			return service;
		    return super.getService(ref);
		}
	  };
	 eventHelper = new CyEventHelperImpl(bc);	
	// CORE3 MODEL SPECIFIC END
	// now cause notifications:
	net1Node1.getAttrs().set ("canonicalName", "gene1");
	net1Node2.getAttrs().set ("canonicalName", "gene2");
	net2Node1.getAttrs().set ("canonicalName", "gene3");
	net2Node2.getAttrs().set ("canonicalName", "gene4");
    }


    ////// Listeners ////////

    // BAM //
    // respond to a Node attribute change to our "selected" node (net1Node1):
    private class BAMAttributeSetListener implements Listener<FakeCyRow,EventNotification<FakeCyRow>> {
	public void receiveNotification (EventNotification<FakeCyRow> note) {
	    String attributeChanged = (String)note.getSupportingInfo().get(0);
	    String attributeOldValue = (String)note.getSupportingInfo().get(1);
	    System.out.println ("Called BAMAttributeSetListener.receiveNotification, CyRow= " +
				note.getContext() +
				" attributeChanged = " + attributeChanged + 
				" attributeOldValue = " + attributeOldValue);
	}
    }
    // CORE3 BEGIN //
    
    private class CORE3AttributeSetListenerImpl implements CORE3AttributeSetListener {
	final private FakeCyRow rowOfInterest;
	public CORE3AttributeSetListenerImpl (FakeCyRow rowOfInterest) {
	    this.rowOfInterest = rowOfInterest;
	}

	public void handleEvent (CORE3AttributeSetEvent e) {
	    // Notice that instance notifications are not supported
	    // and that this method may get called lots of times it
	    // doesn't need to:
	    if (e.getSource() != rowOfInterest) return;
	    String attributeChanged = e.getAttributeName();
	    Object attributeOldValue = e.getAttributeOldValue();
	    System.out.println ("Called CORE3 AttributeSetListener.receiveNotification, CyRow= " +
				e.getSource() +
				" attributeChanged = " + attributeChanged + 
				" attributeOldValue = " + attributeOldValue);
	}
    }

    interface CORE3AttributeSetListener extends CyListener {
	public void handleEvent (CORE3AttributeSetEvent e);
    }

    private class CORE3AttributeSetEventImpl implements CORE3AttributeSetEvent {

	final private String attrName;
	final private Object attrOldValue;
	final private FakeCyRow source;
	public CORE3AttributeSetEventImpl (FakeCyRow source, String attrName, Object attrOldValue) {
	    this.source = source;
	    this.attrName = attrName;
	    this.attrOldValue = attrOldValue;
	}
	public String  getAttributeName() {
	    return attrName;
	}
	public Object  getAttributeOldValue () {
	    return attrOldValue;
	}
	public FakeCyRow getSource() {
	    return source;
	}
    }

    interface CORE3AttributeSetEvent extends CyEvent<FakeCyRow> {
	String  getAttributeName();
	Object  getAttributeOldValue ();
    }

    // CORE3 END //

    // *****************TEST OBJECTS & NOTIFIERS*****************

    private class FakeCyNetwork {
	private int id;
	
	private List<FakeCyNode> nodes = new ArrayList<FakeCyNode>();
	FakeCyNetwork () {	
	    id = netGlobalID++;
	}
	
	// Follow C3 API:
	public FakeCyNode addNode () {
	    FakeCyNode node = new FakeCyNode();
	    nodes.add (node);
	    return node;
	}
    	
	public String toString() {
	    return "FakeCyNetwork: " + id;
	}
    }

    public class FakeCyNode {
	private int id;
	private  FakeCyRow cyRow = new FakeCyRow();
	
	public FakeCyNode () {
	    id = nodeGlobalID++;
	}
	public String toString() {
	    return "FakeCyNode " + id;
	}
	public FakeCyRow getAttrs() {
	    return cyRow;
	}
    }

    public class FakeCyRow implements Notifier {
	private int id;
	private Map<String,String> fakeRow = new HashMap<String,String>();
	
	public FakeCyRow () {
	    id = rowGlobalID++;
	}
	
	public String get (String attribute) {
	    return fakeRow.get(attribute);
	}
	
	public void set (String attribute, String value) {
	    String oldVal = fakeRow.get(attribute);
	    fakeRow.put (attribute,value);
	    performBAMNotification (attribute, oldVal);
	    performCore3Notification (attribute, oldVal);
	}

	// BAM MODEL Notification:
	private void performBAMNotification (String attribute, Object oldVal) {
	    EventNotification<FakeCyRow> note =
		bamFactory.createEventNotification(this, // notifier
						this, // context
						bamFactory.createEventAction("ATTRIBUTE.SET"), // EventAction
						attribute, // supporting arg 1
						oldVal); // supporting arg 2
	    bamManager.dispatch (this, note);	    
	}

	// CORE3 MODEL Notification:
	private void performCore3Notification (String attribute, Object oldVal) {
		eventHelper.fireSynchronousEvent(new CORE3AttributeSetEventImpl(this, attribute, oldVal),
		                                 CORE3AttributeSetListener.class);
	}

	public String toString() {
	    return "FakeCyRow " + id;
	}
    }
}


