package org.genmapp.subgeneviewer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;

import org.genmapp.subgeneviewer.controller.SubgeneController;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CytoscapeDesktop;
import ding.view.DGraphView;

public class SubgeneViewerPlugin extends CytoscapePlugin implements
		PropertyChangeListener {


	public static final int EXON_VIEW = 1;
	public static final int SNP_VIEW = 2;
	public static final int CHIP_VIEW = 3;
	public static final int DOMAIN_VIEW = 4;

	
	private static SubgeneViewerFrame _frame = null;

	private static SubgeneController _controller;

	public SubgeneViewerPlugin() {

		_controller = new SubgeneController();

		// Listen for Network View Focus
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);

	}

	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {
			((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas()
					.addMouseListener(_controller);
			System.out.println("SGV: Network View Focused");
		}
	}

	public static SubgeneViewerFrame get_frame() {
		if (_frame == null) {
			try {
				_frame = new SubgeneViewerFrame();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return _frame;
	}

}
