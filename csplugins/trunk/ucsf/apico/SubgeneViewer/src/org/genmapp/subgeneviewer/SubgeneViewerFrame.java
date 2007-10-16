package org.genmapp.subgeneviewer;

import java.awt.Component;
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

import org.genmapp.subgeneviewer.splice.view.GraphWalker;
import org.genmapp.subgeneviewer.splice.view.SpliceNetworkView;
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
		
		// AJK: 10/16/07: experiment with using view as JPanel, so as to not overpaint
		_panel = new JPanel();
		_panel.setBorder(new TitledBorder("Subgene Viewer"));
		_panel.setOpaque(false);
		_panel.setAutoscrolls(true);
		_panel.setLayout(new BoxLayout(_panel, BoxLayout.Y_AXIS));
		_scrollPane = new JScrollPane(_panel);

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

		_panel.add(view);
		_panel.validate();
		_panel.setBorder(new TitledBorder(view.getParentNode().getIdentifier()));
		_scrollPane.validate();
		_cnvList.add(view);
		view.setOpaque(true);
		view.setVisible(true);
		view.validate();
		view.repaint();	
	}
	
	// AJK: 10/16/07 hack to see if we can't keep subgeneview from being overpainted
	public void paint(Graphics g)
	{
		super.paint(g);
		for (int i = 0; i < _panel.getComponentCount(); i++)
		{
			Component comp = _panel.getComponent(i);
			// boo hoo, breaks encapsulation
			if (comp instanceof SpliceNetworkView)
			{
				SpliceNetworkView netView = (SpliceNetworkView) comp;
				System.out.println("Got splice view: " + netView);
				GraphWalker.renderView(netView);
			}
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
