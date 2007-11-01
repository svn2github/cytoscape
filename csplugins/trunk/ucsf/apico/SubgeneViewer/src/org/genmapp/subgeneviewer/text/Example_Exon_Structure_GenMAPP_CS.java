package org.genmapp.subgeneviewer.text;

import java.util.Iterator;

import org.genmapp.subgeneviewer.splice.view.Block;
import org.genmapp.subgeneviewer.splice.view.Feature;
import org.genmapp.subgeneviewer.splice.view.Region;
import org.genmapp.subgeneviewer.splice.view.SpliceEvent;
import org.genmapp.subgeneviewer.splice.view.SpliceNetworkView;
import org.genmapp.subgeneviewer.splice.view.StartSite;

public class Example_Exon_Structure_GenMAPP_CS {
	
	/**
	 * generates test data structures for SubgeneViewer class
	 */

	public static SpliceNetworkView testSpliceNetworkView (String nodeId)
	{
		SpliceNetworkView netView = new SpliceNetworkView();
		Block block;
		Region region;
		Feature feature;
		String _id;
		String toBlock;
		String toRegion;
		String start;
		SpliceEvent spliceEvent;
		StartSite startSite;
		
		if (nodeId.equals("FAKE")){
			
			System.out.println("Node id is "+nodeId);
			// E1
			block = netView.addBlock("1", "e");
			region = block.addRegion("1");
			region.setColor(0, 51, 102);
			feature = region.addFeature("1", "E1");
			feature.setColor(163, 163, 163);
			
			// Start site
			startSite = new StartSite(region);
			region.containsStartSite(true);
			
//			// Splice Events
//			toBlock = "3");
//			toRegion = "1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I1
			block = netView.addBlock("2", "I");
			region = block.addRegion("1");
			region.setColor(255, 255, 255);
			feature = region.addFeature("1", "I1");
			feature.setColor(163, 163, 163);
			
			// E2
			block = netView.addBlock("3", "e");
			region = block.addRegion("1");
			region.setColor(0, 51, 102);
			feature = region.addFeature("1", "E2");
			feature.setColor(163, 163, 163);
			
			// Splice Events
			toBlock = "5";
			toRegion = "1";
			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
			spliceEvent.setRegion(region);
			spliceEvent.setId(toBlock, toRegion);
			
			// Splice Events
			toBlock = "7";
			toRegion = "1";
			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
			spliceEvent.setRegion(region);
			spliceEvent.setId(toBlock, toRegion);
	
			// I2
			block = netView.addBlock("4", "I");
			region = block.addRegion("1");
			region.setColor(255, 255, 255);
			feature = region.addFeature("1", "I2");
			feature.setColor(163, 163, 163);
			
			// E3
			block = netView.addBlock("5", "e");
			region = block.addRegion("1");
			region.setColor(225, 225, 255);
			feature = region.addFeature("1", "E3");
			feature.setColor(0, 188, 0);
		
			//Splice Events
			toBlock = "7";
			toRegion = "1";
			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
			spliceEvent.setRegion(region);
			spliceEvent.setId(toBlock, toRegion);
		
			// I3
			block = netView.addBlock("6", "I");
			region = block.addRegion("1");
			region.setColor(255, 255, 255);
			feature = region.addFeature("1", "I3");
			feature.setColor(163, 163, 163);

			// E4
			block = netView.addBlock("7", "e");
			region = block.addRegion("1");
			region.setColor(0, 51, 102);
			feature = region.addFeature("1", "E4");
			feature.setColor(163, 163, 163);
		
//			// Splice Events
//			toBlock = "9");
//			toRegion = "1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I4
			block = netView.addBlock("8", "I");
			region = block.addRegion("1");
			region.setColor(255, 255, 255);
			feature = region.addFeature("1", "I4");
			feature.setColor(163, 163, 163);
			
			// E5
			block = netView.addBlock("9", "e");
			region = block.addRegion("1");
			region.setColor(0, 51, 102);
			feature = region.addFeature("1", "E5");
			feature.setColor(163, 163, 163);
		
		}
		
		else if (nodeId.equals("PPP2R3A")){
			System.out.println("Node id is "+nodeId);
		
			// E1
			block = netView.addBlock("1", "e");
			region = block.addRegion("1");
			region.setColor(225, 225, 255);
			feature = region.addFeature("1", "E1");
			feature.setColor(0, 255, 0);
			
			// Start site
			startSite = new StartSite(region);
			region.containsStartSite(true);
			
			// Splice Events
			toBlock = "5";
			toRegion = "1";
			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
			spliceEvent.setRegion(region);
			spliceEvent.setId(toBlock, toRegion);
			

			// I1
			block = netView.addBlock("2", "I");
			region = block.addRegion("1");
			region.setColor(255, 255, 255);
			feature = region.addFeature("1", "I1");
			feature.setColor(163, 163, 163);
			
			// E2

			block = netView.addBlock("3", "e");
			region = block.addRegion("1");
			region.setColor(225, 225, 255);
			feature = region.addFeature(new String ("1"), "E2");
			feature.setColor(255, 0, 0);

			block = netView.addBlock("3", "e");
			region = block.addRegion("1");

			
			// Start site
			startSite = new StartSite(region);
			region.containsStartSite(true);
			
			// Splice Events
			toBlock = "5";
			toRegion = "1";
			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
			spliceEvent.setRegion(region);
			spliceEvent.setId(toBlock, toRegion);
			

			feature = region.addFeature("1", "E2");
			feature.setColor(214, 0, 0);
			

			// E2-1
			feature = region.addFeature("2", "E2-1");
			feature.setColor(240, 0, 0);
			// E2-2
			feature = region.addFeature("3", "E2-2");
			feature.setColor(240, 0, 0);
			
			// E2-3
			feature = region.addFeature("4", "E2-3");
			feature.setColor(189, 0, 0);
			// E2-4
			feature = region.addFeature("5", "E2-4");
			feature.setColor(255, 0, 0);
			// E2-5
			feature = region.addFeature("6", "E2-5");
			feature.setColor(240, 0, 0);
			
			// I2
			block = netView.addBlock("4", "I");
			region = block.addRegion("1");
			region.setColor(255, 255, 255);
			feature = region.addFeature("1", "I2");
			feature.setColor(163, 163, 163);
			
			// I2-1
			feature = region.addFeature("2", "I2-1");
			feature.setColor(163, 163, 163);
			
			// E3
			block = netView.addBlock("5", "e");
			region = block.addRegion("1");
			region.setColor(0, 51, 102);
			feature = region.addFeature("1", "E3");
			feature.setColor(163, 163, 163);
			
//			//Splice Events
//			toBlock = "7");
//			toRegion = "1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// E3-1
			feature = region.addFeature("2", "E3-1");
			feature.setColor(163, 163, 163);
			// E3-2
			feature = region.addFeature("3", "E3-2");
			feature.setColor(163, 163, 163);
		
			// I3
			block = netView.addBlock("6", "I");
			region = block.addRegion("1");
			region.setColor(255, 255, 255);
			feature = region.addFeature("1", "I3");
			feature.setColor(163, 163, 163);
			
			// I3-1
			feature = region.addFeature("2", "I3-1");
			feature.setColor(163, 163, 163);

			// E4
			block = netView.addBlock("7", "e");
			region = block.addRegion("1");
			region.setColor(0, 51, 102);
			feature = region.addFeature("1", "E4");
			feature.setColor(163, 163, 163);
			
//			// Splice Events
//			toBlock = "9");
//			toRegion = "1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I4
			block = netView.addBlock("8", "I");
			region = block.addRegion("1");
			region.setColor(0, 0, 255);
			feature = region.addFeature("1", "I4");
			feature.setColor(163, 163, 163);
			
			// E5
			block = netView.addBlock("9", "e");
			region = block.addRegion("1");
			region.setColor(0, 51, 102);
			feature = region.addFeature("1", "E5");
			feature.setColor(163, 163, 163);
			
//			// Splice Events
//			toBlock = "11");
//			toRegion = "1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I5
			block = netView.addBlock("10", "I");
			region = block.addRegion("1");
			region.setColor(255, 255, 255);
			feature = region.addFeature("1", "I5");
			feature.setColor(163, 163, 163);
			
			// I5-1
			feature = region.addFeature("2", "I5-1");
			feature.setColor(163, 163, 163);
			
			// I5-2
			feature = region.addFeature("3", "I5-2");
			feature.setColor(163, 163, 163);
			
			// E6
			block = netView.addBlock("11", "e");
			region = block.addRegion("1");
			region.setColor(0, 51, 102);
			feature = region.addFeature("1", "E6");
			feature.setColor(163, 163, 163);
			
//			// Splice Events
//			toBlock = "13");
//			toRegion = "1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// E6-1
			feature = region.addFeature("2", "E6-1");
			feature.setColor(163, 163, 163);
			
			// I6
			block = netView.addBlock("12", "I");
			region = block.addRegion("1");
			region.setColor(255, 255, 255);
			feature = region.addFeature("1", "I6");
			feature.setColor(163, 163, 163);
			
			// I6-1
			feature = region.addFeature("2", "I6-1");
			feature.setColor(163, 163, 163);
			
			// I6-2
			feature = region.addFeature("3", "I6-2");
			feature.setColor(163, 163, 163);
			
			// I6-3
			feature = region.addFeature("4", "I6-3");
			feature.setColor(163, 163, 163);
			
			// I6-4
			feature = region.addFeature("5", "I6-4");
			feature.setColor(163, 163, 163);
			
			// I6-5
			feature = region.addFeature("6", "I6-5");
			feature.setColor(163, 163, 163);
			
			// I6-6
			feature = region.addFeature("7", "I6-6");
			feature.setColor(163, 163, 163);
			
			// E7
			block = netView.addBlock("13", "e");
			region = block.addRegion("1");
			region.setColor(0, 51, 102);
			feature = region.addFeature("1", "E7");
			feature.setColor(163, 163, 163);
			
//			// Splice Events
//			toBlock = "15");
//			toRegion = "1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I7
			block = netView.addBlock("14", "I");
			region = block.addRegion("1");
			region.setColor(255, 255, 255);
			feature = region.addFeature("1", "I7");
			feature.setColor(163, 163, 163);
			
			// E8
			block = netView.addBlock("15", "e");
			region = block.addRegion("1");
			region.setColor(0, 51, 102);
			feature = region.addFeature("1", "E8");
			feature.setColor(163, 163, 163);
			
//			// Splice Events
//			toBlock = "17");
//			toRegion = "1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I8
			block = netView.addBlock("16", "I");
			region = block.addRegion("1");
			region.setColor(255, 255, 255);
			feature = region.addFeature("1", "I8");
			feature.setColor(163, 163, 163);
			
			// E9
			block = netView.addBlock("17", "e");
			region = block.addRegion("1");
			region.setColor(0, 51, 102);
			feature = region.addFeature("1", "E9");
			feature.setColor(0, 184, 0);
			
//			// Splice Events
//			toBlock = "19");
//			toRegion = "1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I9
			block = netView.addBlock("18", "I");
			region = block.addRegion("1");
			region.setColor(255, 255, 255);
			feature = region.addFeature("1", "I9");
			feature.setColor(163, 163, 163);
			
			// E10
			block = netView.addBlock("19", "e");
			region = block.addRegion("1");
			region.setColor(0, 51, 102);
			feature = region.addFeature("1", "E10");
			feature.setColor(163, 163, 163);
			
//			// Splice Events
//			toBlock = "21");
//			toRegion = "1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I10
			block = netView.addBlock("20", "I");
			region = block.addRegion("1");
			region.setColor(255, 255, 255);
			feature = region.addFeature("1", "I10");
			feature.setColor(163, 163, 163);
			
			// E11
			block = netView.addBlock("21", "e");
			region = block.addRegion("1");
			region.setColor(0, 51, 102);
			feature = region.addFeature("1", "E11");
			feature.setColor(163, 163, 163);
			
//			// Splice Events
//			toBlock = "23");
//			toRegion = "1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I11
			block = netView.addBlock("22", "I");
			region = block.addRegion("1");
			region.setColor(255, 255, 255);
			feature = region.addFeature("1", "I11");
			feature.setColor(203, 0, 0);
			
			// I11-1
			feature = region.addFeature("2", "I11-1");
			feature.setColor(240, 0, 0);
			
			// I11-2
			feature = region.addFeature("3", "I11-2");
			feature.setColor(220, 0, 0);
			
			// E12
			block = netView.addBlock("23", "e");
			region = block.addRegion("1");
			region.setColor(0, 51, 102);
			feature = region.addFeature("1", "E12");
			feature.setColor(163, 163, 163);
			
//			// Splice Events
//			toBlock = "25");
//			toRegion = "1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I12
			block = netView.addBlock("24", "I");
			region = block.addRegion("1");
			region.setColor(255, 255, 255);
			feature = region.addFeature("1", "I12");
			feature.setColor(163, 163, 163);
			
			// E13
			block = netView.addBlock("25", "e");
			region = block.addRegion("1");
			region.setColor(0, 51, 102);
			feature = region.addFeature("1", "E13");
			feature.setColor(163, 163, 163);
			
//			// Splice Events
//			toBlock = "27");
//			toRegion = "1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);

			// I13
			block = netView.addBlock("26", "I");
			region = block.addRegion("1");
			region.setColor(255, 255, 255);
			feature = region.addFeature("1", "I13");
			feature.setColor(163, 163, 163);
			
			// E14
			block = netView.addBlock("27", "e");
			region = block.addRegion("1");
			region.setColor(0, 51, 102);
			feature = region.addFeature("1", "E14");
			feature.setColor(163, 163, 163);
			
//			// Splice Events
//			toBlock = "29");
//			toRegion = "1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I14
			block = netView.addBlock("28", "I");
			region = block.addRegion("1");
			region.setColor(255, 255, 255);
			feature = region.addFeature("1", "I14");
			feature.setColor(163, 163, 163);
			
			// I14-1
			feature = region.addFeature("2", "I14-1");
			feature.setColor(163, 163, 163);
			
			// I14-2
			feature = region.addFeature("3", "I14-2");
			feature.setColor(163, 163, 163);
			
			// I14-3
			feature = region.addFeature("4", "I14-3");
			feature.setColor(163, 163, 163);
			
			// I14-4
			feature = region.addFeature("5", "I14-4");
			feature.setColor(163, 163, 163);
			
			// I14-5
			feature = region.addFeature("6", "I14-5");
			feature.setColor(163, 163, 163);
			
			// I14-6
			feature = region.addFeature("7", "I14-6");
			feature.setColor(163, 163, 163);
			
			// I14-7
			feature = region.addFeature("8", "I14-7");
			feature.setColor(163, 163, 163);
			
			// I14-8
			feature = region.addFeature("9", "I14-8");
			feature.setColor(163, 163, 163);
			
			// I14-9
			feature = region.addFeature("10", "I14-9");
			feature.setColor(163, 163, 163);
			
			// I14-10
			feature = region.addFeature("11", "I14-10");
			feature.setColor(163, 163, 163);
			
			// E15
			block = netView.addBlock("29", "e");
			region = block.addRegion("1");
			region.setColor(0, 51, 102);
			feature = region.addFeature("1", "E15");
			feature.setColor(163, 163, 163);
			
			// E15-1
			feature = region.addFeature("2", "E15-1");
			feature.setColor(163, 163, 163);
			
			//E15-2
			feature = region.addFeature("3", "E15-2");
			feature.setColor(163, 163, 163);
		
		}
		
		else {
		System.out.println("Didn't click on the right node!!");
		}
		
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
