package cytoscape.demo;

import cytoscape.cytopanel.CytoPanel;
import cytoscape.cytopanel.BiModalJSplitPane;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * A Sample Program used to demonstrate the CytoPanel API.
 *
 * @author Ethan Cerami
 */
public class CytoPanelDemo {

    /**
     * Main Method.
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {

        //  Create Master Frame
        JFrame frame = new JFrame("Cytoscape CytoPanel API");

        //  Create Embedded SpitPanes
        BiModalJSplitPane topLeftPane = createTopLeftPane(frame);
        BiModalJSplitPane topPane = createTopPane(frame, topLeftPane);
        BiModalJSplitPane masterPane = createMasterPane(frame, topPane);

        //  Add Master Pane to Frame
        Container contentPane = frame.getContentPane();
        contentPane.add(masterPane, BorderLayout.CENTER);

        //  Pack Frame and Show it.
        frame.pack();
        frame.show();
    }

    /**
     * Creates the TopLeft Pane.
     * @return BiModalJSplitPane Object.
     */
    private static BiModalJSplitPane createTopLeftPane(JFrame frame) {
        String tab1Name = "Network View";
        String tab2Name = "Attribute Browser";

        //  Create CytoPanel with Tabs along the Left Side
        CytoPanel cytoPanel = new CytoPanel(JTabbedPane.LEFT);

        //  Add Two Sample Tabs
        cytoPanel.addTab(tab1Name,
						 DemoUtil.createSamplePanel (tab1Name),
						 DemoUtil.getSampleToolTip(tab1Name));
        cytoPanel.addTab(tab2Name,
						 DemoUtil.createSamplePanel (tab2Name),
						 DemoUtil.getSampleToolTip(tab2Name));

        //  Get a Dummy Screenshot of a Sample Cytoscape Network
        JPanel networkViewPanel = createNetworkViewPanel();

        //  Create the Split Pane;  split horizontally, and sho the split
        BiModalJSplitPane splitPane = new BiModalJSplitPane(frame,
															JSplitPane.HORIZONTAL_SPLIT,
															BiModalJSplitPane.MODE_SHOW_SPLIT,
															cytoPanel,
															networkViewPanel);
		// BiModalSplitPane is cytoPanelContainer
		cytoPanel.setCytoPanelContainer(splitPane);

        return splitPane;
    }

    /**
     * Creates the Top Panel.
     * @param topLeftPane TopLeftPane Object.
     * @return BiModalJSplitPane Object
     */
    private static BiModalJSplitPane createTopPane(JFrame frame, BiModalJSplitPane topLeftPane) {
        String tab1Name = "PlugIn Example 1";
        String tab2Name = "PlugIn Example 2";
        String tab3Name = "PlugIn Example 3";

        //  Create CytoPanel with Tabs along the Right Side
        CytoPanel tabs = new CytoPanel(JTabbedPane.RIGHT);

        //  Add Three Sample Tabs
        tabs.addTab(tab1Name,
					DemoUtil.createSamplePanel (tab1Name),
					DemoUtil.getSampleToolTip(tab1Name));
        tabs.addTab(tab2Name,
					DemoUtil.createSamplePanel (tab2Name),
					DemoUtil.getSampleToolTip(tab2Name));
        tabs.addTab(tab3Name,
					DemoUtil.createSamplePanel (tab3Name),
					DemoUtil.getSampleToolTip(tab3Name));

        //  Create the Split Pane; split horizontally, hide the split
        BiModalJSplitPane splitPane = new BiModalJSplitPane(frame,
															JSplitPane.HORIZONTAL_SPLIT,
															BiModalJSplitPane.MODE_HIDE_SPLIT,
															topLeftPane,
															tabs);
		// BiModalSplitPane is cytoPanelContainer
		tabs.setCytoPanelContainer(splitPane);

        //  Set the Resize Weight so that all extra space goes to
        //  the left component.
        splitPane.setResizeWeight(1);

        //  Close the Tab Drawer
        tabs.closeTabDrawer();
        return splitPane;
    }

    /**
     * Creates the Master Split Pane.
     * @param topSplitPane BiModalJSplitPane Object.
     * @return BiModalJSplitPane Object.
     */
    private static BiModalJSplitPane createMasterPane (JFrame frame, BiModalJSplitPane topSplitPane) {
        String tab4Name = "PlugIn Example 4";
        String tab5Name = "PlugIn Example 5";
        String tab6Name = "PlugIn Example 6";

        //  Create CytoPanel with Tabs along the Bottom
        CytoPanel tabs = new CytoPanel(JTabbedPane.BOTTOM);

        //  Create Three Sample Tabs
        tabs.addTab(tab4Name,
					DemoUtil.createSamplePanel (tab4Name),
					DemoUtil.getSampleToolTip(tab4Name));
        tabs.addTab(tab5Name,
					DemoUtil.createSamplePanel (tab5Name),
					DemoUtil.getSampleToolTip(tab5Name));
        tabs.addTab(tab6Name,
					DemoUtil.createSamplePanel (tab6Name),
					DemoUtil.getSampleToolTip(tab6Name));

        //  Create the Split Pane;  split vertically, hide the split
        BiModalJSplitPane splitPane = new BiModalJSplitPane(frame,
															JSplitPane.VERTICAL_SPLIT,
															BiModalJSplitPane.MODE_HIDE_SPLIT,
															topSplitPane,
															tabs);
		// BiModalSplitPane is cytoPanelContainer
		tabs.setCytoPanelContainer(splitPane);

        //  Set Resize Weight so that top component gets all the extra space.
        splitPane.setResizeWeight(1);

        //  Close the Tab Drawer.
        tabs.closeTabDrawer();

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
}
