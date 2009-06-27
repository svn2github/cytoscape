package org.cytoscape.view.presentation.processing.internal.shape;

import processing.core.PApplet;
import processing.core.PImage;

public class Cube extends AbstractObjectShape {

	private final PImage texture;

	public Cube(PApplet parent, PImage texture) {
		super(parent);
		this.texture = texture;
	}

	@Override
	public void draw() {

		p.beginShape(PApplet.QUADS);
		//p.texture(texture);

		// +Z "front" face
		p.vertex(-1, -1, 1, 0, 0);
		p.vertex(1, -1, 1, 1, 0);
		p.vertex(1, 1, 1, 1, 1);
		p.vertex(-1, 1, 1, 0, 1);

		// -Z "back" face
		p.vertex(1, -1, -1, 0, 0);
		p.vertex(-1, -1, -1, 1, 0);
		p.vertex(-1, 1, -1, 1, 1);
		p.vertex(1, 1, -1, 0, 1);

		// +Y "bottom" face
		p.vertex(-1, 1, 1, 0, 0);
		p.vertex(1, 1, 1, 1, 0);
		p.vertex(1, 1, -1, 1, 1);
		p.vertex(-1, 1, -1, 0, 1);

		// -Y "top" face
		p.vertex(-1, -1, -1, 0, 0);
		p.vertex(1, -1, -1, 1, 0);
		p.vertex(1, -1, 1, 1, 1);
		p.vertex(-1, -1, 1, 0, 1);

		// +X "right" face
		p.vertex(1, -1, 1, 0, 0);
		p.vertex(1, -1, -1, 1, 0);
		p.vertex(1, 1, -1, 1, 1);
		p.vertex(1, 1, 1, 0, 1);

		// -X "left" face
		p.vertex(-1, -1, -1, 0, 0);
		p.vertex(-1, -1, 1, 1, 0);
		p.vertex(-1, 1, 1, 1, 1);
		p.vertex(-1, 1, -1, 0, 1);

		p.endShape();

	}

}
