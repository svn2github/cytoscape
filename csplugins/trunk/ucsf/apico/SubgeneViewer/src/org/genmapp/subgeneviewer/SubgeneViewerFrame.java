package org.genmapp.subgeneviewer;

import java.awt.Dimension;
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

import org.genmapp.subgeneviewer.view.SubgeneNetworkView;

import cytoscape.Cytoscape;
import ding.view.DGraphView;

public class SubgeneViewerFrame extends JFrame implements MouseMotionListener {

	private static List<JPanel> _cnvList = new ArrayList<JPanel>();

	private static JScrollPane _scrollPane;

	private static JPanel _panel;

	private static ImageIcon icon;

	public SubgeneViewerFrame() {

		this.setTitle("Subgene Viewer");
		
		_panel = new JPanel();
		_panel.setBorder(new TitledBorder("Subgene Viewer"));
		_panel.setOpaque(false);
		_panel.setAutoscrolls(true);
		_panel.setLayout(new BoxLayout(_panel, BoxLayout.Y_AXIS));
		_scrollPane = new JScrollPane(_panel);
		_scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		_scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.getContentPane().add(_scrollPane);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(800, 300);
		this.setLocationRelativeTo(((DGraphView) Cytoscape
				.getCurrentNetworkView()).getCanvas());
		
	}
	

	
	public void addView (SubgeneNetworkView view)
	{
		// todo: Alex, write this
		if (_cnvList.size() >= 3) {
			System.out.println("SGV: Viewer MAX = 3 views");
			return;
		}
		System.out.println("SGV: adding view to frame");

		view.setBorder(new TitledBorder(view.getParentNode().getIdentifier()));
		view.setOpaque(false);
		view.setAutoscrolls(true);
		view.setVisible(true);
//		Dimension minSize = new Dimension(800,300*_cnvList.size());
//		view.setLayout(new BoxLayout(
		view.setPreferredSize(new Dimension(view.getBounds().width, view.getBounds().height));
		_panel.add(view);
//		_panel.setPreferredSize(minSize);
		_panel.validate();
//		_scrollPane.add(view);
		_scrollPane.validate();
		_cnvList.add(view);
//		view.repaint();	
//		this.add(view);
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
