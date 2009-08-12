package org.cytoscape.view.presentation.processing.internal.shape;

import java.util.List;
import java.util.Set;

import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.Pickable;

import toxi.geom.Quad;
import toxi.geom.Vec3D;

public class CyQuad extends Quad implements CyDrawable, Pickable {

	public CyQuad(Vec3D[] vertices, int vertOffset) {
		super(vertices, vertOffset);
		// TODO Auto-generated constructor stub
	}

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

		
	}

	public boolean isPicked() {
		// TODO Auto-generated method stub
		return false;
	}

	public void pick(float x, float y) {
		// TODO Auto-generated method stub
		
	}

	public void setContext(View<?> viewModel, VisualProperty<?> vp) {
		// TODO Auto-generated method stub
		
	}

	public void addChild(CyDrawable child) {
		// TODO Auto-generated method stub
		
	}

}
