package org.cytoscape.bamevent;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.cytoscape.bamevent.impl.EventNotificationImpl;

/**
 * Test that an EventNotification can be subclassed and passed to an
 * Listener. Show this by creating an AttributeEventNotification
 * that passes extra information about attribute changes to a CyRow
 * and compare it with the generic EventNofication for a CyRow.
 */
public class SubClassEventNotificationTest extends TestCase {
    // Used to make an id for FakeCyRow:
    static private int globalID = 1;
    
    private EventFactory factory = ConcreteEventFactory.getInstance();
    private EventManager manager = factory.getEventManager();

    public static Test suite() {
        // Will dynamically add all methods as tests that begin with 'test'
        // and have no arguments:
        return new TestSuite(SubClassEventNotificationTest.class);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public void testSubClassEventNotification () {
	FakeCyRow row = new FakeCyRow ();

	// interested in the attributes associates with node1:
	EventConditions ec = factory.createEventConditions (factory.createEventAction("ATTRIBUTE.SET"));
	//  This is an instance-based event, it applies to all 
	manager.registerInterest (row, new RegularAttributeSetListener(), ec);	
	manager.registerInterest (row, new SpecialAttributeSetListener(), ec);
	// Cause regular event to be fired:
	row.set ("canonicalName", "gene1");
	// Cause subclass event to be fired:
	row.setWithSpecialNotification ("canonicalName", "gene1");
    }

    ////// Event Receivers ////////

    // Show we can use a subclass of EventNotification:
    public interface AttributeEventNotification<T> extends EventNotification<T> {
	public String getAttributeName ();
	public Object getAttributeOldValue ();
	public Object getAttributeNewValue ();
    }
    
    class AttributeEventNotificationImpl<T> extends EventNotificationImpl<T> implements  AttributeEventNotification<T> {
	private String attrName;
	private Object attrOldValue;
	private Object attrNewValue;	
	public AttributeEventNotificationImpl (Notifier notifier, T context, EventAction action,
					       String attributeName,
					       Object attributeOldValue,
					       Object attributeNewValue) {
	    super (notifier,context,action);
	    attrName = attributeName;
	    attrOldValue = attributeOldValue;
	    attrNewValue = attributeNewValue;
	}
	public String  getAttributeName() {
	    return attrName;
	}
	public Object  getAttributeOldValue () {
	    return attrOldValue;
	}
	public Object  getAttributeNewValue () {
	    return attrNewValue;
	}
	
    }

    private class RegularAttributeSetListener implements Listener<FakeCyRow,EventNotification<FakeCyRow>> {
	public void receiveNotification (EventNotification<FakeCyRow> note) {
	    String attributeChanged = (String)note.getSupportingInfo().get(0);
	    Object attributeOldValue =  note.getSupportingInfo().get(1);
	    Object  attributeNewValue = note.getSupportingInfo().get(2);
	    System.out.println ("Called RegularAttributeSetListener.receiveNotification, CyRow= " +
				note.getContext() +
				" belongs to net1 gene1 " + 
				" attributeChanged = " + attributeChanged + 
				" attributeOldValue = " + attributeOldValue + 
				" attributeNewValue = " + attributeNewValue);
	}
    }
    private class SpecialAttributeSetListener implements Listener<FakeCyRow,AttributeEventNotification<FakeCyRow>> {
    
	public void receiveNotification (AttributeEventNotification<FakeCyRow> note) {
	    String attributeChanged = note.getAttributeName();
	    Object  attributeOldValue = note.getAttributeOldValue();
	    Object  attributeNewValue = note.getAttributeNewValue();
	    System.out.println ("Called SpecialAttributeSetListener.receiveNotification, CyRow= " +
				note.getContext() +
				" attributeChanged = " + attributeChanged + 
				" attributeOldValue = " + attributeOldValue + 
				" attributeNewValue = " + attributeNewValue);
	}
    }

    // Roughly follow Core3 CyRow format:
    private class FakeCyRow implements Notifier {
	private int id;
	private Map<String,String> fakeRow = new HashMap<String,String>();
	
	public FakeCyRow () {
	    id = globalID++;
	}
	
	public String get (String attribute) {
	    return fakeRow.get(attribute);
	}
	
	// Uses regular EventNotification:
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
	
	// Same as set(), but uses AttributeEventNotification:
	public void setWithSpecialNotification (String attribute, String value) {
	    String oldVal = fakeRow.get(attribute);
	    fakeRow.put (attribute,value);
	    AttributeEventNotification<FakeCyRow> note =
		new AttributeEventNotificationImpl(this, // notifier
						   this, // context
						   factory.createEventAction("ATTRIBUTE.SET"), // EventAction
						   attribute,
						   oldVal,
						   value
						   );
	    manager.dispatch (this, note);	    
	}

	public String toString() {
	    return "FakeCyRow " + id;
	}
    }
}
