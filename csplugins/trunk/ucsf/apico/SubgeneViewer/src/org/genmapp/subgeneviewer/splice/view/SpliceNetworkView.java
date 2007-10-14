package org.genmapp.subgeneviewer.splice.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.genmapp.subgeneviewer.view.SubgeneNetworkView;

import cytoscape.CyNode;

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
	
	private CyNode parentNode = null;

	public Block addBlock(String id, String type) {
		Block block = new Block(this);
		block.setId(id);
		block.setType(type);
		listOfBlocks.add(block);
		block.setNetworkView(this);
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

	public CyNode getParentNode() {
		return parentNode;
	}

	public void setParentNode(CyNode parentNode) {
		this.parentNode = parentNode;
	}

	public void parseSplice(String nodeId) {
		FileParser parser = new FileParser(this, nodeId, splice);
	}

	private static ImageIcon icon;
	
	String splice = "splice";
	
	public void renderSplice(String nodeId) {
		// todo: write this sucker, Alex
		icon = new ImageIcon(
				"/Applications/Cytoscape_v2.5.1/plugins/subgene_mock.jpg");

		JPanel exonPanel = new JPanel() {
			protected void paintComponent(Graphics g) {
				g.drawImage(icon.getImage(), 5, 10, null);
				super.paintComponent(g);
			}
		};
		exonPanel.setBorder(new TitledBorder("Exon Structure Viewer"));
		exonPanel.setOpaque(false);

		// todo: make this a calculated value
		exonPanel.setPreferredSize(new Dimension(455, 140));
		
		this.add(exonPanel);

	}

}
