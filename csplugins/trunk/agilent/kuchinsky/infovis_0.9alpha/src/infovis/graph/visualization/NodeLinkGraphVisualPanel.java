/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization;

import infovis.Visualization;
import infovis.column.ColumnFilter;
import infovis.graph.visualization.layout.GraphLayoutFactory;
import infovis.panel.DefaultVisualPanel;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;


/**
 * Class NodeLinkGraphVisualPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.13 $
 */
public class NodeLinkGraphVisualPanel extends DefaultVisualPanel {
    protected JComboBox layoutAlgoCombo;
    protected JCheckBox paintLinksButton;
    protected JPanel layoutSpecificPanel;
    
    public NodeLinkGraphVisualPanel(Visualization visualization, ColumnFilter filter) {
        super(visualization, filter);
    }
    
    public NodeLinkGraphVisualization getNodeLinkGraphVisualization() {
        return (NodeLinkGraphVisualization)getVisualization()
            .findVisualization(NodeLinkGraphVisualization.class);
    }
    
    protected void createAll() {
        super.createAll();
        addOrientationButtons();
        addVisibilityButtons();
        addLayoutControls();
    }
    
    protected void addVisibilityButtons() {
        paintLinksButton = new JCheckBox("Paint Links");
        paintLinksButton.setSelected(
            getNodeLinkGraphVisualization().isPaintingLinks());
        paintLinksButton.addActionListener(this);
        add(paintLinksButton);
    }
    
    protected void addLayoutControls() {
        Vector v = new Vector();
        for (Iterator iter = GraphLayoutFactory.layoutNamesIterator();
            iter.hasNext(); ) {
            String name = (String)iter.next();
            v.add(name);
        }
        layoutAlgoCombo = new JComboBox(v);
        NodeLinkGraphLayout l = 
            (NodeLinkGraphLayout)getNodeLinkGraphVisualization().getLayout();
        if (l != null) {
            layoutAlgoCombo.setSelectedItem(l.getName());
        }
        layoutAlgoCombo.addActionListener(this);
        addJCombo("Layout", layoutAlgoCombo);
        layoutSpecificPanel = new JPanel();
        add(layoutSpecificPanel);
    }
    
    public void updateLayoutSpecificPanel() {
        
    }

    /*
    public void contentsChanged(ListDataEvent e) {
    	if (e.getSource() == layoutProgramCombo.getModel()) {
            getNodeLinkGraphVisualization().setLayoutProgram(
                (String)layoutProgramCombo.getSelectedItem());
        }
        else if (e.getSource() == layoutRatioCombo.getModel()) {
            getNodeLinkGraphVisualization().setLayoutRatio(
                (String)layoutRatioCombo.getSelectedItem());
        }
        else
            super.contentsChanged(e);
    }
    */
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == paintLinksButton) {
            getNodeLinkGraphVisualization().setPaintingLinks(
                paintLinksButton.isSelected());
            getVisualization().repaint();
        }
        else if (e.getSource() == layoutAlgoCombo) {
            getNodeLinkGraphVisualization()
                .setLayout(GraphLayoutFactory.createLayout(
                        (String)layoutAlgoCombo.getSelectedItem(),
                        getNodeLinkGraphVisualization()));
        }
        else
            super.actionPerformed(e);
    }
    
}
