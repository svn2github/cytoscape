// our package
package cytoscape.demo;

// imports
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cytoscape.cytopanel.CytoPanel;
import cytoscape.cytopanel.ICytoPanel;
import cytoscape.cytopanel.BiModalJSplitPane;
import cytoscape.cytopanel.CytoPanelConstants;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;

/**
 * A Sample Program used to demonstrate the CytoPanel API.
 *
 * @author Ethan Cerami, Ben Gross
 */
public class CytoPanelDemo {

	// cytoPanels
	private static CytoPanel cytoPanelWest;
	private static CytoPanel cytoPanelEast;
	private static CytoPanel cytoPanelSouth;

	// menu items used to test show/hide
	private static JCheckBoxMenuItem cytoPanelMenuItemWest;
	private static JCheckBoxMenuItem cytoPanelMenuItemEast;
	private static JCheckBoxMenuItem cytoPanelMenuItemSouth;

    /**
     * Main Method.
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {

        //  Create Master Frame
        JFrame frame = new JFrame("Cytoscape CytoPanel API");
		JMenuBar menuBar = createMenuBar();
		frame.setJMenuBar(menuBar);

        //  Create Embedded SpitPanes
        BiModalJSplitPane topLeftPane = createTopLeftPane(frame);
        BiModalJSplitPane topPane = createTopPane(frame, topLeftPane);
        BiModalJSplitPane masterPane = createMasterPane(frame, topPane);

        //  Add Master Pane to Frame
        Container contentPane = frame.getContentPane();
        contentPane.add(masterPane, BorderLayout.CENTER);

		// lets show this damn thing!
		frame.pack();
        frame.show();
    }

    /**
     * Creates the TopLeft Pane.
	 * @param frame Application Frame.
     * @return BiModalJSplitPane Object.
     */
    private static BiModalJSplitPane createTopLeftPane(JFrame frame) {
        String tab1Name = "Network View";
        String tab2Name = "Attribute Browser";

        //  Create CytoPanel with Tabs along the Left Side
        cytoPanelWest = new CytoPanel(SwingConstants.WEST,
									  JTabbedPane.TOP,
									  CytoPanelConstants.CYTOPANEL_STATE_SHOW);

        //  Add Two Sample Tabs
        cytoPanelWest.add(tab1Name, null,
						  DemoUtil.createSamplePanel(tab1Name),
						  DemoUtil.getSampleToolTip(tab1Name));
        cytoPanelWest.add(tab2Name, null,
						  DemoUtil.createSamplePanel(tab2Name),
						  DemoUtil.getSampleToolTip(tab2Name));

        //  Get a Dummy Screenshot of a Sample Cytoscape Network
        JPanel networkViewPanel = createNetworkViewPanel();

		//  Create the Split Pane;  split horizontally, and show the split
		BiModalJSplitPane splitPane = new BiModalJSplitPane(frame,
															JSplitPane.HORIZONTAL_SPLIT,
															BiModalJSplitPane.MODE_SHOW_SPLIT,
															cytoPanelWest,
															networkViewPanel);

		// set the CytoPanelContainer
		cytoPanelWest.setCytoPanelContainer(splitPane);

		// outta here
        return splitPane;
    }

    /**
     * Creates the Top Panel.
	 * @param frame Application Frame.
     * @param topLeftPane TopLeftPane Object.
     * @return BiModalJSplitPane Object
     */
    private static BiModalJSplitPane createTopPane(JFrame frame, BiModalJSplitPane topLeftPane) {
        String tab1Name = "PlugIn Example 1";
        String tab2Name = "PlugIn Example 2";
        String tab3Name = "PlugIn Example 3";

        //  Create CytoPanel with CytoPanelEast along the Right Side
        cytoPanelEast = new CytoPanel(SwingConstants.EAST,
									  JTabbedPane.TOP,
									  CytoPanelConstants.CYTOPANEL_STATE_HIDE);

        //  Add Three Sample CytoPanelEast
        cytoPanelEast.add(tab1Name, null,
				 DemoUtil.createSamplePanel(tab1Name),
				 DemoUtil.getSampleToolTip(tab1Name));
        cytoPanelEast.add(tab2Name, null,
				 DemoUtil.createSamplePanel(tab2Name),
				 DemoUtil.getSampleToolTip(tab2Name));
        cytoPanelEast.add(tab3Name, null,
				 DemoUtil.createSamplePanel(tab3Name),
				 DemoUtil.getSampleToolTip(tab3Name));

		BiModalJSplitPane splitPane = new BiModalJSplitPane(frame, 
															JSplitPane.HORIZONTAL_SPLIT,
															BiModalJSplitPane.MODE_HIDE_SPLIT,
															topLeftPane,
															cytoPanelEast);

		// set the CytoPanelContainer
		cytoPanelEast.setCytoPanelContainer(splitPane);

        //  Set the Resize Weight so that all extra space goes to
        //  the left component.
		splitPane.setResizeWeight(1.0);

		// outta here
        return splitPane;
    }

    /**
     * Creates the Master Split Pane.
	 * @param frame Application Frame.
     * @param topSplitPane BiModalJSplitPane Object.
     * @return BiModalJSplitPane Object.
     */
    private static BiModalJSplitPane createMasterPane (JFrame frame, BiModalJSplitPane topSplitPane) {
        String tab4Name = "PlugIn Example 4";
        String tab5Name = "PlugIn Example 5";
        String tab6Name = "PlugIn Example 6";

        //  Create CytoPanel with CytoPanelSouth along the Bottom
        cytoPanelSouth = new CytoPanel(SwingConstants.SOUTH,
									   JTabbedPane.BOTTOM,
									   CytoPanelConstants.CYTOPANEL_STATE_HIDE);

        //  Create Three Sample CytoPanelSouth
        cytoPanelSouth.add(tab4Name, null,
				 DemoUtil.createSamplePanel(tab4Name),
				 DemoUtil.getSampleToolTip(tab4Name));
        cytoPanelSouth.add(tab5Name, null,
				 DemoUtil.createSamplePanel(tab5Name),
				 DemoUtil.getSampleToolTip(tab5Name));
        cytoPanelSouth.add(tab6Name, null,
				 DemoUtil.createSamplePanel(tab6Name),
				 DemoUtil.getSampleToolTip(tab6Name));

		BiModalJSplitPane splitPane = new BiModalJSplitPane(frame,
															JSplitPane.VERTICAL_SPLIT,
															BiModalJSplitPane.MODE_HIDE_SPLIT,
															topSplitPane,
															cytoPanelSouth);

		// set the cytoPanelContainer
		cytoPanelSouth.setCytoPanelContainer(splitPane);

        //  Set Resize Weight so that top component gets all the extra space.
		splitPane.setResizeWeight(1.0);

		// outta here
        return splitPane;
    }

    /**
     * Create a Panel with a Dummy Cytoscape Network.
     * @return JPanel Object.
     */
    private static JPanel createNetworkViewPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        //  Get JPG Image
        URL url = CytoPanelDemo.class.getResource ("resources/network.jpg");
        ImageIcon networkIcon = new ImageIcon(url);
        JLabel pic = new JLabel(networkIcon);

        //  Place Image in a ScrollPane
        JScrollPane scrollPane = new JScrollPane(pic);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        //  Set Initial Dimensions
        centerPanel.setPreferredSize(new Dimension (400,400));
        centerPanel.setMinimumSize(new Dimension (200,200));
        return centerPanel;
    }

	private static JMenuBar createMenuBar() {
		final JMenuBar menuBar = new JMenuBar();

		// file menu item
		JMenu mFile = new JMenu("File");
		mFile.setMnemonic(KeyEvent.VK_F);

		// exit file sub menu item
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.setMnemonic(KeyEvent.VK_X);
		ActionListener exitMenuItemListener = new ActionListener() { 
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		};
		exitItem.addActionListener(exitMenuItemListener);
		mFile.add(exitItem);

		// add file menu to menu bar
		menuBar.add(mFile);

		// window menu item
		JMenu mWindow = new JMenu("Window");
		mWindow.setMnemonic('W');

		// our west action listener
		ActionListener cytoPanelWestWindowMenuItemListener = new ActionListener(){ 
			public void actionPerformed(ActionEvent e){
				if (cytoPanelMenuItemWest.isSelected()){
					cytoPanelWest.setState(CytoPanelConstants.CYTOPANEL_STATE_SHOW);
				}
				else{
					cytoPanelWest.setState(CytoPanelConstants.CYTOPANEL_STATE_HIDE);
				}
			}
		};
		// our east action listener
		ActionListener cytoPanelEastWindowMenuItemListener = new ActionListener(){ 
			public void actionPerformed(ActionEvent e){
				if (cytoPanelMenuItemEast.isSelected()){
					cytoPanelEast.setState(CytoPanelConstants.CYTOPANEL_STATE_SHOW);
				}
				else{
					cytoPanelEast.setState(CytoPanelConstants.CYTOPANEL_STATE_HIDE);
				}
			}
		};
		// our south action listener
		ActionListener cytoPanelSouthWindowMenuItemListener = new ActionListener(){ 
			public void actionPerformed(ActionEvent e){
				if (cytoPanelMenuItemSouth.isSelected()){
					cytoPanelSouth.setState(CytoPanelConstants.CYTOPANEL_STATE_SHOW);
				}
				else{
					cytoPanelSouth.setState(CytoPanelConstants.CYTOPANEL_STATE_HIDE);
				}
			}
		};

		// cytopanel west window sub menu item
		cytoPanelMenuItemWest = new JCheckBoxMenuItem(CytoPanelConstants.CYTOPANEL_TITLE_WEST);
		cytoPanelMenuItemWest.setMnemonic(KeyEvent.VK_1);
		cytoPanelMenuItemWest.setSelected(true);
		cytoPanelMenuItemWest.addActionListener(cytoPanelWestWindowMenuItemListener);
		mWindow.add(cytoPanelMenuItemWest);

		// cytopanel east window sub menu item
		cytoPanelMenuItemEast = new JCheckBoxMenuItem(CytoPanelConstants.CYTOPANEL_TITLE_EAST);
		cytoPanelMenuItemEast.setMnemonic(KeyEvent.VK_2);
		cytoPanelMenuItemEast.setSelected(false);
		cytoPanelMenuItemEast.addActionListener(cytoPanelEastWindowMenuItemListener);
		mWindow.add(cytoPanelMenuItemEast);

		// cytopanel south window sub menu item
		cytoPanelMenuItemSouth = new JCheckBoxMenuItem(CytoPanelConstants.CYTOPANEL_TITLE_SOUTH);
		cytoPanelMenuItemSouth.setMnemonic(KeyEvent.VK_3);
		cytoPanelMenuItemSouth.setSelected(false);
		cytoPanelMenuItemSouth.addActionListener(cytoPanelSouthWindowMenuItemListener);
		mWindow.add(cytoPanelMenuItemSouth);

		// add windown item to menu bar
		menuBar.add(mWindow);

		// outta here
		return menuBar;
	}
}
