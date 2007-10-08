// $Id: MergeDialog.java,v 1.10 2007/04/25 15:28:07 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2007 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander, Benjamin Gross
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathway_commons.view;

// imports

import cytoscape.CyNetwork;
import ding.view.NodeContextMenuListener;
import org.mskcc.pathway_commons.util.NetworkUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class provides a gui that is displayed when a pathwaycommons.org
 * request is received.  The gui will allow a user to create a new network
 * or merge the current request into an existing network.  If more than one
 * network exists, the user will choose which network to merge into from a list.
 *
 * @author Benjamin Gross.
 */
public class MergeDialog extends JDialog {

    /**
     * dialog width.
     */
    private static final int DIALOG_WIDTH = 500;

    /**
     * dialog height.
     */
    private static final int DIALOG_HEIGHT = 350;

    /**
     * ref to the pathway commons request string (url)
     */
    private String pathwayCommonsRequest;

    /**
     * list of network titles -
     * the list box index is used as an index into this list to get the
     */
    private String[] bpNetworkTitles;

    /**
     * map of network title to biopax networks
     */
    private Map<String, CyNetwork> bpNetworkMap;

    /**
     * currently selected network -
     * set when user chooses network from JList
     */
    private CyNetwork currentlySelectedNetwork;

    /*
      * ref to network listener - for context menus
      */
    NodeContextMenuListener nodeContextMenuListener;

    /**
     * Merge network button - global so we can enable/disable as needed
     */
    JButton mergeButton;

    /**
     * Constructor.
     *
     * @param owner                   The frame in which the dialog is displayed.
     * @param title                   Title of the frame.
     * @param modal                   Is dialog modal or not.
     * @param pathwayCommonsRequest   String
     * @param bpNetworkSet            Set<CyNetwork>.
     * @param nodeContextMenuListener NodeContextMenuListener
     */
    public MergeDialog(Frame owner,
            String title,
            boolean modal,
            String pathwayCommonsRequest,
            Set<CyNetwork> bpNetworkSet,
            NodeContextMenuListener nodeContextMenuListener) {

        // set our super class
        super(owner, title, modal);

        // init members
        this.pathwayCommonsRequest = pathwayCommonsRequest;
        bpNetworkTitles = new String[bpNetworkSet.size()];
        bpNetworkMap = new HashMap<String, CyNetwork>();
        // here we link network titles to CyNetworks -
        // this is required because we only have a network
        // when a user selects it from the JList component
        int lc = 0;
        for (CyNetwork net : bpNetworkSet) {
            String netTitle = net.getTitle();
            bpNetworkTitles[lc++] = netTitle;
            bpNetworkMap.put(netTitle, net);
        }
        this.nodeContextMenuListener = nodeContextMenuListener;

        // setup the gui
        initUI();

        // if only one network has been loadad, select it by default
        if (bpNetworkTitles.length == 1) {
            currentlySelectedNetwork = bpNetworkMap.get(bpNetworkTitles[0]);
        }
    }

    /**
     * Sets up the UI.
     */
    private void initUI() {

        // the panel which will contain all widgets
        JPanel dialogPanel = new JPanel();

        // we will use box layout
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.PAGE_AXIS));

        // instructions label
        JPanel infoPanel = initInfoPanel();

        // network list
        JPanel networkListPanel = initNetworkListPanel();

        // panel for buttons
        JPanel buttonsPanel = initButtonsPanel();

        // add all components to the dialog panel
        dialogPanel.add(infoPanel);
        dialogPanel.add(networkListPanel);
        dialogPanel.add(buttonsPanel);

        // add panel to this dialog
        setContentPane(dialogPanel);

        // set dialog dimensions
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);

        // we are not resizable
        setResizable(false);
    }

    /**
     * Sets up the infopanel panel.
     *
     * @return JPanel.
     */
    private JPanel initInfoPanel() {

        // panel we will return
        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(new EmptyBorder(10, 2, 10, 5)); // top, left, bottom, right

        // create text area to display message
        JTextArea textArea = new JTextArea("A new network has just been downloaded from Pathway Commons." +
                "  You have the option to create a new network within Cytoscape" +
                " or merge the new network into an existing Cytoscape network.",
                3, 40);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground((Color) UIManager.get("Label.background"));
        textArea.setForeground((Color) UIManager.get("Label.foreground"));
        textArea.setFont(new Font(null, Font.PLAIN, 13));

        // add text area to panel
        infoPanel.add(textArea, BorderLayout.CENTER);

        // outta here
        return infoPanel;
    }

    /**
     * Set up the network set panel.
     */
    private JPanel initNetworkListPanel() {

        // create the tree panel
        JPanel networkListPanel = new JPanel();
        networkListPanel.setLayout(new BorderLayout());
        networkListPanel.setBorder(new TitledBorder("Available Networks"));

        // we start with a progress panel first
        JScrollPane networkListScrollPane = createNetworkListScrollPane();

        // add progress panel to the tree panel
        networkListPanel.add(networkListScrollPane, BorderLayout.CENTER);

        // outta here
        return networkListPanel;
    }

    /**
     * Method to load network set into a scroll pane.
     *
     * @return JScrollPane which contains tree of PathwayPreviewObjects.
     */
    private JScrollPane createNetworkListScrollPane() {

        // setup our list
        final JList dataList = new JList(bpNetworkTitles);
        dataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // if list contains only one item, select it by default
        if (bpNetworkTitles.length == 1) dataList.setSelectedIndex(0);
        dataList.setCellRenderer(new MyCellRenderer());
        dataList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    int selectedIndex = dataList.getSelectedIndex();
                    CyNetwork cyNetwork = bpNetworkMap.get(bpNetworkTitles[selectedIndex]);
                    if (cyNetwork != null) {
                        // a network has been selected, process
                        networkSelected(cyNetwork);
                    }
                }
            }
        });

        // setup the scroll pane
        JScrollPane scrollPane = new JScrollPane(dataList);
        scrollPane.setVerticalScrollBarPolicy
                (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy
                (JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // outta here
        return scrollPane;
    }

    /**
     * Called when list selection has changed - item selected.
     *
     * @param cyNetwork CyNetwork
     */
    private void networkSelected(CyNetwork cyNetwork) {
        mergeButton.setEnabled(true);
        currentlySelectedNetwork = cyNetwork;
    }

    /**
     * Sets up the buttons panel.
     *
     * @return JPanel.
     */
    private JPanel initButtonsPanel() {

        // panel we will return
        JPanel buttonsPanel = new JPanel();

        // cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });

        // create button
        JButton createButton = new JButton("Create New Network");
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new NetworkUtil(pathwayCommonsRequest, null,
                        false, nodeContextMenuListener).start();
                setVisible(false);
                dispose();
            }
        });

        // merge button - disabled until user selects network to merge
        mergeButton = new JButton("Merge with Network");
        mergeButton.setEnabled((bpNetworkTitles.length == 1));
        mergeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new NetworkUtil(pathwayCommonsRequest, currentlySelectedNetwork,
                        true, nodeContextMenuListener).start();
                setVisible(false);
                dispose();
            }
        });

        // add widgets to buttons panel
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(createButton);
        buttonsPanel.add(mergeButton);

        // outta here
        return buttonsPanel;
    }

    /**
     * Inner Class which extends JLabel to paint cells within our network list
     */
    class MyCellRenderer extends JLabel implements ListCellRenderer {

        /**
         * ref to selected icon
         */
        final ImageIcon selectedIcon;

        /**
         * ref to unselected icon
         */
        final ImageIcon unselectedIcon;

        /**
         * Constructor.
         */
        public MyCellRenderer() {
            URL url;

            // store selected icon
            url = MergeDialog.class.getResource
                    ("resources/run_tool.gif");
            selectedIcon = new ImageIcon(url);

            // store unselected icon
            url = MergeDialog.class.getResource
                    ("resources/types.gif");
            unselectedIcon = new ImageIcon(url);
        }

        /**
         * Our implementation of getListCellRendererComponent.
         * We just reconfigure the JLabel each time we're called.
         * (taken from javadocs).
         *
         * @param list         JList
         * @param value        Object, value to display
         * @param index        int, cell index
         * @param isSelected   boolean, is the list selected
         * @param cellHasFocus boolean, the list and the cell have the focus
         */
        public Component getListCellRendererComponent(JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            setText(value.toString());
            if (isSelected) {
                setIcon(selectedIcon);
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setIcon(unselectedIcon);
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
			setFont(list.getFont());
			setOpaque(true);
			return this;
		}
	}
}
