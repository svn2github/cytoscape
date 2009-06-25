package org.cytoscape.view.presentation.processing.internal.ui;

import processing.core.PApplet;
import processing.core.PGraphics3D;
import processing.core.PMatrix;


/**
 * This is for frontend messages.
 * 
 * @author kono
 *
 */
public class BillBoard {
	
	private PApplet p;

	private PGraphics3D m_engine;
	private PMatrix m_inv;

	// From setup() call billboard = new Billboard( (PGraphics3)(this.g));
	public BillBoard(PApplet p) {
		this.p = p;
	}

}