/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel;

import infovis.Table;
import infovis.Visualization;
import infovis.column.ColumnFilter;
import infovis.column.filter.InternalFilter;
import infovis.table.FilteredTable;
import infovis.visualization.VisualizationLayers;
import infovis.visualization.magicLens.ExcentricLabelVisualization;
import infovis.visualization.ruler.RulerVisualization;

import javax.swing.*;

/**
 * Control Panel for showing selectio, dynamic queries and eveything else.
 * 
 * Should evolve with new components.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.39 $
 * 
 * @infovis.factory ControlPanelFactory
 *                  infovis.visualization.DefaultVisualization
 */
public class ControlPanel extends JTabbedPane {
    protected Visualization               visualization;
    //protected RulerVisualization          rulerVisualization;
    protected VisualizationLayers         layers;
    protected ExcentricLabelVisualization excentricVisualization;
    protected Table                       table;
    protected VisualizationPanel          visualizationPanel;
    protected FilteredTable               filteredTable;
    protected JTable                      jtable;
    protected JComponent                  detail;
    protected JTabbedPane                 tabs;
    protected DynamicQueryPanel           dynamicQueryPanel;
    protected JComponent                  visual;
    protected ExcentricLabelControlPanel  excentric;
    protected FisheyeControlPanel         fisheyes;

    /**
     * Constructor for ControlPanel.
     */
    public ControlPanel(Visualization visualization) {
        this(visualization, InternalFilter.sharedInstance());
    }

    /**
     * Constructor for ControlPanel.
     */
    public ControlPanel(Visualization visualization, ColumnFilter filter) {
        this.table = visualization.getTable();
        if (visualization instanceof VisualizationLayers) {
            layers = (VisualizationLayers) visualization;
        }
        else {
            layers = new VisualizationLayers(visualization);
        }

        excentricVisualization = ExcentricLabelVisualization.find(layers);
        if (excentricVisualization == null) {
            excentricVisualization = new ExcentricLabelVisualization(visualization, null);
            //visualization = excentricVisualization;
            layers.add(excentricVisualization, 
                    VisualizationLayers.MAGIC_LENS_LAYER);
        }
        this.visualization = layers;

        RulerVisualization rulerVisualization = RulerVisualization.find(layers);
        if (rulerVisualization == null 
                && visualization.getRulerTable() != null) {
            rulerVisualization = new RulerVisualization(visualization);
            layers.add(rulerVisualization, VisualizationLayers.RULER_LAYER);
        }

        filteredTable = new FilteredTable(this.table, filter);
        tabs = this;
        detail = createDetailControlPanel();
        if (detail != null) {
            tabs.addTab(
                    "Detail", 
                    null, 
                    detail, 
                    "Detailed values of the selected items");
        }
        JComponent details = createFiltersControlPanel();
        if (details != null) {
            tabs.addTab(
                    "Filters",
                    null,
                    details,
                    "Filtering according to attributes values");
        }
        JComponent visualPanel = createVisualPanel();
        visualPanel.add(Box.createVerticalGlue());
        tabs.addTab(
                "Visual",
                null,
                new JScrollPane(visualPanel),
                "Settings of visual attributes for this visualization");
        createOtherTabs();
    }

    public void dispose() {
        visualization.setParent(null);
        visualization.dispose();

        visualization = null;
    }

    /**
     * Creates other tables required by the Visualization.
     */
    protected void createOtherTabs() {
        tabs.add("Excentric", createExcentricLabelControPanel());
        tabs.add("Fisheyes", createFisheyesControlPanel());
        if (getVisualization().getRulerTable() != null) {
            tabs.add("Rulers", createRulersControlPanel());
        }
    }

    /**
     * @return
     */
    protected JComponent createRulersControlPanel() {
        return new RulersControlPanel(visualization);
    }

    /**
     * Creates a tab for changing the Excentric Labels paramters.
     */
    protected JComponent createExcentricLabelControPanel() {
        excentric = new ExcentricLabelControlPanel(visualization);
        return excentric;
    }

    /**
     * Creates a tab for changing the Fisheyes paramters.
     */
    protected JComponent createFisheyesControlPanel() {
        fisheyes = new FisheyeControlPanel(visualization);
        return fisheyes;
    }

    /**
     * Creates a tab for showing selected items on a table.
     */
    protected JComponent createDetailControlPanel() {
        return DetailTable.createDetailJTable(
                getFilteredTable(), 
                visualization.getSelection());
    }

    /**
     * Creates a tab for showing dynamic query filters.
     */
    protected JComponent createFiltersControlPanel() {
        dynamicQueryPanel = new DynamicQueryPanel(
                getVisualization(),
                getFilteredTable());
        JScrollPane queryScroll = new JScrollPane(dynamicQueryPanel);
        return queryScroll;
    }

    /**
     * Creates a tab for showing how visual variable are visualized and change
     * it.
     */
    protected JComponent createVisualPanel() {
        return new DefaultVisualPanel(visualization, getFilter());
    }

    /**
     * Returns the jtable.
     * 
     * @return JTable
     */
    public JTable getJtable() {
        return jtable;
    }

    /**
     * Returns the table.
     * 
     * @return Table
     */
    public Table getTable() {
        return table;
    }

    /**
     * Returns the dynamicQueryPanel.
     * 
     * @return DynamicQueryPanel
     */
    public DynamicQueryPanel getDynamicQueryPanel() {
        return dynamicQueryPanel;
    }

    /**
     * Returns the detail.
     * 
     * @return DetailTable
     */
    public JComponent getDetail() {
        return detail;
    }

    /**
     * Returns the stdVisual.
     * 
     * @return DefaultVisualPanel
     */
    public JComponent getStdVisual() {
        return visual;
    }

    /**
     * Returns the tabs.
     * 
     * @return JTabbedPane
     */
    public JTabbedPane getTabs() {
        return tabs;
    }

    /**
     * Returns the visualization.
     * 
     * @return BasicVisualization
     */
    public Visualization getVisualization() {
        //return visualization;
        return layers;
    }

    /**
     * Returns the filteredTable.
     * 
     * @return FilteredTable
     */
    public FilteredTable getFilteredTable() {
        return filteredTable;
    }

    /**
     * Returns the filter.
     * 
     * @return ColumnFilter
     */
    public ColumnFilter getFilter() {
        return filteredTable.getFilter();
    }

    /**
     * Sets the filter.
     * 
     * @param filter
     *            The filter to set
     */
    public void setFilter(ColumnFilter filter) {
        filteredTable.setFilter(filter);
    }

    public VisualizationPanel getVisualizationPanel() {
        return visualizationPanel;
    }

    public void setVisualizationPanel(VisualizationPanel visualizationPanel) {
        this.visualizationPanel = visualizationPanel;
    }
}
