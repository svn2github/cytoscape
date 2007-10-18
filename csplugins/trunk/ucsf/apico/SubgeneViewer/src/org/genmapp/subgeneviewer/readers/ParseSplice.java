package org.genmapp.subgeneviewer.readers;

import java.util.regex.Pattern;

import org.genmapp.subgeneviewer.splice.view.Block;
import org.genmapp.subgeneviewer.splice.view.Feature;
import org.genmapp.subgeneviewer.splice.view.Region;
import org.genmapp.subgeneviewer.splice.view.SpliceEvent;
import org.genmapp.subgeneviewer.splice.view.SpliceNetworkView;
import org.genmapp.subgeneviewer.splice.view.StartSite;

public class ParseSplice implements LineParser{
	
	SpliceNetworkView view;
	String id;
	
	Block block;
	Region region;
	Feature feature;
	SpliceEvent spliceEvent;
	StartSite startSite;
	
	public ParseSplice (SpliceNetworkView v, String nodeId, FileParser parser)
	{
		view =v;
		id = nodeId;
		
		parser.addLineParser(this);
		parser.doit();
	}
	
	public void processLineTerms (String[] temp){
		
		Pattern pattern;
		
		block = view.addBlock(temp[3], temp[2]);

		region = block.addRegion(temp[4]);
		
		feature = region.addFeature(temp[5], temp[1]);
		
		
		if ((temp.length > 7) && !(temp[6].trim().equals("")) && !(temp[7].trim().equals(""))){
			
			pattern = Pattern.compile("\\|");
			
			String[] spliceBlocks = pattern.split((temp[6].trim()));

			String[] spliceRegions = pattern.split((temp[7].trim()));
			
			if (spliceBlocks.length >1){
				for (int i = 0; i<spliceRegions.length; i++){
					spliceEvent = region.addSpliceEvent(spliceBlocks[i], spliceRegions[i]);
					spliceEvent.setId(spliceBlocks[i], spliceRegions[i]);
					spliceEvent.setRegion(region);
					}
				}
			
			else{
				spliceEvent = region.addSpliceEvent(temp[6], temp[7]);
				spliceEvent.setId(temp[6], temp[7]);
				spliceEvent.setRegion(region);
					}
				}

		if ((temp.length > 8) && !(temp[8].trim().equals(""))){
			
			startSite = new StartSite(region);
			region.containsStartSite(true);
		}
	
}
}




