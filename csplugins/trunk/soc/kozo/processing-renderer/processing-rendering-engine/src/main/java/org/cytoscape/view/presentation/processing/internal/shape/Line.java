package org.cytoscape.view.presentation.processing.internal.shape;

import java.awt.Color;
import java.util.List;
import java.util.Set;

import org.cytoscape.view.presentation.processing.CyDrawable;

import processing.core.PApplet;
import toxi.geom.Vec3D;

public class Line implements CyDrawable {
	
	private final PApplet p;
	
	private Vec3D source;
	private Vec3D target;
	
	private Color color;
	
	public Line(PApplet p) {
		this.p = p;
		this.source = new Vec3D();
		this.target = new Vec3D();
	}

	public void draw() {
		p.stroke(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
		p.line(source.x, source.y, source.z, target.x, target.y, target.z);		
	}

	public List<CyDrawable> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Class<?>> getCompatibleModels() {
		// TODO Auto-generated method stub
		return null;
	}

}
