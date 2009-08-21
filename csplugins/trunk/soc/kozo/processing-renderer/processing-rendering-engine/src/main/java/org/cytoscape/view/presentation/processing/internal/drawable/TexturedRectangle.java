package org.cytoscape.view.presentation.processing.internal.drawable;

import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_SIZE;

import java.net.URL;

import org.cytoscape.view.model.View;

import processing.core.PApplet;
import processing.core.PImage;

public class TexturedRectangle extends AbstractSolidCyDrawable {

	protected float width;
	protected float height;
	
	private PImage texture;
	
	private static final String DEF_IMG_URL = "";

	public TexturedRectangle(PApplet parent) {
		super(parent);
		URL defURL = this.getClass().getClassLoader().getResource("images/textures/SABP2.png");
		texture = this.p.loadImage(defURL.toString());
		
		if(texture == null) {
			System.out.println("Texture NULL!!!!!!!!!!!!!! ");
		} else {
			System.out.println("Texture = " + texture.width);
			
		}
	}

	public void draw() {
		//super.draw();
		p.noStroke();
		p.pushMatrix();
		p.translate(0, 0, location.z);
		p.beginShape();
		p.texture(texture);
		p.vertex(location.x, location.y, 0, 0);
		p.vertex(location.x+width, location.y, texture.width, 0);
		p.vertex(location.x+width, location.y+height, texture.width, texture.height);
		p.vertex(location.x, location.y+height, 0, texture.height);
		p.endShape();
		
		p.popMatrix();
	}

	public void setContext(View<?> viewModel) {
		super.setContext(viewModel);

//		
//		width = size;
//		height = viewModel.getVisualProperty(NODE_Y_SIZE).floatValue();
//		if (height <= 0)
//			height = DEF_SIZE;
		
		width = texture.width;
		height = texture.height;

	}
}