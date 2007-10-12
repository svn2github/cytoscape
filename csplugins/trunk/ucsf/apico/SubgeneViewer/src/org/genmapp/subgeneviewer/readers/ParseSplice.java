package org.genmapp.subgeneviewer.readers;

import org.genmapp.subgeneviewer.splice.view.Block;
import org.genmapp.subgeneviewer.splice.view.Feature;
import org.genmapp.subgeneviewer.splice.view.Region;
import org.genmapp.subgeneviewer.splice.view.SpliceEvent;
import org.genmapp.subgeneviewer.splice.view.SpliceNetworkView;

public class ParseSplice implements LineParser{
	
	SpliceNetworkView view;
	String id;
	
	Block block;
	Region region;
	Feature feature;
	SpliceEvent spliceEvent;
	
	public ParseSplice (SpliceNetworkView v, String nodeId, FileParser parser)
	{
		view =v;
		id = nodeId;
		
		parser.addLineParser(this);
		parser.doit();
	}
	
	public void processLineTerms (String[] temp){
		
		block = view.addBlock(temp[3], temp[2]);

		region = block.addRegion(temp[4]);
		
		feature = region.addFeature(temp[5], temp[1]);
		
		/*if (!(temp[6].equals(""))){
			
			spliceEvent.setId(temp[6], temp[7]);
			spliceEvent.setRegion(region);
			region.addSpliceEvent(temp[6], temp[7]);
		}
		else{}*/
		 
	}
	
}




