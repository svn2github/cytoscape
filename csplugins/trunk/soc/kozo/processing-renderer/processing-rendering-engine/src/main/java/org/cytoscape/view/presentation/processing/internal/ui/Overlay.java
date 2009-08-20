package org.cytoscape.view.presentation.processing.internal.ui;

import java.awt.Color;

import processing.core.PApplet;
import processing.core.PFont;

public class Overlay {

	// Pre-defined font sizes
	private final float S_FONT_SIZE = 12f;
	private float L_FONT_SIZE = 32f;

	// reference to parent
	private PApplet p;

	private PFont bigFont;
	private PFont smallFont;

	private String title = "?";

	// Main info window appearence parameters
	private static final Color INFO_WIN_COLOR = new Color(10, 10, 10, 100);
	private static final Color INFO_WIN_BORDER_COLOR = new Color(220, 220, 220,
			150);
	private static final Color INFO_WIN_FONT_COLOR = new Color(255, 255, 255,
			200);
	private static final float INFO_WIN_BORDER_WIDTH = 5f;

	private static final float ARC_SIZE = 10f;
	private static final float PADDING = 10f;

	// Sizes of window

	public Overlay(PApplet p, String title) {
		this.p = p;
		this.title = title;

		bigFont = this.p.createFont("SansSerif", L_FONT_SIZE);
		smallFont = this.p.createFont("SansSerif", S_FONT_SIZE);
	}

	public void draw() {
		drawInfoWindow();

		// p.fill(255, 255, 255, 90);
		// p.stroke(100, 100, 100, 200);
		// p.rect(10, 10, p.width - 20, 30);
		// p.noStroke();
		// p.fill(255, 0, 0, 90);
		// p.rect(11, 11, p.width / 3, 29);

	}

	private void drawInfoWindow() {
		p.fill(INFO_WIN_COLOR.getRed(), INFO_WIN_COLOR.getGreen(),
				INFO_WIN_COLOR.getBlue(), INFO_WIN_COLOR.getAlpha());
		p.stroke(INFO_WIN_BORDER_COLOR.getRed(), INFO_WIN_BORDER_COLOR
				.getGreen(), INFO_WIN_BORDER_COLOR.getBlue(),
				INFO_WIN_BORDER_COLOR.getAlpha());
		p.strokeJoin(PApplet.ROUND);
		p.strokeWeight(INFO_WIN_BORDER_WIDTH);

		float width = p.width - PADDING * 2;
		float windowHeight = 70f;

		p.rect(10, p.height - windowHeight - PADDING, width, windowHeight);

		p.fill(INFO_WIN_FONT_COLOR.getRed(), INFO_WIN_FONT_COLOR.getGreen(),
				INFO_WIN_FONT_COLOR.getBlue(), INFO_WIN_FONT_COLOR.getAlpha());
		p.textFont(bigFont);
		p.textAlign(PApplet.LEFT, PApplet.CENTER);
		p.text("Network: " + title, PADDING*2, (p.height - PADDING - windowHeight/2f));
		p.textFont(smallFont);
		p.text((int) p.frameRate + " FPS", p.width - 60, p.height-PADDING*3);

	}
}
