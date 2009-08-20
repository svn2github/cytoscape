package org.cytoscape.view.presentation.processing.internal.drawable;

import static org.cytoscape.view.presentation.processing.visualproperty.ProcessingVisualLexicon.SPHERE_DETAIL;

import org.cytoscape.view.model.View;

import processing.core.PApplet;

public class Sphere  extends AbstractSolidCyDrawable {
	
	private static final long serialVersionUID = -3971892445041605908L;
	
	private int detail;
	
	public Sphere(PApplet parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	public void draw() {
		super.draw();

		p.sphereDetail(detail);
		p.pushMatrix();
		p.translate(location.x, location.y, location.z);
		p.sphere(size);
		p.popMatrix();

	}
	
	public void setContext(View<?> viewModel) {
		super.setContext(viewModel);
		detail = viewModel.getVisualProperty(SPHERE_DETAIL).intValue();	
	}
	
	

}
