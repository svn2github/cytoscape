package org.genmapp.subgeneviewer.controller;

import giny.model.Node;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import org.genmapp.subgeneviewer.SubgeneViewerFrame;
import org.genmapp.subgeneviewer.SubgeneViewerPlugin;
import org.genmapp.subgeneviewer.splice.controller.SpliceController;
import org.genmapp.subgeneviewer.splice.view.SpliceNetworkView;
import org.genmapp.subgeneviewer.text.Example_Exon_Structure_GenMAPP_CS;
import org.genmapp.subgeneviewer.view.SubgeneNetworkView;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import ding.view.DGraphView;

public class SubgeneController extends MouseAdapter {

	public static final int EXON_VIEW = 1;

	public static final int SNP_VIEW = 2;

	public static final int CHIP_VIEW = 3;

	public static final int DOMAIN_VIEW = 4;

	// temp: default view type
	private SubgeneNetworkView _view = new SubgeneNetworkView();

	private List<Integer> _viewsInDatabase = new ArrayList<Integer>();

	private List<Integer> _viewsToView = new ArrayList<Integer>();

	private String _nodeId;

	private String _nodeLabel;

	public void mousePressed(MouseEvent e) {

		if (e.getClickCount() >= 2
				&& (((DGraphView) Cytoscape.getCurrentNetworkView())
						.getPickedNodeView(e.getPoint()) != null)) {

			System.out.println("SGV: double click on node");

			_nodeLabel = ((DGraphView) Cytoscape.getCurrentNetworkView())
					.getPickedNodeView(e.getPoint()).getLabel().getText();
			
			_nodeId = ((DGraphView) Cytoscape.getCurrentNetworkView()).getPickedNodeView(e.getPoint()).getNode().getIdentifier();
			
			getViewsInDatabase(_nodeLabel);
			getViewsToView();

			SubgeneViewerFrame frame = SubgeneViewerPlugin.get_frame();
			System.out.println("Built subgeneViewer frame " + frame);

			for (Integer viewType : _viewsToView) {
				if (_viewsInDatabase.contains(viewType)) {

					switch (viewType) {
					case EXON_VIEW: {
						System.out.println("Adding view");
						_view = SpliceController.buildSpliceViewer(_nodeLabel, _nodeId);
						System.out.println("added view: " + _view);
						break;
					}
					case SNP_VIEW: {
						// todo
					}
					case CHIP_VIEW: {
						// todo
					}
					case DOMAIN_VIEW: {
						// todo
					}
					}
					frame.addView(_view, _nodeLabel);
				}
			}
			frame.setVisible(true);
		}
		else if ((e.getClickCount() >= 2) && (e.isAltDown()) && (e.isControlDown()))
		{
			SpliceNetworkView spliceView = Example_Exon_Structure_GenMAPP_CS.
			testSpliceNetworkView(_nodeLabel);
			Example_Exon_Structure_GenMAPP_CS.dumpSpliceNetworkView
			(spliceView);
		}
	}

	/**
	 * Check database for available subgene views
	 * 
	 * @param nodeId
	 */
	public void getViewsInDatabase(String nodeId) {
		_viewsInDatabase.clear();
		// temp: check individual gene files for subgene info

		// temp: assume test file is for every node that is clicked
		_viewsInDatabase.add(EXON_VIEW);
	}

	/**
	 * Check user preferences for which views to view
	 */
	public void getViewsToView() {
		_viewsToView.clear();
		// temp: assume user only want to view EXON view
		_viewsToView.add(EXON_VIEW);
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
