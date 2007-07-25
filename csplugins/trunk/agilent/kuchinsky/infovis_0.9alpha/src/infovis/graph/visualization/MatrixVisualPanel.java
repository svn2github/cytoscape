/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization;

import infovis.Column;
import infovis.Visualization;
import infovis.column.ColumnFilter;
import infovis.panel.DefaultVisualPanel;
import infovis.panel.FilteredColumnListModel;
import infovis.utils.InverseComparator;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataEvent;

/**
 * Panel for Visual Controls on Treemaps.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.15 $
 */
public class MatrixVisualPanel extends DefaultVisualPanel {
    protected FilteredColumnListModel vertexLabelModel;
    protected JComboBox               vertexLabelCombo;
    protected FilteredColumnListModel sortRowModel;
    protected JComboBox               sortRowCombo;
    protected JCheckBox               inverseSortRow;
    protected FilteredColumnListModel sortColumnModel;
    protected JComboBox               sortColumnCombo;
    protected JCheckBox               inverseSortColumn;

    public MatrixVisualization getMatrix() {
        return (MatrixVisualization)getVisualization()
            .findVisualization(MatrixVisualization.class);
    }

    /**
     * Constructor for MatrixVisualPanel.
     * @param visualization
     */
    public MatrixVisualPanel(MatrixVisualization visualization, ColumnFilter filter) {
        super(visualization, filter);
    }
    
    /**
     * @see infovis.panel.DefaultVisualPanel#createAll(Visualization)
     */
    protected void createAll() {
        super.createAll();
//        addColor(getVisualization());
//        addLabel(getVisualization());
        addVertexLabel(getVisualization());
        addSortRow(getVisualization());
        addSortColumn(getVisualization());
    }
    
    /**
     * Adds a JComboBox to change the column used for labeling vertices.
     *
     * param visualization the Visualization.
     */
    protected void addVertexLabel(Visualization visualization) {        
        MatrixVisualization matrix = getMatrix();
        vertexLabelModel = new FilteredColumnListModel(matrix.getGraph().getVertexTable(), filter);
        vertexLabelCombo = createJCombo(vertexLabelModel, matrix.getVertexLabelColumn(),
                                  "Label Vertex by");
        
    }

    /**
     * Adds a JComboBox to change the column used for sorting rows.
     *
     * param visualization the Visualization.
     */
    protected void addSortRow(Visualization visualization) {       
        MatrixVisualization matrix = getMatrix();
        sortRowModel = new FilteredColumnListModel(matrix.getGraph().getVertexTable(), filter);
        inverseSortRow = new JCheckBox("Inverse Sort Row");
//        
//        RowComparator comp = matrix.getRowComparator();
//        Column c = (comp instanceof Column) ? (Column)comp : null;
//        if (c == null 
//                && comp != null
//                && comp instanceof InverseComparator) {
//            InverseComparator inv = (InverseComparator)comp;
//            if (inv.getComparator() instanceof Column) {
//                c = (Column)inv.getComparator();
//            }
//        }
//        if (c != null) {
//            if (comp != null) {
//                inverseSortRow.setSelected(true);
//            }
//        }
        sortRowCombo = createJCombo(sortRowModel, null, "Sort Row by");
        inverseSortRow.addChangeListener(this);
        add(inverseSortRow);
    }
    
    /**
     * Adds a JComboBox to change the column used for sorting columns.
     *
     * param visualization the Visualization.
     */
    protected void addSortColumn(Visualization visualization) {       
        MatrixVisualization matrix = getMatrix();
        sortColumnModel = new FilteredColumnListModel(matrix.getGraph().getVertexTable(), filter);
        inverseSortColumn= new JCheckBox("Inverse Sort Column");
        
//        RowComparator comp = matrix.getColumnComparator();
//        Column c = (comp instanceof Column) ? (Column)comp : null;
//        if (c == null
//                && comp != null
//                && comp instanceof InverseComparator) {
//            InverseComparator inv = (InverseComparator)comp;
//            if (inv.getComparator() instanceof Column) {
//                c = (Column)inv.getComparator();
//            }
//        }
//        if (c != null) {
//            if (comp != null) {
//                inverseSortColumn.setSelected(true);
//            }
//        }
        sortColumnCombo = createJCombo(sortColumnModel, null, "Sort Column by");
        inverseSortColumn.addChangeListener(this);
        add(inverseSortColumn);
    }
    
    protected void updateSortRow() {
        Column col = (Column)sortRowCombo.getSelectedItem();
        if (inverseSortRow.isSelected()) {
            getMatrix().setRowComparator(new InverseComparator(col));
        }
        else {
            getMatrix().setRowComparator(col);
        }
    }
    
    protected void updateSortColumn() {
        Column col = (Column)sortColumnCombo.getSelectedItem();
        if (inverseSortColumn.isSelected()) {
            getMatrix().setColumnComparator(new InverseComparator(col));
        }
        else {
            getMatrix().setColumnComparator(col);
        }
    }
    /**
     * @see infovis.panel.DefaultVisualPanel#contentsChanged(ListDataEvent)
     */
    public void contentsChanged(ListDataEvent e) {
        if (e.getSource() == null)
            return;
        if (e.getSource() == vertexLabelModel) {
            Column col = (Column)vertexLabelCombo.getSelectedItem();
            getMatrix().setVertexLabelColumn(col);
        }
        else if (e.getSource() == sortRowModel) {
            updateSortRow();
        }
        else if (e.getSource() == sortColumnModel) {
            updateSortColumn();
        }
        else
            super.contentsChanged(e);
    }

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == inverseSortColumn) {
            updateSortColumn();
        }
        else if (e.getSource() == inverseSortRow) {
            updateSortRow();
        }
        else 
        super.stateChanged(e);
    }
}
