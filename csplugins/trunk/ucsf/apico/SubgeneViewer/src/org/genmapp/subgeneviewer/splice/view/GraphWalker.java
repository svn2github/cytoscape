package org.genmapp.subgeneviewer.splice.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Iterator;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

public class GraphWalker {

	/**
	 * Static methods for walking through the SubgeneNetworkView data structures
	 * and performing functions as we go along.
	 * 
	 * the vertical arrangement of the SubgeneNetworkView should look as follows
	 * 
	 * **********************************************************************************************
	 * VGAP (vertical Gap) -------------------- TITLE_AND_LEGEND_HEIGHT (TBD)
	 * 
	 * --------------------- VGAP --------------------- Label Track NodeHeight
	 * ---------------------- Block and Region Track (2 * NodeHeight)
	 * ---------------------- Splice Event track (NodeHeight)
	 * ---------------------- Label track (NodeHeight) ----------------------
	 * Feature track (NodeHeight) ---------------------- Feature Labels
	 * (NodeHeight) ---------------------- VGAP
	 * 
	 * **********************************************************************************************
	 * 
	 * @author ajk
	 * 
	 */
	// todo: empirically use defaults for now, but drive this from Vizmapper
	// later
	private static final int NODE_HEIGHT = 20;

	private static final int NODE_WIDTH = 40;

	private static final int VGAP = NODE_HEIGHT / 2;

	private static final int HGAP = NODE_WIDTH / 2;

	private static final int BLOCK_HEIGHT = NODE_HEIGHT / 2;

	private static final int START_SITE_HEIGHT = NODE_HEIGHT;

	private static final int SPLICE_EVENT_HEIGHT = NODE_HEIGHT;

	private static final int LABEL_TRACK_HEIGHT = NODE_HEIGHT;

	private static final int TITLE_LEGEND_HEIGHT = NODE_HEIGHT;

	private static final int FLAG_WIDTH = 10; // width of bounding box for
												// triangle of flag on StartSite

	private static final int FLAG_HEIGHT = 10; // height of bounding box for
												// triangle of flag on StartSite

	/**
	 * perform a depth-first search of a SubgeneNetworkView and set bounds for
	 * all Blocks, Regions, Features works by side-effect
	 * 
	 * @param view
	 */
	public static void calculateBounds(SpliceNetworkView view) {
		// coordinate system runs from top of
		int xOffset = HGAP;
		int blockBoundsY = VGAP + TITLE_LEGEND_HEIGHT + VGAP
				+ LABEL_TRACK_HEIGHT + START_SITE_HEIGHT;
		int featuresBoundsY = blockBoundsY + NODE_HEIGHT // for block
				+ SPLICE_EVENT_HEIGHT + LABEL_TRACK_HEIGHT;
		Block block;
		Region region;
		Feature feature;
		int blockXOffset = xOffset;
		int regionXOffset = xOffset;
		int featureXOffset = xOffset;

		Iterator<Block> blocks = view.getBlockInterator();
		while (blocks.hasNext()) {

			block = blocks.next();
			blockXOffset = xOffset;
			String type = block.getType();

			Iterator<Region> regions = block.getRegionInterator();
			while (regions.hasNext()) {
				region = regions.next();
				regionXOffset = xOffset;

				Iterator<Feature> features = region.getFeatureInterator();
				while (features.hasNext()) {
					feature = features.next();
					featureXOffset = xOffset;
					feature.setBounds(featureXOffset + (HGAP / 2),
							featuresBoundsY, NODE_WIDTH, NODE_HEIGHT);
					xOffset += NODE_WIDTH + HGAP;
				}

				// regions sit within and subdivide blocks
				// System.out.println("setting bounds for region: " + region);
				// System.out.println("of block type: " + type);
				if (type.trim().equals("I")) {
					region
							.setBounds(regionXOffset, blockBoundsY
									+ (NODE_HEIGHT / 2) - 1, xOffset
									- regionXOffset, 1); // 2-pixel line for
					// introns
				} else if (type.trim().equals("e")) {
					region.setBounds(regionXOffset, blockBoundsY
							+ (NODE_HEIGHT / 4), xOffset - regionXOffset,
							NODE_HEIGHT / 2); // block/region height is half
					// of a feature's height
				}
				// System.out.println("set bounds for region: " + region);
				// System.out.println("= bounds: " + region.getBounds());

			}

			if (type.trim().equals("I")) {
				block.setBounds(blockXOffset, blockBoundsY + (NODE_HEIGHT / 2)
						- 1, xOffset - blockXOffset, 2); // 2-pixel line for
				// introns
			} else if (type.trim().equals("e")) {
				block.setBounds(regionXOffset,
						blockBoundsY + (NODE_HEIGHT / 4), xOffset
								- blockXOffset, NODE_HEIGHT / 2); // block/region
				// height is
				// half of a
				// feature's
				// height
			}
		}

	}

	/**
	 * perform a depth-first search of a SubgeneNetworkView and render all
	 * Blocks, Regions, Features, Splice Events, and Start sites
	 * 
	 * @param view
	 */
	public static void renderView(SpliceNetworkView view) {
		Block block;
		Region region;
		Feature feature;
		Graphics g = view.getGraphics();
		Rectangle rect;

		g.setColor(Color.black);

		Iterator<Block> blocks = view.getBlockInterator();
		while (blocks.hasNext()) {

			block = blocks.next();
			Iterator<Region> regions = block.getRegionInterator();
			while (regions.hasNext()) {
				region = regions.next();
				rect = region.getBounds();
				// System.out.println ("Drawing region: " + region.getId() +
				// " in bounding box " + rect);
				g.setColor(new Color(225, 225, 255));
				g.fillRect(rect.x, rect.y, rect.width, rect.height);
				g.setColor(Color.black);
				g.drawRect(rect.x, rect.y, rect.width, rect.height);

				Iterator<Feature> features = region.getFeatureInterator();
				while (features.hasNext()) {
					feature = features.next();
					rect = feature.getBounds();
					// System.out.println ("Drawing feature: " + feature.getId()
					// +
					// " in bounding box " + rect);
					g.drawRect(rect.x, rect.y, rect.width, rect.height);

					// mapping color
					CyNode node = feature.getCyNode();
					CyAttributes att = Cytoscape.getNodeAttributes();
					System.out.println("returned node is "
							+ node.getIdentifier());
					int attR = att.getIntegerAttribute(node.getIdentifier(),
							"red").intValue();
					int attG = att.getIntegerAttribute(node.getIdentifier(),
							"green").intValue();
					int attB = att.getIntegerAttribute(node.getIdentifier(),
							"blue").intValue();

					Color secondColor = new Color(attR, attG, attB);
					// map from green to red through black, THIS IS A TEMPORARY
					// HACK

					g.setColor(secondColor);
					g.fillRect(rect.x, rect.y, rect.width, rect.height);

					System.out.println("color is " + secondColor);

					// g.drawString(feature.getId(), rect.x + 1, rect.y + 1);
					if (attR + attG + attB > 500) {
						g.setColor(Color.black);
					} else {
						g.setColor(Color.white);
					}
					g.drawString(feature.getFeature_id().trim(), rect.x + 3,
							rect.y + g.getFont().getSize() + 3);
					g.setColor(Color.black);

				}
				// now paint Splice Events
				Iterator<SpliceEvent> splices = region.listOfSpliceEvents
						.iterator();

				while (splices.hasNext()) {
					SpliceEvent splice = splices.next();
					String blockString = splice.get_toBlock();
					Block toBlock = view.getBlock(blockString);
					String regionString = splice.get_toRegion();
					Region toRegion = toBlock.getRegion(regionString);

					Rectangle regionBounds = view.getBounds(region);
					if (regionBounds == null) {
						return;
					}

					int startX = regionBounds.x + regionBounds.width;
					int startY = regionBounds.y + regionBounds.height;

					System.out.println("Getting bounds for region: " + toRegion
							+ " with name: " + regionString);
					Rectangle toRegionBounds = view.getBounds(toRegion);
					if (toRegionBounds == null) {
						return;
					}

					int endX = toRegionBounds.x;
					int endY = toRegionBounds.y + toRegionBounds.height;

					g.setColor(Color.blue);

					// draw the lines.
					// consider using Graphics2D so that we have control over
					// width of line
					g.drawLine(startX, startY, ((startX + endX) / 2), startY
							+ NODE_HEIGHT);
					g.drawLine(((startX + endX) / 2), startY + NODE_HEIGHT,
							endX, endY);
				}

				// now render Start Site, at this region
				if (region.containsStartSite()) {
					Rectangle regionBounds = region.getBounds();
					if (regionBounds == null) {
						return;
					}

					int startX = regionBounds.x;
					int startY = regionBounds.y - START_SITE_HEIGHT; // draw
																		// flag
																		// above
																		// region
					int endY = regionBounds.y;

					g.setColor(Color.red);

					// draw the staff of the flag
					// consider using Graphics2D so that we have control over
					// width of line
					g.drawLine(startX, startY, startX, endY);

					// now draw the flag
					Polygon p = new Polygon();
					p.addPoint(startX, startY);
					p.addPoint(startX + FLAG_WIDTH, startY + (FLAG_HEIGHT / 2));
					p.addPoint(startX, startY + FLAG_HEIGHT);
					g.fillPolygon(p);
					g.setColor(Color.black);

				}

			}
		}
	}

}
