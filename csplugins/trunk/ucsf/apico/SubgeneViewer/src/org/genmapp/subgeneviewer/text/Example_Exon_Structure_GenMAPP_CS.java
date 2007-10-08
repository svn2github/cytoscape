package org.genmapp.subgeneviewer.text;

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
		Integer _id;
		
		// E1
		_id = new Integer(1);
		block = netView.addBlock(_id, "e");
		region = block.addRegion(_id);
		feature = region.addFeature(_id, "E1");
		// E2
		_id = new Integer(2);
		region = block.addRegion(_id);
		feature = region.addFeature(new Integer (1), "E2");
		// E2-1
		feature = region.addFeature(new Integer(2), "E2-1");
		// E2-2
		feature = region.addFeature(new Integer(3), "E2-2");
		// E2-3
		feature = region.addFeature(new Integer(4), "E2-3");
		// E2-4
		feature = region.addFeature(new Integer(5), "E2-4");
		// E2-5
		feature = region.addFeature(new Integer(6), "E2-5");
		
		// I2
		_id = new Integer(3);
		block = netView.addBlock(_id, "i");
		region = block.addRegion(new Integer (1));
		feature = region.addFeature(new Integer(1), "I2");
		// I2-1
		feature = region.addFeature(new Integer(2), "I2-1");
		
		// E3
		_id = new Integer(4);
		block = netView.addBlock(_id, "e");
		region = block.addRegion(new Integer (1));
		feature = region.addFeature(new Integer (1), "E3");
		// E3-1
		feature = region.addFeature(new Integer(2), "E3-1");
		// E3-2
		feature = region.addFeature(new Integer(3), "E3-2");
	
		// I3
		_id = new Integer(5);
		block = netView.addBlock(_id, "i");
		region = block.addRegion(new Integer (1));
		feature = region.addFeature(new Integer (1), "I3");
		// I3-1
		feature = region.addFeature(new Integer(2), "I3-1");

		// E4
		_id = new Integer(6);
		block = netView.addBlock(_id, "e");
		region = block.addRegion(new Integer (1));
		feature = region.addFeature(new Integer (1), "E4");		
					
		return netView;
	}
}
