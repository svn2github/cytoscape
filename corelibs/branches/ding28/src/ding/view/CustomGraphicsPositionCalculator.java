package ding.view;

import giny.view.ObjectPosition;
import giny.view.Position;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cytoscape.render.stateful.CustomGraphic;
import cytoscape.render.stateful.NodeDetails;

public class CustomGraphicsPositionCalculator {
	
	/**
	 * Defines displacement.
	 */
	private static final Map<Position, Float[]> DISPLACEMENT_MAP;
	
	static {
		DISPLACEMENT_MAP = new HashMap<Position, Float[]>();
		DISPLACEMENT_MAP.put(Position.CENTER, new Float[]{0f, 0f} );
		
		DISPLACEMENT_MAP.put(Position.NORTH,  new Float[]{0f, 0.5f});
		DISPLACEMENT_MAP.put(Position.NORTH_WEST, new Float[]{-0.5f, 0.5f});
		DISPLACEMENT_MAP.put(Position.NORTH_EAST, new Float[]{0.5f, 0.5f});
		
		DISPLACEMENT_MAP.put(Position.SOUTH,  new Float[]{0f, -0.5f});
		DISPLACEMENT_MAP.put(Position.SOUTH_WEST,  new Float[]{-0.5f, -0.5f});
		DISPLACEMENT_MAP.put(Position.SOUTH_EAST,  new Float[]{0.5f, -0.5f});
		
		DISPLACEMENT_MAP.put(Position.WEST,  new Float[]{-0.5f, 0f});
		
		DISPLACEMENT_MAP.put(Position.EAST,  new Float[]{0.5f, 0f});
	}
	
	private double dispW;
	private double dispH;
	private double dispNW;
	private double dispNH;
	
	CustomGraphicsPositionCalculator() {
		this.dispH = 0;
		this.dispW = 0;
		this.dispNH = 0;
		this.dispNW = 0;
	}
	
	
	public void calculate(final ObjectPosition p, final DNodeView nv) {
		final Iterator<CustomGraphic> itr = nv.customGraphicIterator();
		final List<CustomGraphic> cgList = new ArrayList<CustomGraphic>();
		
		while(itr.hasNext()) {
			CustomGraphic cg = itr.next();
			cg = new CustomGraphic(cg.getShape(), cg.getPaint(), NodeDetails.ANCHOR_CENTER);
		}
		
		final double nw = nv.getWidth();
		final double nh = nv.getHeight();
		Position anc = p.getAnchor();
		Position ancN = p.getTargetAnchor();
		
		
		
	}
	
	private Shape calcDisplacement(ObjectPosition p, Position anc, Position ancN, CustomGraphic cg, DNodeView nv) {
		final double nodeW = nv.getWidth();
		final double nodeH = nv.getHeight();
		final double cgW = cg.getShape().getBounds().getWidth();
		final double cgH = cg.getShape().getBounds().getHeight();
		
		final Float[] disp1 = DISPLACEMENT_MAP.get(anc);
		final Float[] disp2 = DISPLACEMENT_MAP.get(ancN);
		
		// 1. Displacement for graphics
		final double dispX = disp1[0] * nodeW;
		final double dispY = disp1[1] * nodeH;
		
		final double dispNX = disp2[0] * cgW;
		final double dispNY = disp2[1] * cgH;
		
		// calc total and apply transform
		double totalDispX = dispX + dispNX + p.getOffsetX();
		double totalDispY = dispY + dispNY + p.getOffsetY();
		
		final AffineTransform tf = AffineTransform.getTranslateInstance(totalDispX, totalDispY);
		return tf.createTransformedShape(cg.getShape());
		
	}
	
	

}
