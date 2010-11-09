/*
 File: CytoscapeDesktop.java

 Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.internal.view;

import org.cytoscape.CytoscapeShutdown;
import org.cytoscape.view.CySwingApplication;
import org.cytoscape.view.CytoPanel;
import org.cytoscape.view.CytoPanelName;
import org.cytoscape.view.CytoPanelState;
import org.cytoscape.view.CyAction;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import java.util.Dictionary;


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
	protected CytoscapeMenus cyMenus;

	/**
	 * The NetworkViewManager can support three types of interfaces.
	 * Tabbed/InternalFrame/ExternalFrame
	 */
	protected NetworkViewManager networkViewManager;


	//
	// CytoPanel Variables
	//
	private CytoPanelImp cytoPanelWest;
	private CytoPanelImp cytoPanelEast;
	private CytoPanelImp cytoPanelSouth;
	private CytoPanelImp cytoPanelSouthWest; 

	// Status Bar TODO: Move this to log-swing to avoid cyclic dependency.
	private JPanel main_panel;
	private final CytoscapeShutdown shutdown; 

	/**
	 * Creates a new CytoscapeDesktop object.
	 */
	public CytoscapeDesktop(CytoscapeMenus cyMenus, NetworkViewManager networkViewManager, NetworkPanel networkPanel, CytoscapeShutdown shut) {
		super("Cytoscape Desktop (New Session)");

		this.cyMenus = cyMenus;
		this.networkViewManager = networkViewManager;
		this.networkPanel = networkPanel;
		this.shutdown = shut;

		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(SMALL_ICON)));

		main_panel = new JPanel();
		main_panel.setLayout(new BorderLayout());


		// create the CytoscapeDesktop
		BiModalJSplitPane masterPane = setupCytoPanels(networkPanel, networkViewManager);

		// note - proper networkViewManager has been properly selected in
		// setupCytoPanels()
		main_panel.add(masterPane, BorderLayout.CENTER);
		main_panel.add(cyMenus.getJToolBar(), BorderLayout.NORTH);

		// Remove status bar.
		setJMenuBar(cyMenus.getJMenuBar());

		//don't automatically close window. Let shutdown.exit(returnVal)
		//handle this
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					shutdown.exit(0);
				}
			});

		// show the Desktop
		setContentPane(main_panel);
		pack();
		setSize(DEF_DESKTOP_SIZE);
		setVisible(true);
		toFront();
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
	private BiModalJSplitPane setupCytoPanels(NetworkPanel networkPanel,
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
	private BiModalJSplitPane createTopRightPane(NetworkViewManager networkViewManager) {
		// create cytopanel with tabs along the top
		cytoPanelEast = new CytoPanelImp(CytoPanelName.EAST, JTabbedPane.TOP, CytoPanelState.HIDE);

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
	private BiModalJSplitPane createRightPane(BiModalJSplitPane topRightPane) {
		// create cytopanel with tabs along the bottom
		cytoPanelSouth = new CytoPanelImp(CytoPanelName.SOUTH, JTabbedPane.BOTTOM,
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
		cytoPanelSouthWest = new CytoPanelImp(CytoPanelName.SOUTH_WEST,
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
	private BiModalJSplitPane createMasterPane(NetworkPanel networkPanel,
	                                             BiModalJSplitPane rightPane) {
		// create cytopanel with tabs along the top
		cytoPanelWest = new CytoPanelImp(CytoPanelName.WEST, JTabbedPane.TOP, CytoPanelState.DOCK);

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

	NetworkViewManager getNetworkViewManager() {
		return networkViewManager;
	}

	public void addAction(CyAction action, Dictionary props) {
		cyMenus.addAction(action);
	}

	public void addAction(CyAction action) {
		cyMenus.addAction(action);
	}

	public void removeAction(CyAction action, Dictionary props) {
		cyMenus.removeAction(action);
	}

	public void removeAction(CyAction action) {
		cyMenus.removeAction(action);
	}

	public JMenu getJMenu(String name) {
		return cyMenus.getJMenu(name);
	}

	public JMenuBar getJMenuBar() {
		return cyMenus.getJMenuBar();
	}

	public JToolBar getJToolBar() {
		return cyMenus.getJToolBar();
	}

	public JFrame getJFrame() {
		return this;
	}

	public CytoPanel getCytoPanel(final CytoPanelName compassDirection) {
		// return appropriate cytoPanel based on compass direction
		switch (compassDirection) {
		case SOUTH:
			return (CytoPanel)cytoPanelSouth;
		case EAST:
			return (CytoPanel) cytoPanelEast;
		case WEST:
			return (CytoPanel) cytoPanelWest;
		case SOUTH_WEST:
			return (CytoPanel) cytoPanelSouthWest;
		}

		// houston we have a problem
		throw new IllegalArgumentException("Illegal Argument:  " + compassDirection
		                                   + ".  Must be one of:  {SOUTH,EAST,WEST,SOUTH_WEST}.");
	}
}
