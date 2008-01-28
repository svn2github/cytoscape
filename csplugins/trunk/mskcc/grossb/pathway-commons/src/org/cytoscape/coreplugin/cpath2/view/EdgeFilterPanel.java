package org.cytoscape.coreplugin.cpath2.view;

import cytoscape.CyNetwork;
import cytoscape.CyEdge;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.actions.GinyUtils;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import giny.view.EdgeView;

/**
 * Edge Filter Dialog.
 */
public class EdgeFilterPanel extends JPanel {
    private CyNetwork cyNetwork;
    private HashSet checkBoxSet;

    /**
     * Constructor.
     * @param cyNetwork CyNetwork Object.
     */
    public EdgeFilterPanel(CyNetwork cyNetwork) {
        this.cyNetwork = cyNetwork;
        initGui();
    }

    /**
     * Initializes GUI.
     */
    private void initGui() {
        checkBoxSet = new HashSet();
        Set interactionSet = new TreeSet();
        CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
        Iterator edgeIterator = cyNetwork.edgesIterator();
        while (edgeIterator.hasNext()) {
            CyEdge edge = (CyEdge) edgeIterator.next();
            String interactionType = edgeAttributes.getStringAttribute(edge.getIdentifier(),
                    Semantics.INTERACTION);
            interactionSet.add(interactionType);
        }
        Iterator interactionIterator = interactionSet.iterator();

        JPanel edgeSetPanel = new JPanel();
        edgeSetPanel.setBorder(new TitledBorder("Edge Filter"));
        edgeSetPanel.setLayout(new BoxLayout(edgeSetPanel, BoxLayout.Y_AXIS));
        while (interactionIterator.hasNext()) {
            String interactionType = (String) interactionIterator.next();
            JCheckBox checkBox = new JCheckBox (interactionType);
            checkBox.setActionCommand(interactionType);
            checkBox.addActionListener(new ApplyEdgeFilter(cyNetwork, checkBoxSet));
            checkBox.setSelected(true);
            edgeSetPanel.add(checkBox);
            checkBoxSet.add(checkBox);
        }
        this.setLayout (new BorderLayout());
        this.add(edgeSetPanel, BorderLayout.CENTER);
        this.setVisible(true);
    }

}

/**
 * Apply Edge Filter.
 */
class ApplyEdgeFilter implements ActionListener {
    private CyNetwork cyNetwork;
    private HashSet checkBoxSet;

    /**
     * Constructor.
     * @param cyNetwork     CyNetwork Object.
     * @param checkBoxSet   Set of JCheckBox Objects.
     */
    public ApplyEdgeFilter (CyNetwork cyNetwork, HashSet checkBoxSet) {
        this.cyNetwork = cyNetwork;
        this.checkBoxSet = checkBoxSet;
    }

    /**
     * Check Box selected or unselected.
     * @param actionEvent Action Event.
     */
    public void actionPerformed(ActionEvent actionEvent) {
        HashSet selectedInteractionSet = new HashSet();
        Iterator checkBoxIterator = checkBoxSet.iterator();
        while (checkBoxIterator.hasNext()) {
            JCheckBox checkBox = (JCheckBox) checkBoxIterator.next();
            String action = checkBox.getActionCommand();
            if (checkBox.isSelected()) {
                selectedInteractionSet.add(action);
            }
        }
        ArrayList edgeSet = new ArrayList();
        CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
        List edgeList = cyNetwork.edgesList();

        int allEdgeIds[] = new int[edgeList.size()];
        for (int i=0; i< edgeList.size(); i++) {
            CyEdge edge = (CyEdge) edgeList.get(i);
            String interactionType = edgeAttributes.getStringAttribute(edge.getIdentifier(),
                    Semantics.INTERACTION);
            if (!selectedInteractionSet.contains(interactionType)) {
                edgeSet.add(edge);
            }
            allEdgeIds[i] = edge.getRootGraphIndex();
        }

        CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
        GinyUtils.unHideAll(networkView);
        ArrayList edgeViewList = new ArrayList();
        for (int i=0; i<edgeSet.size(); i++) {
            CyEdge edge = (CyEdge) edgeSet.get(i);
            EdgeView edgeView = networkView.getEdgeView(edge);
            edgeViewList.add(edgeView);

        }
        networkView.hideGraphObjects(edgeViewList);
    }
}
