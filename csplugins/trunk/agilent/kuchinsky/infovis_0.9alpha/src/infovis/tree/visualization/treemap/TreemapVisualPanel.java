/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization.treemap;

import infovis.Visualization;
import infovis.column.ColumnFilter;
import infovis.tree.visualization.AdditiveTreeVisualPanel;
import infovis.tree.visualization.TreemapVisualization;

import java.awt.Dimension;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.event.ListDataEvent;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.10 $
 */
public class TreemapVisualPanel extends AdditiveTreeVisualPanel {
    JComboBox algorithmCombo;

    /**
     * Constructor for TreemapVisualPanel.
     * @param visualization
     * @param filter
     */
    public TreemapVisualPanel(Visualization visualization, ColumnFilter filter) {
        super(visualization, filter);
    }
    
    public TreemapVisualization getTreemapVisualization() {
        return (TreemapVisualization)getVisualization()
            .findVisualization(TreemapVisualization.class);
    }
    
    /**
     * @see infovis.panel.DefaultVisualPanel#createAll(Visualization)
     */
    protected void createAll() {
        super.createAll();
        addAlgorithmCombo();
    }
    

    protected void addAlgorithmCombo() {
        algorithmCombo = new JComboBox();
        algorithmCombo.setAlignmentX(LEFT_ALIGNMENT);
        algorithmCombo.setBorder(BorderFactory.createTitledBorder("Treemap Algorithm"));
        algorithmCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                                           (int)algorithmCombo.getPreferredSize()
                                                     .getHeight()));
        for (Iterator iter = TreemapFactory.getInstance().iterator();
            iter.hasNext(); ) {
            addAlgorithm((String)iter.next());
        }

        algorithmCombo.getModel().setSelectedItem(getTreemapVisualization().getTreemap().getName());                                                     
        algorithmCombo.getModel().addListDataListener(this);
        add(algorithmCombo);
    }
    
    protected void addAlgorithm(String name) {
        
        DefaultComboBoxModel model = (DefaultComboBoxModel)algorithmCombo.getModel();
        
        model.addElement(name);
    }

    /**
     * @see infovis.panel.DefaultVisualPanel#contentsChanged(ListDataEvent)
     */
    public void contentsChanged(ListDataEvent e) {
        if (e.getSource() == algorithmCombo.getModel()
            && e.getType() == ListDataEvent.CONTENTS_CHANGED) {
            Treemap tm = TreemapFactory.createTreemap(
                    (String)algorithmCombo.getSelectedItem(), 
                    getTreemapVisualization());
            if (tm == null) {
                tm = SliceAndDice.instance;
            }
            getTreemapVisualization().setLayout(tm);
        }
        else
            super.contentsChanged(e);
    }

}
