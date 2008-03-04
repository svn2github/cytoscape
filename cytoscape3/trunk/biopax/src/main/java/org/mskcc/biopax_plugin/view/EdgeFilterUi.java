package org.mskcc.biopax_plugin.view;

import org.cytoscape.GraphPerspective;
import org.cytoscape.Edge;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.actions.GinyUtils;
import org.cytoscape.attributes.CyAttributes;
import cytoscape.data.Semantics;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.util.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import giny.view.EdgeView;

/**
 * Edge Filter Dialog.
 */
public class EdgeFilterUi extends JDialog {
    private GraphPerspective cyNetwork;
    private HashSet checkBoxSet;

    /**
     * Constructor.
     * @param cyNetwork GraphPerspective Object.
     */
    public EdgeFilterUi(GraphPerspective cyNetwork) {
        this.cyNetwork = cyNetwork;
        initGui();
    }

    /**
     * Initializes GUI.
     */
    private void initGui() {
        this.setModal(true);
        this.setTitle("Dynamic Edge Filter");
        checkBoxSet = new HashSet();
        Set interactionSet = new TreeSet();
        CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
        Iterator edgeIterator = cyNetwork.edgesIterator();
        while (edgeIterator.hasNext()) {
            Edge edge = (Edge) edgeIterator.next();
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

        Container contentPane = this.getContentPane();
        contentPane.setLayout (new BorderLayout());
        contentPane.add(edgeSetPanel, BorderLayout.CENTER);
        this.setLocationRelativeTo(Cytoscape.getDesktop());
        this.pack();
        this.setVisible(true);
    }

}

/**
 * Apply Edge Filter.
 */
class ApplyEdgeFilter implements ActionListener {
    private GraphPerspective cyNetwork;
    private HashSet checkBoxSet;

    /**
     * Constructor.
     * @param cyNetwork     GraphPerspective Object.
     * @param checkBoxSet   Set of JCheckBox Objects.
     */
    public ApplyEdgeFilter (GraphPerspective cyNetwork, HashSet checkBoxSet) {
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
        java.util.List edgeList = cyNetwork.edgesList();

        int allEdgeIds[] = new int[edgeList.size()];
        for (int i=0; i< edgeList.size(); i++) {
            Edge edge = (Edge) edgeList.get(i);
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
            Edge edge = (Edge) edgeSet.get(i);
            EdgeView edgeView = networkView.getEdgeView(edge);
            edgeViewList.add(edgeView);

        }
        networkView.hideGraphObjects(edgeViewList);
    }
}
