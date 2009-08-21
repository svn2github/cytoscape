package org.cytoscape.view.presentation.processing.internal.drawable;

import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.Pickable;
import org.cytoscape.view.presentation.processing.internal.ProcessingNetworkRenderer;

import processing.core.PApplet;

/**
 * Wrapper for JOGL-based Cube object.
 * 
 * @author kono
 * 
 */
public class Cube extends AbstractSolidCyDrawable implements Pickable {

	private static final long serialVersionUID = -3971892445041605908L;

	public Cube(PApplet parent) {
		super(parent);
	}

	public void draw() {
		super.draw();

		p.pushMatrix();
		p.translate(location.x, location.y, location.z);
		p.box(size);
		p.popMatrix();
		

	}

	
	public boolean isPicked() {
		return selected;
	}

	public void pick(float cx, float cy) {

		final float distance = PApplet.dist(cx, cy, p.screenX(location.x,
				location.y, location.z), p.screenY(location.x, location.y,
				location.z));
		System.out.println("Distance = " + distance);
		if (distance < 200) {
			selected = true;
			System.out.println("PICKED!!");
			this.r = 0;
			g = 250;
			b = 0;
			alpha = 255;
			System.out.println("Color of PICKED node" + g);
		} else
			selected = false;

	}

}
