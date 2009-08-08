package org.cytoscape.view.presentation.processing.internal.ui;

import processing.core.PApplet;
import processing.core.PFont;

public class Overlay {
	
	// Pre-defined font sizes
	private final float smallSize = 12;
	private float fontSize = 32;
	
	// reference to parent
	private PApplet p;
	
	private PFont bigFont;
	private PFont smallFont;
	
	private String title;
	
	public Overlay(PApplet p, String title) {
		this.p = p;
		this.title = title;
		
		bigFont = this.p.createFont("SansSerif", fontSize);
		smallFont = this.p.createFont("SansSerif", smallSize);
		
	}
	
	public void draw() {
		
		p.fill(255, 255, 255, 90);
		p.noStroke();
		p.rect(10, p.height - 100, p.width-20, 90);
//		p.rect(p.width-420, 60, 400, p.height-200);
//		p.fill(50, 50, 50, 120);
//		p.rect(p.width-420, 60, 400, 80);
		
		p.fill(255, 255, 255, 230);
		p.textFont(bigFont);
		p.text("Network: " + title, 30, p.height-90+fontSize);
		p.textFont(smallFont);
		p.text((int)p.frameRate + " FPS", p.width-100, p.height-50+fontSize);
		
		p.fill(255, 255, 255, 90);
		p.stroke(100, 100, 100, 200);
		p.rect(10, 10, p.width-20, 30);
		p.noStroke();
		p.fill(255, 0, 0, 90);
		p.rect(11, 11, p.width/3, 29);
		
	}
	
	private void drawWindow() {
		
	}

}
