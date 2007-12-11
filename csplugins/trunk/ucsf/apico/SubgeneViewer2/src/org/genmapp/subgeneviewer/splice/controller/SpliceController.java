package org.genmapp.subgeneviewer.splice.controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.genmapp.subgeneviewer.controller.SubgeneController;
import org.genmapp.subgeneviewer.splice.SpliceViewBuilder;
import org.genmapp.subgeneviewer.splice.SpliceViewPanel;

import cytoscape.Cytoscape;
import ding.view.DGraphView;

/**
 * The splice controller is one of many possible subgene viewer controllers. The
 * splice controller is responsible for listening and responding to mouse
 * events, checking for required data, prompting calculations and views.
 * 
 */
public class SpliceController extends MouseAdapter implements SubgeneController {

	private String _nodeId;

	private String _nodeLabel;

	/**
	 * When user double-clicks on a node, the node's ID and label are retrieved
	 * and passed to the networkViewBuilder.
	 */
	public void mousePressed(MouseEvent e) {

		if (e.getClickCount() >= 2
				&& (((DGraphView) Cytoscape.getCurrentNetworkView())
						.getPickedNodeView(e.getPoint()) != null)) {

			System.out.println("SGV: double click on node");

			_nodeLabel = ((DGraphView) Cytoscape.getCurrentNetworkView())
					.getPickedNodeView(e.getPoint()).getLabel().getText();

			_nodeId = ((DGraphView) Cytoscape.getCurrentNetworkView())
					.getPickedNodeView(e.getPoint()).getNode().getIdentifier();

			System.out.println("Checking for exon structure data");
			boolean dataReady = exonDataCheck();

			if (dataReady) {
				System.out.println("Building splice view");
				buildSpliceView();
			}
			else {
				System.out.println("Insufficient exon structure data for this gene");
			}
		}
	}

	/**
	 * Verifies integrity of data at server or loaded as node attributes
	 */
	public boolean exonDataCheck() {
		//TODO: do data check
		return true;
	}

	private JPanel defaultAppearencePanel = new JPanel();
	private Map<String, Image> defaultImageManager = new HashMap<String, Image>();
	private String visualStyle = "GenMAPP";
	final String focus = Cytoscape.getCurrentNetwork().getIdentifier();

	/**
	 * 
	 */
	public void buildSpliceView() {

		defaultAppearencePanel.setMinimumSize(new Dimension(500, 200));
		defaultAppearencePanel.setPreferredSize(new Dimension(800, 300));
		defaultAppearencePanel.setSize(defaultAppearencePanel.getPreferredSize());
		defaultAppearencePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		defaultAppearencePanel.setLayout(new BoxLayout(defaultAppearencePanel, BoxLayout.Y_AXIS));
		
		final SpliceViewPanel panel = 
			(SpliceViewPanel) SpliceViewBuilder.showDialog(Cytoscape.getDesktop());
		createDefaultImage(visualStyle, (DGraphView) panel.getView(),
		                   defaultAppearencePanel.getSize());
		setDefaultPanel(defaultImageManager.get(visualStyle));
		
		Cytoscape.getDesktop().setFocus(focus);
		Cytoscape.getDesktop().repaint();

		SpliceViewBuilder.getSpliceView(visualStyle);

	}
	
	/**
	 * Create image of a default dummy network and save in a Map object.
	 *
	 * @param vsName
	 * @param view
	 * @param size
	 */
	private void createDefaultImage(String vsName, DGraphView view, Dimension size) {

		defaultAppearencePanel.setLayout(new BorderLayout());

		final Image image = view.createImage((int)size.getWidth(), (int)size.getHeight(), 0.9);

		defaultImageManager.put(vsName, image);
	}
	private void setDefaultPanel(final Image defImage) {
		if (defImage == null) {
			return;
		}

		defaultAppearencePanel.removeAll();

//		final JButton defaultImageButton = new JButton();
//		defaultImageButton.setUI(new BlueishButtonUI());
//
//		defaultImageButton.setIcon(new ImageIcon(defImage));
//		//defaultImageButton.setBackground(bgColor);
//		defaultAppearencePanel.add(defaultImageButton, BorderLayout.CENTER);
//		defaultImageButton.addMouseListener(new DefaultMouseListener());
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
