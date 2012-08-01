package org.cytoscape.neildhruva.chartapp;

import java.util.List;

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
	 * @param currentNetwork The current {@link CyNetwork}.
	 * @param cyTable The {@link CyTable} that contains values to be plotted on the {@link JFreeChart}
	 * @return {@link CytoChart}. 
	 */
	CytoChart createChart(CyNetwork currentNetwork, CyTable cyTable);
	
	/**
	 * Creates a {@link CytoChart} that is used to manipulate the JPanel, set Rows, Columns, AxisMode, Height and Width.
	 * @param currentNetwork The current {@link CyNetwork}.
	 * @param cyTable The {@link CyTable} that contains values to be plotted on the {@link JFreeChart}
	 * @param mode {@link AxisMode}
	 * @return The {@link CytoChart}.
	 */
	CytoChart createChart(CyNetwork currentNetwork, CyTable cyTable, AxisMode mode);
	
	/**
	 * Creates a {@link CytoChart} that is used to manipulate the JPanel, set Rows, Columns, AxisMode, Height and Width.
	 * @param currentNetwork The current {@link CyNetwork}.
	 * @param cyTable The {@link CyTable} that contains values to be plotted on the {@link JFreeChart}
	 * @param mode {@link AxisMode}
	 * @param height Height of the <code>ChartPanel</code>
	 * @param width Width of the <code>ChartPanel</code>
	 * @return The {@link CytoChart}.
	 */
	CytoChart createChart(CyNetwork currentNetwork, CyTable cyTable, AxisMode mode, int height, int width);
	
	/**
	 * Creates a {@link CytoChart} that is used to manipulate the JPanel, set Rows, Columns, AxisMode, Height and Width.
	 * @param currentNetwork The current {@link CyNetwork}.
	 * @param cyTable The {@link CyTable} that contains values to be plotted on the {@link JFreeChart}
	 * @param mode {@link AxisMode}
	 * @param height Height of the <code>ChartPanel</code>
	 * @param width Width of the <code>ChartPanel</code>
	 * @param rows Canonical names of rows to be plotted.
	 * @param columns Names of columns to be plotted.
	 * @return The {@link CytoChart}.
	 */
	CytoChart createChart(CyNetwork currentNetwork, CyTable cyTable, AxisMode mode, int height, int width, List<String> rows, List<String> columns);

	/**
	 * Gets the chart saved across sessions.
	 * @param cyTable The <code>CyTable</code> which is plotted in the chart.
	 * @param currentNetwork The <code>CyNetwork</code> to which the CyTable and the chart are attached.
	 * @return The {@link CytoChart}.
	 */
	CytoChart getSavedChart(CyNetwork currentNetwork, CyTable cyTable);
	
	/**
	 * Checks of the chart corresponding to the {@link CyTable} and the {@link CyNetwork} is saved.
	 * @param cyTable The {@link CyTable} which is plotted in the chart.
	 * @param currentNetwork The {@link CyNetwork} to which the {@link CyTable} and the chart are attached.
	 * @return Whether or not the chart corresponding to the {@link CyTable} and the {@link CyNetwork} is saved.
	 */
	Boolean isChartSaved(CyTable cyTable, CyNetwork currentNetwork);
}
