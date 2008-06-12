
package org.cytoscape.attrs.events.impl;

import org.cytoscape.attrs.events.AttributeSetEvent;
import org.cytoscape.attrs.events.AttributeDeletedEvent;

import org.cytoscape.attrs.CyAttributes;

public class AttrEvent 
	implements AttributeSetEvent, 
	           AttributeDeletedEvent {

	public static AttributeDeletedEvent getAttributeDeletedEvent(CyAttributes source, String attributeName) {
		return new AttrEvent(source,attributeName,null,null);
	}

	public static AttributeSetEvent getAttributeSetEvent(CyAttributes source, String attributeName, Object oldValue, Object newValue) {
		return new AttrEvent(source,attributeName,oldValue,newValue);
	}

	final Object oldValue;
	final Object newValue;
	final CyAttributes source;
	final String attributeName; 

	private AttrEvent(CyAttributes source, String attributeName, Object oldValue, Object newValue) {
		this.source = source;
		this.attributeName = attributeName;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public CyAttributes getSource() {
		return source;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public Object getNewValue() {
		return newValue;
	}
}
