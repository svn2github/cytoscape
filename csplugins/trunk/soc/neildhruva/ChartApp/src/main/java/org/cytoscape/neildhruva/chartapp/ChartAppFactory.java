package org.cytoscape.neildhruva.chartapp;

import java.util.List;
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
		ROWS{
		    public String toString() {
		        return "ROWS";
		    }
		},
		/**
		 * Columns on X-Axis
		 */
		COLUMNS{
		    public String toString() {
		        return "COLUMNS";
		    }
		}
	}
	
	/**
	 * Creates a {@link CytoChart} that is used to manipulate the JPanel, set Rows, Columns, AxisMode, Height and Width.
	 * @param chartName Name of the chart.
	 * @param cyTable The {@link CyTable} that contains values to be plotted on the {@link JFreeChart}
	 * @return {@link CytoChart}. 
	 */
	CytoChart createChart(String chartName, CyTable cyTable);
	
	/**
	 * Creates a {@link CytoChart} that is used to manipulate the JPanel, set Rows, Columns, AxisMode, Height and Width.
	 * @param chartName Name of the chart.
	 * @param cyTable The {@link CyTable} that contains values to be plotted on the {@link JFreeChart}
	 * @param mode {@link AxisMode}
	 * @return The {@link CytoChart}.
	 */
	CytoChart createChart(String chartName, CyTable cyTable, AxisMode mode);
	
	/**
	 * Creates a {@link CytoChart} that is used to manipulate the JPanel, set Rows, Columns, AxisMode, Height and Width.
	 * @param chartName Name of the chart.
	 * @param cyTable The {@link CyTable} that contains values to be plotted on the {@link JFreeChart}
	 * @param mode {@link AxisMode}
	 * @param rows Canonical names of rows to be plotted.
	 * @param columns Names of columns to be plotted.
	 * @return The {@link CytoChart}.
	 */
	CytoChart createChart(String chartName, CyTable cyTable, AxisMode mode, List<String> rows, List<String> columns);

	/**
	 * Gets the chart saved across sessions.
	 * @param chartName Name of the chart.
	 * @param cyTable The <code>CyTable</code> which is plotted in the chart.
	 * @return The {@link CytoChart}.
	 */
	CytoChart getSavedChart(String chartName, CyTable cyTable);
	
	/**
	 * Checks of the chart corresponding to the {@link CyTable} and the {@link CyNetwork} is saved.
	 * @param chartName Name of the chart.
	 * @param cyTable The {@link CyTable} which is plotted in the chart.
	 * @return Whether or not the chart corresponding to the {@link CyTable} and the {@link CyNetwork} is saved.
	 */
	Boolean isChartSaved(String chartName, CyTable cyTable);
}
