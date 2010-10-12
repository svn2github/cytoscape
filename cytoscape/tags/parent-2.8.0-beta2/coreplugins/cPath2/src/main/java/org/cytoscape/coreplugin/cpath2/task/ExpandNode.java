package org.cytoscape.coreplugin.cpath2.task;

import ding.view.NodeContextMenuListener;
import giny.view.NodeView;

import javax.swing.*;
import java.net.URL;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.cytoscape.coreplugin.cpath2.view.SearchHitsPanel;
import org.cytoscape.coreplugin.cpath2.view.InteractionBundlePanel;
import org.cytoscape.coreplugin.cpath2.view.model.InteractionBundleModel;
import org.cytoscape.coreplugin.cpath2.web_service.CPathProperties;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

/**
 * Expand a Node Feature.
 */
public class ExpandNode implements NodeContextMenuListener, ActionListener {
    private NodeView nodeView;

    public ExpandNode (NodeView nodeView) {
        this.nodeView = nodeView;
    }

    public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu jPopupMenu) {
        this.nodeView = nodeView;
        CPathProperties cpathProperties = CPathProperties.getInstance();
        JMenuItem menuItem = new JMenuItem ("Get Neighbors from:  "
                + cpathProperties.getCPathServerName());
        menuItem.addActionListener(this);
        jPopupMenu.add(menuItem);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        GetParentInteractions task = new GetParentInteractions((CyNode) nodeView.getNode());

        // Configure JTask Dialog Pop-Up Box
        JTaskConfig jTaskConfig = new JTaskConfig();
        jTaskConfig.setOwner(Cytoscape.getDesktop());
        jTaskConfig.displayCloseButton(true);
        jTaskConfig.displayStatus(true);
        jTaskConfig.setAutoDispose(true);

        // Execute Task in New Thread; pops open JTask Dialog Box.
        TaskManager.executeTask(task, jTaskConfig);
    }
}
