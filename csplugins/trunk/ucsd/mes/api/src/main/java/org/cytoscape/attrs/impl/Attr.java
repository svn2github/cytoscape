package org.cytoscape.attrs.impl;

class Attr {

	private final Class type;

	private Object attr;
	private String description;
	private boolean editable;
	private boolean visible;

	Attr(Object attr, String description, boolean editable, boolean visible) {

		if ( attr == null )
			throw new NullPointerException("attr is null");
		
		if ( description == null )
			throw new NullPointerException("description is null");

		this.attr = attr;
		this.type = attr.getClass();
		this.description = description;
		this.editable = editable;
		this.visible = visible;
	}

	Object getAttr() {
		return attr;
	}

	void setAttr(Object o) {
		if ( o == null )
			throw new NullPointerException("attr is null");
		
		if ( o.getClass() != type )
			throw new IllegalArgumentException("attr is of the wrong type");

		synchronized(this) {
			attr = o;
		}
	}

	Class getType() {
		return type;
	}

	String getDescription() {
		return description;
	}

	void setDescription(String s) {
		if ( s == null )
			throw new NullPointerException("description is null");

		synchronized(this) {
			description = s;
		}
	}

	boolean isEditable() {
		return editable;
	}

	synchronized void setEditable(boolean b) {
		editable = b;
	}

	boolean isVisible() {
		return visible;
	}

	synchronized void setVisible(boolean b) {
		visible = b;
	}
}
