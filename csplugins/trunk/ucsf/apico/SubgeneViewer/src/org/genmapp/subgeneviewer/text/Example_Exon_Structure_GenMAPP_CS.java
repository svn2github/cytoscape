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
		
		if (nodeId.equals("b")){
			
			System.out.println("Node id is "+nodeId);
			// E1
			_id = new String("1");
			block = netView.addBlock(_id, "e");
			region = block.addRegion(new String("1"));
			feature = region.addFeature(_id, "E1");
			// red color
			feature.setColor(240, 0, 50);
			//System.out.println("Color for "+feature+" is "+feature.getColor());
			
			// Start site
			startSite = new StartSite(region);
			region.containsStartSite(true);
			
//			// Splice Events
//			toBlock = new String("3");
//			toRegion = new String("1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I1
			_id = new String("2");
			block = netView.addBlock(_id, "I");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String("1"), "I1");
			feature.setColor(240, 0, 50);
			
			// E2
			_id = new String("3");
			block = netView.addBlock(_id, "e");
			region = block.addRegion(new String("1"));
			
			feature = region.addFeature(new String ("1"), "E2");
			
			//red color
			feature.setColor(240, 150, 130);
			
			// Splice Events
			toBlock = new String("5");
			toRegion = new String("1");
			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
			spliceEvent.setRegion(region);
			spliceEvent.setId(toBlock, toRegion);
			
			// Splice Events
			toBlock = new String("7");
			toRegion = new String("1");
			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
			spliceEvent.setRegion(region);
			spliceEvent.setId(toBlock, toRegion);
	
			// I2
			_id = new String("4");
			block = netView.addBlock(_id, "I");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String("1"), "I2");
			
			// green color
			feature.setColor(100, 80, 70);
			
			// E3
			_id = new String("5");
			block = netView.addBlock(_id, "e");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "E3");
			
			//green color
			feature.setColor(90, 110, 70);
		
//			//Splice Events
//			toBlock = new String("7");
//			toRegion = new String("1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
		
			// I3
			_id = new String("6");
			block = netView.addBlock(_id, "I");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "I3");
			
			//green color
			feature.setColor(90, 110, 70);

			// E4
			_id = new String("7");
			block = netView.addBlock(_id, "e");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "E4");
			
			//red color
			feature.setColor(240, 150, 130);
		
//			// Splice Events
//			toBlock = new String("9");
//			toRegion = new String("1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I4
			_id = new String("8");
			block = netView.addBlock(_id, "I");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "I4");
			
			//green color
			feature.setColor(90, 110, 70);
			
			// E5
			_id = new String("9");
			block = netView.addBlock(_id, "e");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "E5");
			
			//red color
			feature.setColor(180, 30, 60);
		
		}
		
		else if (nodeId.equals("a")){
			System.out.println("Node id is "+nodeId);
		
			// E1
			_id = new String("1");
			block = netView.addBlock(_id, "e");
			region = block.addRegion(new String("1"));
			feature = region.addFeature(_id, "E1");
			
			// Start site
			startSite = new StartSite(region);
			region.containsStartSite(true);
			
			// Splice Events
			toBlock = new String("5");
			toRegion = new String("1");
			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
			spliceEvent.setRegion(region);
			spliceEvent.setId(toBlock, toRegion);
			
			// I1
			_id = new String("2");
			block = netView.addBlock(_id, "I");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String("1"), "I1");
			
			// E2
			_id = new String("3");
			block = netView.addBlock(_id, "e");
			region = block.addRegion(new String("1"));
			
			// Start site
			startSite = new StartSite(region);
			region.containsStartSite(true);
			
//			// Splice Events
//			toBlock = new String("5");
//			toRegion = new String("1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
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
			_id = new String("4");
			block = netView.addBlock(_id, "I");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String("1"), "I2");
			
			// I2-1
			feature = region.addFeature(new String("2"), "I2-1");
			
			// E3
			_id = new String("5");
			block = netView.addBlock(_id, "e");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "E3");
			
//			//Splice Events
//			toBlock = new String("7");
//			toRegion = new String("1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// E3-1
			feature = region.addFeature(new String("2"), "E3-1");
			// E3-2
			feature = region.addFeature(new String("3"), "E3-2");
		
			// I3
			_id = new String("6");
			block = netView.addBlock(_id, "I");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "I3");
			
			// I3-1
			feature = region.addFeature(new String("2"), "I3-1");

			// E4
			_id = new String("7");
			block = netView.addBlock(_id, "e");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "E4");
			
//			// Splice Events
//			toBlock = new String("9");
//			toRegion = new String("1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I4
			_id = new String("8");
			block = netView.addBlock(_id, "I");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "I4");
			
			// E5
			_id = new String("9");
			block = netView.addBlock(_id, "e");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "E5");
			
//			// Splice Events
//			toBlock = new String("11");
//			toRegion = new String("1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I5
			_id = new String("10");
			block = netView.addBlock(_id, "I");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "I5");
			
			// I5-1
			feature = region.addFeature(new String("2"), "I5-1");
			
			// I5-2
			feature = region.addFeature(new String("2"), "I5-2");
			
			// E6
			_id = new String("11");
			block = netView.addBlock(_id, "e");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "E6");
			
//			// Splice Events
//			toBlock = new String("13");
//			toRegion = new String("1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// E6-1
			feature = region.addFeature(new String("2"), "E6-1");
			
			// I6
			_id = new String("12");
			block = netView.addBlock(_id, "I");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "I6");
			
			// I6-1
			feature = region.addFeature(new String("2"), "I6-1");
			
			// I6-2
			feature = region.addFeature(new String("2"), "I6-2");
			
			// I6-3
			feature = region.addFeature(new String("2"), "I6-3");
			
			// I6-4
			feature = region.addFeature(new String("2"), "I6-4");
			
			// I6-5
			feature = region.addFeature(new String("2"), "I6-5");
			
			// I6-6
			feature = region.addFeature(new String("2"), "I6-6");
			
			// E7
			_id = new String("13");
			block = netView.addBlock(_id, "e");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "E7");
			
//			// Splice Events
//			toBlock = new String("15");
//			toRegion = new String("1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I7
			_id = new String("14");
			block = netView.addBlock(_id, "I");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "I7");
			
			// E8
			_id = new String("15");
			block = netView.addBlock(_id, "e");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "E8");
			
//			// Splice Events
//			toBlock = new String("17");
//			toRegion = new String("1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I8
			_id = new String("16");
			block = netView.addBlock(_id, "I");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "I8");
			
			// E9
			_id = new String("17");
			block = netView.addBlock(_id, "e");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "E9");
			
//			// Splice Events
//			toBlock = new String("19");
//			toRegion = new String("1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I9
			_id = new String("18");
			block = netView.addBlock(_id, "I");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "I9");
			
			// E10
			_id = new String("19");
			block = netView.addBlock(_id, "e");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "E10");
			
//			// Splice Events
//			toBlock = new String("21");
//			toRegion = new String("1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I10
			_id = new String("20");
			block = netView.addBlock(_id, "I");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "I10");
			
			// E11
			_id = new String("21");
			block = netView.addBlock(_id, "e");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "E11");
			
//			// Splice Events
//			toBlock = new String("23");
//			toRegion = new String("1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I11
			_id = new String("22");
			block = netView.addBlock(_id, "I");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "I11");
			
			// I11-1
			feature = region.addFeature(new String("2"), "I11-1");
			
			// I11-2
			feature = region.addFeature(new String("2"), "I11-2");
			
			// E12
			_id = new String("23");
			block = netView.addBlock(_id, "e");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "E12");
			
//			// Splice Events
//			toBlock = new String("25");
//			toRegion = new String("1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I12
			_id = new String("24");
			block = netView.addBlock(_id, "I");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "I12");
			
			// E13
			_id = new String("25");
			block = netView.addBlock(_id, "e");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "E13");
			
//			// Splice Events
//			toBlock = new String("27");
//			toRegion = new String("1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);

			// I13
			_id = new String("26");
			block = netView.addBlock(_id, "I");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "I13");
			
			// E14
			_id = new String("27");
			block = netView.addBlock(_id, "e");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "E14");
			
//			// Splice Events
//			toBlock = new String("29");
//			toRegion = new String("1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
			
			// I14
			_id = new String("28");
			block = netView.addBlock(_id, "I");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "I14");
			
			// I14-1
			feature = region.addFeature(new String("2"), "I14-1");
			
			// I14-2
			feature = region.addFeature(new String("2"), "I14-2");
			
			// I14-3
			feature = region.addFeature(new String("2"), "I14-3");
			
			// I14-4
			feature = region.addFeature(new String("2"), "I14-4");
			
			// I14-5
			feature = region.addFeature(new String("2"), "I14-5");
			
			// I14-6
			feature = region.addFeature(new String("2"), "I14-6");
			
			// I14-7
			feature = region.addFeature(new String("2"), "I14-7");
			
			// I14-8
			feature = region.addFeature(new String("2"), "I14-8");
			
			// I14-9
			feature = region.addFeature(new String("2"), "I14-9");
			
			// I14-10
			feature = region.addFeature(new String("2"), "I14-10");
			
			// E15
			_id = new String("29");
			block = netView.addBlock(_id, "e");
			region = block.addRegion(new String ("1"));
			feature = region.addFeature(new String ("1"), "E15");
			
			// E15-1
			feature = region.addFeature(new String("2"), "E15-1");
			
			//E15-2
			feature = region.addFeature(new String("2"), "E15-2");
			
//			// Splice Events
//			toBlock = new String("31");
//			toRegion = new String("1");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
//			
//			toBlock = new String("31");
//			toRegion = new String("2");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
//			
//			// Splice Events
//			toBlock = new String("31");
//			toRegion = new String("3");
//			spliceEvent = region.addSpliceEvent(toBlock, toRegion);
//			spliceEvent.setRegion(region);
//			spliceEvent.setId(toBlock, toRegion);
//			
//			// I15
//			_id = new String("30");
//			block = netView.addBlock(_id, "I");
//			region = block.addRegion(new String ("1"));
//			feature = region.addFeature(new String ("1"), "I15");
//			
//			// E16
//			_id = new String("31");
//			block = netView.addBlock(_id, "e");
//			region = block.addRegion(new String ("1"));
//			feature = region.addFeature(new String ("1"), "E16");
//			
//			// E16-1
//			region = block.addRegion(new String ("2"));
//			feature = region.addFeature(new String("2"), "E16-1");
//			
//			// E16-2
//			region = block.addRegion(new String ("3"));
//			feature = region.addFeature(new String("3"), "E16-2");
		
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
