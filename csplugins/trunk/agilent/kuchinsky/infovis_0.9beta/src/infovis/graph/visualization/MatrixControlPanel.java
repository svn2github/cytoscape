/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization;

import infovis.Graph;
import infovis.Visualization;
import infovis.column.filter.*;
import infovis.graph.InDegree;
import infovis.graph.OutDegree;
import infovis.panel.*;
import infovis.table.FilteredTable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Control panel for a Matrix BasicVisualization.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.26 $
 * 
 * @infovis.factory ControlPanelFactory infovis.graph.visualization.MatrixVisualization
*/
public class MatrixControlPanel extends ControlPanel {
    protected DynamicQueryPanel rowQueryPanel;
    protected DynamicQueryPanel columnQueryPanel;
    protected FilteredTable filteredGraph;
//    protected JCheckBox hideFiltered;
    protected JCheckBox squareEdges;

    /**
     * Constructor for MatrixControlPanel.
     *
     * @param visualization
     */
    public MatrixControlPanel(Visualization visualization) {
        super(visualization);
    }

    /**
     * Returns the MatrixVisualization.
     *
     * @return the MatrixVisualization.
     */
    public MatrixVisualization getMatrix() {
        return (MatrixVisualization) getVisualization()
            .findVisualization(MatrixVisualization.class);
    }

    /**
     * Returns the Graph
     *
     * @return Return the Graph
     */
    public Graph getGraph() {
        return getMatrix().getGraph();
    }

    /**
     * Return a filtered graph for the vertex table
     *
     * @return a filtered graph for the vertex table
     */
    public FilteredTable getFilteredGraph() {
        if (filteredGraph == null) {
            filteredGraph =
                new FilteredTable(
                    getGraph().getVertexTable(),
                    new ComposeExceptFilter(
                        new ExceptNamed(OutDegree.OUTDEGREE_COLUMN),
                        new ComposeExceptFilter(
                            new ExceptNamed(InDegree.INDEGREE_COLUMN),
                            InternalFilter.sharedInstance())));
        }
        return filteredGraph;
    }
    
    protected void createOtherTabs() {
        super.createOtherTabs();
        int visual = tabs.indexOfTab("Visual");
        tabs.insertTab(
                "Row Visual", 
                null, 
                createRowVisualPanel(), 
                "Setting of visual attributes for the visualization of rows",
                visual+1);
        tabs.insertTab(
                "Column Visual",
                null,
                createColumnVisualPanel(),
                "Setting of visual attributes for the visualization of columns",
                visual+2);
    }
    
    protected JComponent createRowVisualPanel() {
        return new DefaultVisualPanel(
                getMatrix().getRowVisualization(),
                getFilteredGraph().getFilter());
    }
    
    protected JComponent createColumnVisualPanel() {
        return new DefaultVisualPanel(
                getMatrix().getColumnVisualization(),
                getFilteredGraph().getFilter());
    }
    
    protected JComponent createDetailControlPanel() {
        MatrixVisualization matrix = getMatrix();
        
        Box stack = Box.createVerticalBox();
        JComponent detail = super.createDetailControlPanel();
        detail.setBorder(BorderFactory.createTitledBorder("Edge details"));
        stack.add(detail);

        JScrollPane rowJScroll = DetailTable.createDetailJTable(
                getFilteredGraph(),
                matrix.getRowSelection());
        rowJScroll.setBorder(BorderFactory.createTitledBorder("Row details"));
        stack.add(rowJScroll);

        JScrollPane columnJScroll = DetailTable.createDetailJTable(
                getFilteredGraph(),
                matrix.getColumnSelection());
        columnJScroll.setBorder(BorderFactory.createTitledBorder("Column details"));
        stack.add(columnJScroll);
        return stack;
    }

    /**
     * @see infovis.panel.ControlPanel#createFiltersPane()
     */
    protected JComponent createFiltersControlPanel() {
        Box stack = new Box(BoxLayout.Y_AXIS);
        JComponent filters = super.createFiltersControlPanel();
        filters.setBorder(
            BorderFactory.createTitledBorder("Edge dynamic queries"));
        stack.add(filters);

        Box checkBoxes = new Box(BoxLayout.X_AXIS);
        squareEdges =
            new JCheckBox("Square Edges", getMatrix().isSquared());
        squareEdges.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getMatrix().setSquared(squareEdges.isSelected());
            }
        });
        checkBoxes.add(squareEdges);
        stack.add(checkBoxes);

        rowQueryPanel =
            new DynamicQueryPanel(
                getMatrix().getRowVisualization(),
                getGraph().getVertexTable(),
                getMatrix().getRowFilter(),
                getFilteredGraph().getFilter());
        JScrollPane rowQueryScroll =
            new JScrollPane(
                rowQueryPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        rowQueryScroll.setBorder(
            BorderFactory.createTitledBorder("Row dynamic queries"));
        stack.add(rowQueryScroll);

        columnQueryPanel =
            new DynamicQueryPanel(
                getMatrix().getColumnVisualization(),
                getGraph().getVertexTable(),
                getMatrix().getColumnFilter(),
                getFilteredGraph().getFilter());
        JScrollPane columnQueryScroll =
            new JScrollPane(
                columnQueryPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        columnQueryScroll.setBorder(
            BorderFactory.createTitledBorder("Column dynamic queries"));
        stack.add(columnQueryScroll);

        return stack;
    }
    
//    protected JComponent createVisualPanel() {
//        MatrixVisualPanel visualPanel =
//            new MatrixVisualPanel(
//                getMatrix(),
//                getFilteredGraph().getFilter());
//        return visualPanel;
//    }

}
