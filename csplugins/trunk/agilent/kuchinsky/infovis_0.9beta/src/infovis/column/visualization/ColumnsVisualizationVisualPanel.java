/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column.visualization;

import infovis.Visualization;
import infovis.column.ColumnFilter;
import infovis.panel.DefaultVisualPanel;
import infovis.panel.FilteredColumnListModel;
import infovis.panel.render.AbstractVisualColumnPanel;
import infovis.visualization.VisualColumnDescriptor;
import infovis.visualization.render.AbstractVisualColumn;

import java.awt.Container;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class ColumnsVisualizationVisualPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class ColumnsVisualizationVisualPanel extends DefaultVisualPanel {
    protected FilteredColumnListModel sortColumnModel;
    protected JComboBox               sortColumnCombo;
    protected JCheckBox               inverseSortColumn;
    
    public ColumnsVisualizationVisualPanel(Visualization visualization, ColumnFilter filter) {
        super(visualization, filter);
    }
//    protected void createAll() {
//        super.createAll();
//        addSortColumn(getVisualization());    
//    }
    
    public ColumnsVisualization getColumnsVisualization() {
        return (ColumnsVisualization)getVisualization()
            .findVisualization(ColumnsVisualization.class);
    }


//    /**
//     * Adds a JComboBox to change the column used for sorting columns.
//     *
//     * param visualization the Visualization.
//     */
//    protected void addSortColumn(Visualization visualization) {       
//        ColumnsVisualization columns = getColumnsVisualization();
//        sortColumnModel = new FilteredColumnListModel(
//                columns.getVisualization(0).getTable(), 
//                filter);
//        inverseSortColumn= new JCheckBox("Inverse Sort Column");
//        sortColumnCombo = createJCombo(sortColumnModel, null, "Sort Column by");
//        inverseSortColumn.addChangeListener(this);
//        add(inverseSortColumn);
//    }
//    
//    protected void updateSortColumn() {
//        Column col = (Column)sortColumnCombo.getSelectedItem();
//        if (col == null)
//        if (inverseSortColumn.isSelected()) {
//            getColumnsVisualization().setRowComparator(new InverseComparator(col));
//        }
//        else {
//            getColumnsVisualization().setRowComparator(col);
//        }
//    }
//    
//    public void contentsChanged(ListDataEvent e) {
//        if (e.getSource() == null)
//            return;
//        if (e.getSource() == sortColumnModel) {
//            updateSortColumn();
//        }
//        else
//            super.contentsChanged(e);
//    }
    
    public void setVisual(String visual, boolean set) {
        Visualization vis = 
            (Visualization)
            getVisualization().findVisualization(Visualization.class);
        if (vis == null) return;
        int i = 0;
        for (Visualization sub = vis.getVisualization(i);
            sub != null; 
            sub = vis.getVisualization(++i)) {
            ColumnVisualization colVis = (ColumnVisualization)sub;
            VisualColumnDescriptor vc = 
                colVis.getVisualColumnDescriptor(visual);
            if (set) {
                if (vc.getFilter() == null 
                        || ! vc.getFilter().filter(colVis.getColumn())) {
                    colVis.setVisualColumn(visual, colVis.getColumn());
                }
            }
            else {
                colVis.setVisualColumn(visual, null);
            }
        }
    }
    
    public boolean canSelect(String visual) {
        Visualization vis = 
            (Visualization)
            getVisualization().findVisualization(Visualization.class);
        if (vis == null) return false;
        int i = 0;
        for (Visualization sub = vis.getVisualization(i);
            sub != null; 
            sub = vis.getVisualization(++i)) {
            ColumnVisualization colVis = (ColumnVisualization)sub;
            VisualColumnDescriptor vc = 
                colVis.getVisualColumnDescriptor(visual);
            if (vc.getFilter() == null
                    || !vc.getFilter().filter(colVis.getColumn())) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isSelected(String visual) {
        Visualization vis = 
            (Visualization)
            getVisualization().findVisualization(Visualization.class);
        if (vis == null) return false;
        int i = 0;
        for (Visualization sub = vis.getVisualization(i);
            sub != null; 
            sub = vis.getVisualization(++i)) {
            ColumnVisualization colVis = (ColumnVisualization)sub;
            if (colVis.getColumn() != colVis.getVisualColumn(visual)) {
                return false;
            }
        }
        return true;
    }
    
    protected JComponent getPanelFor(AbstractVisualColumn vc) {
        AbstractVisualColumnPanel panel = 
            (AbstractVisualColumnPanel)super.getPanelFor(vc);
        if (panel == null) return null;
        JComponent columnSelector = panel.getColumnSelector();
        if (columnSelector == null) {
            return null;
        }
        Container parent = columnSelector.getParent();
        parent.remove(columnSelector);
        final String visual = vc.getName();
        final JCheckBox checkBox = new JCheckBox("Apply");
        checkBox.setAlignmentX(LEFT_ALIGNMENT);
        
        if (! canSelect(visual)) {
            checkBox.setEnabled(false);
        }
        else if (isSelected(visual)) {
            checkBox.setSelected(true);
        }
        checkBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setVisual(visual, checkBox.isSelected());
            }
        });
        parent.add(checkBox, 0);
        return panel;
    }
}
