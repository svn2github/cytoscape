package org.genmapp.subgeneviewer.splice.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.genmapp.subgeneviewer.readers.ParseSplice;
import org.genmapp.subgeneviewer.view.SubgeneNetworkView;

import cytoscape.CyNode;

public class SpliceNetworkView extends SubgeneNetworkView {

	// todo: will have a list of blocks
	List<Block> listOfBlocks = new ArrayList<Block>();

	// todo: will have a list of splice events
	List<SpliceEvent> listOfSpliceEvents = new ArrayList<SpliceEvent>();

	// todo: list of start sites
	List<StartSite> listOfStartSites = new ArrayList<StartSite>();

	private CyNode parentNode = null;

	public Block addBlock(Integer id, String type) {
		Block block = new Block(this);
		block.setId(id);
		block.setType(type);
		listOfBlocks.add(block);
		block.setNetworkView(this);
		return block;
	}

	public void removeBlock(Block block) {
		listOfBlocks.remove(block);
	}

	public CyNode getParentNode() {
		return parentNode;
	}

	public void setParentNode(CyNode parentNode) {
		this.parentNode = parentNode;
	}

	public void parseSplice(String nodeId) {
		ParseSplice parser = new ParseSplice(this, nodeId);
	}

	private static ImageIcon icon;
	
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
