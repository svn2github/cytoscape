package org.genmapp.subgeneviewer.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.genmapp.subgeneviewer.SubgeneViewerFrame;
import org.genmapp.subgeneviewer.SubgeneViewerPlugin;

import cytoscape.Cytoscape;
import ding.view.DGraphView;

public class SubgeneController extends MouseAdapter {

	public void mousePressed(MouseEvent e) {
		if (e.getClickCount() >= 2
				&& (((DGraphView) Cytoscape.getCurrentNetworkView())
						.getPickedNodeView(e.getPoint()) != null)) {
			System.out.println("SGV: double click on node");
			SubgeneViewerFrame frame = SubgeneViewerPlugin.get_frame();
			chooseViews(frame);
			frame.setVisible(true);
		}
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
