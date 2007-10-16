package org.genmapp.subgeneviewer.splice.view;

import java.util.Iterator;

/**
 * perform a depth-first search of a SubgeneNetworkView and set bounds for all Blocks, Regions, Features
 * works by side-effect
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
public class RecursiveBoundsCalculator {
	

	// todo: empirically use defaults for now, but drive this from Vizmapper later
	private static final int NODE_HEIGHT = 10;
	private static final int NODE_WIDTH = 20;
	private static final int VGAP = NODE_HEIGHT / 2;
	private static final int HGAP = NODE_WIDTH / 2;
	private static final int BLOCK_HEIGHT = NODE_HEIGHT / 2;
	private static final int START_SITE_HEIGHT = NODE_HEIGHT;
	private static final int SPLICE_EVENT_HEIGHT = NODE_HEIGHT;
	private static final int LABEL_TRACK_HEIGHT = NODE_HEIGHT;
	private static final int TITLE_LEGEND_HEIGHT = 3 * NODE_HEIGHT;
	
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
				
				if (type.equals("i"))
				{
					region.setBounds(regionXOffset, blockBoundsY + (NODE_HEIGHT / 2) - 1, xOffset - regionXOffset, 
							2);  // 2-pixel line for introns
				}
				else if (type.equals("e"))
				{
					region.setBounds(regionXOffset, blockBoundsY + (NODE_HEIGHT / 4), xOffset - regionXOffset, 
							NODE_HEIGHT / 2);  // block/region height is half of a feature's height
				}
			}
			if (type.equals("i"))
			{
				block.setBounds(blockXOffset, blockBoundsY + (NODE_HEIGHT / 2) - 1, xOffset - blockXOffset, 
						2);  // 2-pixel line for introns
			}
			else if (type.equals("e"))
			{
				block.setBounds(regionXOffset, blockBoundsY + (NODE_HEIGHT / 4), xOffset - blockXOffset, 
						NODE_HEIGHT / 2);  // block/region height is half of a feature's height
			}		
		}	
		
	}
	

}
