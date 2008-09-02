package org.cytoscape.coreplugin.cpath2.view;

import org.cytoscape.coreplugin.cpath2.util.NetworkMergeUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

/**
 * Merge Panel
 *
 * @author Ethan Cerami.
 */
public class MergePanel extends JPanel {
    private JComboBox networkComboBox;

    public MergePanel() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        NetworkMergeUtil mergeUtil = new NetworkMergeUtil();
        if (mergeUtil.mergeNetworksExist()) {
            Vector networkVector = mergeUtil.getMergeNetworks();
            networkComboBox = new JComboBox(networkVector);
            JLabel label = new JLabel("Create / Merge:  ");
            this.add(label);
            this.add(networkComboBox);
            networkComboBox.setSelectedIndex(0);
        }
    }

    public JComboBox getNetworkComboBox() {
        return this.networkComboBox;
    }
}