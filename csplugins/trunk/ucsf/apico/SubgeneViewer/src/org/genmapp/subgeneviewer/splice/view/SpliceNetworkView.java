package org.genmapp.subgeneviewer.splice.view;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.genmapp.subgeneviewer.readers.FileParser;
import org.genmapp.subgeneviewer.view.SubgeneNetworkView;

public class SpliceNetworkView extends SubgeneNetworkView {

	// todo: will have a list of blocks
	List<Block> listOfBlocks = new ArrayList<Block>();
	public Iterator<Block> getBlockInterator()
	{
		return listOfBlocks.iterator();
	}
	

	// todo: will have a list of splice events
	List<SpliceEvent> listOfSpliceEvents = new ArrayList<SpliceEvent>();
	public Iterator<SpliceEvent> getSpliceEventInterator()
	{
		return listOfSpliceEvents.iterator();
	}

	// todo: list of start sites
	List<StartSite> listOfStartSites = new ArrayList<StartSite>();
	public Iterator<StartSite> getStartSiteInterator()
	{
		return listOfStartSites.iterator();
	}
	
	public Block addBlock(String id, String type) {
		Block block = getBlock(id);
		if (block != null)
		{
			return block;
			
		}
		block = new Block(this);
		block.setId(id);
		block.setType(type);
		listOfBlocks.add(block);
		block.setNetworkView(this);
//		this.add(block); // for repaint()
		return block;
	}

	public SpliceEvent addSpliceEvent(String id) {
		SpliceEvent spliceEvent = new SpliceEvent(this);
		spliceEvent.setId(id);
		listOfSpliceEvents.add(spliceEvent);
		spliceEvent.setNetworkView(this);
		return spliceEvent;
	}

	public StartSite addStartSite(String id) {
		StartSite startSite = new StartSite(this);
		startSite.setId(id);
		listOfStartSites.add(startSite);
		startSite.setNetworkView(this);
		return startSite;
	}

	public void removeBlock(Block block) {
		listOfBlocks.remove(block);
	}
	
	/**
	 * get block by ID 
	 * currently iterates through list of blocks until it finds a match
	 * this is inefficient but may not be an issue if lists are small
	 * @param id
	 */
	public Block getBlock (String id)
	{
		Block myBlock = null;
		Iterator<Block> it = this.getBlockInterator();
		while (it.hasNext())
		{
			myBlock = it.next();
			if (myBlock.getId().equals(id))
			{
				return myBlock;
			}
		}
		return null;
	}


	public void parseSplice(String nodeId) {
		FileParser parser = new FileParser(this, nodeId, splice);
	}
	
	String splice = "splice";
	
	public void paint (Graphics g)
	{
		super.paint(g);
//		System.out.println ("Painting children for view: " + this);
//		System.out.println ("For " + this.getComponentCount() + " children.");
//		this.paintChildren(g);
		GraphWalker.renderView(this);
	}
	
	public void renderSplice(String nodeId) {
		
		GraphWalker.calculateBounds(this);
	}

}
