package org.cytoscape.neildhruva.chartapp;

import java.util.List;

import javax.swing.JPanel;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.neildhruva.chartapp.ChartAppFactory.AxisMode;
import org.jfree.chart.ChartPanel;

public interface CytoChart {
	
	/**
	 * Gets the <code>JPanel</code> that contains the chart and other components.
	 * @return <code>JPanel</code> containing chart, checkboxes etc.
	 */
	public JPanel getJPanel();
	
	/**
	 * Gets the <code>ChartPanel</code> that contains the chart.
	 * @return <code>ChartPanel</code> containing the chart.
	 */
	public ChartPanel getChartPanel();
	
	/**
	 * Sets the <code>ChartPanel</code> width
	 * @param width
	 */
	public void setWidth(int width);
	
	/**
	 * Sets the <code>ChartPanel</code> height
	 * @param height Height of the 
	 */
	public void setHeight(int height);
	
	/**
	 * Change the rows of <code>CyTable</code> to be displayed in the chart.
	 * @param rows Canonical names associated with each row.
	 */
	public void setRows(List<String> rows);
	
	/**
	 * Change the columns of <code>CyTable</code> to be displayed in the chart.
	 * @param columns Names of columns to be plotted.
	 */
	public void setColumns(String[] columns);
	
	/**
	 * Change the <code>CyTable</code> to be plotted on the chart.
	 * @param cyTable The <code>CyTable</code> containing the rows and columns to be plotted on the chart.
	 */
	public void setCyTable(CyTable cyTable);
	
	/**
	 * Sets whether X-Axis contains rows or columns
	 * @param mode {@link AxisMode}
	 */
	public void setAxisMode(AxisMode mode);
	
	/**
     * Hides the column from the chart.
     * @param columnName Name of the column that has to be hidden.
     */
    public void removeColumn(String columnName);

    /**
     * Makes a column visible in the chart.
     * @param columnName Name of the column that has to be made visible.
     */
	public void addColumn(String columnName);

    /**
     * Hides the row from the chart.
     * @param rowName Canonical name of the row that has to be hidden.
     */
    public void removeRow(String rowName);

    /**
     * Makes a row visible in the chart.
     * @param rowName Canonical name of the row that has to be made visible.
     */
	public void addRow(String rowName);
	
		
}
