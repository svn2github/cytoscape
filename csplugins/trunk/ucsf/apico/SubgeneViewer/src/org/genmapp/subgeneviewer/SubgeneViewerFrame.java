package org.genmapp.subgeneviewer;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import cytoscape.Cytoscape;
import ding.view.DGraphView;

public class SubgeneViewerFrame extends JFrame implements MouseMotionListener {

	private static List<JPanel> _cnvList = new ArrayList<JPanel>();

	private static JScrollPane _scrollPane;

	private static JPanel _panel;

	private static ImageIcon icon;

	public SubgeneViewerFrame() {

		_panel = new JPanel();
		_panel.setBorder(new TitledBorder("Subgene Viewer"));
		_panel.setOpaque(false);
		_panel.setAutoscrolls(true);
		_panel.setLayout(new BoxLayout(_panel, BoxLayout.Y_AXIS));
		_scrollPane = new JScrollPane(_panel);
		this.getContentPane().add(_scrollPane);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(475, 210);
		this.setLocationRelativeTo(((DGraphView) Cytoscape
				.getCurrentNetworkView()).getCanvas());
	}

	public void addViewToFrame(Integer view_type) {

		switch (view_type) {
		case 1: // add exon view
			if (_cnvList.size() >= 3) {
				System.out.println("SGV: Viewer MAX = 3 views");
				return;
			}

			System.out.println("SGV: exon view");

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
			exonPanel.setPreferredSize(new Dimension(455, 140));
			_panel.add(exonPanel);
			_panel.validate();
			_scrollPane.validate();
			_cnvList.add(exonPanel);

		case 2: // add snp view
		case 3: // add Chip view
		case 4: // add protein domain view
		default: // this.add(Cytoscape.getCurrentNetworkView().getComponent());
		}
	}

	public void mouseDragged(MouseEvent e) {
		// scroll when dragging
		Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
		_panel.scrollRectToVisible(r);
	}

	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
