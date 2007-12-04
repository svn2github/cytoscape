package org.genmapp.subgeneviewer;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.genmapp.subgeneviewer.view.SubgeneNetworkView;

import cytoscape.Cytoscape;

public class SubgeneViewerFrame extends JDialog implements MouseMotionListener {

	/**
	 * Layout Region Dimensions
	 */
	Double x, y; // Top left corner of the rectangle.

	Double w, h; // Width and height of the rectangle.

	/**
	 * Buttons and Text Labels
	 */
	private JButton applyButton;

	private JButton helpButton;

	private JButton cancelButton;

	private static final String APPLY_BUTTON_TEXT = "Select";

	private static final String HELP_BUTTON_TEXT = "Help";

	private static final String CANCEL_BUTTON_TEXT = "Close";

//	/**
//	 * URL for BubbleRouter manual.
//	 */
//	private String helpURL = "http://www.genmapp.org/BubbleRouter/manual.htm";

	/**
	 * Constructor.
	 * 
	 * Returns region attribute name and values.
	 */
	public SubgeneViewerFrame(SubgeneNetworkView view, String title) {

	/**
	 * Initialize, based on current network
	 */
	Container container = getContentPane();
	container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
	this.setTitle("Subgene Viewer");

	/**
	 * If we are working on Linux, set always on top to true. // This is a
	 * hack to deal with numerous z-ordering bugs on Linux.
	 */
	String os = System.getProperty("os.name");
	if (os != null) {
		if (os.toLowerCase().startsWith("linux")) {
			this.setAlwaysOnTop(true);
		}
	}

	/**
	 * Create Master Panel
	 */
	JPanel masterPanel = new JPanel();
	masterPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
	masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.Y_AXIS));


	/**
	 * Add View Panel with Scroll
	 */
	view.setBorder(new TitledBorder(title));
	masterPanel.add(view);

	JScrollPane scrollPane = new JScrollPane(view);
	masterPanel.add(scrollPane);


	/**
	 * Add Button Panel
	 */
	masterPanel.add(Box.createVerticalGlue());
	JPanel buttonPanel = createButtonPanel();
	masterPanel.add(buttonPanel);
	container.add(masterPanel);

	/**
	 * Pack, set modality, and center on screen
	 */
	pack();
	setModal(false);
	setLocationRelativeTo(Cytoscape.getDesktop());
	setSize(800,300);
	setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	setVisible(true);
}

/**
 * Enable / Disable Apply Button.
 * 
 * @param enable
 *            Enable flag;
 */
void enableApplyButton(boolean enable) {
	if (applyButton != null) {
		applyButton.setEnabled(enable);
	}
}

/**
 * Creates Button Panel.
 * 
 * @return JPanel Object.
 */
private JPanel createButtonPanel() {
	JPanel buttonPanel = new JPanel();
	buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

	/**
	 * Help Button
	 */
	helpButton = new JButton(HELP_BUTTON_TEXT);
	helpButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			//cytoscape.util.OpenBrowser.openURL(helpURL);
		}
	});

	/**
	 * Cancel Button
	 */
	cancelButton = new JButton(CANCEL_BUTTON_TEXT);
	cancelButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SubgeneViewerFrame.this.setVisible(false);
			SubgeneViewerFrame.this.dispose();
		}
	});

	/**
	 * Apply Button
	 */
	applyButton = new JButton(APPLY_BUTTON_TEXT);
	applyButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			//do something
		}
	});

	buttonPanel.add(Box.createHorizontalGlue());
//	buttonPanel.add(helpButton);
//	buttonPanel.add(Box.createRigidArea(new Dimension(15, 0)));
	buttonPanel.add(cancelButton);
//	buttonPanel.add(applyButton);

	return buttonPanel;
}




//
///** Frame contents  
//  ------------------------------- Frame ----
//  | ---------------------- _scrollPane --- |
//  |	| --------------------- _panel --  * | |
//  |	| |  ---------------- view ---  |  * | |
//  |	| |  | Gene A				 |  |  s | |
//  |	| |  |   <exon structure>    |  |  c | |
//  |	| |  |   <data features>     |  |  r | |
//  |	| |  -------------------------  |  o | |
//  |	|								|  l | |
//  |	| |  ---------------- view ---  |  l | |
//  |	| |  | Gene B			  	 |  |  b | |
//  |	| |  |   <exon structure>    |  |  a | |
//  |	| |  |   <data features>     |  |  r | |
//  |	| |  -------------------------  |  * | |
//  |	| -------------------------------  * | |					
//  |	-------------------------------------- |
//  ------------------------------------------
//*/	
//	private static List<JPanel> _cnvList = new ArrayList<JPanel>();
//
//	private static JScrollPane _scrollPane;
//
//	private static JPanel _panel;
//
//	private static JPanel _buttonPanel;
//
//	private static ImageIcon icon;
//
//	JButton close = new JButton("X");
//
//	public SubgeneViewerFrame() {
//
//		this.setTitle("Subgene Viewer");
//		
//		_panel = new JPanel();
//		_panel.setBorder(new TitledBorder("Subgene Viewer"));
//		_panel.setOpaque(true);
//		_panel.setLayout(new BoxLayout(_panel, BoxLayout.Y_AXIS));
//		_scrollPane = new JScrollPane(_panel);
//		_scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//		_scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		this.getContentPane().add(_scrollPane);
//		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		this.setSize(800, 300);
//		this.setLocationRelativeTo(((DGraphView) Cytoscape
//				.getCurrentNetworkView()).getCanvas());
//		
//	}
//	

	
//	public void addView (SubgeneNetworkView view, String title)
//	{
//		if (_cnvList.size() >= 3) {
//			System.out.println("SGV: Viewer MAX = 3 views");
//			return;
//		}
//		System.out.println("SGV: adding view to frame");
//
//		//close button per view
////		view.add(close);
////		close.addActionListener(new ActionListener() {
////			public void actionPerformed(ActionEvent e) {
////				SubgeneNetworkView view2 = e.getSource();
////				_panel.remove(view);
////				view.setVisible(false);
////			}});
//		
//		
//		view.setBorder(new TitledBorder(title));
//		//view.setBackground(new Color(247,243,213));
//		view.setOpaque(true);
//		_panel.add(view);
//		view.repaint();
//		_scrollPane.repaint();
//		this.repaint();
//		_cnvList.add(view);
//	}
	
	public void mouseDragged(MouseEvent e) {
		// scroll when dragging
//		Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
//		_panel.scrollRectToVisible(r);
	}

	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
