package org.cytoscape.view.presentation.processing.visualproperty;

import org.cytoscape.view.model.AbstractVisualProperty;

import processing.core.PImage;

public class PImageVisualProperty extends AbstractVisualProperty<PImage> {

	public PImageVisualProperty(String ot, PImage def, String id, String name) {
		super(ot, def, id, name);
	}

	public String getSerializableString(PImage value) {
		return value.toString();
	}

	public PImage parseSerializableString(String value) {
		// TODO Parser required.
		return null;
	}

}