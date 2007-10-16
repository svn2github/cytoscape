package org.genmapp.subgeneviewer.splice.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Iterator;

public class GraphWalker {
	

	/**
	 * Static methods for walking through the SubgeneNetworkView data structures
	 * and performing functions as we go along.
	 * 
	 * the vertical arrangement of the SubgeneNetworkView should look as follows
	 * 
	 *********************************************************************************************** 
	 * VGAP (vertical Gap)
	 * --------------------
	 * TITLE_AND_LEGEND_HEIGHT  (TBD)
	 * 
	 * ---------------------
	 * VGAP
	 * ---------------------
	 * Label Track               NodeHeight
	 * ----------------------
	 * Block and Region Track    (2 * NodeHeight)
	 * ----------------------
	 * Splice Event track        (NodeHeight)
	 * ----------------------
	 * Label track               (NodeHeight)
	 * ----------------------
	 * Feature track             (NodeHeight)
	 * ----------------------
	 * Feature Labels            (NodeHeight)
	 * ----------------------
	 * VGAP
	 * 
	 *********************************************************************************************** 
	 * 
	 * @author ajk
	 *
	 */
	// todo: empirically use defaults for now, but drive this from Vizmapper later
	private static final int NODE_HEIGHT = 20;
	private static final int NODE_WIDTH = 40;
	private static final int VGAP = NODE_HEIGHT / 2;
	private static final int HGAP = NODE_WIDTH / 2;
	private static final int BLOCK_HEIGHT = NODE_HEIGHT / 2;
	private static final int START_SITE_HEIGHT = NODE_HEIGHT;
	private static final int SPLICE_EVENT_HEIGHT = NODE_HEIGHT;
	private static final int LABEL_TRACK_HEIGHT = NODE_HEIGHT;
	private static final int TITLE_LEGEND_HEIGHT = 3 * NODE_HEIGHT;

	/**
	 * perform a depth-first search of a SubgeneNetworkView and set bounds for all Blocks, Regions, Features
	 * works by side-effect 
	 * @param view
	 */
	public static void calculateBounds (SpliceNetworkView view)
	{
		// coordinate system runs from top of 
		int xOffset = 0;
		int blockBoundsY = VGAP + TITLE_LEGEND_HEIGHT + VGAP + LABEL_TRACK_HEIGHT + 
		START_SITE_HEIGHT;
		int featuresBoundsY = blockBoundsY + NODE_HEIGHT // for block
		                                   + SPLICE_EVENT_HEIGHT + LABEL_TRACK_HEIGHT;
		Block block;
		Region region;
		Feature feature;
		int blockXOffset = xOffset;
		int regionXOffset = xOffset;
		int featureXOffset = xOffset;
		
		Iterator<Block> blocks = view.getBlockInterator();
		while (blocks.hasNext())
		{
		
			block = blocks.next();
			blockXOffset = xOffset;
			String type = block.getType();
			
			Iterator<Region> regions = block.getRegionInterator();
			while (regions.hasNext())
			{
				region = regions.next();
				regionXOffset = xOffset;
				
				Iterator<Feature> features = region.getFeatureInterator();
				while (features.hasNext())
				{
					feature = features.next();
					featureXOffset = xOffset;
					feature.setBounds(featureXOffset, featuresBoundsY, NODE_WIDTH, NODE_HEIGHT);
					xOffset += NODE_WIDTH + HGAP;
				}
				
				// regions sit within and subdivide blocks
//				System.out.println("setting bounds for region: " + region);
//				System.out.println("of block type: " + type);
				if (type.trim().equals("I"))
				{
					region.setBounds(regionXOffset, blockBoundsY + (NODE_HEIGHT / 2) - 1, xOffset - regionXOffset, 
							2);  // 2-pixel line for introns
				}
				else if (type.trim().equals("e"))
				{
					region.setBounds(regionXOffset, blockBoundsY + (NODE_HEIGHT / 4), xOffset - regionXOffset, 
							NODE_HEIGHT / 2);  // block/region height is half of a feature's height
				}
//				System.out.println("set bounds for region: " + region);
//				System.out.println("= bounds: " + region.getBounds());

			}

			if (type.trim().equals("I"))
			{
				block.setBounds(blockXOffset, blockBoundsY + (NODE_HEIGHT / 2) - 1, xOffset - blockXOffset, 
						2);  // 2-pixel line for introns
			}
			else if (type.trim().equals("e"))
			{
				block.setBounds(regionXOffset, blockBoundsY + (NODE_HEIGHT / 4), xOffset - blockXOffset, 
						NODE_HEIGHT / 2);  // block/region height is half of a feature's height
			}		
		}			
	}
	
	/**
	 * perform a depth-first search of a SubgeneNetworkView and render all
	 * Blocks, Regions, Features, Splice Events, and Start sites
	 * @param view
	 */
	public static void renderView (SpliceNetworkView view)
	{
		Block block;
		Region region;
		Feature feature;
		Graphics g = view.getGraphics();
		Rectangle rect;

		g.setColor(Color.black);
		
		Iterator<Block> blocks = view.getBlockInterator();
		while (blocks.hasNext())
		{
			
			block = blocks.next();
			Iterator<Region> regions = block.getRegionInterator();
			while (regions.hasNext())
			{
				region = regions.next();
				rect = region.getBounds();
//				System.out.println ("Drawing region: " + region.getId() + 
//						" in bounding box " + rect);
				g.drawRect(rect.x, rect.y, rect.width, rect.height);
				
				
				Iterator<Feature> features = region.getFeatureInterator();
				while (features.hasNext())
				{
					feature = features.next();
					rect = feature.getBounds();
//					System.out.println ("Drawing feature: " + feature.getId() + 
//							" in bounding box " + rect);
					g.drawRect(rect.x, rect.y, rect.width, rect.height);

//					g.drawString(feature.getId(), rect.x + 1, rect.y + 1);
					g.setColor(Color.blue);
					
					g.drawString(feature.getFeature_id().trim(), 
							rect.x, rect.y + g.getFont().getSize());
					g.setColor(Color.black);

				}
			}
		}			
	}

}
