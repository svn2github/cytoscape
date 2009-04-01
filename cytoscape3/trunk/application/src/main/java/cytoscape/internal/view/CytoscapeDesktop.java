/*
 File: CytoscapeDesktop.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package cytoscape.internal.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeVersion;
import cytoscape.CyNetworkManager;

import cytoscape.view.CytoPanel;
import cytoscape.view.CytoPanelState;
import cytoscape.view.CySwingApplication;
import cytoscape.view.CyMenus;



/**
 * The CytoscapeDesktop is the central Window for working with Cytoscape
 */
public class CytoscapeDesktop extends JFrame implements CySwingApplication {

	private final static long serialVersionUID = 1202339866271348L;

	/*
	 * Default Desktop Size (slitly wider than 2.4 and before for new UI)
	 */
	private static final Dimension DEF_DESKTOP_SIZE = new Dimension(950, 700);

	private static final String SMALL_ICON = "/images/c16.png";


	/**
	 * The network panel that sends out events when a network is selected from
	 * the Tree that it contains.
	 */
	protected NetworkPanel networkPanel;

	/**
	 * The CyMenus object provides access to the all of the menus and toolbars
	 * that will be needed.
	 */
	protected CyMenus cyMenus;

	/**
	 * The NetworkViewManager can support three types of interfaces.
	 * Tabbed/InternalFrame/ExternalFrame
	 */
	protected NetworkViewManager networkViewManager;


	//
	// CytoPanel Variables
	//
	protected CytoPanelImp cytoPanelWest;
	protected CytoPanelImp cytoPanelEast;
	protected CytoPanelImp cytoPanelSouth;
	protected CytoPanelImp cytoPanelSouthWest; 

	// Status Bar
	protected JLabel statusBar;
	protected JPanel main_panel;

	private CytoscapeVersion version;

	/**
	 * Creates a new CytoscapeDesktop object.
	 */
	public CytoscapeDesktop(CyMenus cyMenus, NetworkViewManager networkViewManager, NetworkPanel networkPanel , CytoscapeVersion version) {
		super("Cytoscape Desktop (New Session)");

		this.cyMenus = cyMenus;
		this.networkViewManager = networkViewManager;
		this.networkPanel = networkPanel;
		this.version = version;

		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(SMALL_ICON)));

		main_panel = new JPanel();
		main_panel.setLayout(new BorderLayout());


		// create the CytoscapeDesktop
		BiModalJSplitPane masterPane = setupCytoPanels(networkPanel, networkViewManager);

		// note - proper networkViewManager has been properly selected in
		// setupCytoPanels()
		main_panel.add(masterPane, BorderLayout.CENTER);
		main_panel.add(cyMenus.getToolBar().getJToolBar(), BorderLayout.NORTH);

		// Remove status bar.
		initStatusBar(main_panel);
		setJMenuBar(cyMenus.getMenuBar().getJMenuBar());

		//don't automatically close window. Let Cytoscape.exit(returnVal)
		//handle this, based upon user confirmation.
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					Cytoscape.exit(0);
				}
			});

		// show the Desktop
		setContentPane(main_panel);
		pack();
		setSize(DEF_DESKTOP_SIZE);
		setVisible(true);
		toFront();
	}

	private void initStatusBar(JPanel panel) {
		statusBar = new JLabel();
		statusBar.setBorder(new EmptyBorder(0, 7, 5, 7));
		statusBar.setForeground(new Color(75, 75, 75));
		panel.add(statusBar, BorderLayout.SOUTH);
		setStatusBarMsg("Welcome to Cytoscape " + version.getFullVersion()
		                + "              Right-click + drag  to  ZOOM" 
						+ "             Middle-click + drag  to  PAN");
	}

	/**
	 * Sets the Status Bar Message.
	 *
	 * @param msg
	 *            Status Bar Message.
	 */
	public void setStatusBarMsg(String msg) {
		statusBar.setText(msg);
	}

	/**
	 * Clears the Status Bar Message.
	 */
	public void clearStatusBar() {
		// By using mutiple white spaces, layout for the statusBar is preserved.
		statusBar.setText("   ");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public NetworkPanel getNetworkPanel() {
		return networkPanel;
	}

	/**
	 * Gets the NetworkView Manager.
	 *
	 * @return NetworkViewManager Object.
	 */
	public NetworkViewManager getNetworkViewManager() {
		return this.networkViewManager;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public JFrame getJFrame() {
		return this;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public CyMenus getCyMenus() {
		return cyMenus;
	}


	// ---------------------------------------------------------------------------//
	// Cytopanels - Public and Protected methods

	/**
	 * Gets a cytoPanel given a Compass direction.
	 *
	 * @param compassDirection
	 *            Compass Direction (SwingConstants.{SOUTH,EAST,WEST}).
	 * @return CytoPanel The CytoPanel that lives in the region specified by
	 *         compass direction.
	 */
	public CytoPanel getCytoPanel(int compassDirection) {
		// return appropriate cytoPanel based on compass direction
		switch (compassDirection) {
			case SwingConstants.SOUTH:
				return (CytoPanel) cytoPanelSouth;

			case SwingConstants.EAST:
				return (CytoPanel) cytoPanelEast;

			case SwingConstants.WEST:
				return (CytoPanel) cytoPanelWest;

			case SwingConstants.SOUTH_WEST:
				return (CytoPanel) cytoPanelSouthWest;
		}

		// houston we have a problem
		throw new IllegalArgumentException("Illegal Argument:  " + compassDirection
		                                   + ".  Must be one of:  SwingConstants.{SOUTH,EAST,WEST,SOUTH_WEST}.");
	}

	/**
	 * Create the CytoPanels UI.
	 *
	 * @param networkPanel
	 *            to load on left side of right bimodal.
	 * @param networkViewManager
	 *            to load on left side (CytoPanel West).
	 * @return BiModalJSplitPane Object.
	 */
	protected BiModalJSplitPane setupCytoPanels(NetworkPanel networkPanel,
	                                            NetworkViewManager networkViewManager) {
		// bimodals that our Cytopanels Live within
		BiModalJSplitPane topRightPane = createTopRightPane(networkViewManager);
		BiModalJSplitPane rightPane = createRightPane(topRightPane);
		BiModalJSplitPane masterPane = createMasterPane(networkPanel, rightPane);

		return masterPane;
	}

	/**
	 * Creates the TopRight Pane.
	 *
	 * @param networkViewManager
	 *            to load on left side of top right bimodal.
	 * @return BiModalJSplitPane Object.
	 */
	protected BiModalJSplitPane createTopRightPane(NetworkViewManager networkViewManager) {
		// create cytopanel with tabs along the top
		cytoPanelEast = new CytoPanelImp(SwingConstants.EAST, JTabbedPane.TOP, CytoPanelState.HIDE);

		// determine proper network view manager component
		Component networkViewComp = null;

		networkViewComp = (Component) networkViewManager.getDesktopPane();

		// create the split pane - we show this on startup
		BiModalJSplitPane splitPane = new BiModalJSplitPane(this, JSplitPane.HORIZONTAL_SPLIT,
		                                                    BiModalJSplitPane.MODE_HIDE_SPLIT,
		                                                    networkViewComp, cytoPanelEast);

		// set the cytopanelcontainer
		cytoPanelEast.setCytoPanelContainer(splitPane);

		// set the resize weight - left component gets extra space
		splitPane.setResizeWeight(1.0);

		// outta here
		return splitPane;
	}

	/**
	 * Creates the Right Panel.
	 *
	 * @param topRightPane
	 *            TopRightPane Object.
	 * @return BiModalJSplitPane Object
	 */
	protected BiModalJSplitPane createRightPane(BiModalJSplitPane topRightPane) {
		// create cytopanel with tabs along the bottom
		cytoPanelSouth = new CytoPanelImp(SwingConstants.SOUTH, JTabbedPane.BOTTOM,
		                                  CytoPanelState.HIDE);

		// create the split pane - hidden by default
		BiModalJSplitPane splitPane = new BiModalJSplitPane(this, JSplitPane.VERTICAL_SPLIT,
		                                                    BiModalJSplitPane.MODE_HIDE_SPLIT,
		                                                    topRightPane, cytoPanelSouth);

		// set the cytopanel container
		cytoPanelSouth.setCytoPanelContainer(splitPane);

		// set resize weight - top component gets all the extra space.
		splitPane.setResizeWeight(1.0);

		// create cytopanel with tabs along the top for manual layout
		cytoPanelSouthWest = new CytoPanelImp(SwingConstants.SOUTH_WEST,
	                                                             JTabbedPane.TOP,
	                                                             CytoPanelState.HIDE);

		// outta here
		return splitPane;
	}

	/**
	 * Creates the Master Split Pane.
	 *
	 * @param networkPanel
	 *            to load on left side of CytoPanel (cytoPanelWest).
	 * @param rightPane
	 *            BiModalJSplitPane Object.
	 * @return BiModalJSplitPane Object.
	 */
	protected BiModalJSplitPane createMasterPane(NetworkPanel networkPanel,
	                                             BiModalJSplitPane rightPane) {
		// create cytopanel with tabs along the top
		cytoPanelWest = new CytoPanelImp(SwingConstants.WEST, JTabbedPane.TOP, CytoPanelState.DOCK);

		// add the network panel to our tab
		String tab1Name = new String("Network");
		cytoPanelWest.add(tab1Name, new ImageIcon(getClass().getResource("/images/class_hi.gif")),
		                  networkPanel, "Cytoscape Network List");

		// create the split pane - hidden by default
		BiModalJSplitPane splitPane = new BiModalJSplitPane(this, JSplitPane.HORIZONTAL_SPLIT,
		                                                    BiModalJSplitPane.MODE_SHOW_SPLIT,
		                                                    cytoPanelWest, rightPane);

		// set the cytopanel container
		cytoPanelWest.setCytoPanelContainer(splitPane);

		// outta here
		return splitPane;
	}

}
