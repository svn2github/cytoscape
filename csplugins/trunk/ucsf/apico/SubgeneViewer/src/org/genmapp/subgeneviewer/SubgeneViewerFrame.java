package org.genmapp.subgeneviewer;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.genmapp.subgeneviewer.view.SubgeneNetworkView;

import cytoscape.Cytoscape;
import ding.view.DGraphView;

public class SubgeneViewerFrame extends JFrame implements MouseMotionListener {

/** Frame contents  
  ------------------------------- Frame ----
  | ---------------------- _scrollPane --- |
  |	| --------------------- _panel --  * | |
  |	| |  ---------------- view ---  |  * | |
  |	| |  | Gene A				 |  |  s | |
  |	| |  |   <exon structure>    |  |  c | |
  |	| |  |   <data features>     |  |  r | |
  |	| |  -------------------------  |  o | |
  |	|								|  l | |
  |	| |  ---------------- view ---  |  l | |
  |	| |  | Gene B			  	 |  |  b | |
  |	| |  |   <exon structure>    |  |  a | |
  |	| |  |   <data features>     |  |  r | |
  |	| |  -------------------------  |  * | |
  |	| -------------------------------  * | |					
  |	-------------------------------------- |
  ------------------------------------------
*/	
	private static List<JPanel> _cnvList = new ArrayList<JPanel>();

	private static JScrollPane _scrollPane;

	private static JPanel _panel;

	private static JPanel _buttonPanel;

	private static ImageIcon icon;

	JButton close = new JButton("X");

	public SubgeneViewerFrame() {

		this.setTitle("Subgene Viewer");
		
		_panel = new JPanel();
		_panel.setBorder(new TitledBorder("Subgene Viewer"));
		_panel.setOpaque(true);
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
		if (_cnvList.size() >= 3) {
			System.out.println("SGV: Viewer MAX = 3 views");
			return;
		}
		System.out.println("SGV: adding view to frame");

		//close button per view
//		view.add(close);
//		close.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				SubgeneNetworkView view2 = e.getSource();
//				_panel.remove(view);
//				view.setVisible(false);
//			}});
		
		
		view.setBorder(new TitledBorder(view.getParentNode().getIdentifier()));
		//view.setBackground(new Color(247,243,213));
		view.setOpaque(true);
		_panel.add(view);
		view.repaint();
		_scrollPane.repaint();
		this.repaint();
		_cnvList.add(view);
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
