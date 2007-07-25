/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization;

import infovis.*;
import infovis.column.ColumnFilter;
import infovis.panel.DefaultVisualPanel;
import infovis.panel.FilteredColumnListModel;
import infovis.panel.render.SortPseudoColumnPanel;
import infovis.utils.Permutation;
import infovis.utils.RowIterator;
import infovis.visualization.render.SortPseudoVisualColumn;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.event.ListDataEvent;

/**
 * Panel for Visual Controls on Treemaps.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.16 $
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
        SortPseudoVisualColumn vc = new SortPseudoVisualColumn("Sort Row by");
        vc.setVisualization(visualization);
        add(new SortPseudoColumnPanel(vc) {
            public Permutation getPermutation() {
                Permutation perm = getMatrix().getRowPermutation();
                if (perm == null) {
                    return new Permutation(iterator());
                }
                else {
                    return perm;
                }
            }

            public void setPermutation(Permutation perm) {
                getMatrix().setRowPermutation(perm);
            }

            public RowIterator iterator() {
                return getMatrix().getVertexTable().iterator();
            }

            public Table getTable() {
                return getMatrix().getVertexTable();
            }
        });
    }
    
    /**
     * Adds a JComboBox to change the column used for sorting columns.
     *
     * param visualization the Visualization.
     */
    protected void addSortColumn(Visualization visualization) {
        SortPseudoVisualColumn vc = new SortPseudoVisualColumn("Sort Column by");
        vc.setVisualization(visualization);
        add(new SortPseudoColumnPanel(vc) {
           public Permutation getPermutation() {
               Permutation perm = getMatrix().getColumnPermutation();
               if (perm == null) {
                   return new Permutation(iterator());
               }
               else {
                   return perm;
               }
           }
           public void setPermutation(Permutation perm) {
               getMatrix().setColumnPermutation(perm);
           }
           public RowIterator iterator() {
            return getMatrix().getVertexTable().iterator();
          }

           public Table getTable() {
               return getMatrix().getVertexTable();
           }
        });
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
        else
            super.contentsChanged(e);
    }
}
