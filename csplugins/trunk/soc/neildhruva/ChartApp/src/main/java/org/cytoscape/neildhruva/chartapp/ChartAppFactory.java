package org.cytoscape.neildhruva.chartapp;

import javax.swing.JPanel;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

public interface ChartAppFactory {
	
	/**
	 * Whether X-Axis is assigned Rows or Columns.
	 */
	public static enum AxisMode {
		/**
		 * Rows on X-Axis
		 */
		ROWS,
		/**
		 * Columns on X-Axis
		 */
		COLUMNS
	}
	
	/**
	 * Creates a {@link JPanel} consisting of a JFreeChart, checkboxes with column names etc.
	 * @param currentNetwork The current {@link CyNetwork}.
	 * @param cyTable The {@link CyTable} that contains values to be plotted on the {@link JFreeChart}
	 * @return The {@link JPanel} consisting of the chart, checkboxes with column names etc.
	 */
	CytoChart createChart(CyNetwork currentNetwork, CyTable cyTable);
	
	/**
	 * Creates a {@link JPanel} consisting of a JFreeChart, checkboxes with column names etc.
	 * @param currentNetwork The current {@link CyNetwork}.
	 * @param cyTable The {@link CyTable} that contains values to be plotted on the {@link JFreeChart}
	 * @param mode {@link AxisMode}
	 * @return The {@link JPanel} consisting of the chart, checkboxes with column names etc.
	 */
	CytoChart createChart(CyNetwork currentNetwork, CyTable cyTable, AxisMode mode);
	
	/**
	 * Creates a {@link JPanel} consisting of a JFreeChart, checkboxes with column names etc.
	 * @param currentNetwork The current {@link CyNetwork}.
	 * @param cyTable The {@link CyTable} that contains values to be plotted on the {@link JFreeChart}
	 * @param mode {@link AxisMode}
	 * @param height Height of the <code>ChartPanel</code>
	 * @param width Width of the <code>ChartPanel</code>
	 * @return
	 */
	CytoChart createChart(CyNetwork currentNetwork, CyTable cyTable, AxisMode mode, int height, int width);
	
	/**
	 * Creates a {@link JPanel} consisting of a JFreeChart, checkboxes with column names etc.
	 * @param currentNetwork The current {@link CyNetwork}.
	 * @param cyTable The {@link CyTable} that contains values to be plotted on the {@link JFreeChart}
	 * @param mode {@link AxisMode}
	 * @param height Height of the <code>ChartPanel</code>
	 * @param width Width of the <code>ChartPanel</code>
	 * @param rows Canonical names of rows to be plotted.
	 * @param columns Names of columns to be plotted.
	 * @return
	 */
	CytoChart createChart(CyNetwork currentNetwork, CyTable cyTable, AxisMode mode, int height, int width, String[] rows, String[] columns);

}
