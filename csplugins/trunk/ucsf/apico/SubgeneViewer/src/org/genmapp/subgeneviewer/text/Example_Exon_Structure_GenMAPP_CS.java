package org.genmapp.subgeneviewer.text;

import java.util.Iterator;

import org.genmapp.subgeneviewer.splice.view.Block;
import org.genmapp.subgeneviewer.splice.view.Feature;
import org.genmapp.subgeneviewer.splice.view.Region;
import org.genmapp.subgeneviewer.splice.view.SpliceNetworkView;

public class Example_Exon_Structure_GenMAPP_CS {
	
	/**
	 * generates test data structures for SubgeneViewer class
	 */

	public static SpliceNetworkView testSpliceNetworkView_E1_E4 ()
	{
		SpliceNetworkView netView = new SpliceNetworkView();
		Block block;
		Region region;
		Feature feature;
		String _id;
		
		// E1
		_id = new String("1");
		block = netView.addBlock(_id, "e");
		region = block.addRegion(_id);
		feature = region.addFeature(_id, "E1");
		// E2
		_id = new String("2");
		region = block.addRegion(_id);
		feature = region.addFeature(new String ("1"), "E2");
		// E2-1
		feature = region.addFeature(new String("2"), "E2-1");
		// E2-2
		feature = region.addFeature(new String("3"), "E2-2");
		// E2-3
		feature = region.addFeature(new String("4"), "E2-3");
		// E2-4
		feature = region.addFeature(new String("5"), "E2-4");
		// E2-5
		feature = region.addFeature(new String("6"), "E2-5");
		
		// I2
		block = netView.addBlock(_id, "i");
		region = block.addRegion(new String ("1"));
		feature = region.addFeature(new String("1"), "I2");
		// I2-1
		feature = region.addFeature(new String("2"), "I2-1");
		
		// E3
		_id = new String("3");
		block = netView.addBlock(_id, "e");
		region = block.addRegion(new String ("1"));
		feature = region.addFeature(new String ("1"), "E3");
		// E3-1
		feature = region.addFeature(new String("2"), "E3-1");
		// E3-2
		feature = region.addFeature(new String("3"), "E3-2");
	
		// I3
		_id = new String("4");
		block = netView.addBlock(_id, "i");
		region = block.addRegion(new String ("1"));
		feature = region.addFeature(new String ("1"), "I3");
		// I3-1
		feature = region.addFeature(new String("2"), "I3-1");

		// E4
		_id = new String("5");
		block = netView.addBlock(_id, "e");
		region = block.addRegion(new String ("1"));
		feature = region.addFeature(new String ("1"), "E4");		
					
		return netView;
	}
	
	/**
	 * prints out the input argument SpliceNetworkView contents
	 * @param view
	 */
	public static void dumpSpliceNetworkView(SpliceNetworkView view)
	{
		// first print out header line
		System.out.println("feature_id\ttype\tblock\tregion\tfeature");
		
		// now iterate through blocks, regions, features
		Iterator<Block> block_it = view.getBlockInterator();
		while (block_it.hasNext())
		{
			Block block = block_it.next();
			Iterator<Region> region_it = block.getRegionInterator();
			while (region_it.hasNext())
			{
				Region region = region_it.next();
				Iterator<Feature> feature_it = region.getFeatureInterator();
				while (feature_it.hasNext())
				{
					Feature feature = feature_it.next();
					System.out.println(feature.getFeature_id() + "\t" + 
							block.getType() + "\t" + block.getId() + "\t" +
							region.getId() + "\t" + feature.getId());
				}
			}
		}
	}

}
