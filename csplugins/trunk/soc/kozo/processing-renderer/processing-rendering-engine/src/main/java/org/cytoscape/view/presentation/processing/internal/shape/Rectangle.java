package org.cytoscape.view.presentation.processing.internal.shape;

import java.util.List;
import java.util.Set;

import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.Pickable;

import processing.core.PApplet;

import toxi.geom.Vec3D;

public class Rectangle extends Vec3D implements CyDrawable, Pickable {
	
private PApplet p;
	
	private float size;
	private int r, g, b, alpha;

	public void draw() {
		// TODO Auto-generated method stub
		
	}

	public List<CyDrawable> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Class<?>> getCompatibleModels() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setContext(View<?> viewModel) {
		// TODO Auto-generated method stub
		
	}

	public boolean isPicked() {
		// TODO Auto-generated method stub
		return false;
	}

	public void pick(float x, float y) {
		// TODO Auto-generated method stub
		
	}

}
