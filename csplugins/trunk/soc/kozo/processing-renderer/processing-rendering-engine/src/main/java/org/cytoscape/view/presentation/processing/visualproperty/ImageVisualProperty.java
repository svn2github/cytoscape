package org.cytoscape.view.presentation.processing.visualproperty;

import java.awt.Image;

import org.cytoscape.view.model.AbstractVisualProperty;

public class ImageVisualProperty extends AbstractVisualProperty<Image>{

	public ImageVisualProperty(String ot, Image def, String id, String name) {
		super(ot, def, id, name);
	}

	public String getSerializableString(Image value) {
		// TODO Auto-generated method stub
		return null;
	}

	public Image parseSerializableString(String value) {
		// TODO Auto-generated method stub
		return null;
	}

}
