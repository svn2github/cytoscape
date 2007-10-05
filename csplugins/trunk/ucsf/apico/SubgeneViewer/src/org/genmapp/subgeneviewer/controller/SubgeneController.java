package org.genmapp.subgeneviewer.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.genmapp.subgeneviewer.SubgeneViewerFrame;
import org.genmapp.subgeneviewer.SubgeneViewerPlugin;
import org.genmapp.subgeneviewer.splice.view.SpliceNetworkView;
import org.genmapp.subgeneviewer.view.SubgeneNetworkView;

import cytoscape.Cytoscape;
import ding.view.DGraphView;

public class SubgeneController extends MouseAdapter {
	
	Integer viewType = SubgeneViewerPlugin.EXON_VIEW;
	SubgeneNetworkView view = new SubgeneNetworkView();
	String nodeId;

	public void mousePressed(MouseEvent e) {
		
		if (e.getClickCount() >= 2
				&& (((DGraphView) Cytoscape.getCurrentNetworkView())
						.getPickedNodeView(e.getPoint()) != null)) {
			// todo: get the id for the node that we are on
			nodeId = ((DGraphView) Cytoscape.getCurrentNetworkView())
			.getPickedNodeView(e.getPoint()).getLabel().getText();
			
			switch (viewType)
			{
				case SubgeneViewerPlugin.EXON_VIEW:
				{
					view = buildSpliceViewer(nodeId);
					break;
				}
			}

			System.out.println("SGV: double click on node");

			// temporary functions for demo
			SubgeneViewerFrame frame = SubgeneViewerPlugin.get_frame();
//			chooseViews(frame);
			frame.addView(view);
			frame.setVisible(true);
		}
	}
	
	public SubgeneNetworkView buildSpliceViewer(String nodeId)
	{
		SpliceNetworkView spliceView = new SpliceNetworkView();
		spliceView.parseSplice(nodeId);
		spliceView.renderSplice(nodeId);
		return spliceView;
		
	}

	private void chooseViews(SubgeneViewerFrame f) {
		//todo: Check database to see which views have supporting data 
		
		//todo: Store user preference for which available views to view
		
		//temp: choose EXON_VIEW for now
		f.addViewToFrame(SubgeneViewerPlugin.EXON_VIEW);
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

}

// todo: double click capture to open pop up subgene network; then send back to
// svplugin saying build a cnv for this x
