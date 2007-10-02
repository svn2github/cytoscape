package org.genmapp.subgeneviewer.splice.model;

import java.util.ArrayList;
import java.util.List;

import org.genmapp.subgeneviewer.model.SubgeneNetwork;

public class SpliceNetwork extends SubgeneNetwork {

	// todo: will have a list of blocks
	List<Block> listOfBlocks = new ArrayList<Block>();

	// todo: will have a list of splice events
	List<SpliceEvent> listOfSpliceEvents = new ArrayList<SpliceEvent>();

	// todo: list of start sites
	List<StartSite> listOfStartSites = new ArrayList<StartSite>();

}
