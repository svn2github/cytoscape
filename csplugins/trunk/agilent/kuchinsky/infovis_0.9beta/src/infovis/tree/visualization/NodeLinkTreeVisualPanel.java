/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization;

import infovis.Visualization;
import infovis.column.ColumnFilter;
import infovis.tree.visualization.nodelink.TreeLayoutFactory;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Visual Panel for NodeLinkTreeVisualization.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.10 $
 */
public class NodeLinkTreeVisualPanel extends TreeVisualPanel {
    protected JComboBox  layoutAlgoCombo;
    protected JComponent layoutSpecificPanel;

    protected JSlider    siblingSepSlider;
    protected JSlider    subtreeSepSlider;
    protected JSlider    levelSepSlider;

    /**
     * Constructor.
     * @param visualization the visualization.
     * @param filter the ColumnFilter.
     */
    public NodeLinkTreeVisualPanel(
            Visualization visualization,
            ColumnFilter filter) {
        super(visualization, filter);
    }

    /**
     * Returns the NodeLinkTreeVisualization.
     * @return the NodeLinkTreeVisualization.
     */
    public NodeLinkTreeVisualization getNodeLinkTreeVisualization() {
        return (NodeLinkTreeVisualization) getVisualization()
                .findVisualization(NodeLinkTreeVisualization.class);
    }

    protected void createAll() {
        super.createAll();
        addLayoutControls();
    }

    protected void addLayoutControls() {
        Vector v = new Vector();
        for (Iterator iter = TreeLayoutFactory.layoutNamesIterator(); iter
                .hasNext();) {
            String name = (String) iter.next();
            v.add(name);
        }
        layoutAlgoCombo = new JComboBox(v);
        NodeLinkTreeLayout l = (NodeLinkTreeLayout) getNodeLinkTreeVisualization()
                .getLayout();
        if (l != null) {
            layoutAlgoCombo.setSelectedItem(l.getName());
        }
        layoutAlgoCombo.addActionListener(this);
        addJCombo("Layout", layoutAlgoCombo);
        layoutSpecificPanel = Box.createVerticalBox();
        layoutSpecificPanel.setAlignmentX(LEFT_ALIGNMENT);
        add(layoutSpecificPanel);
        addParameterControls();
    }

    protected void addParameterControls() {
        final NodeLinkTreeLayout layout = (NodeLinkTreeLayout) getNodeLinkTreeVisualization()
                .getLayout();
        siblingSepSlider = new JSlider(0, 100, (int) layout
                .getSiblingSeparation());
        siblingSepSlider.setPaintLabels(true);
        siblingSepSlider.setPaintTicks(true);
        siblingSepSlider.setMajorTickSpacing(10);
        siblingSepSlider.setMinorTickSpacing(5);
        siblingSepSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                layout.setSiblingSeparation(siblingSepSlider.getValue());
                getNodeLinkTreeVisualization().invalidate();
            }
        });
        setTitleBorder(siblingSepSlider, "Sibling Separator");
        layoutSpecificPanel.add(siblingSepSlider);

        subtreeSepSlider = new JSlider(0, 100, (int) layout
                .getSubtreeSeparation());
        subtreeSepSlider.setPaintLabels(true);
        subtreeSepSlider.setPaintTicks(true);
        subtreeSepSlider.setMajorTickSpacing(50);
        subtreeSepSlider.setMinorTickSpacing(10);
        subtreeSepSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                layout.setSubtreeSeparation(subtreeSepSlider.getValue());
                getNodeLinkTreeVisualization().invalidate();
            }
        });
        setTitleBorder(subtreeSepSlider, "Subtree Separator");
        layoutSpecificPanel.add(subtreeSepSlider);

        levelSepSlider = new JSlider(0, 100, (int) layout.getLevelSeparation());
        levelSepSlider.setPaintLabels(true);
        levelSepSlider.setPaintTicks(true);
        levelSepSlider.setMajorTickSpacing(50);
        levelSepSlider.setMinorTickSpacing(10);
        levelSepSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                layout.setLevelSeparation(levelSepSlider.getValue());
                getNodeLinkTreeVisualization().invalidate();
            }
        });
        setTitleBorder(levelSepSlider, "Level Separator");
        layoutSpecificPanel.add(levelSepSlider);
    }

    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == layoutAlgoCombo) {
            layoutSpecificPanel.removeAll();
            getNodeLinkTreeVisualization()
                    .setLayout(
                            TreeLayoutFactory.createLayout(
                                    (String) layoutAlgoCombo.getSelectedItem(),
                                    getNodeLinkTreeVisualization()));
            addParameterControls();

        }
        else
            super.actionPerformed(e);
    }
}
